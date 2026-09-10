package com.helger.jcodemodel.expressions.typed.primitives;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.expressions.typed.TypedExpressionWrapper;

/// expression with a `float` type. Name is shorter to avoid name clash with java.lang . 
public class FltExpression extends TypedExpressionWrapper <Float>
{

  public FltExpression (IJExpression raw)
  {
    super (raw);
  }

  public static FltExpression of (float value)
  {
    return new FltExpression (JExpr.lit (value));
  }

  public static FltExpression of (IJExpression raw)
  {
    return new FltExpression (raw);
  }

  //
  // unary
  //

  //
  // binary
  //

  //
  // ternary
  //

}
