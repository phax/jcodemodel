package com.helger.jcodemodel.tests.unnamed;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;

@TestJCM
public class UnnamedTestGen {

  public final String rootPackage = getClass().getPackageName();

  public void basic(JPackage rootPck, JCodeModel jcm) throws JCodeModelException {
    JDefinedClass cl = rootPck._class("SimpleUnnamed");
    JMethod eq = cl.method(JMod.PUBLIC, jcm.BOOLEAN, "equals");
    // TODO use unnamed variable name later (when avail in the project)
    eq.param(jcm.ref(Object.class), "_unnamed");
    eq.body()._return(JExpr.FALSE);
  }

}
