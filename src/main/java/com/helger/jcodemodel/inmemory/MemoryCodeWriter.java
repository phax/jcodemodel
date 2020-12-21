package com.helger.jcodemodel.inmemory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.writer.AbstractCodeWriter;
import com.helger.jcodemodel.writer.JCMWriter;

/**
 * An {@link AbstractCodeWriter} that stores the files created in the program
 * memory.
 * <p>
 * Can be loaded directly from a JCodeModel using the static
 * {@link #from(JCodeModel)} All the .java files and the resources are then
 * stored internally with their full path.
 * </p>
 * <p>
 * Give access to the internal resources using {@link #getBinaries()} can also
 * be compiled into memory using {@link #compile()}
 * </p>
 */
public class MemoryCodeWriter extends AbstractCodeWriter
{
  private static final JavaCompiler JAVAC = ToolProvider.getSystemJavaCompiler ();

  private final Map <String, NonBlockingByteArrayOutputStream> m_aBinaries = new HashMap <> ();

  public MemoryCodeWriter ()
  {
    super (Charset.defaultCharset (), System.lineSeparator ());
  }

  @Override
  public void close () throws IOException
  {
    // nothing.
  }

  /**
   * @return an unmodifiable map of the internal binaries.
   */
  @Nonnull
  public Map <String, NonBlockingByteArrayOutputStream> getBinaries ()
  {
    return Collections.unmodifiableMap (m_aBinaries);
  }

  @Override
  public OutputStream openBinary (final String sDirName, final String sFilename) throws IOException
  {
    final String sFullname = sDirName + "/" + sFilename;

    return m_aBinaries.computeIfAbsent (sFullname, k -> new NonBlockingByteArrayOutputStream ());
  }

  @Nonnull
  public <T extends DynamicClassLoader> T compile (@Nonnull final T aDynamicClassLoader)
  {
    final List <JavaFileObject> aCompilationUnits = new ArrayList <> ();

    final Map <String, NonBlockingByteArrayOutputStream> aNonJava = new HashMap <> ();

    for (final Entry <String, NonBlockingByteArrayOutputStream> e : getBinaries ().entrySet ())
      if (e.getKey ().endsWith (".java"))
        try
        {
          // Use the configured encoding
          aCompilationUnits.add (new SourceJavaFile (e.getKey (), e.getValue ().getAsString (encoding ())));

          final String className = e.getKey ().replaceAll ("/", ".").replace (".java", "");
          final CompiledCodeJavaFile cc = new CompiledCodeJavaFile (className);
          aDynamicClassLoader.setCode (cc);
        }
        catch (final Exception e1)
        {
          throw new UnsupportedOperationException ("catch this exception", e1);
        }
      else
        aNonJava.put (e.getKey (), e.getValue ());

    if (!aCompilationUnits.isEmpty ())
      try
      {
        final ForwardingJavaFileManager <JavaFileManager> aFileManager = new ClassLoaderFileManager (JAVAC.getStandardFileManager (x -> System.err.println ("file diagnostic " +
                                                                                                                                                            x),
                                                                                                                                   null,
                                                                                                                                   null),
                                                                                                     aDynamicClassLoader);
        final JavaCompiler.CompilationTask task = JAVAC.getTask (null,
                                                                 aFileManager,
                                                                 x -> System.err.println (" compile diagnostic " + x),
                                                                 null,
                                                                 null,
                                                                 aCompilationUnits);
        task.call ();
      }
      catch (final Exception e1)
      {
        throw new UnsupportedOperationException ("catch this exception", e1);
      }
    aDynamicClassLoader.addResources (aNonJava);
    return aDynamicClassLoader;
  }

  /**
   * creates a dynamic class loaders that delegates unknown resources and
   * classes to the classloader of the this class.
   */
  protected static DynamicClassLoader dynCL ()
  {
    return new DynamicClassLoader (JavaCompiler.class.getClassLoader ());
  }

  /**
   * shortcut for {@link #compile(DynamicClassLoader)} with a correct class
   * loader
   *
   * @return The classloader used
   */
  @Nonnull
  public DynamicClassLoader compile ()
  {
    return compile (dynCL ());
  }

  @Nonnull
  public static MemoryCodeWriter from (@Nonnull final JCodeModel jcm)
  {
    final MemoryCodeWriter codeWriter = new MemoryCodeWriter ();
    try
    {
      new JCMWriter (jcm).build (codeWriter);
    }
    catch (final IOException e)
    {
      throw new UnsupportedOperationException ("catch this exception", e);
    }
    return codeWriter;
  }
}
