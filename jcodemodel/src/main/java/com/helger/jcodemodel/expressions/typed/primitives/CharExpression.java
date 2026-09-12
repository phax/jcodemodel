package com.helger.jcodemodel.expressions.typed.primitives;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.expressions.typed.java.lang.StringExpression;

/// expression with a `char` type.
public class CharExpression extends ASubIntExpression <Character, CharExpression, IntExpression>
{

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

  public static CharExpression of (char value)
  {
    return new CharExpression (JExpr.lit (value));
  }

  public static CharExpression of (IJExpression raw)
  {
    return new CharExpression (raw);
  }

  /// @return `that + other`
  public StringExpression plus (StringExpression other)
  {
    return new StringExpression (raw.plus (other.raw ()));
  }

}
