package com.helger.jcodemodel.expressions.typed.primitives;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJExpression;

/// mother class of expressions resolving to an float-promoted type (float and sub long)
public abstract class ASubFloatExpression <T, Self extends ASubFloatExpression <T, Self, ?>, PosType extends ASubFloatExpression <?, ?, ?>>
                                          extends
                                          ANumericExpression <T, Self, PosType>
{

  protected ASubFloatExpression (@NonNull IJExpression raw)
  {
    super (raw);
  }

  //
  // promoting operators
  //

  /// @return `that + other`
  public FltExpression div (ASubFloatExpression <?, ?, ?> other)
  {
    return new FltExpression (raw.div (other.raw ()));
  }

  /// @return `that + other`
  public FltExpression plus (ASubFloatExpression <?, ?, ?> other)
  {
    return new FltExpression (raw.plus (other.raw ()));
  }

  /// @return `that * other`
  public FltExpression mult (ASubFloatExpression <?, ?, ?> other)
  {
    return new FltExpression (raw.mul (other.raw ()));
  }

  /// @return `that - other`
  public FltExpression sub (ASubFloatExpression <?, ?, ?> other)
  {
    return new FltExpression (raw.minus (other.raw ()));
  }

}
