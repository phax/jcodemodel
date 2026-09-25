package com.helger.jcodemodel.expressions.typed;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.expressions.ITypedExpression;


public class TypedExpressionWrapper <RunTimeType> implements ITypedExpression <RunTimeType>
{

  protected final IJExpression raw;

  public TypedExpressionWrapper (@NonNull IJExpression raw)
  {
    this.raw = raw;
  }

  @NonNull
  public IJExpression raw ()
  {
    return raw;
  }

}
