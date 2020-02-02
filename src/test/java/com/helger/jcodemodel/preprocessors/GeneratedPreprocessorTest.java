package com.helger.jcodemodel.preprocessors;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.helger.jcodemodel.JAnnotationUse;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JCodeModelException;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.preprocessors.GeneratedProcessor;
import com.helger.jcodemodel.util.EFileSystemConvention;

public class GeneratedPreprocessorTest {

  @Test
  public void testAddGenerated() throws JCodeModelException, IOException {
    JCodeModel cm = new JCodeModel().setFileSystemConvention(EFileSystemConvention.LINUX);
    JDefinedClass cl = cm._class("my.Test");
    GeneratedProcessor test = cm.processor(GeneratedProcessor.class);
    test.withGenerator(JCodeModel.class);
    Assert.assertTrue(test.apply(cm, true));
    Assert.assertFalse(test.apply(cm, true));
    JAnnotationUse annotation = cl.annotations().iterator().next();
    Assert.assertNull(annotation.getAnnotationMembers().get("comments"));
    Assert.assertNotNull(annotation.getAnnotationMembers().get("date"));
    // TODO more tests when memory compiler. Otherwise too many casts.
    // Assert.assertEquals(JCodeModel.class.getSimpleName(),
    // ((JAnnotationStringValue)annotation.getAnnotationMembers().get("value")).);
  }
}
