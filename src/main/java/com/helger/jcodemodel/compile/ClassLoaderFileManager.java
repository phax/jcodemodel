package com.helger.jcodemodel.compile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardLocation;

import com.helger.commons.annotation.UnsupportedOperation;

/**
 * java file manager that also checks and writes inside a given
 * {@link DynamicClassLoader}. This is used during compilation of a
 * {@link com.helger.jcodemodel.JCodeModel} specification.
 * <p>
 * basically must overwrite the
 * {@link #list(javax.tools.JavaFileManager.Location, String, Set, boolean)}
 * method to check inside the jar
 * </p>
 * <p>
 * most of the code comes from
 * http://atamur.blogspot.fr/2009/10/using-built-in-javacompiler-with-custom.html
 * </p>
 */
public class ClassLoaderFileManager extends ForwardingJavaFileManager <JavaFileManager>
{
  public static final String CLASS_FILE_EXTENSION = JavaFileObject.Kind.CLASS.extension;

  private final DynamicClassLoader m_aCL;

  public ClassLoaderFileManager (final JavaFileManager aFileManager, final DynamicClassLoader cl)
  {
    super (aFileManager);
    m_aCL = cl;
  }

  @Override
  public ClassLoader getClassLoader (final Location location)
  {
    return m_aCL;
  }

  @Override
  public boolean hasLocation (final Location location)
  {
    return super.hasLocation (location);
  }

  @Override
  public Iterable <JavaFileObject> list (final Location location,
                                         @Nonnull final String packageName,
                                         @Nonnull final Set <Kind> kinds,
                                         final boolean recurse) throws IOException
  {
    if (location == StandardLocation.PLATFORM_CLASS_PATH || packageName.startsWith ("java"))
      // let standard manager handle
      return super.list (location, packageName, kinds, recurse);

    if (location == StandardLocation.CLASS_PATH && kinds.contains (JavaFileObject.Kind.CLASS))
      // app specific classes are here
      return find (packageName);

    return Collections.emptyList ();
  }

  public List <JavaFileObject> find (@Nonnull final String packageName) throws IOException
  {
    final String javaPackageName = packageName.replaceAll ("\\.", "/");
    final List <JavaFileObject> result = new ArrayList <> ();
    final Enumeration <URL> urlEnumeration = m_aCL.getResources (javaPackageName);
    while (urlEnumeration.hasMoreElements ())
    { // one URL for each jar on the
      // classpath that has the given
      // package
      final URL packageFolderURL = urlEnumeration.nextElement ();
      result.addAll (listUnder (packageName, packageFolderURL));
    }

    return result;
  }

  private Collection <JavaFileObject> listUnder (final String packageName, final URL packageFolderURL)
  {
    final File directory = new File (packageFolderURL.getFile ());
    if (directory.isDirectory ())
      // local execution
      return processDir (packageName, directory);
    return processJar (packageFolderURL);
  }

  private List <JavaFileObject> processJar (final URL packageFolderURL)
  {
    final List <JavaFileObject> result = new ArrayList <> ();
    try
    {
      final String jarUri = packageFolderURL.toExternalForm ().split ("!")[0];

      final JarURLConnection jarConn = (JarURLConnection) packageFolderURL.openConnection ();
      final String rootEntryName = jarConn.getEntryName ();
      final int rootEnd = rootEntryName.length () + 1;

      final Enumeration <JarEntry> entryEnum = jarConn.getJarFile ().entries ();
      while (entryEnum.hasMoreElements ())
      {
        final JarEntry jarEntry = entryEnum.nextElement ();
        final String name = jarEntry.getName ();
        if (name.startsWith (rootEntryName) && name.indexOf ('/', rootEnd) == -1 && name.endsWith (CLASS_FILE_EXTENSION))
        {
          final URI uri = URI.create (jarUri + "!/" + name);
          String binaryName = name.replaceAll ("/", ".");
          binaryName = binaryName.replaceAll (CLASS_FILE_EXTENSION + "$", "");

          result.add (new CustomJavaFileObject (binaryName, uri));
        }
      }
    }
    catch (final Exception e)
    {
      throw new RuntimeException ("Wasn't able to open " + packageFolderURL + " as a jar file", e);
    }
    return result;
  }

