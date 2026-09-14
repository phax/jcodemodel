package com.helger.jcodemodel.expressions.typed.primitives;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.expressions.ITypedExpression;
import com.helger.jcodemodel.expressions.typed.java.lang.ObjectExpression;

public class ArrayExpression <ElementType> extends ObjectExpression <ElementType []>
{

  public ArrayExpression (IJExpression raw)
  {
    super (raw);
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
