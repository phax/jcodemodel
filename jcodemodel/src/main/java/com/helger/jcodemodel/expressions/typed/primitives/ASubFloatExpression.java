package com.helger.jcodemodel.expressions.typed.primitives;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;

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
    return FltExpression.of (raw ().div (JExpr.lit (s)));
  }

  public ASubFloatExpression <?, ?, ?> div (byte b)
  {
    return FltExpression.of (raw ().div (JExpr.lit (b)));
  }

  public ASubFloatExpression <?, ?, ?> div (char c)
  {
    return FltExpression.of (raw ().div (JExpr.lit (c)));
  }

  /// @return `that % other`
  public FltExpression mod (ASubFloatExpression <?, ?, ?> other)
  {
    return new FltExpression (raw.mod (other.raw ()));
  }

  public ASubFloatExpression <?, ?, ?> mod (float f)
  {
    return FltExpression.of (raw ().mod (JExpr.lit (f)));
  }

  public ASubFloatExpression <?, ?, ?> mod (long l)
  {
    return FltExpression.of (raw ().mod (l));
  }

  public ASubFloatExpression <?, ?, ?> mod (int i)
  {
    return FltExpression.of (raw ().mod (i));
  }

  public ASubFloatExpression <?, ?, ?> mod (short s)
  {
    return FltExpression.of (raw ().mod (JExpr.lit (s)));
  }

  public ASubFloatExpression <?, ?, ?> mod (byte b)
  {
    return FltExpression.of (raw ().mod (JExpr.lit (b)));
  }

  public ASubFloatExpression <?, ?, ?> mod (char c)
  {
    return FltExpression.of (raw ().mod (JExpr.lit (c)));
  }

  /// @return `that * other`
  public FltExpression mult (ASubFloatExpression <?, ?, ?> other)
  {
    return new FltExpression (raw.mul (other.raw ()));
  }

  public ASubFloatExpression <?, ?, ?> mult (float f)
  {
    return FltExpression.of (raw ().mul (f));
  }

  public ASubFloatExpression <?, ?, ?> mult (long l)
  {
    return FltExpression.of (raw ().mul (l));
  }

  public ASubFloatExpression <?, ?, ?> mult (int i)
  {
    return FltExpression.of (raw ().mul (i));
  }

  public ASubFloatExpression <?, ?, ?> mult (short s)
  {
    return FltExpression.of (raw ().mul (JExpr.lit (s)));
  }

  public ASubFloatExpression <?, ?, ?> mult (byte b)
  {
    return FltExpression.of (raw ().mul (JExpr.lit (b)));
  }

  public ASubFloatExpression <?, ?, ?> mult (char c)
  {
    return FltExpression.of (raw ().mul (JExpr.lit (c)));
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
    return FltExpression.of (raw ().plus (JExpr.lit (s)));
  }

  public ASubFloatExpression <?, ?, ?> plus (byte b)
  {
    return FltExpression.of (raw ().plus (JExpr.lit (b)));
  }

  public ASubFloatExpression <?, ?, ?> plus (char c)
  {
    return FltExpression.of (raw ().plus (JExpr.lit (c)));
  }

  /// @return `that - other`
  public FltExpression sub (ASubFloatExpression <?, ?, ?> other)
  {
    return new FltExpression (raw.minus (other.raw ()));
  }

  public ASubFloatExpression <?, ?, ?> sub (float f)
  {
    return FltExpression.of (raw ().minus (f));
  }

  public ASubFloatExpression <?, ?, ?> sub (long l)
  {
    return FltExpression.of (raw ().minus (l));
  }

  public ASubFloatExpression <?, ?, ?> sub (int i)
  {
    return FltExpression.of (raw ().minus (i));
  }

  public ASubFloatExpression <?, ?, ?> sub (short s)
  {
    return FltExpression.of (raw ().minus (JExpr.lit (s)));
  }

  public ASubFloatExpression <?, ?, ?> sub (byte b)
  {
    return FltExpression.of (raw ().minus (JExpr.lit (b)));
  }

  public ASubFloatExpression <?, ?, ?> sub (char c)
  {
    return FltExpression.of (raw ().minus (JExpr.lit (c)));
  }

}
