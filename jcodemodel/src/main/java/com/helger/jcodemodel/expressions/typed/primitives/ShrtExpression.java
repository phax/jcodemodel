package com.helger.jcodemodel.expressions.typed.primitives;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.expressions.ITypedExpression;
import com.helger.jcodemodel.expressions.typed.AReferenceExpression;

/// expression with a `short` type.
public class ShrtExpression extends ASubShortExpression <Short, ShrtExpression, ShrtExpression>
{

  public static class ShortArrExp extends AReferenceExpression <short []>
  {

    public ShortArrExp (IJExpression raw)
    {
      super (raw);
    }

    /// create an initialization to provided values
    public static ShortArrExp of (JCodeModel jcm, short... values)
    {
      JInvocation invoke = jcm.ref (short [].class)._new ();
      if (values != null)
        for (short v : values)
          invoke.arg (v);
      return new ShortArrExp (invoke);
    }

    /// @return `that[index]`
    public ShrtExpression at (ASubIntExpression <?, ?, ?> index)
    {
      return ShrtExpression.of (JExpr.component (raw (), index.raw ()));
    }

    /// @return `that.length`
    public IntExpression length ()
    {
      return new IntExpression (JExpr.ref (raw, "length"));
    }

  }

  public static ShrtExpression of (IJExpression raw)
  {
    return new ShrtExpression (raw);
  }

  public static ShrtExpression of (ITypedExpression <?> untyped)
  {
    return of (untyped.raw ());
  }

  public static ShrtExpression of (short value)
  {
    return new ShrtExpression (JExpr.lit (value));
  }

  public static ShrtExpression param (JMethod m, String name)
  {
    return m.paramTyped (name, short.class, ShrtExpression::of);
  }

  public static ShrtExpression unboxing (ITypedExpression <Short> object)
  {
    return of (object);
  }

  ///

  public ShrtExpression (IJExpression raw)
  {
    super (raw);
  }

  @Override
  protected ShrtExpression wrapSelf (IJExpression exp)
  {
    return new ShrtExpression (exp);
  }

  @Override
  protected ShrtExpression wrapPos (IJExpression exp)
  {
    return new ShrtExpression (exp);
  }

}
