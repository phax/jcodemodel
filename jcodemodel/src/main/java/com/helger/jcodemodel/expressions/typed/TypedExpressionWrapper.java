package com.helger.jcodemodel.expressions.typed;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.expressions.ITypedExpression;


public class TypedExpressionWrapper <RunTimeType> implements ITypedExpression <RunTimeType>
{

  protected final IJExpression raw;

  public TypedExpressionWrapper (IJExpression raw)
  {
    this.raw = raw;
  }

  public IJExpression raw ()
  {
    return raw;
  }

}
