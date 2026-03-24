package com.helger.jcodemodel.compile.annotation;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.writer.JCMWriter;
import com.helger.jcodemodel.writer.ProgressCodeWriter.IProgressTracker;

public class GenerateTestFiles {

  private static final String OUTPUT_DIR = "src/generated/javatest";

  private static final String CLASS_SCAN_DIR = "src/main/java";

  private final File outputDir;

  private final File classScanDir;

  public static void main(String[] args) {
    String rootPath = args == null || args.length == 0 ? "." : args[0];
    File outputFile = new File(rootPath, OUTPUT_DIR);
    File classScanDir = new File(rootPath, CLASS_SCAN_DIR);
    new GenerateTestFiles(outputFile, classScanDir)
        .apply();
  }

  public GenerateTestFiles(File outputDir, File classScanDir) {
    this.outputDir = outputDir;
    this.classScanDir = classScanDir;
  }

  void apply() {
    delete(outputDir);
    outputDir.mkdirs();
    scanClasses(classScanDir).forEach(this::applyCandidateClass);

  }

  void delete(File file) {
    if (file.isDirectory()) {
      for (File sub : file.listFiles()) {
        delete(sub);
      }
    }
    file.delete();
  }

  Stream<String> scanClasses(File rootDir) {
    return scanClasses(rootDir, "", Stream.of());
  }

  Stream<String> scanClasses(File dir, String packageName, Stream<String> stream) {
    List<String> newFound = new ArrayList<>();
    if(!dir.isDirectory()) {
      throw new RuntimeException("file " + dir.getAbsolutePath() + " expected to be a dir");
    }
    for (File child : dir.listFiles()) {
      if (child.isDirectory()) {
        stream = scanClasses(child, (packageName.isEmpty() ? "" : packageName + ".") + child.getName(), stream);

      } else if (child.isFile() && child.getName().endsWith(".java")) {
        newFound.add(packageName + "." + child.getName().replace(".java", ""));
      }
    }
    if (!newFound.isEmpty()) {
      stream = Stream.concat(stream, newFound.stream());
    }
    return stream;
  }

  void applyCandidateClass(String className) {
    Class<?> clazz;
    try {
      clazz = Class.forName(className);
      if (clazz.getAnnotation(TestJCM.class) != null) {
        runGeneration(clazz);
      }
    } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException | InstantiationException | NoSuchMethodException | SecurityException
        | IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void runGeneration(Class<?> clazz)
      throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException,
      NoSuchMethodException, SecurityException, IOException {
    for (Method m : clazz.getDeclaredMethods()) {
      // only apply to methods public, with 0 args, and that produce a JCodeModel
      if ((m.getModifiers() & Modifier.PUBLIC) > 0
          && m.getParameterCount() == 0) {
        if (m.getReturnType().equals(JCodeModel.class)) {
          m.setAccessible(true);
          JCodeModel produced = null;
          if ((m.getModifiers() & Modifier.STATIC) > 0) {
            produced = (JCodeModel) m.invoke(null);
          } else {
            Object inst = clazz.getDeclaredConstructor().newInstance();
            produced = (JCodeModel) m.invoke(inst);
          }
          if (produced != null) {
            new JCMWriter(produced).build(outputDir, (IProgressTracker) null);
          }
        }
      }
    }
  }

}
