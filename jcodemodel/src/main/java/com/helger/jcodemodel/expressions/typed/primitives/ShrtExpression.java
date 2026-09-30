package com.helger.jcodemodel.expressions.typed.primitives;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.expressions.ITypedExpression;

/// expression with a `short` type.
public class ShrtExpression extends ASubShortExpression <Short, ShrtExpression, ShrtExpression>
{

  public static class ShortArrExp extends ArrayExpression <Short>
  {

    public ShortArrExp (IJExpression raw)
    {
      super (raw);
    }

    @Override
    public ShrtExpression at (ASubIntExpression <?, ?, ?> index)
    {
      return ShrtExpression.of (super.at (index));
    }

  }

  public static ShrtExpression of (IJExpression raw)
  {
    return new ShrtExpression (raw);
  }

  public static ShrtExpression of (ITypedExpression <?> untyped)
  {
    return of (untyped.raw ());
  }

  public static ShrtExpression param (JMethod m, String name)
  {
    return m.paramTyped (name, short.class, ShrtExpression::of);
  }

  public static ShrtExpression of (short value)
  {
    return new ShrtExpression (JExpr.lit (value));
  }

  ///

  public ShrtExpression (IJExpression raw)
  {
    super (raw);
  }

  @Override
  protected ShrtExpression wrapSelf (IJExpression exp)
  {
    return new ShrtExpression (exp);
  }

  @Override
  protected ShrtExpression wrapPos (IJExpression exp)
  {
    return new ShrtExpression (exp);
  }

}
