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
package com.helger.jcodemodel.compile.annotation;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.processing.Generated;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.JReferencedClass;
import com.helger.jcodemodel.writer.FormatterSettings;
import com.helger.jcodemodel.writer.JCMWriter;
import com.helger.jcodemodel.writer.ProgressCodeWriter.IProgressTracker;

public class GenerateTestFiles
{
  private static final String OUTPUT_DIR = "src/generated/javatest";
  private static final String CLASS_SCAN_DIR = "src/main/java";
  private static final String LICENCE = """
      Licensed under the Apache License, Version 2.0 (the "License");
      you may not use this file except in compliance with the License.
      You may obtain a copy of the License at

              http://www.apache.org/licenses/LICENSE-2.0

      Unless required by applicable law or agreed to in writing, software
      distributed under the License is distributed on an "AS IS" BASIS,
      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
      See the License for the specific language governing permissions and
      limitations under the License.
      """;

  private final File m_aOutputDir;
  private final File m_aClassScanDir;
  private final int m_nJavaFeature;

  public static void main (final String [] args)
  {
    final String rootPath = args == null || args.length == 0 ? "." : args[0];
    final File outputFile = new File (rootPath, OUTPUT_DIR);
    final File classScanDir = new File (rootPath, CLASS_SCAN_DIR);
    new GenerateTestFiles (outputFile, classScanDir).apply ();
  }

  /**
   * Resolve the Java feature to be used by parsing the system property <code>java.feature</code>.
   * Falls back to {@link JCMWriter#DEFAULT_JAVA_FEATURE} when the property is unset or cannot be
   * parsed as an integer.
   */
  public static int extractJavaFeature ()
  {
    final String sValue = System.getProperty ("java.feature");
    if (sValue == null || sValue.isBlank ()) {
      return JCMWriter.DEFAULT_JAVA_FEATURE;
    }
    try
    {
      // accept full version strings like "17.0.5" by taking the major component
      final int nDot = sValue.indexOf ('.');
      return Integer.parseInt (nDot < 0 ? sValue : sValue.substring (0, nDot));
    }
    catch (final NumberFormatException ex)
    {
      return JCMWriter.DEFAULT_JAVA_FEATURE;
    }
  }

  public GenerateTestFiles (final File outputDir, final File classScanDir)
  {
    m_aOutputDir = outputDir;
    m_aClassScanDir = classScanDir;
    m_nJavaFeature = extractJavaFeature ();
  }

  void apply ()
  {
    delete (m_aOutputDir);
    m_aOutputDir.mkdirs ();
    scanClasses (m_aClassScanDir).forEach (this::applyCandidateClass);
  }

  void delete (final File file)
  {
    if (file.isDirectory ())
    {
      for (final File sub : file.listFiles ())
      {
        delete (sub);
      }
    }
    file.delete ();
  }

  Stream <String> scanClasses (final File rootDir)
  {
    return scanClasses (rootDir, "", Stream.of ());
  }

  Stream <String> scanClasses (final File dir, final String packageName, final Stream <String> stream)
  {
    Stream <String> ret = stream;
    final List <String> newFound = new ArrayList <> ();
    if (!dir.isDirectory ())
    {
      throw new RuntimeException ("file " + dir.getAbsolutePath () + " expected to be a dir");
    }
    for (final File child : dir.listFiles ())
    {
      if (child.isDirectory ())
      {
        ret = scanClasses (child, (packageName.isEmpty () ? "" : packageName + ".") + child.getName (), ret);

      }
      else
        if (child.isFile () && child.getName ().endsWith (".java"))
        {
          newFound.add (packageName + "." + child.getName ().replace (".java", ""));
        }
    }
    if (!newFound.isEmpty ())
    {
      ret = Stream.concat (ret, newFound.stream ());
    }
    return ret;
  }

