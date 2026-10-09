package com.helger.jcodemodel.expressions.typed.primitives;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.expressions.ITypedExpression;
import com.helger.jcodemodel.expressions.typed.AReferenceExpression;

/// expression with a `char` type.
public class CharExpression extends ASubIntExpression <Character, CharExpression, IntExpression>
{
  public static class CharArrExp extends AReferenceExpression <char []>
  {

    public CharArrExp (IJExpression raw)
    {
      super (raw);
    }

    /// create an initialization to provided values
    public static CharArrExp of (JCodeModel jcm, char... values)
    {
      JInvocation invoke = jcm.ref (char [].class)._new ();
      if (values != null)
        for (char v : values)
          invoke.arg (v);
      return new CharArrExp (invoke);
    }

    /// @return `that[index]`
    public CharExpression at (ASubIntExpression <?, ?, ?> index)
    {
      return CharExpression.of (JExpr.component (raw (), index.raw ()));
    }

    /// @return `that.length`
    public IntExpression length ()
    {
      return new IntExpression (JExpr.ref (raw, "length"));
    }

  }

  public static CharExpression of (char value)
  {
    return new CharExpression (JExpr.lit (value));
  }

  public static CharExpression of (IJExpression raw)
  {
    return new CharExpression (raw);
  }

  public static CharExpression of (ITypedExpression <?> untyped)
  {
    return of (untyped.raw ());
  }

  public static CharExpression param (JMethod m, String name)
  {
    return m.paramTyped (name, char.class, CharExpression::of);
  }

  public static CharExpression unboxing (ITypedExpression <Character> object)
  {
    return of (object);
  }

  //

  public CharExpression (IJExpression raw)
  {
    super (raw);
  }

  @Override
  protected CharExpression wrapSelf (IJExpression exp)
  {
    return new CharExpression (exp);
  }

  @Override
  protected IntExpression wrapPos (IJExpression exp)
  {
    return new IntExpression (exp);
  }

}
