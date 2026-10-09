package com.helger.jcodemodel.expressions.typed.primitives;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;
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
    return BoolExpression.of (raw.gte (other.raw ()));
  }

  public BoolExpression ge (double d)
  {
    return BoolExpression.of (raw ().gte (JExpr.lit (d)));
  }

  public BoolExpression ge (float f)
  {
    return BoolExpression.of (raw ().gte (JExpr.lit (f)));
  }

  public BoolExpression ge (long l)
  {
    return BoolExpression.of (raw ().gte (JExpr.lit (l)));
  }

  public BoolExpression ge (int i)
  {
    return BoolExpression.of (raw ().gte (JExpr.lit (i)));
  }

  public BoolExpression ge (short s)
  {
    return BoolExpression.of (raw ().gte (JExpr.lit (s)));
  }

  public BoolExpression ge (byte b)
  {
    return BoolExpression.of (raw ().gte (JExpr.lit (b)));
  }

  public BoolExpression ge (char c)
  {
    return BoolExpression.of (raw ().gte (JExpr.lit (c)));
  }

  /// @return `that > other`
  public BoolExpression gt (ASubDoubleExpression <?, ?, ?> other)
  {
    return BoolExpression.of (raw.gt (other.raw ()));
  }

  public BoolExpression gt (double d)
  {
    return BoolExpression.of (raw ().gt (JExpr.lit (d)));
  }

  public BoolExpression gt (float f)
  {
    return BoolExpression.of (raw ().gt (JExpr.lit (f)));
  }

  public BoolExpression gt (long l)
  {
    return BoolExpression.of (raw ().gt (JExpr.lit (l)));
  }

  public BoolExpression gt (int i)
  {
    return BoolExpression.of (raw ().gt (JExpr.lit (i)));
  }

  public BoolExpression gt (short s)
  {
    return BoolExpression.of (raw ().gt (JExpr.lit (s)));
  }

  public BoolExpression gt (byte b)
  {
    return BoolExpression.of (raw ().gt (JExpr.lit (b)));
  }

  public BoolExpression gt (char c)
  {
    return BoolExpression.of (raw ().gt (JExpr.lit (c)));
  }

  /// @return `that <= other`
  public BoolExpression le (ASubDoubleExpression <?, ?, ?> other)
  {
    return BoolExpression.of (raw.lte (other.raw ()));
  }

  public BoolExpression le (double d)
  {
    return BoolExpression.of (raw ().lte (JExpr.lit (d)));
  }

  public BoolExpression le (float f)
  {
    return BoolExpression.of (raw ().lte (JExpr.lit (f)));
  }

  public BoolExpression le (long l)
  {
    return BoolExpression.of (raw ().lte (JExpr.lit (l)));
  }

  public BoolExpression le (int i)
  {
    return BoolExpression.of (raw ().lte (JExpr.lit (i)));
  }

  public BoolExpression le (short s)
  {
    return BoolExpression.of (raw ().lte (JExpr.lit (s)));
  }

  public BoolExpression le (byte b)
  {
    return BoolExpression.of (raw ().lte (JExpr.lit (b)));
  }

  public BoolExpression le (char c)
  {
    return BoolExpression.of (raw ().lte (JExpr.lit (c)));
  }

  /// @return `that < other`
  public BoolExpression lt (ASubDoubleExpression <?, ?, ?> other)
  {
    return new BoolExpression (raw.lt (other.raw ()));
  }

  public BoolExpression lt (double d)
  {
    return BoolExpression.of (raw ().lt (JExpr.lit (d)));
  }

  public BoolExpression lt (float f)
  {
    return BoolExpression.of (raw ().lt (JExpr.lit (f)));
  }

  public BoolExpression lt (long l)
  {
    return BoolExpression.of (raw ().lt (JExpr.lit (l)));
  }

  public BoolExpression lt (int i)
  {
    return BoolExpression.of (raw ().lt (JExpr.lit (i)));
  }

  public BoolExpression lt (short s)
  {
    return BoolExpression.of (raw ().lt (JExpr.lit (s)));
  }

  public BoolExpression lt (byte b)
  {
    return BoolExpression.of (raw ().lt (JExpr.lit (b)));
  }

  public BoolExpression lt (char c)
  {
    return BoolExpression.of (raw ().lt (JExpr.lit (c)));
  }
}
