package com.helger.jcodemodel.inmemory;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

  public Map<String, CompiledCode> customCompiledCode = new HashMap<>();

  public DynamicClassLoader(ClassLoader parent) {
    super(parent);
  }

  public void setCode(CompiledCode cc) {
    customCompiledCode.put(cc.getName(), cc);
  }

  public DynamicClassLoader withCode(JCodeModel cm) {
    generate(cm, this);
    return this;
  }

  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    CompiledCode cc = customCompiledCode.get(name);
    if (cc == null) {
      return super.findClass(name);
    }
    byte[] byteCode = cc.getByteCode();
    return defineClass(name, byteCode, 0, byteCode.length);
  }

  /**
   * generate bytecode in a classloader from a {@link JCodeModel}. This allows
   * to use, with reflection, the classes defined in the codemodel by loading
   * them from the classloader.
   *
   * @param <T>
   *          the subtype of DynamicClassLoader
   * @param cm
   *          the codemodel to load
   * @param cl
   *          the classloader to add the definitions into
   * @return cl
   */
  public static <T extends DynamicClassLoader> T generate(JCodeModel cm, T cl) {
    MapCodeWriter codeWriter = new MapCodeWriter();
    try {
      new JCMWriter(cm).build(codeWriter);
    } catch (IOException e) {
      throw new UnsupportedOperationException("catch this exception", e);
    }
    ArrayList<JavaFileObject> compilationUnits = new ArrayList<>();
    for (Entry<String, ByteArrayOutputStream> e : codeWriter.getBinaries().entrySet()) {
      try {
        compilationUnits.add(new SourceCode(e.getKey(), e.getValue().toString()));
        String className = e.getKey().replaceAll("/", ".").replace(".java", "");
        CompiledCode cc = new CompiledCode(className);
        cl.setCode(cc);
      } catch (Exception e1) {
        throw new UnsupportedOperationException("catch this exception", e1);
      }
    }
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
    return cl;
  }

  private static JavaCompiler javac = ToolProvider.getSystemJavaCompiler();

  /**
   * shortcut for {@link #generate(JCodeModel, DynamicClassLoader)} with a
   * correct classloader
   */
  public static DynamicClassLoader generate(JCodeModel cm) {
    return generate(cm, new DynamicClassLoader(JavaCompiler.class.getClassLoader()));
  }
}
