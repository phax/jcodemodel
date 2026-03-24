package com.helger.jcodemodel.tests.basic;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;

@TestJCM
public class SimpleClassGenerating {

  public JCodeModel createSimple1() throws JCodeModelException {
    JCodeModel cm = new JCodeModel();
    cm._class("com.helger.jcodemodel.tests.basic.Simple1");
    return cm;
  }

  public JCodeModel createSimple2() throws JCodeModelException {
    JCodeModel cm = new JCodeModel();
    cm._class("com.helger.jcodemodel.tests.basic.Simple2");
    return cm;
  }

  /** protected so should not be selected */
  protected JCodeModel protectedCall() {
    return null;
  }

}
