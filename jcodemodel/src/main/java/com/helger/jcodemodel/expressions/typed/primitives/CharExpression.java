package com.helger.jcodemodel.expressions.typed.primitives;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.expressions.typed.java.lang.StringExpression;

/// expression with a `char` type.
public class CharExpression extends ASubIntExpression <Character>
{

  public CharExpression (IJExpression raw)
  {
    super (raw);
  }

  public static CharExpression of (char value)
  {
    return new CharExpression (JExpr.lit (value));
  }

  public static CharExpression of (IJExpression raw)
  {
    return new CharExpression (raw);
  }

  //
  // unary
  //

  //
  // binary
  //

  /// @return `that + other`
  public StringExpression plus (StringExpression other)
  {
    return new StringExpression (raw.plus (other.raw ()));
  }

  //
  // ternary
  //

}
