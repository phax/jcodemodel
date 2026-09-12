package com.helger.jcodemodel.expressions.typed.primitives;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;

/// mother class of expressions resolving to an float-promoted type (float and sub long)
public class ASubFloatExpression <T> extends ANumericExpression <T>
{

  protected ASubFloatExpression (@NonNull IJExpression raw)
  {
    super (raw);
  }

  //
  // promoting operators
  //

  /// @return `that + other`
  public FltExpression div (ASubFloatExpression <?> other)
  {
    return new FltExpression (raw.div (other.raw ()));
  }

  /// @return `that + other`
  public FltExpression div (float other)
  {
    return new FltExpression (raw.div (JExpr.lit (other)));
  }

  /// @return `that + other`
  public FltExpression plus (ASubFloatExpression <?> other)
  {
    return new FltExpression (raw.plus (other.raw ()));
  }

  /// @return `that + other`
  public FltExpression plus (float other)
  {
    return new FltExpression (raw.plus (JExpr.lit (other)));
  }

  /// @return `that * other`
  public FltExpression mult (ASubFloatExpression <?> other)
  {
    return new FltExpression (raw.mul (other.raw ()));
  }

  /// @return `that * other`
  public FltExpression mult (float other)
  {
    return new FltExpression (raw.mul (JExpr.lit (other)));
  }

  /// @return `that - other`
  public FltExpression sub (ASubFloatExpression <?> other)
  {
    return new FltExpression (raw.minus (other.raw ()));
  }

  /// @return `that - other`
  public FltExpression sub (float other)
  {
    return new FltExpression (raw.minus (JExpr.lit (other)));
  }

}
