package com.helger.jcodemodel.preprocessors;

import java.io.File;
import java.io.IOException;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JCodeModelException;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.preprocessors.AuthorProcessor;
import com.helger.jcodemodel.preprocessors.BeanProcessor;
import com.helger.jcodemodel.preprocessors.GeneratedProcessor;
import com.helger.jcodemodel.util.EFileSystemConvention;
import com.helger.jcodemodel.writer.JCMWriter;

public class AllProcessorsTest
{

  public static void main (String[] args) throws JCodeModelException, IOException
  {
    JCodeModel cm = new JCodeModel ().setFileSystemConvention (EFileSystemConvention.LINUX);
    JDefinedClass cl = cm._class ("my.Test");
    JFieldVar field1 = cl.field (JMod.PRIVATE, cm.ref (String.class), "field1");
    JFieldVar field2 = cl.field (JMod.PRIVATE | JMod.FINAL, cm.ref (String.class), "field2");
    BeanProcessor bp = cm.processor (BeanProcessor.class);
    bp.add (field1, field2);
    AuthorProcessor ap = cm.processor (AuthorProcessor.class);
    ap.add ("glelouet", "none");
    GeneratedProcessor gp = cm.processor (GeneratedProcessor.class);
    gp.withGenerator (AllProcessorsTest.class).withComment ("for test purposes");
    File out = new File ("target/AllProcessorsTest/");
    out.mkdirs ();
    new JCMWriter (cm).build (out);
  }

}
