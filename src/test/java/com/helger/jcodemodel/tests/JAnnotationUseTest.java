package com.helger.jcodemodel.tests;

import org.junit.Assert;
import org.junit.Test;

import com.helger.jcodemodel.JAnnotationUse;
import com.helger.jcodemodel.JClassAlreadyExistsException;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.tests.util.CodeModelTestsUtils;

public class JAnnotationUseTest
{

  @Test
  public void generatesGenericParam () throws JClassAlreadyExistsException
  {

    final JCodeModel codeModel = new JCodeModel ();
    final JDefinedClass testClass = codeModel._class ("Test");
    final JAnnotationUse suppressWarningAnnotation = testClass.annotate (SuppressWarnings.class);
    suppressWarningAnnotation.param ("value", JExpr.lit ("unused"));

    Assert.assertEquals ("@java.lang.SuppressWarnings(\"unused\")",
                         CodeModelTestsUtils.generate (suppressWarningAnnotation));

  }

}
