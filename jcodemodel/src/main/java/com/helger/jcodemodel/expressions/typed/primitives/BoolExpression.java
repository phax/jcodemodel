package com.helger.jcodemodel.expressions.typed.primitives;

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
  public <T> TypedExpressionWrapper <T> ternary (TypedExpressionWrapper <? extends T> pass,
                                                 TypedExpressionWrapper <? extends T> fail)
  {
    return new TypedExpressionWrapper <> (JOp.cond (raw, pass.raw (), fail.raw ()));
  }

}
