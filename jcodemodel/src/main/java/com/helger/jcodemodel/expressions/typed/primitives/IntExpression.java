package com.helger.jcodemodel.expressions.typed.primitives;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.expressions.ITypedExpression;

/// expression with a `int` type.
public class IntExpression extends ASubIntExpression <Integer, IntExpression, IntExpression>
{

  public static class Array extends ArrayExpression <Integer>
  {

    public Array (IJExpression raw)
    {
      super (raw);
    }

    @Override
    public IntExpression at (ASubIntExpression <?, ?, ?> index)
    {
      return IntExpression.of (super.at (index));
    }

  }

  public static IntExpression of (IJExpression raw)
  {
    return new IntExpression (raw);
  }

  public static IntExpression of (ITypedExpression <?> untyped)
  {
    return of (untyped.raw ());
  }

  public static IntExpression param (JMethod m, String name)
  {
    return m.paramTyped (name, int.class, IntExpression::of);
  }

  public static IntExpression of (int value)
  {
    return new IntExpression (JExpr.lit (value));
  }

  ///

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

}
