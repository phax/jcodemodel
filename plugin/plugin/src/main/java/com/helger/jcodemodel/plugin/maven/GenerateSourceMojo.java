/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.jcodemodel.plugin.maven;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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

import jakarta.annotation.Nullable;

@Mojo (name = "generate-source", threadSafe = true, defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateSourceMojo extends AbstractMojo
{
  /**
   * passed to the generator in case it needs project-specific variables, like path etc.
   */
  @Component
  private MavenProject project;

  /**
   * target directory to place the generated java files into. The directory is created but not
   * cleaned.
   */
  @Parameter (property = "jcodemodel.outdir", defaultValue = "src/generated/java")
  private String outputDir;

  @Parameter (property = "jcodemodel.rootpackage", defaultValue = "")
  private String rootPackage;

  /**
   * source of the data to transmit to the generator when building the model. can be a url, a file.
   */
  @Parameter (property = "jcodemodel.source")
  private String source;

  @Parameter (property = "jcodemodel.data")
  private String data;

  /**
   * The fullly qualified name of the generator used. Only needed if
   * <ul>
   * <li>you use several generators in the plugin dependencies,</li>
   * <li>the generator does not provide a {@link #GENERATOR_CLASS_FILE} file to load the class
   * automatically</li>
   * <li>you want a different generator class than the one it defaults to</li>
   * </ul>
   */
  @Parameter (property = "jcodemodel.generator")
  private String generator;

  /**
   * direct Map of params to transmit to the generator.
   */
  @Parameter (property = "jcodemodel.params")
  private Map <String, String> params;

  @Override
  public void execute () throws MojoExecutionException, MojoFailureException
  {
    final File dir = javaOutputFolder ();
    getLog ().debug ("generating model into " + dir.getAbsolutePath ());
    dir.mkdirs ();
    ICodeModelBuilder cmb = null;
    try
    {
      cmb = findBuilder ();
    }
    catch (final Exception e)
    {
      throw new MojoFailureException (e);
    }
    if (cmb == null)
    {
      throw new MojoExecutionException ("could not load the generator class");
    }
    getLog ().info ("Generator " +
                    cmb.getClass ().getCanonicalName () +
                    " generates model into " +
                    dir.getAbsolutePath () +
                    " with params " +
                    params);

    if (rootPackage != null && !rootPackage.isBlank ())
    {
      cmb.setRootPackage (rootPackage);
    }
    if (params != null)
    {
      cmb.configure (params);
    }

    final JCodeModel cm = new JCodeModel ();
    if (data != null && !data.isBlank () && source != null && !source.isBlank ())
    {
      getLog ().warn ("discarding source param " + source + " as data is already set");
    }
    final InputStream source = data == null || data.isBlank () ? findSource () : new ByteArrayInputStream (data
                                                                                                               .getBytes ());
    try
    {
      cmb.build (cm, source);
      new JCMWriter (cm).build (dir, (IProgressTracker) null);
    }
    catch (JCodeModelException | IOException e)
    {
      throw new MojoFailureException (e);
    }
  }

  /**
   * deduce the out java files output folder
   */
  protected File javaOutputFolder ()
  {
    if (outputDir == null || outputDir.isBlank ())
    {
      return new File (project.getBasedir (), "src/generated/java");
    }
    else
      if (outputDir.startsWith ("/"))
      {
        return new File (outputDir);
      }
      else
      {
        return new File (project.getBasedir (), outputDir);
      }
  }

  public static final String GENERATOR_CLASS_FILE = "jcodemodel/plugin/generator";

  /**
   * deduce the generator's class and instantiate it
   */
  protected ICodeModelBuilder findBuilder () throws Exception
  {
    String generatorClass = generator;
    if (generatorClass == null)
    {
      generatorClass = findGeneratorClass ();
    }
    return generatorClass == null || generatorClass.isBlank () ? null : (ICodeModelBuilder) Class.forName (
                                                                                                           generatorClass)
                                                                                                 .getDeclaredConstructor ()
                                                                                                 .newInstance ();
  }

  @Nullable
  protected String findGeneratorClass () throws IOException
  {
    try (final InputStream is = getClass ().getClassLoader ().getResourceAsStream (GENERATOR_CLASS_FILE))
    {
      if (is != null)
      {
        final String className = new String (is.readAllBytes (), StandardCharsets.UTF_8);
        getLog ().debug ("using generator class " + className);
        return className;
      }
      getLog ().error ("can't load resource " + GENERATOR_CLASS_FILE);
      return null;
    }
  }

  @Nullable
  protected InputStream findSource () throws MojoExecutionException
  {
    if (source == null || source.isBlank ())
      return null;

    // dumb checking : is it a file ? a URL ?
    try
    {
      final File targetFile = source.startsWith ("/") ? new File (source) : new File (project.getBasedir (), source);
      return new FileInputStream (targetFile);
    }
    catch (final Exception e)
    {
      getLog ().info ("while trying to open " + source + " as a file", e);
    }

    try
    {
      final URL url = new URL (source);
      return url.openStream ();
    }
    catch (final IOException e)
    {
      getLog ().info ("while trying to open " + source + " as a url", e);
    }

    throw new MojoExecutionException ("could not open provided source " + source + " as a file or url");
  }

}
