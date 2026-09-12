package com.helger.jcodemodel.expressions.typed.primitives;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.expressions.ITypedExpression;

/// expression with a `float` type. Name is shorter to avoid name clash with java.lang . 
public class FltExpression extends ASubFloatExpression <Float, FltExpression, FltExpression>
{

  public FltExpression (IJExpression raw)
  {
    super (raw);
  }

  @Override
  protected FltExpression wrapSelf (IJExpression exp)
  {
    return new FltExpression (exp);
  }

  @Override
  protected FltExpression wrapPos (IJExpression exp)
  {
    return new FltExpression (exp);
  }

  public static FltExpression of (float value)
  {
    return new FltExpression (JExpr.lit (value));
  }

  public static FltExpression of (IJExpression raw)
  {
    return new FltExpression (raw);
  }

  public static FltExpression of (ITypedExpression <?> untyped)
  {
    return of (untyped.raw ());
  }

  public static FltExpression param (JMethod m, String name)
  {
    return m.paramTyped (name, float.class, FltExpression::of);
  }

}
