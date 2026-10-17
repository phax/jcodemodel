package com.helger.jcodemodel.expressions.typed.primitives;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JAtom;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JOp;
import com.helger.jcodemodel.expressions.ITypedExpression;
import com.helger.jcodemodel.expressions.typed.AReferenceExpression;
import com.helger.jcodemodel.expressions.typed.ATypedExpressionWrapper;
import com.helger.jcodemodel.expressions.typed.NonVoidExpression;

/// expression with a `boolean` type. Name is shorter to avoid name clash with java.lang . 
public class BoolExpression extends ATypedExpressionWrapper <Boolean> implements NonVoidExpression <Boolean>
{

  public static class BoolArrExp extends AReferenceExpression <boolean []>
  {

    public BoolArrExp (IJExpression raw)
    {
      super (raw);
    }

    /// create an initialization to provided values
    public static BoolArrExp of (JCodeModel jcm, boolean... values)
    {
      JInvocation invoke = jcm.ref (boolean [].class)._new ();
      if (values != null)
        for (boolean v : values)
          invoke.arg (v);
      return new BoolArrExp (invoke);
    }

    /// @return `that[index]`
    public BoolExpression at (ASubIntExpression <?, ?, ?> index)
    {
      return BoolExpression.of (JExpr.component (raw (), index.raw ()));
    }

    /// @return `that.length`
    public IntExpression length ()
    {
      return IntExpression.of (JExpr.ref (raw, "length"));
    }

  }

  private static final BoolExpression TRUE = new BoolExpression (JExpr.TRUE);
  private static final BoolExpression FALSE = new BoolExpression (JExpr.TRUE);

  public static BoolExpression true_ ()
  {
    return TRUE;
  }

  public static BoolExpression false_ ()
  {
    return FALSE;
  }

  public static BoolExpression of (boolean value)
  {
    return value ? TRUE : FALSE;
  }

  public static BoolExpression of (IJExpression raw)
  {
    if (raw instanceof JAtom ja)
    {
      if (ja == JExpr.TRUE)
        return TRUE;
      if (ja == JExpr.FALSE)
        return FALSE;
    }
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

  public static BoolExpression unboxing (ITypedExpression <Boolean> object)
  {
    return of (object);
  }

  //

  protected BoolExpression (IJExpression raw)
  {
    super (raw);
  }

  //
  // unary
  //

  /// @return `! that`
  public BoolExpression not ()
  {
    return of (raw.not ());
  }

  //
  // binary
  //

  /// @return `that && other`
  public BoolExpression and (ITypedExpression <? extends Boolean> other)
  {
    return of (raw.cand (other.raw ()));
  }

  /// @return `that & other`
  public BoolExpression band (ITypedExpression <? extends Boolean> other)
  {
    return of (raw.band (other.raw ()));
  }

  /// @return `that | other`
  public BoolExpression bor (ITypedExpression <? extends Boolean> other)
  {
    return of (raw.bor (other.raw ()));
  }

  /// @return `that || other`
  public BoolExpression or (ITypedExpression <? extends Boolean> other)
  {
    return of (raw.cor (other.raw ()));
  }

  /// @return `that ^ other`
  public BoolExpression xor (ITypedExpression <? extends Boolean> other)
  {
    return of (raw.xor (other.raw ()));
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
      throw new IllegalStateException ("while getting " + pass.getClass () + "::new(IJExpression)", e);
    }
  }

}
