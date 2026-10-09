package com.helger.jcodemodel.expressions.typed.primitives;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.expressions.ITypedExpression;
import com.helger.jcodemodel.expressions.typed.AReferenceExpression;

/// expression with a `byte` type.
public class BytExpression extends ASubShortExpression <Byte, BytExpression, BytExpression>
{

  public static class ByteArrExp extends AReferenceExpression <byte []>
  {

    public ByteArrExp (IJExpression raw)
    {
      super (raw);
    }

    /// create an initialization to provided values
    public static ByteArrExp of (JCodeModel jcm, byte... values)
    {
      JInvocation invoke = jcm.ref (byte [].class)._new ();
      if (values != null)
        for (byte v : values)
          invoke.arg (v);
      return new ByteArrExp (invoke);
    }

    /// @return `that[index]`
    public BytExpression at (ASubIntExpression <?, ?, ?> index)
    {
      return BytExpression.of (JExpr.component (raw (), index.raw ()));
    }

    /// @return `that.length`
    public IntExpression length ()
    {
      return new IntExpression (JExpr.ref (raw, "length"));
    }

  }

  public static BytExpression of (IJExpression raw)
  {
    return new BytExpression (raw);
  }

  public static BytExpression of (ITypedExpression <?> untyped)
  {
    return of (untyped.raw ());
  }

  public static BytExpression of (byte value)
  {
    return new BytExpression (JExpr.lit (value));
  }

  public static BytExpression param (JMethod m, String name)
  {
    return m.paramTyped (name, byte.class, BytExpression::of);
  }

  public static BytExpression unboxing (ITypedExpression <Byte> object)
  {
    return of (object);
  }

  ///

  public BytExpression (IJExpression raw)
  {
    super (raw);
  }

  @Override
  protected BytExpression wrapSelf (IJExpression exp)
  {
    return new BytExpression (exp);
  }

  @Override
  protected BytExpression wrapPos (IJExpression exp)
  {
    return new BytExpression (exp);
  }

}
