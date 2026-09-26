package com.helger.jcodemodel.expressions.typed.primitives;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JOp;
import com.helger.jcodemodel.expressions.ITypedExpression;
import com.helger.jcodemodel.expressions.typed.TypedExpressionWrapper;

/// expression with a `boolean` type. Name is shorter to avoid name clash with java.lang . 
public class BoolExpression extends TypedExpressionWrapper <Boolean>
{

  public static class BoolArrExp extends ArrayExpression <Boolean>
  {

    public BoolArrExp (IJExpression raw)
    {
      super (raw);
    }

    @Override
    public BoolExpression at (ASubIntExpression <?, ?, ?> index)
    {
      return BoolExpression.of (super.at (index));
    }

  }

  public static BoolExpression of (boolean value)
  {
    return new BoolExpression (JExpr.lit (value));
  }

  public static BoolExpression true_ ()
  {
    return of (true);
  }

  public static BoolExpression false_ ()
  {
    return of (false);
  }

  public static BoolExpression of (IJExpression raw)
  {
    return new BoolExpression (raw);
  }

  public static BoolExpression of (ITypedExpression <?> untyped)
  {
    return of (untyped.raw ());
  }

  public static BoolExpression param (JMethod m, String name)
  {
    return m.paramTyped (name, boolean.class, BoolExpression::of);
  }

  //

  public BoolExpression (IJExpression raw)
  {
    super (raw);
  }

  //
  // unary
  //

  /// @return `! that`
  public BoolExpression not ()
  {
    return new BoolExpression (raw.not ());
  }

  //
  // binary
  //

  /// @return `that && other`
  public BoolExpression and (ITypedExpression <? extends Boolean> other)
  {
    return new BoolExpression (raw.cand (other.raw ()));
  }

  /// @return `that & other`
  public BoolExpression band (ITypedExpression <? extends Boolean> other)
  {
    return new BoolExpression (raw.band (other.raw ()));
  }

  /// @return `that | other`
  public BoolExpression bor (ITypedExpression <? extends Boolean> other)
  {
    return new BoolExpression (raw.bor (other.raw ()));
  }

  /// @return `that || other`
  public BoolExpression or (ITypedExpression <? extends Boolean> other)
  {
    return new BoolExpression (raw.cor (other.raw ()));
  }

  /// @return `that ^ other`
  public BoolExpression xor (ITypedExpression <? extends Boolean> other)
  {
    return new BoolExpression (raw.xor (other.raw ()));
  }

  //
  // ternary
  //

  /// @return `that ? pass : fail`
  public <V extends ITypedExpression <?>> V ternary (V pass, V fail)
  {
    // use reflect to get the constructor of the pass instance, then build a new one with the
    // ternary expression.
    // This means the actual type is always pass class, but this appears as the common lower bound
    // in the signature.
    try
    {
      @SuppressWarnings ("unchecked")
      Constructor <? extends V> cons = (Constructor <? extends V>) pass.getClass ().getConstructor (IJExpression.class);
      return (V) cons.newInstance (JOp.cond (raw, pass.raw (), fail.raw ()));
    }
    catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException |
           IllegalArgumentException | InvocationTargetException e)
    {
      throw new IllegalStateException (e);
    }
  }

}
