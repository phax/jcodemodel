package com.helger.jcodemodel.expressions.typed.primitives;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.expressions.typed.TypedExpressionWrapper;

/// expression with a `char` type.
public class CharExpression extends TypedExpressionWrapper <Character>
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

}
