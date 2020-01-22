package com.helger.jcodemodel.preprocess;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JCodeModelException;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JDocComment;
import com.helger.jcodemodel.util.EFileSystemConvention;

public class AuthorPreprocessorTest {

  @Test
  public void testAddAuthor() throws JCodeModelException, IOException {
    JCodeModel cm = new JCodeModel().setFileSystemConvention(EFileSystemConvention.LINUX);
    JDefinedClass cl = cm._class("my.Test");
    cl.javadoc().addTag(JDocComment.TAG_AUTHOR).add("existingAuthor1");
    cl.javadoc().append("@author author1\n");
    cl.javadoc().append("@author existingAuthor2\n");
    AuthorProcessor test = cm.processor(AuthorProcessor.class);
    test.add("newAuthor1", "existingAuthor1", "newAuthor2", "existingAuthor2");
    Assert.assertTrue(test.apply(cm, true));
    Assert.assertFalse(test.apply(cm, true));
    // TODO more tests with memory compiler
  }

}
