package com.helger.jcodemodel.expressions.typed.primitives;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.expressions.ITypedExpression;

/// expression with a `char` type.
public class CharExpression extends ASubIntExpression <Character, CharExpression, IntExpression>
{
  public static class Array extends ArrayExpression <Character>
  {

    public Array (IJExpression raw)
    {
      super (raw);
    }

    @Override
    public CharExpression at (ASubIntExpression <?, ?, ?> index)
    {
      return CharExpression.of (super.at (index));
    }

  }

  public static CharExpression of (char value)
  {
    return new CharExpression (JExpr.lit (value));
  }

  public static CharExpression of (IJExpression raw)
  {
    return new CharExpression (raw);
  }

  public static CharExpression of (ITypedExpression <?> untyped)
  {
    return of (untyped.raw ());
  }

  public static CharExpression param (JMethod m, String name)
  {
    return m.paramTyped (name, char.class, CharExpression::of);
  }

  //

  public CharExpression (IJExpression raw)
  {
    super (raw);
  }

  @Override
  protected CharExpression wrapSelf (IJExpression exp)
  {
    return new CharExpression (exp);
  }

  @Override
  protected IntExpression wrapPos (IJExpression exp)
  {
    return new IntExpression (exp);
  }

}
