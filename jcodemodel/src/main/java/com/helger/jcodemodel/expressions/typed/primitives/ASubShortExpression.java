package com.helger.jcodemodel.expressions.typed.primitives;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJExpression;

public abstract class ASubShortExpression <T, Self extends ASubShortExpression <T, Self, ?>, PosType extends ASubShortExpression <?, ?, ?>>
                                          extends
                                          ASubIntExpression <T, Self, PosType>
{

  protected ASubShortExpression (@NonNull IJExpression raw)
  {
    super (raw);
  }

}
