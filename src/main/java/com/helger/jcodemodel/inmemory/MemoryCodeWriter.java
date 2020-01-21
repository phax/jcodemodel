package com.helger.jcodemodel.inmemory;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.writer.AbstractCodeWriter;
import com.helger.jcodemodel.writer.JCMWriter;

/**
 * An {@see AbstractCodeWriter} that stores the files created in the program
 * memory.
 *
 * <p>
 * Can be loaded directly from a JCodeMOdel using the static
 * {@link {from(JCodeModel)}. All the .java files and the resources are then
 * stored internally with their full path.
 * </p>
 * <p>
 * Give access to the internal resources using {@link #getBinaries()}
 * </p>
 * <p>
 * can also be compiled into memory using {@link compile()}
 * </p>
 *
 *
 */
public class MemoryCodeWriter extends AbstractCodeWriter {

  public MemoryCodeWriter() {
    super(Charset.defaultCharset(), System.lineSeparator());
  }

  public static MemoryCodeWriter from(JCodeModel jcm) {
    MemoryCodeWriter codeWriter = new MemoryCodeWriter();
    try {
      new JCMWriter(jcm).build(codeWriter);
    } catch (IOException e) {
      throw new UnsupportedOperationException("catch this exception", e);
    }
    return codeWriter;
  }

  private HashMap<String, ByteArrayOutputStream> binaries = new HashMap<>();

  @Override
  public void close() throws IOException {
    // nothing.
  }

  /**
   *
   * @return an unmodifiable map of the internal binaries.
   */
  public Map<String, ByteArrayOutputStream> getBinaries() {
    return Collections.unmodifiableMap(binaries);
  }

  @Override
  public OutputStream openBinary(String sDirName, String sFilename) throws IOException {
    String fullname = sDirName + "/" + sFilename;

    ByteArrayOutputStream ret = binaries.get(fullname);
    if (ret == null) {
      ret = new ByteArrayOutputStream();
      binaries.put(fullname, ret);
    }
    return ret;
  }

  public <T extends DynamicClassLoader> T compile(T cl) {
    ArrayList<JavaFileObject> compilationUnits = new ArrayList<>();
    HashMap<String, ByteArrayOutputStream> nonJava = new HashMap<>();
    for (Entry<String, ByteArrayOutputStream> e : getBinaries().entrySet()) {
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
   * shortcut for {@link #compile(DynamicClassLoader)} with a correct class
   * loader
   */
  public DynamicClassLoader compile() {
    return compile(dynCL());
  }

  /**
   * creates a dynamic class loaders that delegates unknown resources and
   * classes to the classloader of the javacompiler.
   */
  protected static DynamicClassLoader dynCL() {
    return new DynamicClassLoader(JavaCompiler.class.getClassLoader());
  }

  private static final JavaCompiler javac = ToolProvider.getSystemJavaCompiler();

}
