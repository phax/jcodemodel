package com.helger.jcodemodel.expressions.typed.primitives;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.expressions.ITypedExpression;
import com.helger.jcodemodel.expressions.typed.TypedExpressionWrapper;
import com.helger.jcodemodel.expressions.typed.java.lang.StringExpression;

/// Root class for numeric expression. The arithmetic operators promote any type to double, unless both are subtypes.
/// So the type represented is a double-promotable one (double or sub float)
/// 
/// https://docs.oracle.com/javase/specs/jls/se26/html/jls-5.html#jls-5.6
/// basically promotion for numerics is double, float, long, int
/// 
/// The subtype-check is done in subclasses ; this one only considers doubles .
///  
/// This class could be named ASubDoubleExpression but ANumerical seems more explicit. 
/// 
/// @param Self the self type, class itself in the concrete.
/// @param PosType the returned numeric expression type constructed from pos(). +double is double, but +char is int . Same for negative.
public abstract class ANumericExpression <T, Self extends ANumericExpression <T, Self, ?>, PosType extends ANumericExpression <?, ?, ?>>
                                         extends
                                         TypedExpressionWrapper <T>
{

  protected ANumericExpression (@NonNull IJExpression raw)
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

  /// @return `that --`
  public Self decrPost ()
  {
    return wrapSelf (raw.postdecr ());
  }

  /// @return `-- that`
  public Self decrPre ()
  {
    return wrapSelf (raw.predecr ());
  }

  /// @return `that + other`
  public DblExpression div (ANumericExpression <?, ?, ?> other)
  {
    return new DblExpression (raw.div (other.raw ()));
  }

  /// @return `that ++`
  public Self incrPost ()
  {
    return wrapSelf (raw.postincr ());
  }

  /// @return `++ that`
  public Self incrPre() {
    return wrapSelf (raw.preincr ());
  }

  /// @return `that + other`
  public DblExpression plus (ANumericExpression <?, ?, ?> other)
  {
    return new DblExpression (raw.plus (other.raw ()));
  }

  /// @return `that * other`
  public DblExpression mult (ANumericExpression <?, ?, ?> other)
  {
    return new DblExpression (raw.mul (other.raw ()));
  }

  /// @return `- that`
  public PosType neg ()
  {
    return wrapPos (raw.minus ());
  }

  /// @return `+ that`
  public PosType pos ()
  {
    return wrapPos (raw.plus ());
  }

  /// @return `that - other`
  public DblExpression sub (ANumericExpression <?, ?, ?> other)
  {
    return new DblExpression (raw.minus (other.raw ()));
  }

  //
  // non-promoting operators : The arguments can be promoted, but the output remains the same
  //

  /// @return `that == other`
  public BoolExpression eq (ANumericExpression <?, ?, ?> other)
  {
    return new BoolExpression (raw.eq (other.raw ()));
  }

  /// @return `that >= other`
  public BoolExpression ge (ANumericExpression <?, ?, ?> other)
  {
    return new BoolExpression (raw.gte (other.raw ()));
  }

  /// @return `that > other`
  public BoolExpression gt (ANumericExpression <?, ?, ?> other)
  {
    return new BoolExpression (raw.gt (other.raw ()));
  }

  /// @return `that <= other`
  public BoolExpression le (ANumericExpression <?, ?, ?> other)
  {
    return new BoolExpression (raw.lte (other.raw ()));
  }

  /// @return `that < other`
  public BoolExpression lt (ANumericExpression <?, ?, ?> other)
  {
    return new BoolExpression (raw.lt (other.raw ()));
  }

  /// @return `that != other`
  public BoolExpression ne (ANumericExpression <?, ?, ?> other)
  {
    return new BoolExpression (raw.ne (other.raw ()));
  }

  // operator + with a String operands promotes anything else to String.
  /// creates `that + other`
  public StringExpression plus (ITypedExpression <? extends String> other)
  {
    return new StringExpression (raw.plus (other.raw ()));
  }
}
