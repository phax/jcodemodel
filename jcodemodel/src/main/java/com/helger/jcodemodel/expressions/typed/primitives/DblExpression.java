package com.helger.jcodemodel.expressions.typed.primitives;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.expressions.ITypedExpression;

/// expression with a `double` type. Name is shorter to avoid name clash with java.lang . 
public class DblExpression extends ANumericExpression <Double, DblExpression, DblExpression>
{

  public static class DoubleArrExp extends ArrayExpression <Double>
  {

    public DoubleArrExp (IJExpression raw)
    {
      super (raw);
    }

    @Override
    public DblExpression at (ASubIntExpression <?, ?, ?> index)
    {
      return DblExpression.of (super.at (index));
    }

  }

  public static DblExpression of (double value)
  {
    return new DblExpression (JExpr.lit (value));
  }

  public static DblExpression of (IJExpression raw)
  {
    return new DblExpression (raw);
  }

  public static DblExpression of (ITypedExpression <?> untyped)
  {
    return of (untyped.raw ());
  }

  public static DblExpression param (JMethod m, String name)
  {
    return m.paramTyped (name, double.class, DblExpression::of);
  }

  //

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

}
