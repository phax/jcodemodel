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

  /// @return `that / other`
  public LngExpression div (ASubLongExpression <?, ?, ?> other)
  {
    return new LngExpression (raw.div (other.raw ()));
  }

  public ASubLongExpression <?, ?, ?> div (long l)
  {
    return LngExpression.of (raw ().div (l));
  }

  public ASubLongExpression <?, ?, ?> div (int i)
  {
    return LngExpression.of (raw ().div (i));
  }

  public ASubLongExpression <?, ?, ?> div (short s)
  {
    return LngExpression.of (raw ().div (s));
  }

  public ASubLongExpression <?, ?, ?> div (byte b)
  {
    return LngExpression.of (raw ().div (b));
  }

  public ASubLongExpression <?, ?, ?> div (char c)
  {
    return LngExpression.of (raw ().div (c));
  }

  /// @return `that % other`
  public LngExpression mod (ASubLongExpression <?, ?, ?> other)
  {
    return new LngExpression (raw.mod (other.raw ()));
  }

  public ASubLongExpression <?, ?, ?> mod (long l)
  {
    return LngExpression.of (raw ().mod (l));
  }

  public ASubLongExpression <?, ?, ?> mod (int i)
  {
    return LngExpression.of (raw ().mod (i));
  }

  public ASubLongExpression <?, ?, ?> mod (short s)
  {
    return LngExpression.of (raw ().mod (s));
  }

  public ASubLongExpression <?, ?, ?> mod (byte b)
  {
    return LngExpression.of (raw ().mod (b));
  }

  public ASubLongExpression <?, ?, ?> mod (char c)
  {
    return LngExpression.of (raw ().mod (c));
  }

  /// @return `that * other`
  public LngExpression mult (ASubLongExpression <?, ?, ?> other)
  {
    return new LngExpression (raw.mul (other.raw ()));
  }

  /// @return `that + other`
  public LngExpression plus (ASubLongExpression <?, ?, ?> other)
  {
    return new LngExpression (raw.plus (other.raw ()));
  }

  public ASubLongExpression <?, ?, ?> plus (long l)
  {
    return LngExpression.of (raw ().plus (l));
  }

  public ASubLongExpression <?, ?, ?> plus (int i)
  {
    return LngExpression.of (raw ().plus (i));
  }

  public ASubLongExpression <?, ?, ?> plus (short s)
  {
    return LngExpression.of (raw ().plus (s));
  }

  public ASubLongExpression <?, ?, ?> plus (byte b)
  {
    return LngExpression.of (raw ().plus (b));
  }

  public ASubLongExpression <?, ?, ?> plus (char c)
  {
    return LngExpression.of (raw ().plus (c));
  }

  /// @return `that - other`
  public LngExpression sub (ASubLongExpression <?, ?, ?> other)
  {
    return new LngExpression (raw.minus (other.raw ()));
  }

}
