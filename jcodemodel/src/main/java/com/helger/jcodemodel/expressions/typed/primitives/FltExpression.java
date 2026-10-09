package com.helger.jcodemodel.expressions.typed.primitives;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.expressions.ITypedExpression;
import com.helger.jcodemodel.expressions.typed.AReferenceExpression;

/// expression with a `float` type. Name is shorter to avoid name clash with java.lang . 
public class FltExpression extends ASubFloatExpression <Float, FltExpression, FltExpression>
{
  public static class FloatArrExp extends AReferenceExpression <float []>
  {

    public FloatArrExp (IJExpression raw)
    {
      super (raw);
    }

    /// create an initialization to provided values
    public static FloatArrExp of (JCodeModel jcm, float... values)
    {
      JInvocation invoke = jcm.ref (float [].class)._new ();
      if (values != null)
        for (float v : values)
          invoke.arg (v);
      return new FloatArrExp (invoke);
    }

    /// @return `that[index]`
    public FltExpression at (ASubIntExpression <?, ?, ?> index)
    {
      return FltExpression.of (JExpr.component (raw (), index.raw ()));
    }

    /// @return `that.length`
    public IntExpression length ()
    {
      return new IntExpression (JExpr.ref (raw, "length"));
    }

  }


  public static FltExpression of (float value)
  {
    return new FltExpression (JExpr.lit (value));
  }

  public static FltExpression of (IJExpression raw)
  {
    return new FltExpression (raw);
  }

  public static FltExpression of (ITypedExpression <?> untyped)
  {
    return of (untyped.raw ());
  }

  public static FltExpression param (JMethod m, String name)
  {
    return m.paramTyped (name, float.class, FltExpression::of);
  }

  public static FltExpression unboxing (ITypedExpression <Float> object)
  {
    return of (object);
  }

  //

  public FltExpression (IJExpression raw)
  {
    super (raw);
  }

  @Override
  protected FltExpression wrapSelf (IJExpression exp)
  {
    return new FltExpression (exp);
  }

  @Override
  protected FltExpression wrapPos (IJExpression exp)
  {
    return new FltExpression (exp);
  }

}
