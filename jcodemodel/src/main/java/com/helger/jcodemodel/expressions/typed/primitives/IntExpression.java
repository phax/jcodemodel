package com.helger.jcodemodel.expressions.typed.primitives;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.expressions.ITypedExpression;
import com.helger.jcodemodel.expressions.typed.AReferenceExpression;

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
    return new IntExpression (raw);
  }

  public static IntExpression of (ITypedExpression <?> untyped)
  {
    return of (untyped.raw ());
  }

  public static IntExpression of (int value)
  {
    return new IntExpression (JExpr.lit (value));
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
