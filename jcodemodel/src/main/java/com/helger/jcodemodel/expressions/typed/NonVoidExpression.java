package com.helger.jcodemodel.expressions.typed;

import com.helger.jcodemodel.expressions.ITypedExpression;
import com.helger.jcodemodel.expressions.typed.primitives.BoolExpression;

/// common interface for typed expression that have something, so excluding void.
/// 
/// Provides `that == other` and `that != other` 
public interface NonVoidExpression <T> extends ITypedExpression <T>
{

  /// @return `that == other`
  public default BoolExpression eq (NonVoidExpression <?> other)
  {
    return new BoolExpression (raw ().eq (other.raw ()));
  }

  /// @return `that != other`
  public default BoolExpression ne (NonVoidExpression <?> other)
  {
    return new BoolExpression (raw ().ne (other.raw ()));
  }
}
