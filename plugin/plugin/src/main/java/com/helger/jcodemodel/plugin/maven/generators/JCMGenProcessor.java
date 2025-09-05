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
 * generate a generator descriptor for a single {@link CodeModelBuilder}
 * implementation marked with {@link JCMGen}
 */
@SupportedAnnotationTypes("com.helger.jcodemodel.plugin.maven.generators.JCMGen")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class JCMGenProcessor extends AbstractProcessor {


  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (annotations == null || annotations.isEmpty()) {
      return false;
    }
    for (TypeElement te : annotations) {
      Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(te);
      if (elements.size() > 1) {
        throw new RuntimeException(
            "can't process more than one annotation of type " + te + " , received " + elements);
      }
      for (Element e : elements) {
        TypeMirror annotatedClass = e.asType();
        TypeMirror jcmgen = processingEnv.getElementUtils()
            .getTypeElement(CodeModelBuilder.class.getCanonicalName()).asType();
        boolean isJcmgen = processingEnv.getTypeUtils().isAssignable(annotatedClass, jcmgen);
        if (!isJcmgen) {
          throw new RuntimeException(
              "annotation " + te + " must be applied to subclass of " + jcmgen + ", not the case for "
                  + annotatedClass);
        }
        try {
          FileObject fo = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "",
              GenerateSourceMojo.GENERATOR_CLASS_FILE);
          Writer w = fo.openWriter();
          w.write(annotatedClass.toString());
          w.close();
        } catch (IOException e1) {
          throw new RuntimeException(e1);
        }
      }
    }
    return true;
  }

}
