package com.helger.jcodemodel.expressions.typed.primitives;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.expressions.typed.TypedExpressionWrapper;

/// expression with a `double` type. Name is shorter to avoid name clash with java.lang . 
public class DblExpression extends TypedExpressionWrapper <Double>
{

  public DblExpression (IJExpression raw)
  {
    super (raw);
  }

  public static DblExpression of (double value)
  {
    return new DblExpression (JExpr.lit (value));
  }

  public static DblExpression of (IJExpression raw)
  {
    return new DblExpression (raw);
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
