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
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.string.StringHelper;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.writer.JCMWriter;
import com.helger.jcodemodel.writer.ProgressCodeWriter.IProgressTracker;

@Mojo (name = "generate-source", threadSafe = true, defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateSourceMojo extends AbstractMojo
{
  public static final String GENERATOR_CLASS_FILE = "jcodemodel/plugin/generator";

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
  private String m_sOutputDir;

  @Parameter (name = "rootPackage", property = "jcodemodel.rootpackage", defaultValue = "")
  private String m_sRootPackage;

  /**
   * source of the data to transmit to the generator when building the model. can be a url, a file.
   */
  @Parameter (name = "source", property = "jcodemodel.source")
  private String m_sSource;

  /**
   * Java feature (major release version) the generated class files are targeted at. When unset the
   * default of {@link JCMWriter#DEFAULT_JAVA_FEATURE} is used.
   */
  @Parameter (name = "javaFeature", property = "jcodemodel.java.feature")
  private String m_sJavaFeature;

  @Parameter (name = "data", property = "jcodemodel.data")
  private String m_sData;

  /**
   * The fullly qualified name of the generator used. Only needed if
   * <ul>
   * <li>you use several generators in the plugin dependencies,</li>
   * <li>the generator does not provide a {@link #GENERATOR_CLASS_FILE} file to load the class
   * automatically</li>
   * <li>you want a different generator class than the one it defaults to</li>
   * </ul>
   */
  @Parameter (name = "generator", property = "jcodemodel.generator")
  private String m_sGenerator;

  /**
   * documentation added to the main generated classes.
   */
  @Parameter (name = "classHeader", property = "jcodemodel.classheader")
  private String m_sClassHeader;

  /**
   * direct Map of params to transmit to the generator.
   */
  @Parameter (name = "params", property = "jcodemodel.params")
  private Map <String, String> m_aParams;

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
                    m_aParams);

    if (StringHelper.isNotEmpty (m_sClassHeader))
      cmb.setClassHeader (m_sClassHeader);

    if (StringHelper.isNotEmpty (m_sRootPackage))
      cmb.setRootPackage (m_sRootPackage);

    if (m_aParams != null)
      cmb.configure (m_aParams);

    final JCodeModel cm = new JCodeModel ();
    if (m_sData != null && !m_sData.isBlank () && m_sSource != null && !m_sSource.isBlank ())
    {
      getLog ().warn ("discarding source param " + m_sSource + " as data is already set");
    }
    try (final InputStream aIS = StringHelper.isEmpty (m_sData) ? findSource ()
                                                                : new NonBlockingByteArrayInputStream (m_sData.getBytes (StandardCharsets.UTF_8)))
    {
      cmb.build (cm, aIS);
      new JCMWriter (cm).setJavaFeature (findJavaFeature ()).build (dir, (IProgressTracker) null);
    }
    catch (JCodeModelException | IOException e)
    {
      throw new MojoFailureException (e);
    }
  }

  /**
   * @return the java files output folder
   */
  @NonNull
  protected File javaOutputFolder ()
  {
    if (m_sOutputDir == null || m_sOutputDir.isBlank ())
      return new File (m_aProject.getBasedir (), "src/generated/java");

    if (m_sOutputDir.startsWith ("/"))
      return new File (m_sOutputDir);

    return new File (m_aProject.getBasedir (), m_sOutputDir);
  }

  /*
   * deduce the generator's class and instantiate it
   */
  protected ICodeModelBuilder findBuilder () throws Exception
  {
    String sGeneratorClass = m_sGenerator;
    if (sGeneratorClass == null)
      sGeneratorClass = findGeneratorClass ();

    return StringHelper.isEmpty (sGeneratorClass) ? null : (ICodeModelBuilder) Class.forName (sGeneratorClass)
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
    if (m_sSource == null || m_sSource.isBlank ())
      return null;

    // dumb checking : is it a file ? a URL ?
    try
    {
      final File aTargetFile = m_sSource.startsWith ("/") ? new File (m_sSource)
                                                          : new File (m_aProject.getBasedir (), m_sSource);
      return new FileInputStream (aTargetFile);
    }
    catch (final Exception e)
    {
      getLog ().info ("while trying to open " + m_sSource + " as a file", e);
    }

    try
    {
      final URL aURL = new URL (m_sSource);
      return aURL.openStream ();
    }
    catch (final IOException e)
    {
      getLog ().info ("while trying to open " + m_sSource + " as a url", e);
    }

    throw new MojoExecutionException ("could not open provided source " + m_sSource + " as a file or url");
  }

  /**
   * @return the configured {@link #m_sJavaFeature} parsed as an integer, falling back to
   *         {@link JCMWriter#DEFAULT_JAVA_FEATURE} when unset or blank.
   */
  public int findJavaFeature ()
  {
    if (m_sJavaFeature == null || m_sJavaFeature.isBlank ())
      return JCMWriter.DEFAULT_JAVA_FEATURE;
    return Integer.parseInt (m_sJavaFeature);
  }

  // Setters used by Maven Plexus injection (must match XML element name)

  public void setOutputDir (@Nullable final String sOutputDir)
  {
    m_sOutputDir = sOutputDir;
  }

  public void setRootPackage (@Nullable final String sRootPackage)
  {
    m_sRootPackage = sRootPackage;
  }

  public void setSource (@Nullable final String sSource)
  {
    m_sSource = sSource;
  }

  public void setJavaFeature (@Nullable final String sJavaFeature)
  {
    m_sJavaFeature = sJavaFeature;
  }

  public void setData (@Nullable final String sData)
  {
    m_sData = sData;
  }

  public void setGenerator (@Nullable final String sGenerator)
  {
    m_sGenerator = sGenerator;
  }

  public void setClassHeader (@Nullable final String sClassHeader)
  {
    m_sClassHeader = sClassHeader;
  }

  public void setParams (@Nullable final Map <String, String> aParams)
  {
    m_aParams = aParams;
  }
}
