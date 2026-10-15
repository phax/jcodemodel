package com.helger.jcodemodel.tests.issues.issue201;

import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.JSwitchExpression;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;

/// https://github.com/phax/jcodemodel/issues/201
@TestJCM
public class Issue201TestGen
{
  public void generate (JPackage root) throws JCodeModelException
  {
    var cl = root._class ("Issue201");
    var jm = cl.method (JMod.PUBLIC | JMod.STATIC, root.owner ().VOID, "test");
    var param = jm.param (root.owner ().INT, "i");

    JSwitchExpression sw = JExpr._switch (param);
    jm.body ().add (sw);
    sw._case (JExpr.lit (1)).getBlock ().assign (param, JExpr.lit (42));
    sw._case (JExpr.lit (2)).getBlock ().assign (param, JExpr.lit (40)).assign (param, JExpr.lit (40));
    sw._default ().add (JExpr.assign (param, JExpr.lit (-1)));

    sw = JExpr._switch (param);
    jm.body ().assign (param, sw);
    sw._case (JExpr.lit (1)).yield (JExpr.lit (42));
    sw._default ().yield (param);
  }

}
