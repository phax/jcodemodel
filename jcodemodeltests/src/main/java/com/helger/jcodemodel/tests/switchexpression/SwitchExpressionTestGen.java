package com.helger.jcodemodel.tests.switchexpression;

import com.helger.jcodemodel.*;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;

@TestJCM
public class SwitchExpressionTestGen {

  public void basicSwitch(JPackage root, JCodeModel jcm) throws JCodeModelException {
    JDefinedClass cl = root._class("BasicSwitch");
    JMethod m = cl.method(JMod.PUBLIC, Number.class, "plus1");
    JVar o = m.varParam(jcm.ref(Object.class), "o");
    JSwitchExpression sw = JExpr._switch(o);

    // java 26 ?
    // case 0, -0-> -1 ;
    sw._case(JExpr.lit(0))
        ._case(JExpr.lit(-0))
        .yield(JExpr.lit(-1));

    // java 21
    // case Integer i ->i+1
    // we name variable but could be i. This is the "case" variable
    sw._case(jcm, Integer.class, "i")
        .yieldOn(variable -> JOp.plus(variable, JExpr.lit(1)));

    // java 21
    // case Character c when c >= '0' && c <= '9' -> c - '0' + 1;
    sw._case(jcm, Character.class, "c")
        .when(c -> JOp.gte(c, JExpr.lit('0')))
        .when(c -> JOp.lte(c, JExpr.lit('9')))
        .yieldOn(c -> JOp.plus(JOp.minus(c, JExpr.lit('0')), JExpr.lit(1)));

    // java 26 ?
    // case char c -> 1 + c;
    sw._case(jcm, Character.class, "c")
        .yieldOn(c -> JOp.plus(c, JExpr.lit(1)));

    // java 21
    // case null -> {nullCount++;yield nullCount;}
    JFieldVar nullCountVar = cl.field(JMod.PRIVATE, int.class, "nullCount");
    /// null case is special because it can be merged with default, `case default,
    /// null ->`
    sw._null()
        .add(JExpr.assignPlus(nullCountVar, JExpr.lit(1)))
        .yield(nullCountVar);

    // java 17
    // default- > throw new UnsupportedOperationException();
    sw._default()._throws(jcm, UnsupportedOperationException.class, JOp.plus(JExpr.lit("case not handled : "), o));

    // java 17
    // return switch(o){ … };
    m.body()._return(sw);
  }


  // the code we want
  private int nullCount;
  public Number plus1(Object o) {
    return switch (o) {
//		case 0, -0 -> -1;
    case Integer i -> i + 1;
    case Character c when c >= '0' && c <= '9' -> c - '0' + 1;
    case Character c -> 1 + c;
    case null -> {
      nullCount++;
      yield nullCount;
    }
    default -> throw new UnsupportedOperationException("case not handled : " + o);
    };
  }

}
