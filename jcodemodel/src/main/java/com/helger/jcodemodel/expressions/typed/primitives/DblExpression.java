package com.helger.jcodemodel.expressions.typed.primitives;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.expressions.ITypedExpression;
import com.helger.jcodemodel.expressions.typed.AReferenceExpression;

/// expression with a `double` type. Name is shorter to avoid name clash with java.lang . 
public class DblExpression extends ANumericExpression <Double, DblExpression, DblExpression>
{

  public static class DoubleArrExp extends AReferenceExpression <double []>
  {

    public DoubleArrExp (IJExpression raw)
    {
      super (raw);
    }

    /// create an initialization to provided values
    public static DoubleArrExp of (JCodeModel jcm, double... values)
    {
      JInvocation invoke = jcm.ref (double [].class)._new ();
      if (values != null)
        for (double v : values)
          invoke.arg (v);
      return new DoubleArrExp (invoke);
    }

    /// @return `that[index]`
    public DblExpression at (ASubIntExpression <?, ?, ?> index)
    {
      return DblExpression.of (JExpr.component (raw (), index.raw ()));
    }

    /// @return `that.length`
    public IntExpression length ()
    {
      return new IntExpression (JExpr.ref (raw, "length"));
    }

  }

  public static DblExpression of (double value)
  {
    return new DblExpression (JExpr.lit (value));
  }

  public static DblExpression of (IJExpression raw)
  {
    return new DblExpression (raw);
  }

  public static DblExpression of (ITypedExpression <?> untyped)
  {
    return of (untyped.raw ());
  }

  public static DblExpression param (JMethod m, String name)
  {
    return m.paramTyped (name, double.class, DblExpression::of);
  }

  public static DblExpression unboxing (ITypedExpression <Double> object)
  {
    return of (object);
  }

  //

  public DblExpression (IJExpression raw)
  {
    super (raw);
  }

  @Override
  protected DblExpression wrapSelf (IJExpression exp)
  {
    return new DblExpression (exp);
  }

  @Override
  protected DblExpression wrapPos (IJExpression exp)
  {
    return new DblExpression (exp);
  }

}
