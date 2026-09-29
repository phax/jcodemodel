package com.helger.jcodemodel.expressions.typed.primitives;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.expressions.ITypedExpression;

/// expression with a `byte` type.
public class BytExpression extends ASubShortExpression <Byte, BytExpression, BytExpression>
{

  public static class ByteArrExp extends ArrayExpression <Byte>
  {

    public ByteArrExp (IJExpression raw)
    {
      super (raw);
    }

    @Override
    public BytExpression at (ASubIntExpression <?, ?, ?> index)
    {
      return BytExpression.of (super.at (index));
    }

  }

  public static BytExpression of (IJExpression raw)
  {
    return new BytExpression (raw);
  }

  public static BytExpression of (ITypedExpression <?> untyped)
  {
    return of (untyped.raw ());
  }

  public static BytExpression param (JMethod m, String name)
  {
    return m.paramTyped (name, byte.class, BytExpression::of);
  }

  public static BytExpression of (byte value)
  {
    return new BytExpression (JExpr.lit (value));
  }

  ///

  public BytExpression (IJExpression raw)
  {
    super (raw);
  }

  @Override
  protected BytExpression wrapSelf (IJExpression exp)
  {
    return new BytExpression (exp);
  }

  @Override
  protected BytExpression wrapPos (IJExpression exp)
  {
    return new BytExpression (exp);
  }

}
