package com.helger.jcodemodel.expressions.typed.primitives;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJExpressionStatement;
import com.helger.jcodemodel.expressions.typed.TypedExpressionWrapper;

/// An expression that resolves to a void type, and is a statement. Return type of expressions calling a void method.
public class StatVoidExpression extends TypedExpressionWrapper <Void>
{

  public static StatVoidExpression of (IJExpressionStatement raw)
  {
    return new StatVoidExpression (raw);
  }

  //

  public StatVoidExpression (@NonNull IJExpressionStatement raw)
  {
    super (raw);
  }

  @Override
  public @NonNull IJExpressionStatement raw ()
  {
    return (@NonNull IJExpressionStatement) raw;
  }

}
