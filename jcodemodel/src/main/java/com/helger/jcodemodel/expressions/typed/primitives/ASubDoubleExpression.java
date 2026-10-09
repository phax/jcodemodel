package com.helger.jcodemodel.expressions.typed.primitives;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.expressions.ITypedExpression;
import com.helger.jcodemodel.expressions.typed.ATypedExpressionWrapper;
import com.helger.jcodemodel.expressions.typed.NonVoidExpression;
import com.helger.jcodemodel.expressions.typed.java.lang.StringExpression;

/// Root class for numeric expression. The arithmetic operators promote any type to double, unless both are subtypes.
/// So the type represented is a double-promotable one (double or sub float)
/// 
/// https://docs.oracle.com/javase/specs/jls/se26/html/jls-5.html#jls-5.6
/// basically promotion for numerics is double, float, long, int
/// 
/// The subtype-check is done in subclasses ; this one only considers doubles .
/// 
/// @param Self the self type, the class itself in the concrete implementations.
/// @param PosType the returned numeric expression type constructed from pos(). +double is double, but +char is int.
/// This param is not bound to T because the result of a `+x` maybe a different type from **x**, typically `+'a'` is an int.
public abstract class ASubDoubleExpression <T, Self extends ASubDoubleExpression <T, Self, ?>, PosType extends ASubDoubleExpression <?, ?, ?>>
                                         extends
                                         ATypedExpressionWrapper <T>
                                         implements
                                         NonVoidExpression <T>
{

  protected ASubDoubleExpression (@NonNull IJExpression raw)
  {
    super (raw);
  }

  /// basically constructor.
  protected abstract Self wrapSelf (IJExpression exp);

  /// basically constructor.
  protected abstract PosType wrapPos (IJExpression exp);


  //
  // Promoting operators.
  // Those operators promote the result type based on the operand types. For example, `int + double
  // := double`
  //

  /// @return `that / other`
  public DblExpression div (ASubDoubleExpression <?, ?, ?> other)
  {
    return new DblExpression (raw.div (other.raw ()));
  }

  /// @return `that % other`
  public DblExpression mod (ASubDoubleExpression <?, ?, ?> other)
  {
    return new DblExpression (raw.mod (other.raw ()));
  }

  /// @return `that * other`
  public DblExpression mult (ASubDoubleExpression <?, ?, ?> other)
  {
    return new DblExpression (raw.mul (other.raw ()));
  }

  /// @return `- that`
  public PosType neg ()
  {
    return wrapPos (raw.minus ());
  }

  /// @return `that + other`
  public DblExpression plus (ASubDoubleExpression <?, ?, ?> other)
  {
    return new DblExpression (raw.plus (other.raw ()));
  }

  //
  // added just to show the pain. They should be overridden in sub types, and duplicated for each
  // operator :
  // + - * /
  // > ≥ < ≤ would not be overriden (same return all the time)
  //

  public DblExpression plus (double d)
  {
    return DblExpression.of (raw ().plus (d));
  }

  public DblExpression plus (float f)
  {
    return DblExpression.of (raw ().plus (f));
  }

  public DblExpression plus (long l)
  {
    return DblExpression.of (raw ().plus (l));
  }

  public DblExpression plus (int i)
  {
    return DblExpression.of (raw ().plus (i));
  }

  public DblExpression plus (short s)
  {
    return DblExpression.of (raw ().plus (s));
  }

  public DblExpression plus (byte b)
  {
    return DblExpression.of (raw ().plus (b));
  }

  public DblExpression plus (char c)
  {
    return DblExpression.of (raw ().plus (c));
  }

  // operator + with a String operands promotes anything else to String.
  /// @return `that + other`
  public StringExpression plus (ITypedExpression <? extends String> other)
  {
    return new StringExpression (raw.plus (other.raw ()));
  }

  /// @return `+ that`
  public PosType pos ()
  {
    return wrapPos (raw.plus ());
  }

  /// @return `that --`
  public Self postDecr ()
  {
    return wrapSelf (raw.postdecr ());
  }

  /// @return `that ++`
  public Self postIncr ()
  {
    return wrapSelf (raw.postincr ());
  }

  /// @return `-- that`
  public Self preDecr ()
  {
    return wrapSelf (raw.predecr ());
  }

  /// @return `++ that`
  public Self preIncr ()
  {
    return wrapSelf (raw.preincr ());
  }

  /// @return `that - other`
  public DblExpression sub (ASubDoubleExpression <?, ?, ?> other)
  {
    return new DblExpression (raw.minus (other.raw ()));
  }

  //
  // non-promoting operators : The arguments can be promoted, but the output remains the same.
  // Those don't need to be overloaded in the sub classes.
  //

  /// @return `that >= other`
  public BoolExpression ge (ASubDoubleExpression <?, ?, ?> other)
  {
    return new BoolExpression (raw.gte (other.raw ()));
  }

  /// @return `that > other`
  public BoolExpression gt (ASubDoubleExpression <?, ?, ?> other)
  {
    return new BoolExpression (raw.gt (other.raw ()));
  }

  /// @return `that <= other`
  public BoolExpression le (ASubDoubleExpression <?, ?, ?> other)
  {
    return new BoolExpression (raw.lte (other.raw ()));
  }

  /// @return `that < other`
  public BoolExpression lt (ASubDoubleExpression <?, ?, ?> other)
  {
    return new BoolExpression (raw.lt (other.raw ()));
  }
}
