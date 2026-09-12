package com.helger.jcodemodel.expressions.typed.primitives;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;

/// mother class of expressions resolving to an long-promoted type (long and sub int)
public class ASubLongExpression <T> extends ASubFloatExpression <T>
{

  protected ASubLongExpression (@NonNull IJExpression raw)
  {
    super (raw);
  }

  //
  // binary
  //

  /// @return `that + other`
  public LngExpression div (ASubLongExpression <?> other)
  {
    return new LngExpression (raw.div (other.raw ()));
  }

  /// @return `that + other`
  public LngExpression div (long other)
  {
    return new LngExpression (raw.div (JExpr.lit (other)));
  }

  /// @return `that + other`
  public LngExpression plus (ASubLongExpression <?> other)
  {
    return new LngExpression (raw.plus (other.raw ()));
  }

  /// @return `that + other`
  public LngExpression plus (long other)
  {
    return new LngExpression (raw.plus (JExpr.lit (other)));
  }

  /// @return `that * other`
  public LngExpression mult (ASubLongExpression <?> other)
  {
    return new LngExpression (raw.mul (other.raw ()));
  }

  /// @return `that * other`
  public LngExpression mult (long other)
  {
    return new LngExpression (raw.mul (JExpr.lit (other)));
  }

  /// @return `that - other`
  public LngExpression sub (ASubLongExpression <?> other)
  {
    return new LngExpression (raw.minus (other.raw ()));
  }

  /// @return `that - other`
  public LngExpression sub (long other)
  {
    return new LngExpression (raw.minus (JExpr.lit (other)));
  }

}
