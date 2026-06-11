package com.helger.jcodemodel;

import org.junit.Assert;
import org.junit.Test;

import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.modifiers.EMod;

public class JVarTest {

  @Test
  public void testEMods() throws JCodeModelException {
    JCodeModel jcm = new JCodeModel();
    JDefinedClass jdc = jcm._class("TestClass");
    JMethod jm = jdc.method(JMod.PUBLIC, jcm.VOID, "testmethod");
    JVar jv = jm.body().decl(jcm.ref(String.class), "testVar");
    // can only be set final. Default should be none at all
    Assert.assertTrue(jv.emods().isEmpty());

    // adding forbidden
    jv.emod(EMod.PUBLIC, EMod.STATIC);
    Assert.assertTrue(jv.emods().isEmpty());

    jv.emod(EMod.PUBLIC, EMod.FINAL, EMod.STATIC);
    Assert.assertTrue(jv.isEMod(EMod.FINAL));
    Assert.assertEquals(1, jv.emods().size());
  }

}
