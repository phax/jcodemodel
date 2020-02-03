package com.helger.jcodemodel.preprocessors;

import java.io.File;
import java.io.IOException;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JCodeModelException;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JVar;
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

    JMethod generator = cl.method (JMod.PRIVATE, cm.INT, "makeNext");
    JVar valueParam = generator.param (cm.INT, "value");
    generator.body ()._return (valueParam.plus (1));
    cm.processor (CacheProcessor.class).register (generator, "next");

    JMethod generator2 = cl.method (JMod.PRIVATE, cm.INT, "make100");
    generator2.body ()._return (JExpr.lit (100));
    cm.processor (CacheProcessor.class).register (generator2, "hundred");

    File out = new File ("target/AllProcessorsTest/");
    out.mkdirs ();
    new JCMWriter (cm).build (out);
  }

}
