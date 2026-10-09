package com.helger.jcodemodel.expressions.typed.primitives;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.expressions.ITypedExpression;
import com.helger.jcodemodel.expressions.typed.AReferenceExpression;

/// expression with a `long` type. Name is shorter to avoid name clash with java.lang . 
public class LngExpression extends ASubLongExpression <Long, LngExpression, LngExpression>
{

  public static class LongArrExp extends AReferenceExpression <long []>
  {

    public LongArrExp (IJExpression raw)
    {
      super (raw);
    }

    /// create an initialization to provided values
    public static LongArrExp of (JCodeModel jcm, long... values)
    {
      JInvocation invoke = jcm.ref (long [].class)._new ();
      if (values != null)
        for (long v : values)
          invoke.arg (v);
      return new LongArrExp (invoke);
    }

    /// @return `that[index]`
    public LngExpression at (ASubIntExpression <?, ?, ?> index)
    {
      return LngExpression.of (JExpr.component (raw (), index.raw ()));
    }

    /// @return `that.length`
    public IntExpression length ()
    {
      return new IntExpression (JExpr.ref (raw, "length"));
    }

  }

  public static LngExpression of (long value)
  {
    return new LngExpression (JExpr.lit (value));
  }

  public static LngExpression of (IJExpression raw)
  {
    return new LngExpression (raw);
  }

  public static LngExpression of (ITypedExpression <?> untyped)
  {
    return of (untyped.raw ());
  }

  public static LngExpression param (JMethod m, String name)
  {
    return m.paramTyped (name, long.class, LngExpression::of);
  }

  public static LngExpression unboxing (ITypedExpression <Long> object)
  {
    return of (object);
  }

  //

  public LngExpression (IJExpression raw)
  {
    super (raw);
  }

  @Override
  protected LngExpression wrapSelf (IJExpression exp)
  {
    return new LngExpression (exp);
  }

  @Override
  protected LngExpression wrapPos (IJExpression exp)
  {
    return new LngExpression (exp);
  }

}
