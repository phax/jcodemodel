package com.helger.jcodemodel.expressions.typed.primitives;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJExpression;

/// mother class of expressions resolving to an float-promoted type (float and sub long)
public abstract class ASubFloatExpression <T, Self extends ASubFloatExpression <T, Self, ?>, PosType extends ASubFloatExpression <?, ?, ?>>
                                          extends
                                          ASubDoubleExpression <T, Self, PosType>
{

  protected ASubFloatExpression (@NonNull IJExpression raw)
  {
    super (raw);
  }

  //
  // promoting operators
  //

  /// @return `that / other`
  public FltExpression div (ASubFloatExpression <?, ?, ?> other)
  {
    return new FltExpression (raw.div (other.raw ()));
  }

  public ASubFloatExpression <?, ?, ?> div (float f)
  {
    return FltExpression.of (raw ().div (f));
  }

  public ASubFloatExpression <?, ?, ?> div (long l)
  {
    return FltExpression.of (raw ().div (l));
  }

  public ASubFloatExpression <?, ?, ?> div (int i)
  {
    return FltExpression.of (raw ().div (i));
  }

  public ASubFloatExpression <?, ?, ?> div (short s)
  {
    return FltExpression.of (raw ().div (s));
  }

  public ASubFloatExpression <?, ?, ?> div (byte b)
  {
    return FltExpression.of (raw ().div (b));
  }

  public ASubFloatExpression <?, ?, ?> div (char c)
  {
    return FltExpression.of (raw ().div (c));
  }

  /// @return `that % other`
  public FltExpression mod (ASubFloatExpression <?, ?, ?> other)
  {
    return new FltExpression (raw.mod (other.raw ()));
  }

  /// @return `that * other`
  public FltExpression mult (ASubFloatExpression <?, ?, ?> other)
  {
    return new FltExpression (raw.mul (other.raw ()));
  }

  /// @return `that + other`
  public FltExpression plus (ASubFloatExpression <?, ?, ?> other)
  {
    return new FltExpression (raw.plus (other.raw ()));
  }

  public ASubFloatExpression <?, ?, ?> plus (float f)
  {
    return FltExpression.of (raw ().plus (f));
  }

  public ASubFloatExpression <?, ?, ?> plus (long l)
  {
    return FltExpression.of (raw ().plus (l));
  }

  public ASubFloatExpression <?, ?, ?> plus (int i)
  {
    return FltExpression.of (raw ().plus (i));
  }

  public ASubFloatExpression <?, ?, ?> plus (short s)
  {
    return FltExpression.of (raw ().plus (s));
  }

  public ASubFloatExpression <?, ?, ?> plus (byte b)
  {
    return FltExpression.of (raw ().plus (b));
  }

  public ASubFloatExpression <?, ?, ?> plus (char c)
  {
    return FltExpression.of (raw ().plus (c));
  }

  /// @return `that - other`
  public FltExpression sub (ASubFloatExpression <?, ?, ?> other)
  {
    return new FltExpression (raw.minus (other.raw ()));
  }

}
