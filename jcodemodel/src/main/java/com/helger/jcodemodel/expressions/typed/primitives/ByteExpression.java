package com.helger.jcodemodel.expressions.typed.primitives;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.expressions.ITypedExpression;

/// expression with a `byte` type.
public class ByteExpression extends ASubShortExpression <Byte, ByteExpression, ByteExpression>
{

  public static class ByteArrExp extends ArrayExpression <Byte>
  {

    public ByteArrExp (IJExpression raw)
    {
      super (raw);
    }

    @Override
    public ByteExpression at (ASubIntExpression <?, ?, ?> index)
    {
      return ByteExpression.of (super.at (index));
    }

  }

  public static ByteExpression of (IJExpression raw)
  {
    return new ByteExpression (raw);
  }

  public static ByteExpression of (ITypedExpression <?> untyped)
  {
    return of (untyped.raw ());
  }

  public static ByteExpression param (JMethod m, String name)
  {
    return m.paramTyped (name, byte.class, ByteExpression::of);
  }

  public static ByteExpression of (byte value)
  {
    return new ByteExpression (JExpr.lit (value));
  }

  ///

  public ByteExpression (IJExpression raw)
  {
    super (raw);
  }

  @Override
  protected ByteExpression wrapSelf (IJExpression exp)
  {
    return new ByteExpression (exp);
  }

  @Override
  protected ByteExpression wrapPos (IJExpression exp)
  {
    return new ByteExpression (exp);
  }

}
