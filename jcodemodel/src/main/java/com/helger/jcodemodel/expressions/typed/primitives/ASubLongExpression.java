package com.helger.jcodemodel.expressions.typed.primitives;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJExpression;

/// mother class of expressions resolving to an long-promoted type (long and sub int)
public abstract class ASubLongExpression <T, Self extends ASubLongExpression <T, Self, ?>, PosType extends ASubLongExpression <?, ?, ?>>
                                         extends
                                         ASubFloatExpression <T, Self, PosType>
{

  protected ASubLongExpression (@NonNull IJExpression raw)
  {
    super (raw);
  }

  //
  // binary
  //

  /// @return `that + other`
  public LngExpression div (ASubLongExpression <?, ?, ?> other)
  {
    return new LngExpression (raw.div (other.raw ()));
  }

  /// @return `that + other`
  public LngExpression plus (ASubLongExpression <?, ?, ?> other)
  {
    return new LngExpression (raw.plus (other.raw ()));
  }

  /// @return `that * other`
  public LngExpression mult (ASubLongExpression <?, ?, ?> other)
  {
    return new LngExpression (raw.mul (other.raw ()));
  }

  /// @return `that - other`
  public LngExpression sub (ASubLongExpression <?, ?, ?> other)
  {
    return new LngExpression (raw.minus (other.raw ()));
  }

}
