package com.helger.jcodemodel.expressions.typed.java.lang;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.expressions.typed.ATypeExpressionWrapper;

/// expression with a `int` type.
public class IntExpression extends ATypeExpressionWrapper <Integer>
{

  public IntExpression (IJExpression raw)
  {
    super (raw);
  }

  public static IntExpression of (int value)
  {
    return new IntExpression (JExpr.lit (value));
  }

}
