package com.helger.jcodemodel.tests.basic;

import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;

@TestJCM
public class SimpleClassGenerating {

  public void createSimple1(JPackage root) throws JCodeModelException {
    root._class("Simple1");
  }

  public void createSimple2(JPackage root) throws JCodeModelException {
    root._class("Simple2");
  }

  /**
   * protected so should not be selected
   *
   * @throws JCodeModelException
   */
  protected void protectedCall(JPackage root) throws JCodeModelException {
    root._class("ERROR");
  }

}
