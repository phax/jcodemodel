package com.helger.jcodemodel.expressions.typed.primitives;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.expressions.ITypedExpression;
import com.helger.jcodemodel.expressions.typed.AReferenceExpression;
import com.helger.jcodemodel.expressions.typed.NonVoidExpression;
import com.helger.jcodemodel.expressions.typed.java.lang.ObjectExpression;

/// array of non-primitive types
public class ArrayExpression <ElementType> extends AReferenceExpression <ElementType []>
{

  public ArrayExpression (IJExpression raw)
  {
    super (raw);
  }

  @SafeVarargs
  public static <T> ArrayExpression <T> of (AbstractJType componentType, NonVoidExpression <? extends T>... values)
  {
    JInvocation invoke = componentType.array ()._new ();
    if (values != null)
      for (NonVoidExpression <? extends T> v : values)
      {
        invoke.arg (v.raw ());
      }
    return new ArrayExpression <> (invoke);
  }

  /// @return `that[index]`
  public ITypedExpression <? extends ElementType> at (ASubIntExpression <?, ?, ?> index)
  {
    return new ObjectExpression <> (JExpr.component (raw, index.raw ()));
  }

  /// @return `that.length`
  public IntExpression length ()
  {
    return new IntExpression (JExpr.ref (raw, "length"));
  }

}
