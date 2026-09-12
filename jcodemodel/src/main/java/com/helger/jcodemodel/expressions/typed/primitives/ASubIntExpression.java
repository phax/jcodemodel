package com.helger.jcodemodel.expressions.typed.primitives;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJExpression;

/// mother class of expressions resolving to an int-promoted type (int, char, byte, short)
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

  /// @return `that + other`
  public IntExpression div (ASubIntExpression <?, ?, ?> other)
  {
    return new IntExpression (raw.div (other.raw ()));
  }

  /// @return `that + other`
  public IntExpression plus (ASubIntExpression <?, ?, ?> other)
  {
    return new IntExpression (raw.plus (other.raw ()));
  }

  /// @return `that * other`
  public IntExpression mult (ASubIntExpression <?, ?, ?> other)
  {
    return new IntExpression (raw.mul (other.raw ()));
  }

  /// @return `that - other`
  public IntExpression sub (ASubIntExpression <?, ?, ?> other)
  {
    return new IntExpression (raw.minus (other.raw ()));
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
