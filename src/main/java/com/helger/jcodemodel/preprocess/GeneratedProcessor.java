package com.helger.jcodemodel.preprocess;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.processing.Generated;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JAnnotationUse;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JPackage;

/**
 * add a generated annotation on the first-level classes that are not annotated
 * with {@link Generated}. The {@link #withGenerator(Class) generator class}
 * must be set, or the processor will skip the codemodel.
 *
 * @see https://docs.oracle.com/javase/10/docs/api/javax/annotation/Generated.html
 * @author glelouet
 *
 */
public class GeneratedProcessor extends AJCodePreprocessor {

  @Override
  public boolean apply(JCodeModel jcm, boolean firstPass) {
    if (generator == null) {
      return false;
    }
    String annotationValue = generator.getName();
    String annotationDate = null;
    if (addDate) {
      annotationDate=DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now());
    }
    AbstractJClass generatedRef = jcm.ref(Generated.class);
    boolean modification = false;
    for (JPackage pck : jcm.getAllPackages()) {
      for (JDefinedClass cl : pck.classes()) {
        if (applyClass(cl, annotationValue, annotationDate, comments, generatedRef)) {
          modification = true;
        }
      }
    }
    return modification;
  }

  /**
   * process a class
   *
   * @param cl
   *          the generated class to process
   * @param annotationValue
   *          the generator used, required.
   * @param annotationDate
   *          current date in the ISO-8601 format, optional.
   * @param annotationComments
   *          the comment to add, optional.
   * @return true if the application did change the code model.
   */
  protected boolean applyClass(JDefinedClass cl, String annotationValue, String annotationDate,
      String annotationComments, AbstractJClass generatedRef) {
    for (JAnnotationUse ann : cl.annotations()) {
      AbstractJClass annClass = ann.getAnnotationClass();
      if (annClass.equals(generatedRef)) {
        return false;
      }
    }
    JAnnotationUse annotation = cl.annotate(generatedRef);
    if (annotationDate == null && annotationComments == null) {
      annotation.param(annotationValue);
    } else {
      annotation.param("value", annotationValue);
      if (annotationDate != null) {
        annotation.param("date", annotationDate);
      }
      if (annotationComments != null) {
        annotation.param("comments", annotationComments);
      }
    }
    return true;
  }

  private String comments = null;

  /**
   * set the comment of the generated annotation.
   *
   * @param comment
   *          the comment to be added, can be nul.
   * @return this.
   */
  public GeneratedProcessor withComment(String comment) {
    comments = comment;
    return this;
  }

  private Class<?> generator = null;

  /**
   * set the object that generated the code.
   *
   * @param generator
   *          the class that generated the code. If none set, or null, the
   *          generator is disabled.
   * @return this.
   */
  public GeneratedProcessor withGenerator(Class<?> generator) {
    this.generator = generator;
    return this;
  }

  private boolean addDate = true;

  /**
   * set to add the date in the annotation(default is true)
   *
   * @param addDate
   *          the new value
   * @return this
   */
  public GeneratedProcessor withAddDate(boolean addDate) {
    this.addDate = addDate;
    return this;
  }

}
