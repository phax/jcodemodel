/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2025 Philip Helger + contributors
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
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

  @Parameter(property = "jcodemodel.data")
  private String data;

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
    getLog().info("Generator " + cmb.getClass().getCanonicalName() + " generates model into "
        + dir.getAbsolutePath() + " with params " + params);
    if (params != null) {
      cmb.configure(params);
    }
    JCodeModel cm = new JCodeModel();
    if (data != null && !data.isBlank() && source != null && !source.isBlank()) {
      getLog().warn("discarding source param " + source + " as dat is already set");
    }
    InputStream source = data == null || data.isBlank() ? findSource() : new ByteArrayInputStream(data.getBytes());
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
    if (outputDir == null || outputDir.isBlank()) {
      return new File(project.getBasedir(), "src/generated/java");
    } else if (outputDir.startsWith("/")) {
      return new File(outputDir);
    } else {
      return new File(project.getBasedir(), outputDir);
    }
  }

  public static final String GENERATOR_CLASS_FILE = "jcodemodel/plugin/generator";

  /**
   * deduce the generator's class and instantiate it
   */
  protected CodeModelBuilder findBuilder()
      throws Exception {
    String generatorClass = generator;
    if (generatorClass == null) {
      generatorClass = findGeneratorClass();
    }
    return generatorClass == null || generatorClass.isBlank() ? null
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

  protected InputStream findSource() throws MojoExecutionException {
    if (source == null || source.isBlank()) {
      return null;
    }
    // dumb checking : is it a file ? a URL ?
    try {
      File targetFile = source.startsWith("/") ? new File(source) : new File(project.getBasedir(), source);
      FileInputStream fis = new FileInputStream(targetFile);
      return fis;
    } catch (Exception e) {
      getLog().info("while trying to open " + source + " as a file", e);
    }
    try {
      URL url = new URL(source);
      return url.openStream();
    } catch (IOException e) {
      getLog().info("while trying to open " + source + " as a url", e);
    }
    throw new MojoExecutionException("could not open provided source " + source + " as a file or url");
  }

}
