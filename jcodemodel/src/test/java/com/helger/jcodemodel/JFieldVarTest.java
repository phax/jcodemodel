package com.helger.jcodemodel;

import org.junit.Assert;
import org.junit.Test;

import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.modifiers.EMod;

public class JFieldVarTest {

  @Test
  public void testEMods() throws JCodeModelException {
    JCodeModel jcm = new JCodeModel();
    JDefinedClass jdc = jcm._class("TestClass");
    JVar jfv = jdc.field(JMod.PUBLIC, jcm.ref(String.class), "testVar");
    Assert.assertTrue(jfv.isEMod(EMod.PUBLIC));

    // adding forbidden
    jfv.addEMod(EMod.ABSTRACT, EMod.SEALED);
    Assert.assertTrue(jfv.isEMod(EMod.PUBLIC));
    Assert.assertEquals(1, jfv.emods().size());

    // add exclusions
    jfv.addEMod(EMod.PROTECTED, EMod.FINAL, EMod.STATIC, EMod.PRIVATE);
    Assert.assertTrue(jfv.isEMod(EMod.FINAL));
    Assert.assertTrue(jfv.isEMod(EMod.STATIC));
    Assert.assertTrue(jfv.isEMod(EMod.PRIVATE));
    Assert.assertEquals("mods are : " + jfv.emods(), 3, jfv.emods().size());

    // remove static twice
    jfv.removeEMod(EMod.STATIC);
    Assert.assertFalse(jfv.isEMod(EMod.STATIC));
    jfv.removeEMod(EMod.STATIC);
    Assert.assertFalse(jfv.isEMod(EMod.STATIC));
  }

}
