package com.helger.jcodemodel.expressions.typed.primitives;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.expressions.typed.TypedExpressionWrapper;

/// expression with a `int` type.
public class IntExpression extends TypedExpressionWrapper <Integer>
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
