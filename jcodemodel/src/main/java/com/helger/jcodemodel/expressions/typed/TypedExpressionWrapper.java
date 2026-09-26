package com.helger.jcodemodel.expressions.typed;

import java.util.ArrayList;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.expressions.ITypedExpression;

/// Basic implementation for a non-necessarily object expression.
/// 
/// There are 4 sub implementations to represent
///  - Object, so equals and co methods
///  - numeral primitives, for numeral operations
///  - boolean primitive
///  - void, for example Collection::clear
public class TypedExpressionWrapper <RunTimeType> implements ITypedExpression <RunTimeType>
{

  protected final IJExpression raw;

  public TypedExpressionWrapper (@NonNull IJExpression raw)
  {
    new ArrayList <> ().clear ();
    this.raw = raw;
  }

  @NonNull
  public IJExpression raw ()
  {
    return raw;
  }

}
