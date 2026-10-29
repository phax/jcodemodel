package com.helger.jcodemodel.expressions.typed.primitives;

import java.util.function.IntFunction;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JAtomInt;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.expressions.ITypedExpression;
import com.helger.jcodemodel.expressions.typed.AReferenceExpression;
import com.helger.jcodemodel.literals.IntegerRepresentation;

/// expression with a `int` type.
public class IntExpression extends ASubIntExpression <Integer, IntExpression, IntExpression>
{

  public static class IntArrExp extends AReferenceExpression <int []>
  {

    public IntArrExp (IJExpression raw)
    {
      super (raw);
    }

    /// create an initialization to provided values
    public static IntArrExp of (JCodeModel jcm, int... values)
    {
      JInvocation invoke = jcm.ref (int [].class)._new ();
      if (values != null)
        for (int i : values)
          invoke.arg (i);
      return new IntArrExp (invoke);
    }

    /// @return `that[index]`
    public IntExpression at (ASubIntExpression <?, ?, ?> index)
    {
      return IntExpression.of (JExpr.component (raw (), index.raw ()));
    }

    /// @return `that.length`
    public IntExpression length ()
    {
      return new IntExpression (JExpr.ref (raw, "length"));
    }

  }

  public static IntExpression of (IJExpression raw)
  {
    // if we actually use an int IJExpression with default representation, then cache if possible.
    if (raw instanceof JAtomInt jai)
    {
      if (jai.representation () == IntegerRepresentation.DEFAULT)
        return computeIfAbsent (jai.what (), i -> raw);
    }
    return new IntExpression (raw);
  }

  public static IntExpression of (ITypedExpression <?> untyped)
  {
    return of (untyped.raw ());
  }

  /// just like Integer for the jcm, we cache the values for -127..128
  private static final IntExpression [] CACHE = new IntExpression [256];

  /// Find the data in the cache if possible. The cache can miss if the value is out of cacheable
  /// bounds, or not already cached.
  /// @param produce the creator of a IJExpression when the cache misses.
  private static IntExpression computeIfAbsent (int value, IntFunction <IJExpression> produce)
  {
    int index = value + 127;
    if (index < 0 || index >= CACHE.length)
      return new IntExpression (produce.apply (value));
    IntExpression ret = CACHE[index];
    if (ret == null)
    {
      synchronized (CACHE)
      {
        ret = CACHE[index];
        if (ret == null)
          ret = CACHE[index] = new IntExpression (produce.apply (value));
      }
    }
    return ret;
  }

  public static IntExpression of (int value)
  {
    return computeIfAbsent (value, JExpr::lit);
  }

  public static IntExpression param (JMethod m, String name)
  {
    return m.paramTyped (name, int.class, IntExpression::of);
  }

  public static IntExpression unboxing (ITypedExpression <Integer> object)
  {
    return of (object);
  }

  ///

  public IntExpression (IJExpression raw)
  {
    super (raw);
  }

  @Override
  protected IntExpression wrapSelf (IJExpression exp)
  {
    return new IntExpression (exp);
  }

  @Override
  protected IntExpression wrapPos (IJExpression exp)
  {
    return new IntExpression (exp);
  }

}