  void applyCandidateClass (final String className)
  {
    Class <?> clazz;
    try
    {
      clazz = Class.forName (className);
      if (clazz.getAnnotation (TestJCM.class) != null)
      {
        runGeneration (clazz);
      }
    }
    catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException |
           InstantiationException | NoSuchMethodException | SecurityException | IOException e)
    {
      throw new RuntimeException (e);
    }
  }

  private void runGeneration (final Class <?> clazz) throws IllegalAccessException,
                                                     IllegalArgumentException,
                                                     InvocationTargetException,
                                                     InstantiationException,
                                                     NoSuchMethodException,
                                                     SecurityException,
                                                     IOException
  {
    for (final Method m : clazz.getDeclaredMethods ())
    {
      // only apply to methods public
      // and that produce a JCodeModel or require one or a package
      if ((m.getModifiers () & Modifier.PUBLIC) > 0)
      {
        final boolean returnsJCM = m.getReturnType ().equals (JCodeModel.class);
        boolean requiresJCM = false;
        final Object [] params = new Object [m.getParameterCount ()];
        boolean missingParam = false;
        JCodeModel produced = null;
        JPackage rootPackage = null;
        FormatterSettings settings = null;
        for (int i = 0; i < params.length; i++)
        {
          final Parameter param = m.getParameters ()[i];
          if (param.getType () == JCodeModel.class)
          {
            if (produced == null)
            {
              produced = new JCodeModel ();
            }
            params[i] = produced;
            requiresJCM = true;
          }
          else
            if (param.getType () == JPackage.class)
            {
              // request a root package that is the package of the class
              if (produced == null)
              {
                produced = new JCodeModel ();
              }
              if (rootPackage == null)
              {
                rootPackage = produced._package (clazz.getPackageName ());
              }
              params[i] = rootPackage;
              requiresJCM = true;

            } else if (param.getType() == FormatterSettings.class) {
              if (settings == null) {
                settings = new FormatterSettings();
              }
              params[i] = settings;
            }
            else
            {
              missingParam = true;
            }
        }
        if (!missingParam && (returnsJCM || requiresJCM))
        {
          m.setAccessible (true);
          if ((m.getModifiers () & Modifier.STATIC) > 0)
          {
            if (returnsJCM)
            {
              produced = (JCodeModel) m.invoke (null, params);
            }
            else
            {
              m.invoke (null, params);
            }
          }
          else
          {
            final Object inst = clazz.getDeclaredConstructor ().newInstance ();
            if (returnsJCM)
            {
              produced = (JCodeModel) m.invoke (inst, params);
            }
            else
            {
              m.invoke (inst, params);
            }
          }
          if (produced != null)
          {
            postProcessJCM (produced);
            new JCMWriter(produced)
                .withSettings(settings)
                .setJavaFeature(m_nJavaFeature)
                .build(m_aOutputDir, (IProgressTracker) null);
          }
        }
      }
    }
  }

  protected void postProcessJCM (final JCodeModel jcm)
  {
    // add the licence to classes not having a header comment yet.
    jcm.getAllPackages ()
       .stream ()
       .flatMap (jp -> jp.classes ().stream ())
       .filter (jdc -> !jdc.isHidden ())
       .filter (jdc -> !jdc.hasHeaderComment ())
       .forEach (jdc -> {
         jdc.headerComment ().add (LICENCE);
       });

    // add @Generated(JCodeModel full name) to files' root classes not having that annotation yet.
    jcm.getAllPackages ()
       .stream ()
       .flatMap (jp -> jp.classes ().stream ())
       .filter (jdc -> !jdc.isHidden ())
       .filter (jdc -> jdc.annotations ()
                          .stream ()
                          .filter (ja -> ja.getAnnotationClass () instanceof JReferencedClass)
                          .map (ja -> (JReferencedClass) ja.getAnnotationClass ())
                          .filter (jrc -> jrc.getReferencedClass ().equals (Generated.class))
                          .findAny ()
                          .isEmpty ())
       .forEach (jdc -> {
         jdc.annotate (Generated.class).param (JCodeModel.class.getCanonicalName ());
       });
  }

}
