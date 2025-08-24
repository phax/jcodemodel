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
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.writer.JCMWriter;
import com.helger.jcodemodel.writer.ProgressCodeWriter.IProgressTracker;

@Mojo(name = "generate-source", threadSafe = true, defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateSourceMojo extends AbstractMojo {

  /**
   * passed to the generator in case it needs project-specific variables, like
   * path etc.
   */
  @Component
  private MavenProject project;

  /**
   * target directory to place the generated java files into. The directory is
   * created but not cleaned.
   */
  @Parameter(property = "jcodemodel.outdir", defaultValue = "src/generated/java")
  private String outputDir;

  /**
   * source of the data to transmit to the generator when building the model. can
   * be a url, a file.
   */
  @Parameter(property = "jcodemodel.source")
  private String source;

  /**
   * The fullly qualified name of the generator used. Only needed if
   * <ul>
   * <li>you use several generators in the plugin dependencies,</li>
   * <li>the generator does not provide a {@link #GENERATOR_CLASS_FILE} file to
   * load the class automatically</li>
   * <li>you want a different generator class than the one it defaults to</li>
   * </ul>
   */
  @Parameter(property = "jcodemodel.generator")
  private String generator;

  /**
   * direct Map of params to transmit to the generator.
   */
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
    InputStream source = findSource();
    try {
      cmb.build(cm, source);
      new JCMWriter(cm).build(dir, (IProgressTracker) null);
    } catch (JCodeModelException | IOException e) {
      throw new MojoFailureException(e);
    }

  }

  /**
   * deduce the out java files output folder
   */
  protected File javaOutputFolder() {
    if (outputDir == null) {
      return new File(project.getBasedir(), "src/generated/java");
    } else if (outputDir.startsWith("/")) {
      return new File(outputDir);
    } else {
      return new File(project.getBasedir(), outputDir);
    }
  }

  public final String GENERATOR_CLASS_FILE = "jcodemodel/plugin/generator";

  /**
   * deduce the generator's class and instantiate it
   */
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

  protected InputStream findSource() {
    return null;
  }

}
