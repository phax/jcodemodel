package com.helger.jcodemodel.expressions.typed.primitives;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJExpression;

/// short can be promoted to from byte. So we need abstract class those two extend.
public abstract class ASubShortExpression <T, Self extends ASubShortExpression <T, Self, ?>, PosType extends ASubShortExpression <?, ?, ?>>
                                          extends
                                          ASubIntExpression <T, Self, PosType>
{

  protected ASubShortExpression (@NonNull IJExpression raw)
  {
    super (raw);
  }

}
