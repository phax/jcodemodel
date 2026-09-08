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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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

import com.helger.base.string.StringHelper;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.plugin.maven.ISourcedInputStream.DirectSourced;
import com.helger.jcodemodel.plugin.maven.ISourcedInputStream.FileSourced;
import com.helger.jcodemodel.plugin.maven.ISourcedInputStream.URLSourced;
import com.helger.jcodemodel.writer.JCMWriter;
import com.helger.jcodemodel.writer.ProgressCodeWriter.IProgressTracker;

/// This mojo does the following :
/// 
/// 1. deduce the output folder and ensure it is present. If [m_sOutputDir] is null or blank then default "src/generated/java" is used.
/// 2. deduce the generator to be used. If [m_sGenerator] is null or blank, then loads the resource [GENERATOR_CLASS_FILE] instead.
/// 3. instantiate the generator class and configure it, create a new JCM to modify.
/// 4. list the data to apply the generator to.
///    If provided, [m_sData] is passed.
///    If [m_sSource] is a file, it is opened ; if it is a directory, its children are opened, after being filtered by [sourcesFilter] if non null.
///    If [m_sSource] is a url, it is opened.
///    If no data and no source provided, then null is returned as data.
/// 5. apply the generator on each data separately, modifying the JCM.
/// 6. export the JCM
/// 
/// Note that the plugin does not actually generate code by itself : the generator used is responsible for modifying the JCM.
/// 
/// The simplest way to make it work as a plugin, is to add a single auto executing generator as a dependency.
/// This way the generator will be discovered and loaded automatically, you just need to configure its data/source, if any.  
/// 
@Mojo (name = "generate-source", threadSafe = true, defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateSourceMojo extends AbstractMojo
{

  /// generated/parsed generator description file
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

  /// if the source is a directory, and this param is not null/empty, then only files with a last
  /// name containing this (ignoring case) will be selected as generator sources
  @Parameter (name = "sourcesFilter", property = "jcodemodel.sourcesFilter", required = false)
  private String sourcesFilter;

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

    // always configure, so that the generator can setup its defaults
    cmb.configure (m_aParams == null ? Map.of () : m_aParams);

    final JCodeModel cm = new JCodeModel ();

    final List <ISourcedInputStream> sourcesList = buildSources (cmb, cm);
    try
    {
      new JCMWriter (cm).setJavaFeature (findJavaFeature ()).build (dir, (IProgressTracker) null);
    }
    catch (IOException e)
    {
      throw new MojoFailureException ("after applying sources " + sourcesList, e);
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

    return StringHelper.isEmpty (sGeneratorClass) ? null
                                                  : (ICodeModelBuilder) Class.forName (sGeneratorClass)
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
        final String className = new String (is.readAllBytes (), StandardCharsets.UTF_8).trim ();
        getLog ().debug ("using generator class " + className);
        return className;
      }
      getLog ().error ("can't load resource " + GENERATOR_CLASS_FILE);
      return null;
    }
  }

  /// Apply the generator to the data and to the source, one after the other.
  ///
  /// - if data is set, it is applied first ; then the source is processed
  /// - a directory source is recursively browsed over its files
  /// - a single file source is applied alone
  /// - a url source is opened and applied
  /// - if neither data nor source is set, the generator is applied to [ISourcedInputStream#NULL],
  ///   so that a generator which does not need data still works, while a generator that requires
  ///   data fails rather than being silently skipped.
  ///
  /// The sources are opened one after the other, so that only one stream is open at a time and each
  /// of them is closed, even if the generation fails.
  ///
  /// @param cmb generator to apply
  /// @param cm model to build into
  /// @return the sources that were applied, in the order they were applied.
  /// @throws MojoExecutionException if the source can be opened neither as a file nor as an url.
  /// @throws MojoFailureException if the generation of a source failed.
  @NonNull
  protected List <ISourcedInputStream> buildSources (@NonNull final ICodeModelBuilder cmb,
                                                     @NonNull final JCodeModel cm) throws MojoExecutionException,
                                                                                   MojoFailureException
  {
    final List <ISourcedInputStream> ret = new ArrayList <> ();
    if (StringHelper.isEmpty (m_sData) && StringHelper.isEmpty (m_sSource))
    {
      // no data and no source at all
      ret.add (buildSource (cmb, cm, ISourcedInputStream.NULL));
      return ret;
    }

    if (StringHelper.isNotEmpty (m_sData))
      ret.add (buildSource (cmb, cm, new DirectSourced (m_sData)));

    if (StringHelper.isNotEmpty (m_sSource))
    {
      final File aTargetFile = m_sSource.startsWith ("/") ? new File (m_sSource)
                                                          : new File (m_aProject.getBasedir (), m_sSource);
      if (aTargetFile.exists ())
      {
        final List <File> aSourceFiles = findSourceFiles (aTargetFile);
        if (aSourceFiles.isEmpty ())
          getLog ().warn ("no source file found in " + aTargetFile.getAbsolutePath ());

        for (final File aSourceFile : aSourceFiles)
        {
          getLog ().debug ("applying source file " + aSourceFile.getAbsolutePath ());
          final FileSourced aSourced;
          try
          {
            aSourced = new FileSourced (aSourceFile);
          }
          catch (final FileNotFoundException e)
          {
            // should never happen since the file was just listed
            throw new MojoFailureException ("could not open source file " + aSourceFile.getAbsolutePath (), e);
          }
          ret.add (buildSource (cmb, cm, aSourced));
        }
      }
      else
      {
        // not an existing file nor directory : the last chance is a url
        ret.add (buildSource (cmb, cm, openURLSource ()));
      }
    }
    if (ret.isEmpty ())
      ret.add (buildSource (cmb, cm, ISourcedInputStream.NULL));
    return ret;
  }

  /// Apply the generator to a single source, and close its stream afterwards.
  ///
  /// @param cmb generator to apply
  /// @param cm model to build into
  /// @param sourced the source to apply
  /// @return the applied source
  /// @throws MojoFailureException if the generation failed
  @NonNull
  protected ISourcedInputStream buildSource (@NonNull final ICodeModelBuilder cmb,
                                             @NonNull final JCodeModel cm,
                                             @NonNull final ISourcedInputStream sourced) throws MojoFailureException
  {
    // specification accepts null closable, in that case it's not closed.
    try (InputStream is = sourced.inputStream ())
    {
      cmb.build (cm, sourced);
    }
    catch (JCodeModelException | IOException e)
    {
      throw new MojoFailureException ("while applying source " + sourced, e);
    }
    return sourced;
  }

  /// Open the source as a url.
  ///
  /// @return the opened url source
  /// @throws MojoExecutionException if the source is not a valid url, or can't be opened.
  @NonNull
  protected URLSourced openURLSource () throws MojoExecutionException
  {
    try
    {
      final URL aURL = new URI (m_sSource).toURL ();
      return new URLSourced (m_sSource, aURL.openStream ());
    }
    catch (final URISyntaxException | IllegalArgumentException | IOException e)
    {
      getLog ().error ("source " + m_sSource + " is neither an existing file nor a directory");
      getLog ().error ("while trying to open " + m_sSource + " as a url", e);
      throw new MojoExecutionException ("could not open provided source " + m_sSource + " as a file or url");
    }
  }

  /// List the files to be used as generator sources.
  ///
  /// @param aRootFile file or directory to browse
  /// @return the file itself if it is a normal file ; the matching files it contains, recursively,
  ///         if it is a directory. Never null.
  @NonNull
  protected List <File> findSourceFiles (@NonNull final File aRootFile)
  {
    final List <File> ret = new ArrayList <> ();
    collectSourceFilesRecursive (aRootFile, new HashSet <> (), ret);
    return ret;
  }

  /**
   * recursively collect the files inside the file.
   *
   * @param aRootFile
   *        file to browse
   * @param aVisitedDirs
   *        canonical paths of the directories already visited, to avoid an endless recursion on
   *        symbolic link loops
   * @param aTarget
   *        list the found files are added to
   */
  protected void collectSourceFilesRecursive (@NonNull final File aRootFile,
                                              @NonNull final Set <String> aVisitedDirs,
                                              @NonNull final List <File> aTarget)
  {
    if (aRootFile.isFile ())
    {
      aTarget.add (aRootFile);
      return;
    }

    if (!aRootFile.isDirectory ())
      return;

    // isDirectory() follows the symbolic links, so a link loop would recurse forever
    String sCanonicalPath;
    try
    {
      sCanonicalPath = aRootFile.getCanonicalPath ();
    }
    catch (final IOException e)
    {
      sCanonicalPath = aRootFile.getAbsolutePath ();
    }
    if (!aVisitedDirs.add (sCanonicalPath))
    {
      getLog ().warn ("skipping the already visited directory " + aRootFile.getAbsolutePath ());
      return;
    }

    final File [] aChildren = aRootFile.listFiles ();
    if (aChildren == null)
    {
      getLog ().warn ("can't list the content of the directory " + aRootFile.getAbsolutePath ());
      return;
    }

    Arrays.stream (aChildren)
          // the filter applies to the files, the sub directories are always browsed
          .filter (f -> f.isDirectory () || matchesSourcesFilter (f))
          .sorted (Comparator.comparing (File::getPath))
          .forEach (f -> collectSourceFilesRecursive (f, aVisitedDirs, aTarget));
  }

  /**
   * @param aFile
   *        file to check
   * @return <code>true</code> if the file name matches the {@link #sourcesFilter}, or if no filter
   *         is set.
   */
  protected boolean matchesSourcesFilter (@NonNull final File aFile)
  {
    if (sourcesFilter == null || sourcesFilter.isBlank ())
      return true;

    return aFile.getName ().toLowerCase (Locale.ROOT).contains (sourcesFilter.toLowerCase (Locale.ROOT));
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

  public void setSourcesFilter (@Nullable final String sSourcesFilter)
  {
    sourcesFilter = sSourcesFilter;
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
