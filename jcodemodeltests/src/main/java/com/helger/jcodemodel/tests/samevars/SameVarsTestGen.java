package com.helger.jcodemodel.tests.samevars;

import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.vars.JBlockVar;

@TestJCM
public class SameVarsTestGen {

  public void simpleClass(final JPackage root) throws JCodeModelException {
    final JDefinedClass cl = root._class("SameVarsSimpleClass");

    JFieldVar fld = cl.field(JMod.PUBLIC | JMod.STATIC, cl.owner().INT, "i", JExpr.lit(3));
    fld.andVar("j");
    fld.andVar("k", JExpr.lit(1));

    JFieldVar a1 = cl.field(JMod.PUBLIC | JMod.STATIC, cl.owner().CHAR.array(), "a1");
    a1.andVar("a2");
    a1.andVar("a3", 1, null);

    JMethod met = cl.method(JMod.PUBLIC | JMod.STATIC, cl.owner().VOID, "test");
    JBlockVar jvar = met.body().decl(cl.owner().INT, "i", JExpr.lit(11));
    jvar.andVar("j");
    jvar.andVar("k", JExpr.lit(5));

  }

}
