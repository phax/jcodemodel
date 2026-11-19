package com.helger.jcodemodel.tests.expressions.typed;

import com.helger.jcodemodel.*;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.expressions.typed.java.lang.StringExpression;
import com.helger.jcodemodel.expressions.typed.primitives.IntExpression;

/// generate a static charAt(String, int) with negative index and module to avoid arrayoutofbound.
@TestJCM
public class StringCharAtTestGen
{

  // create a charAt(String, int) using the TypedExpressions
  public void charAtTypedExpr (JPackage jp) throws JCodeModelException
  {
    JDefinedClass cl = jp._class ("StringCharAtTypedExpression");
    JMethod meth = cl.method (JMod.PUBLIC_STATIC_FINAL, jp.owner ().CHAR, "charAt");
    StringExpression s = StringExpression.param (meth, "s");
    IntExpression i = IntExpression.param (meth, "i");
    JBlock body = meth.body ();
    body._if (s.isNull ().or (s.isEmpty ()))._then ()._return (JExpr.lit ((char) 0));
    JConditional ifneg = body._if (i.lt (0));
    ifneg._then ().assign ((IJAssignmentTarget) i.raw (), s.length ().plus (i.mod (s.length ())).mod (s.length ()));
    ifneg._else ().assign ((IJAssignmentTarget) i.raw (), i.mod (s.length ()));
    body._return (s.charAt (i));
  }

  // create a charAt(String, int) on a string using the IJExpr
  public void charAtIJExpr (JPackage jp, JCodeModel jcm) throws JCodeModelException
  {
    JDefinedClass cl = jp._class ("StringCharAtIJExpression");
    JMethod meth = cl.method (JMod.PUBLIC_STATIC_FINAL, jp.owner ().CHAR, "charAt");
    JVar s = meth.param (jcm.ref (String.class), "s");
    JVar i = meth.param (jcm.INT, "i");
    JBlock body = meth.body ();
    body._if (s.eqNull ().cor (s.invoke ("isEmpty")))._then ()._return (JExpr.lit ((char) 0));
    JConditional ifneg = body._if (i.lt (JExpr.lit (0)));
    ifneg._then ().assign (i, s.invoke ("length").plus (i.mod (s.invoke ("length"))).mod (s.invoke ("length")));
    ifneg._else ().assign (i, i.mod (s.invoke ("length")));
    body._return (s.invoke ("charAt").arg (i));
  }
}
