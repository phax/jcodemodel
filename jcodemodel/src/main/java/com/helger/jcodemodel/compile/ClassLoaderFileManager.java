/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2025 Philip Helger + contributors
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
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

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardLocation;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.style.UnsupportedOperation;
import com.helger.base.string.StringHelper;
import com.helger.base.string.StringReplace;

/**
 * java file manager that also checks and writes inside a given {@link DynamicClassLoader}. This is
 * used during compilation of a {@link com.helger.jcodemodel.JCodeModel} specification.
 * <p>
 * basically must overwrite the
 * {@link #list(javax.tools.JavaFileManager.Location, String, Set, boolean)} method to check inside
 * the jar
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
                                         @NonNull final String packageName,
                                         @NonNull final Set <Kind> kinds,
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

  public List <JavaFileObject> find (@NonNull final String packageName) throws IOException
  {
    final String sJavaPackageName = StringReplace.replaceAll (packageName, '.', '/');
    final List <JavaFileObject> result = new ArrayList <> ();
    final Enumeration <URL> urlEnumeration = m_aCL.getResources (sJavaPackageName);
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
      final String jarUri = StringHelper.getExplodedArray ('!', packageFolderURL.toExternalForm (), 2)[0];

      final JarURLConnection jarConn = (JarURLConnection) packageFolderURL.openConnection ();
      final String rootEntryName = jarConn.getEntryName ();
      final int rootEnd = rootEntryName.length () + 1;

      final Enumeration <JarEntry> entryEnum = jarConn.getJarFile ().entries ();
      while (entryEnum.hasMoreElements ())
      {
        final JarEntry jarEntry = entryEnum.nextElement ();
        final String name = jarEntry.getName ();
        if (name.startsWith (rootEntryName) &&
            name.indexOf ('/', rootEnd) == -1 &&
            name.endsWith (CLASS_FILE_EXTENSION))
        {
          final URI uri = URI.create (jarUri + "!/" + name);
          String binaryName = StringReplace.replaceAll (name, '/', '.');
          binaryName = StringHelper.trimEnd (binaryName, CLASS_FILE_EXTENSION);

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

  @NonNull
  private List <JavaFileObject> processDir (@NonNull final String packageName, @NonNull final File directory)
  {
    final List <JavaFileObject> result = new ArrayList <> ();

    final File [] childFiles = directory.listFiles ();
    for (final File childFile : childFiles)
      if (childFile.isFile ())
        // We only want the .class files.
        if (childFile.getName ().endsWith (CLASS_FILE_EXTENSION))
        {
          String binaryName = packageName + "." + childFile.getName ();
          binaryName = StringHelper.trimEnd (binaryName, CLASS_FILE_EXTENSION);

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
        throw new UnsupportedOperationException (e);
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
