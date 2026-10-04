package com.helger.jcodemodel.expressions.typed;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.expressions.ITypedExpression;

/// Convenience implementation for Typed expressions.
/// 
/// There are 4 sub implementations to represent
///  - References, so equals and co methods ; but also null check and instanceof
///  - numeral primitives, for numeral operations
///  - boolean primitive
///  - void, for example Collection::clear
public abstract class ATypedExpressionWrapper <RunTimeType> implements ITypedExpression <RunTimeType>
{

  protected final IJExpression raw;

  public ATypedExpressionWrapper (@NonNull IJExpression raw)
  {
    this.raw = raw;
  }

  @NonNull
  public IJExpression raw ()
  {
    return raw;
  }

  @Override
  public
  String toString ()
  {
    return getClass ().getSimpleName () + "[" + raw () + "]";
  }

}
