package com.helger.jcodemodel.preprocess;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JCodeModelException;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.util.EFileSystemConvention;
import com.helger.jcodemodel.writer.JCMWriter;

public class BeanProcessorGeneratorTest
{

  @Test
  public void testAddBean () throws JCodeModelException, IOException
  {
    JCodeModel cm = new JCodeModel ().setFileSystemConvention (EFileSystemConvention.LINUX);
    JDefinedClass cl = cm._class ("my.Test");
    JFieldVar field1 = cl.field (JMod.PRIVATE, cm.ref (String.class), "field1");
    JFieldVar field2 = cl.field (JMod.PRIVATE | JMod.FINAL, cm.ref (String.class), "field2");
    BeanProcessor test = cm.processor (BeanProcessor.class);
    test.add (field1, field2);
    Assert.assertTrue (test.apply (cm, true));
    Assert.assertFalse (test.apply (cm, false));
    JMethod field1get = null;
    JMethod field1set = null;
    JMethod field2get = null;
    JMethod field2set = null;
    for (JMethod meth : cl.methods ())
    {
      if (meth.name ().equals ("getField1"))
        field1get = meth;
      if (meth.name ().equals ("setField1"))
        field1set = meth;
      if (meth.name ().equals ("getField2"))
        field2get = meth;
      if (meth.name ().equals ("setField2"))
        field2set = meth;
    }
    Assert.assertNotNull (field1get);
    Assert.assertNotNull (field1set);
    Assert.assertNotNull (field2get);
    Assert.assertNull (field2set);

    Assert.assertEquals (field1.type (), field1get.type ());
    Assert.assertEquals (field1.type (), field1set.params ().get (0).type ());
    Assert.assertEquals (field2.type (), field2get.type ());
  }

  public static void main (String[] args) throws JCodeModelException, IOException
  {
    JCodeModel cm = new JCodeModel ().setFileSystemConvention (EFileSystemConvention.LINUX);
    JDefinedClass cl = cm._class ("my.Test");
    JFieldVar field1 = cl.field (JMod.PRIVATE, cm.ref (String.class), "field1");
    JFieldVar field2 = cl.field (JMod.PRIVATE | JMod.FINAL, cm.ref (String.class), "field2");
    BeanProcessor test = cm.processor (BeanProcessor.class);
    test.add (field1, field2);
    File out = new File ("target/BeanProcessorGeneratorTest/");
    out.mkdirs ();
    new JCMWriter (cm).build (out);
  }

}
