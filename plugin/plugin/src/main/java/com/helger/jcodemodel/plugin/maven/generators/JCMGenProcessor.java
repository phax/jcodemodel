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
package com.helger.jcodemodel.plugin.maven.generators;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import com.helger.jcodemodel.plugin.maven.GenerateSourceMojo;
import com.helger.jcodemodel.plugin.maven.ICodeModelBuilder;

/**
 * generate a generator descriptor for a single {@link ICodeModelBuilder} implementation marked with
 * {@link JCMGen}
 */
@SupportedAnnotationTypes ("com.helger.jcodemodel.plugin.maven.generators.JCMGen")
@SupportedSourceVersion (SourceVersion.RELEASE_17)
public class JCMGenProcessor extends AbstractProcessor
{
  @Override
  public boolean process (final Set <? extends TypeElement> annotations, final RoundEnvironment roundEnv)
  {
    if (annotations == null || annotations.isEmpty ())
    {
      return false;
    }
    for (final TypeElement te : annotations)
    {
      final Set <? extends Element> elements = roundEnv.getElementsAnnotatedWith (te);
      if (elements.size () > 1)
        throw new IllegalStateException ("can't process more than one annotation of type " +
                                         te +
                                         " , received " +
                                         elements);

      for (final Element e : elements)
      {
        final TypeMirror annotatedClass = e.asType ();
        final TypeMirror jcmgen = processingEnv.getElementUtils ()
                                               .getTypeElement (ICodeModelBuilder.class.getCanonicalName ())
                                               .asType ();
        final boolean isJcmgen = processingEnv.getTypeUtils ().isAssignable (annotatedClass, jcmgen);
        if (!isJcmgen)
        {
          throw new IllegalStateException ("annotation " +
                                           te +
                                           " must be applied to subclass of " +
                                           jcmgen +
                                           ", not the case for " +
                                           annotatedClass);
        }
        try
        {
          final FileObject fo = processingEnv.getFiler ()
                                             .createResource (StandardLocation.CLASS_OUTPUT,
                                                              "",
                                                              GenerateSourceMojo.GENERATOR_CLASS_FILE);
          try (Writer w = fo.openWriter ())
          {
            w.write (annotatedClass.toString ());
          }
        }
        catch (final IOException e1)
        {
          throw new RuntimeException (e1);
        }
      }
    }
    return true;
  }

}
