package com.helger.jcodemodel.expressions.typed.primitives;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;

/// expression with a `long` type. Name is shorter to avoid name clash with java.lang . 
public class LngExpression extends ASubLongExpression <Long>
{

  public LngExpression (IJExpression raw)
  {
    super (raw);
  }

  public static LngExpression of (long value)
  {
    return new LngExpression (JExpr.lit (value));
  }

  public static LngExpression of (IJExpression raw)
  {
    return new LngExpression (raw);
  }

}
