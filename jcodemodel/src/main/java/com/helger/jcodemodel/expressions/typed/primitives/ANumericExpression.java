package com.helger.jcodemodel.expressions.typed.primitives;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.expressions.typed.TypedExpressionWrapper;

/// Root class for numeric expression. The arithmetic operators promote any type to double, unless both are subtypes.
/// So the type represented is a double-promotable one (double or sub float)
/// 
/// https://docs.oracle.com/javase/specs/jls/se26/html/jls-5.html#jls-5.6
/// basically promotion for numerics is double, float, long, int
/// 
/// The subtype-check is done in subclasses ; this one only considers doubles .
///  
/// This class could be named ASubDoubleExpression but ANumerical seems more explicit. 
public abstract class ANumericExpression <T> extends TypedExpressionWrapper <T>
{

  protected ANumericExpression (@NonNull IJExpression raw)
  {
    super (raw);
  }


  //
  // promoting operators
  //

  /// @return `that + other`
  public DblExpression div (ANumericExpression <?> other)
  {
    return new DblExpression (raw.div (other.raw ()));
  }

  /// @return `that + other`
  public DblExpression div (double other)
  {
    return new DblExpression (raw.div (JExpr.lit (other)));
  }

  /// @return `that + other`
  public DblExpression plus (ANumericExpression <?> other)
  {
    return new DblExpression (raw.plus (other.raw ()));
  }

  /// @return `that + other`
  public DblExpression plus (double other)
  {
    return new DblExpression (raw.plus (JExpr.lit (other)));
  }

  /// @return `that * other`
  public DblExpression mult (ANumericExpression <?> other)
  {
    return new DblExpression (raw.mul (other.raw ()));
  }

  /// @return `that * other`
  public DblExpression mult (double other)
  {
    return new DblExpression (raw.mul (JExpr.lit (other)));
  }

  /// @return `that - other`
  public DblExpression sub (ANumericExpression <?> other)
  {
    return new DblExpression (raw.minus (other.raw ()));
  }

  /// @return `that - other`
  public DblExpression sub (double other)
  {
    return new DblExpression (raw.minus (JExpr.lit (other)));
  }

  //
  // non-promoting operators.
  // The arguments can be promoted, not the output
  //

  /// @return `that >= other`
  public BoolExpression ge (ANumericExpression <?> other)
  {
    return new BoolExpression (raw.gte (other.raw ()));
  }

  /// @return `that >= other`
  public BoolExpression ge (double other)
  {
    return new BoolExpression (raw.gte (JExpr.lit (other)));
  }

  /// @return `that > other`
  public BoolExpression gt (ANumericExpression <?> other)
  {
    return new BoolExpression (raw.gt (other.raw ()));
  }

  /// @return `that > other`
  public BoolExpression gt (double other)
  {
    return new BoolExpression (raw.gt (JExpr.lit (other)));
  }
}
