package com.helger.jcodemodel.tests.switchexpression;

import com.helger.jcodemodel.*;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;

@TestJCM
public class SwitchExpressionTestGen {

  public void basicSwitch(JPackage root, JCodeModel jcm) throws JCodeModelException {
    JDefinedClass cl = root._class("BasicSwitch");
  JMethod m = cl.method(JMod.PUBLIC | JMod.STATIC, boolean.class, "isOdd");
  JVar i = m.param(jcm.INT, "i");
    JSwitchExpression sw = JExpr._switch(i);

  sw._case(JExpr.lit(0))._case(JExpr.lit(2)).yield(JExpr.FALSE);
  sw._case(JExpr.lit(1))._case(JExpr.lit(3)).yield(JExpr.TRUE);
  sw._case(JExpr.lit(4))
      ._case(JExpr.lit(5))
      ._case(JExpr.lit(6))
      ._case(JExpr.lit(7))
      ._case(JExpr.lit(8))
      ._case(JExpr.lit(9))
      .yield(JExpr.invoke(m).arg(JOp.minus(i, JExpr.lit(2))));
    sw._default()._throws(jcm, UnsupportedOperationException.class, JOp.plus(JExpr.lit("case not handled : "), i));

    // java 17
    // return switch(o){ … };
    m.body()._return(sw);
  }

  public void switchEnum(JPackage root, JCodeModel jcm) throws JCodeModelException {
    JDefinedClass cl = root._class("ESwitch");
  JMethod m1 = cl.method(JMod.PUBLIC | JMod.STATIC, jcm.INT, "daysIn");
  JVar o = m1.param(jcm.ref(EnumMonths.class), "em");
    JSwitchExpression sw = JExpr._switch(o);
    m1.body()._return(sw);
  sw._default()
  // requires j21
//    .andNull()
      ._throws(jcm, UnsupportedOperationException.class);
    sw._case(jcm.ref(EnumMonths.JAN))
        .or(jcm.ref(EnumMonths.MAR))
        .yield(JExpr.lit(31));
    sw._case(jcm.ref(EnumMonths.FEB)).yield(JExpr.lit(28));

    JDefinedClass ep = root._enum("EPeriod");
    JEnumConstant yearConstant = ep.enumConstant("YEAR");
  JMethod m2 = cl.method(JMod.PUBLIC | JMod.STATIC, jcm.INT, "daysIn");
  o = m2.param(ep, "ep");
  sw = JExpr._switch(o);
  m2.body()._return(sw);
    sw._case(yearConstant).yield(JExpr.lit(365));
    sw._case(ep.enumConstant("WEEK")).yield(JExpr.lit(7));
    sw._case(ep.enumConstant("MONTH"))._throws(jcm, UnsupportedOperationException.class,
        JExpr.lit("a month can have 28, 30 or 31 days."));
  sw._default()
      ._throws(jcm, UnsupportedOperationException.class);
  }

}
