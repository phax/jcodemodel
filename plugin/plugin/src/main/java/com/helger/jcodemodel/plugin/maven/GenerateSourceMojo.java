package com.helger.jcodemodel.plugin.maven;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.writer.JCMWriter;
import com.helger.jcodemodel.writer.ProgressCodeWriter.IProgressTracker;

@Mojo(name = "generate-source", threadSafe = true)
public class GenerateSourceMojo extends AbstractMojo {

  @Component
  private MavenProject project;

  @Parameter(property = "jcodemodel.outdir")
  private String outputJavaDir;

  @Parameter(property = "jcodemodel.generator")
  private String generator;

  @Parameter(property = "jcodemodel.params")
  private Map<String, String> params;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    File dir = javaOutputFolder();
    getLog().debug("generating model into " + dir.getAbsolutePath());
    dir.mkdirs();
    CodeModelBuilder cmb = null;
    try {
      cmb = findBuilder();
    } catch (Exception e) {
      throw new MojoFailureException(e);
    }
    if (cmb == null) {
      throw new MojoExecutionException("could not load the generator class");
    }
    getLog().info("generating model into " + dir.getAbsolutePath() + " from generator "
        + cmb.getClass().getCanonicalName() + " with params " + params);
    if (params != null) {
      cmb.configure(params);
    }
    JCodeModel cm = new JCodeModel();
    try {
      cmb.build(cm);
      new JCMWriter(cm).build(dir, (IProgressTracker) null);
    } catch (JCodeModelException | IOException e) {
      throw new MojoFailureException(e);
    }

  }

  protected File javaOutputFolder() {
    if (outputJavaDir == null) {
      return new File(project.getBasedir(), "src/generated/java");
    } else if (outputJavaDir.startsWith("/")) {
      return new File(outputJavaDir);
    } else {
      return new File(project.getBasedir(), outputJavaDir);
    }
  }

  public final String GENERATOR_CLASS_FILE = "jcodemodel/plugin/generator";

  protected CodeModelBuilder findBuilder()
      throws Exception {
    String generatorClass = generator;
    if (generatorClass == null) {
      generatorClass = findGeneratorClass();
    }
    return generatorClass == null ? null
        : (CodeModelBuilder) Class.forName(generatorClass).getDeclaredConstructor().newInstance();
  }

  protected String findGeneratorClass() throws IOException {
    try (InputStream is = getClass().getClassLoader().getResourceAsStream(GENERATOR_CLASS_FILE)) {
      if (is != null) {
        String className = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        getLog().debug("using generator class " + className);
        return className;
      } else {
        getLog().error("can't load resource " + GENERATOR_CLASS_FILE);
        return null;
      }
    }
  }

}
