package com.helger.jcodemodel.expressions.typed.primitives;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.expressions.typed.TypedExpressionWrapper;

// mother class of expressions resolving to an int-like type (int, char, byte, short)

// otherwise we can't extends another class that already implement the interface with a different generic. 
public class ASubIntExpression <T> extends TypedExpressionWrapper <T>
{

  protected ASubIntExpression (@NonNull IJExpression raw)
  {
    super (raw);
  }

  //
  // binary
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

}
