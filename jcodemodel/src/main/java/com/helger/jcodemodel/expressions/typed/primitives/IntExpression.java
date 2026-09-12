package com.helger.jcodemodel.expressions.typed.primitives;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;

/// expression with a `int` type.
public class IntExpression extends ASubIntExpression <Integer, IntExpression, IntExpression>
{

  public IntExpression (IJExpression raw)
  {
    super (raw);
  }

  @Override
  protected IntExpression wrapSelf (IJExpression exp)
  {
    return new IntExpression (exp);
  }

  @Override
  protected IntExpression wrapPos (IJExpression exp)
  {
    return new IntExpression (exp);
  }

  public static IntExpression of (int value)
  {
    return new IntExpression (JExpr.lit (value));
  }

  public static IntExpression of (IJExpression raw)
  {
    return new IntExpression (raw);
  }

}
