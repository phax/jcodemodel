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

import com.helger.jcodemodel.plugin.maven.CodeModelBuilder;
import com.helger.jcodemodel.plugin.maven.GenerateSourceMojo;

/**
 * generate a generator descriptor for a single {@link CodeModelBuilder} implementation marked with
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
                                               .getTypeElement (CodeModelBuilder.class.getCanonicalName ())
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
