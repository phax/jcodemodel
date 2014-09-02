package com.helger.jcodemodel.tests;

import org.junit.Assert;
import org.junit.Test;

import com.helger.jcodemodel.JAnnotationUse;
import com.helger.jcodemodel.JClassAlreadyExistsException;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.tests.util.CodeModelTestsUtils;

/**
 * Unit test for class {@link JAnnotationUse}.
 *
 * @author Philip Helger
 */
public class JAnnotationUseTest
{
  @Test
  public void generatesGenericParam () throws JClassAlreadyExistsException
  {
    final JCodeModel codeModel = new JCodeModel ();
    final JDefinedClass testClass = codeModel._class ("Test");
    final JAnnotationUse suppressWarningAnnotation = testClass.annotate (SuppressWarnings.class);
    suppressWarningAnnotation.param (JAnnotationUse.SPECIAL_KEY_VALUE, "unused");

    Assert.assertEquals ("@java.lang.SuppressWarnings(\"unused\")",
                         CodeModelTestsUtils.generate (suppressWarningAnnotation));

  }

  @Test
  public void generatesGenericParam2 () throws JClassAlreadyExistsException
  {
    final JCodeModel codeModel = new JCodeModel ();
    final JDefinedClass testClass = codeModel._class ("Test");
    final JAnnotationUse suppressWarningAnnotation = testClass.annotate (SuppressWarnings.class);
    suppressWarningAnnotation.paramArray (JAnnotationUse.SPECIAL_KEY_VALUE, "unused", "deprecation");

    final String sCRLF = System.getProperty ("line.separator");
    Assert.assertEquals ("@java.lang.SuppressWarnings({" +
                         sCRLF +
                         "    \"unused\"," +
                         sCRLF +
                         "    \"deprecation\"" +
                         sCRLF +
                         "})", CodeModelTestsUtils.generate (suppressWarningAnnotation));

  }
}
