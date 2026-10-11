package com.helger.jcodemodel.expressions.typed.primitives;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;

/// mother class of expressions resolving to an int-promoted type (int, char, byte, short)
/// 
/// It can be used typically in arguments to say a function requires an expression matching one of those.
public abstract class ASubIntExpression <T, Self extends ASubIntExpression <T, Self, ?>, PosType extends ASubIntExpression <?, ?, ?>>
                                        extends
                                        ASubLongExpression <T, Self, PosType>
{

  protected ASubIntExpression (@NonNull IJExpression raw)
  {
    super (raw);
  }

  //
  // promoting operators
  //

  /// @return `that / other`
  public IntExpression div (ASubIntExpression <?, ?, ?> other)
  {
    return new IntExpression (raw.div (other.raw ()));
  }

  public IntExpression div (int i)
  {
    return IntExpression.of (raw ().div (i));
  }

  public IntExpression div (short s)
  {
    return IntExpression.of (raw ().div (JExpr.lit (s)));
  }

  public IntExpression div (byte b)
  {
    return IntExpression.of (raw ().div (JExpr.lit (b)));
  }

  public IntExpression div (char c)
  {
    return IntExpression.of (raw ().div (JExpr.lit (c)));
  }

  /// @return `that % other`
  public IntExpression mod (ASubIntExpression <?, ?, ?> other)
  {
    return new IntExpression (raw.mod (other.raw ()));
  }

  public IntExpression mod (int i)
  {
    return IntExpression.of (raw ().mod (i));
  }

  public IntExpression mod (short s)
  {
    return IntExpression.of (raw ().mod (JExpr.lit (s)));
  }

  public IntExpression mod (byte b)
  {
    return IntExpression.of (raw ().mod (JExpr.lit (b)));
  }

  public IntExpression mod (char c)
  {
    return IntExpression.of (raw ().mod (JExpr.lit (c)));
  }

  /// @return `that * other`
  public IntExpression mult (ASubIntExpression <?, ?, ?> other)
  {
    return new IntExpression (raw.mul (other.raw ()));
  }

  public IntExpression mult (int i)
  {
    return IntExpression.of (raw ().mul (i));
  }

  public IntExpression mult (short s)
  {
    return IntExpression.of (raw ().mul (JExpr.lit (s)));
  }

  public IntExpression mult (byte b)
  {
    return IntExpression.of (raw ().mul (JExpr.lit (b)));
  }

  public IntExpression mult (char c)
  {
    return IntExpression.of (raw ().mul (JExpr.lit (c)));
  }

  /// @return `that + other`
  public IntExpression plus (ASubIntExpression <?, ?, ?> other)
  {
    return new IntExpression (raw.plus (other.raw ()));
  }

  public IntExpression plus (int i)
  {
    return IntExpression.of (raw ().plus (i));
  }

  public IntExpression plus (short s)
  {
    return IntExpression.of (raw ().plus (JExpr.lit (s)));
  }

  public IntExpression plus (byte b)
  {
    return IntExpression.of (raw ().plus (JExpr.lit (b)));
  }

  public IntExpression plus (char c)
  {
    return IntExpression.of (raw ().plus (JExpr.lit (c)));
  }

  /// @return `that - other`
  public IntExpression sub (ASubIntExpression <?, ?, ?> other)
  {
    return new IntExpression (raw.minus (other.raw ()));
  }

  public IntExpression sub (int i)
  {
    return IntExpression.of (raw ().minus (i));
  }

  public IntExpression sub (short s)
  {
    return IntExpression.of (raw ().minus (JExpr.lit (s)));
  }

  public IntExpression sub (byte b)
  {
    return IntExpression.of (raw ().minus (JExpr.lit (b)));
  }

  public IntExpression sub (char c)
  {
    return IntExpression.of (raw ().minus (JExpr.lit (c)));
  }

  //
  // bitwise operators
  // https://docs.oracle.com/javase/tutorial/java/nutsandbolts/op3.html
  //

  /// @return `that & other`
  public IntExpression band (ASubIntExpression <?, ?, ?> other)
  {
    return new IntExpression (raw.band (other.raw ()));
  }

  /// @return `~that`
  public IntExpression bnot ()
  {
    return new IntExpression (raw.complement ());
  }

  /// @return `that | other`
  public IntExpression bor (ASubIntExpression <?, ?, ?> other)
  {
    return new IntExpression (raw.bor (other.raw ()));
  }

  /// @return `that ^ other`
  public IntExpression bxor (ASubIntExpression <?, ?, ?> other)
  {
    return new IntExpression (raw.xor (other.raw ()));
  }

  /// @return `that << other`
  public IntExpression lshift (ASubIntExpression <?, ?, ?> other)
  {
    return new IntExpression (raw.shl (other.raw ()));
  }

  /// @return `that >> other`
  public IntExpression rshift (ASubIntExpression <?, ?, ?> other)
  {
    return new IntExpression (raw.shr (other.raw ()));
  }

}
