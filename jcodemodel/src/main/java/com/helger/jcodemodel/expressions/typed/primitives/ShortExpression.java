package com.helger.jcodemodel.expressions.typed.primitives;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.expressions.ITypedExpression;

/// expression with a `short` type.
public class ShortExpression extends ASubIntExpression <Short, ShortExpression, ShortExpression>
{

  public static class ShortArrExp extends ArrayExpression <Short>
  {

    public ShortArrExp (IJExpression raw)
    {
      super (raw);
    }

    @Override
    public ShortExpression at (ASubIntExpression <?, ?, ?> index)
    {
      return ShortExpression.of (super.at (index));
    }

  }

  public static ShortExpression of (IJExpression raw)
  {
    return new ShortExpression (raw);
  }

  public static ShortExpression of (ITypedExpression <?> untyped)
  {
    return of (untyped.raw ());
  }

  public static ShortExpression param (JMethod m, String name)
  {
    return m.paramTyped (name, short.class, ShortExpression::of);
  }

  public static ShortExpression of (int value)
  {
    return new ShortExpression (JExpr.lit (value));
  }

  ///

  public ShortExpression (IJExpression raw)
  {
    super (raw);
  }

  @Override
  protected ShortExpression wrapSelf (IJExpression exp)
  {
    return new ShortExpression (exp);
  }

  @Override
  protected ShortExpression wrapPos (IJExpression exp)
  {
    return new ShortExpression (exp);
  }

}
