package com.helger.jcodemodel.expressions.typed.primitives;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;

/// expression with a `double` type. Name is shorter to avoid name clash with java.lang . 
public class DblExpression extends ANumericExpression <Double, DblExpression, DblExpression>
{

  public DblExpression (IJExpression raw)
  {
    super (raw);
  }

  @Override
  protected DblExpression wrapSelf (IJExpression exp)
  {
    return new DblExpression (exp);
  }

  @Override
  protected DblExpression wrapPos (IJExpression exp)
  {
    return new DblExpression (exp);
  }

  public static DblExpression of (double value)
  {
    return new DblExpression (JExpr.lit (value));
  }

  public static DblExpression of (IJExpression raw)
  {
    return new DblExpression (raw);
  }

}