  @Nonnull
  private List <JavaFileObject> processDir (@Nonnull final String packageName, @Nonnull final File directory)
  {
    final List <JavaFileObject> result = new ArrayList <> ();

    final File [] childFiles = directory.listFiles ();
    for (final File childFile : childFiles)
      if (childFile.isFile ())
        // We only want the .class files.
        if (childFile.getName ().endsWith (CLASS_FILE_EXTENSION))
        {
          String binaryName = packageName + "." + childFile.getName ();
          binaryName = binaryName.replaceAll (CLASS_FILE_EXTENSION + "$", "");

          result.add (new CustomJavaFileObject (binaryName, childFile.toURI ()));
        }

    return result;
  }

  @Override
  public JavaFileObject getJavaFileForOutput (final Location location,
                                              final String className,
                                              final Kind kind,
                                              final FileObject sibling) throws IOException
  {
    CompiledCodeJavaFile ret = m_aCL.getCode (className);
    if (ret == null)
      try
      {
        ret = new CompiledCodeJavaFile (className);
        m_aCL.setCode (ret);
      }
      catch (final Exception e)
      {
        throw new UnsupportedOperationException ("while creating code for " + className, e);
      }
    return ret;
  }

  @Override
  public String inferBinaryName (final Location location, final JavaFileObject file)
  {
    if (file instanceof CustomJavaFileObject)
      return ((CustomJavaFileObject) file).binaryName ();

    // if it's not CustomJavaFileObject, then it's coming from standard file
    // manager - let it handle the file
    return super.inferBinaryName (location, file);
  }

  /**
   * @author atamur
   * @since 15-Oct-2009
   */
  private static class CustomJavaFileObject implements JavaFileObject
  {
    private final String m_sBinaryName;
    private final URI m_sURI;
    private final String m_sName;

    public CustomJavaFileObject (final String binaryName, final URI uri)
    {
      m_sBinaryName = binaryName;
      m_sURI = uri;
      m_sName = uri.getPath () == null ? uri.getSchemeSpecificPart () : uri.getPath ();
    }

    @Override
    public URI toUri ()
    {
      return m_sURI;
    }

    @Override
    public InputStream openInputStream () throws IOException
    {
      // easy way to handle any URI!
      return m_sURI.toURL ().openStream ();
    }

    @Override
    @UnsupportedOperation
    public OutputStream openOutputStream () throws IOException
    {
      throw new UnsupportedOperationException ();
    }

    @Override
    public String getName ()
    {
      return m_sName;
    }

    @Override
    @UnsupportedOperation
    public Reader openReader (final boolean ignoreEncodingErrors) throws IOException
    {
      throw new UnsupportedOperationException ();
    }

    @Override
    @UnsupportedOperation
    public CharSequence getCharContent (final boolean ignoreEncodingErrors) throws IOException
    {
      throw new UnsupportedOperationException ();
    }

    @Override
    @UnsupportedOperation
    public Writer openWriter () throws IOException
    {
      throw new UnsupportedOperationException ();
    }

    @Override
    public long getLastModified ()
    {
      return 0;
    }

    @Override
    @UnsupportedOperation
    public boolean delete ()
    {
      throw new UnsupportedOperationException ();
    }

    @Override
    public Kind getKind ()
    {
      return Kind.CLASS;
    }

    @Override // copied from SImpleJavaFileManager
    public boolean isNameCompatible (final String simpleName, final Kind kind)
    {
      final String baseName = simpleName + kind.extension;
      return kind.equals (getKind ()) && (baseName.equals (getName ()) || getName ().endsWith ("/" + baseName));
    }

    @Override
    public NestingKind getNestingKind ()
    {
      throw new UnsupportedOperationException ();
    }

    @Override
    public Modifier getAccessLevel ()
    {
      throw new UnsupportedOperationException ();
    }

    public String binaryName ()
    {
      return m_sBinaryName;
    }

    @Override
    public String toString ()
    {
      return "CustomJavaFileObject{" + "uri=" + m_sURI + '}';
    }
  }
}
