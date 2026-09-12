package com.helger.jcodemodel.expressions.typed.primitives;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;

/// mother class of expressions resolving to an int-promoted type (int, char, byte, short)
public class ASubIntExpression <T> extends ASubFloatExpression <T>
{

  protected ASubIntExpression (@NonNull IJExpression raw)
  {
    super (raw);
  }

  //
  // promoting operators
  //

  /// @return `that + other`
  public IntExpression div (ASubIntExpression <?> other)
  {
    return new IntExpression (raw.div (other.raw ()));
  }

  /// @return `that + other`
  public IntExpression div (int other)
  {
    return new IntExpression (raw.div (JExpr.lit (other)));
  }

  /// @return `that + other`
  public IntExpression plus (ASubIntExpression <?> other)
  {
    return new IntExpression (raw.plus (other.raw ()));
  }

  /// @return `that + other`
  public IntExpression plus (int other)
  {
    return new IntExpression (raw.plus (JExpr.lit (other)));
  }

  /// @return `that * other`
  public IntExpression mult (ASubIntExpression <?> other)
  {
    return new IntExpression (raw.mul (other.raw ()));
  }

  /// @return `that * other`
  public IntExpression mult (int other)
  {
    return new IntExpression (raw.mul (JExpr.lit (other)));
  }

  /// @return `that - other`
  public IntExpression sub (ASubIntExpression <?> other)
  {
    return new IntExpression (raw.minus (other.raw ()));
  }

  /// @return `that - other`
  public IntExpression sub (int other)
  {
    return new IntExpression (raw.minus (JExpr.lit (other)));
  }

  //
  // bitwise operators
  // https://docs.oracle.com/javase/tutorial/java/nutsandbolts/op3.html
  //

  /// @return `that & other`
  public IntExpression band (ASubIntExpression <?> other)
  {
    return new IntExpression (raw.band (other.raw ()));
  }

  /// @return `that & other`
  public IntExpression band (int other)
  {
    return new IntExpression (raw.band (JExpr.lit (other)));
  }

  /// @return `~that`
  public IntExpression bnot ()
  {
    return new IntExpression (raw.complement ());
  }

  /// @return `that | other`
  public IntExpression bor (ASubIntExpression <?> other)
  {
    return new IntExpression (raw.bor (other.raw ()));
  }

  /// @return `that | other`
  public IntExpression bor (int other)
  {
    return new IntExpression (raw.bor (JExpr.lit (other)));
  }

  /// @return `that ^ other`
  public IntExpression bxor (ASubIntExpression <?> other)
  {
    return new IntExpression (raw.xor (other.raw ()));
  }

  /// @return `that ^ other`
  public IntExpression bxor (int other)
  {
    return new IntExpression (raw.xor (JExpr.lit (other)));
  }

  /// @return `that << other`
  public IntExpression lshift (ASubIntExpression <?> other)
  {
    return new IntExpression (raw.shl (other.raw ()));
  }

  /// @return `that << other`
  public IntExpression lshift (int other)
  {
    return new IntExpression (raw.shl (JExpr.lit (other)));
  }

  /// @return `that >> other`
  public IntExpression rshift (ASubIntExpression <?> other)
  {
    return new IntExpression (raw.shr (other.raw ()));
  }

  /// @return `that >> other`
  public IntExpression rshift (int other)
  {
    return new IntExpression (raw.shr (JExpr.lit (other)));
  }

}
