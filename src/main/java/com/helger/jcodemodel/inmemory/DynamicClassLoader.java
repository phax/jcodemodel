package com.helger.jcodemodel.inmemory;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.writer.JCMWriter;

/**
 * class loader that allows dynamic classes.
 *
 * <p>
 * add class models using {@link #withCode(JCodeModel)} , then you can use it as
 * a normal classloader, eg {@link ClassLoader.#loadClass(String)}
 * </p>
 *
 */
public class DynamicClassLoader extends ClassLoader {

  private Map<String, CompiledCodeJavaFile> customCompiledCode = new HashMap<>();

  private Map<String, ByteArrayOutputStream> customResources = new HashMap<>();

  public DynamicClassLoader(ClassLoader parent) {
    super(parent);
  }

  public void setCode(CompiledCodeJavaFile cc) {
    customCompiledCode.put(cc.getName(), cc);
  }

  public CompiledCodeJavaFile getCode(String fullClassName) {
    return customCompiledCode.get(fullClassName);
  }

  public void addResources(Map<String, ByteArrayOutputStream> resources) {
    customResources.putAll(resources);
  }


  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    CompiledCodeJavaFile cc = customCompiledCode.get(name);
    if (cc == null) {
      return super.findClass(name);
    }
    byte[] byteCode = cc.getByteCode();
    return defineClass(name, byteCode, 0, byteCode.length);
  }

  URLStreamHandler handler = new URLStreamHandler() {

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
      return new URLConnection(u) {

        ByteArrayOutputStream baos = customResources.get(u.getFile());

        @Override
        public void connect() throws IOException {
          if (baos == null) {
            throw new FileNotFoundException(u.getFile());
          }
        }

        @Override
        public InputStream getInputStream() throws IOException {
          if (baos == null) {
            throw new FileNotFoundException(u.getFile());
          }
          return new ByteArrayInputStream(baos.toByteArray());
        }
      };
    }

  };

  @Override
  protected URL findResource(String name) {
    ByteArrayOutputStream baos = customResources.get(name);
    if (baos != null) {
      try {
        return new URL("memory", null, 0, name, handler);
      } catch (MalformedURLException e) {
        throw new UnsupportedOperationException("catch this", e);
      }
    } else {
      return super.findResource(name);
    }
  }

  ////
  // static methods to produce the code in a dynamicclassloader
  ////

  protected static DynamicClassLoader dynCL() {
    return new DynamicClassLoader(JavaCompiler.class.getClassLoader());
  }

  /**
   * generate bytecode in a class loader from a {@link JCodeModel}. This allows
   * to use, with reflection, the classes defined in the codemodel by loading
   * them from the class loader.
   *
   * @param <T>
   *          the subtype of DynamicClassLoader
   * @param cm
   *          the codemodel to load
   * @param cl
   *          the dynamic class loader to add the definitions into
   * @return cl
   */
  public static <T extends DynamicClassLoader> T generate(JCodeModel cm, T cl) {
    MapCodeWriter codeWriter = new MapCodeWriter();
    try {
      new JCMWriter(cm).build(codeWriter);
    } catch (IOException e) {
      throw new UnsupportedOperationException("catch this exception", e);
    }
    return generate(codeWriter.getBinaries().entrySet(), cl);
  }

  /**
   * shortcut for {@link #generate(JCodeModel, DynamicClassLoader)} with a
   * correct classloader
   */
  public static DynamicClassLoader generate(JCodeModel cm) {
    return generate(cm, dynCL());
  }

  /**
   * generate bytecode in a class loader from an iterable over source files.
   * This allows to use, with reflection, the classes defined by their sources,
   * by loading them from the class loader.
   *
   * @param <T>
   *          the subtype of DynamicClassLoader
   * @param sourceFiles
   *          the source files to load
   * @param cl
   *          the dynamic class loader to add the definitions into
   * @return cl
   */
  public static <T extends DynamicClassLoader> T generate(Iterable<Entry<String, ByteArrayOutputStream>> sourceFiles,
      T cl) {
    ArrayList<JavaFileObject> compilationUnits = new ArrayList<>();
    HashMap<String, ByteArrayOutputStream> nonJava = new HashMap<>();
    for (Entry<String, ByteArrayOutputStream> e : sourceFiles) {
      if (e.getKey().endsWith(".java")) {
        try {
          compilationUnits.add(new SourceJavaFile(e.getKey(), e.getValue().toString()));
          String className = e.getKey().replaceAll("/", ".").replace(".java", "");
          CompiledCodeJavaFile cc = new CompiledCodeJavaFile(className);
          cl.setCode(cc);
        } catch (Exception e1) {
          throw new UnsupportedOperationException("catch this exception", e1);
        }
      } else {
        nonJava.put(e.getKey(), e.getValue());
      }
    }
    if (!compilationUnits.isEmpty()) {
      try {
        ForwardingJavaFileManager<JavaFileManager> fileManager = new ClassLoaderFileManager(
            javac.getStandardFileManager(diagnostic -> System.err.println("file diagnostic " + diagnostic), null, null),
            cl);
        JavaCompiler.CompilationTask task = javac.getTask(null, fileManager,
            diagnostic -> System.err.println(" compile diagnostic " + diagnostic), null, null, compilationUnits);
        task.call();
      } catch (Exception e1) {
        throw new UnsupportedOperationException("catch this exception", e1);
      }
    }
    cl.addResources(nonJava);
    return cl;
  }

  /**
   * shortcut for {@link #generate(Iterable, DynamicClassLoader)} with a correct
   * class loader
   */
  public static DynamicClassLoader generate(Iterable<Entry<String, ByteArrayOutputStream>> sourceFiles) {
    return generate(sourceFiles, dynCL());
  }

  private static final JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
}
