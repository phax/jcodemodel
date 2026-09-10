package com.helger.jcodemodel.expressions.typed.primitives;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;

/// expression with a `int` type.
public class IntExpression extends ASubIntExpression <Integer>
{

  public IntExpression (IJExpression raw)
  {
    super (raw);
  }

  public static IntExpression of (int value)
  {
    return new IntExpression (JExpr.lit (value));
  }

  public static IntExpression of (IJExpression raw)
  {
    return new IntExpression (raw);
  }

  //
  // unary
  //

  //
  // binary
  //

  //
  // ternary
  //

}
