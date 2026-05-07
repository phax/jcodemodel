package com.helger.jcodemodel.tests.basic;

import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;

@TestJCM
public class SimpleClassGenerating {

  public void createSimple1(JPackage root) throws JCodeModelException {
    root._class("Simple1");
  }


  public void createSimple2(JPackage root, JPackage root2) throws JCodeModelException {
    assert root == root2;
    root._class("Simple2");
  }

  /**
   * protected so should not be selected
   */
  protected void protectedCall(JPackage root) throws JCodeModelException {
    root._class("ERROR");
  }

  /**
   * requires a param too many so should not be selected
   */
  public void invalidParamCall(JPackage root, Object o) throws JCodeModelException {
    root._class("ERROR2");
  }

  public static void staticSimple(JPackage root) throws JCodeModelException {
    root._class("Simple3");
  }

}
