package com.helger.jcodemodel;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/// copy of JReturn
public class JYield implements IJStatement {
  /**
   * {@link IJExpression} to yield; may be null.
   */
  private final IJExpression m_aExpr;

  /**
   * JYield constructor
   *
   * @param aExpr
   *              {@link IJExpression} which evaluates to yield value
   */
  public JYield(@Nullable final IJExpression aExpr) {
    m_aExpr = aExpr;
  }

  @Nullable
  public IJExpression expr() {
    return m_aExpr;
  }

  @Override
  public void state(@NonNull final IJFormatter f) {
    f.print("yield");
    if (m_aExpr != null) {
      f.print(' ').generable(m_aExpr);
    }
    f.print(';');
    f.newline();
  }
}