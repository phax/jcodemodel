package com.helger.jcodemodel.plugin.maven;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.plugin.maven.expressions.ExpressionsBuildingProcess;
import com.helger.jcodemodel.writer.JCMWriter;
import com.helger.jcodemodel.writer.ProgressCodeWriter.IProgressTracker;

@Mojo (name = "generate-expressions", threadSafe = true, defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateExpressionsMojo extends AbstractMojo
{
  /**
   * passed to the generator in case it needs project-specific variables, like path etc.
   */
  @Component
  private MavenProject m_aProject;

  /**
   * target directory to place the generated java files into. The directory is created but not
   * cleaned.
   */
  @Parameter (name = "outputDir", property = "jcodemodel.outdir", defaultValue = "src/generated/java")
  private String outputDir;

  // we can't use that example as default value because it would fail for projects that have
  // non-package groupid or artifactid
  /**
   * fully qualified package to create the classes in. Example
   *
   * <pre>
   * ${project.groupId}.${project.artifactId}
   * </pre>
   */
  @Parameter (name = "rootPackage", property = "jcodemodel.rootpackage", defaultValue = "")
  private String rootPackage;

  /**
   * list of classes names we want to generate expressions to mirror.
   */
  @Parameter (name = "classes", property = "jcodemodel.classes")
  private List <String> classes;

  /**
   * Java feature (major release version) the generated class files are targeted at. When unset the
   * default of {@link JCMWriter#DEFAULT_JAVA_FEATURE} is used.
   */
  @Parameter (name = "javaFeature", property = "jcodemodel.java.feature")
  private String javaFeature;

  @Override
  public void execute () throws MojoExecutionException, MojoFailureException
  {
    getLog ().info ("generate expressions for " + classes);
    final File dir = GenerateSourceMojo.javaOutputFolder (m_aProject, outputDir);
    getLog ().debug ("generating model into " + dir.getAbsolutePath ());
    dir.mkdirs ();
    ExpressionsBuildingProcess process = new ExpressionsBuildingProcess (rootPackage);
    for (String className : classes)
    {
      try
      {
        Class <?> targetClass = Class.forName (className);
        process.addTargetClass (targetClass);
      }
      catch (ClassNotFoundException | JCodeModelException e)
      {
        throw new MojoFailureException (e);
      }
    }
    process.processTargets ();
    try
    {
      new JCMWriter (process.jcm).setJavaFeature (GenerateSourceMojo.findJavaFeature (javaFeature))
                                 .build (dir, (IProgressTracker) null);
    }
    catch (final IOException e)
    {
      throw new MojoFailureException ("after applying to classes " + classes, e);
    }
  }

}
