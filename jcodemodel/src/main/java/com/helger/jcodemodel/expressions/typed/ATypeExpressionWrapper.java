package com.helger.jcodemodel.expressions.typed;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.expressions.ITypedExpression;


public abstract class ATypeExpressionWrapper <RunTimeType> implements ITypedExpression <RunTimeType>
{

  protected final IJExpression raw;

  public ATypeExpressionWrapper (IJExpression raw)
  {
    this.raw = raw;
  }

  public IJExpression raw ()
  {
    return raw;
  }

}
