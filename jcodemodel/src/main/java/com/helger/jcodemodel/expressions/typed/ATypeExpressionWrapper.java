package com.helger.jcodemodel.expressions.typed;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.expressions.TypedExpression;


public abstract class ATypeExpressionWrapper <RunTimeType> implements TypedExpression <RunTimeType>
{

  public final IJExpression raw;

  public ATypeExpressionWrapper (IJExpression raw)
  {
    this.raw = raw;
  }

  public IJExpression raw ()
  {
    return raw;
  }

}
