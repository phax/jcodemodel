package com.helger.jcodemodel.tests.typeinference;

import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;

@TestJCM
public class JVarVarTestGen {

  public void VarTest(final JPackage root) throws JCodeModelException {
    var cl = root._class("VarTest");
    var jm = cl.method(JMod.PUBLIC, root.owner().VOID, "test");
    jm.body().decl(root.owner().INT, "baseInt").init(JExpr.lit(42));
    jm.body().decl(null, "testVar").init(JExpr.lit(42));
  }

}
