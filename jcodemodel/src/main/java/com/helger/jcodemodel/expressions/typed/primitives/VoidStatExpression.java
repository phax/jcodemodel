package com.helger.jcodemodel.expressions.typed.primitives;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJExpressionStatement;
import com.helger.jcodemodel.expressions.typed.TypedExpressionWrapper;

/// An expression that resolves to a void type, using a statement. Return type of expressions calling a void method.
public class VoidStatExpression extends TypedExpressionWrapper <Void>
{

  public static VoidStatExpression of (IJExpressionStatement raw)
  {
    return new VoidStatExpression (raw);
  }

  //

  public VoidStatExpression (@NonNull IJExpressionStatement raw)
  {
    super (raw);
  }

  @Override
  public @NonNull IJExpressionStatement raw ()
  {
    return (@NonNull IJExpressionStatement) raw;
  }

}
