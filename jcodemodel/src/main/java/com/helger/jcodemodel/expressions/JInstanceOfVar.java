package com.helger.jcodemodel.expressions;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.IJFormatter;
import com.helger.jcodemodel.JMods;
import com.helger.jcodemodel.JVar;

public class JInstanceOfVar implements IJExpression {

  private final IJExpression m_oExpr;
  private final JVar m_oVar;

  public JInstanceOfVar(@NonNull final IJExpression expr,
      @NonNull final AbstractJType type,
      @NonNull final String name) {
    m_oExpr = expr;
    m_oVar = new JVar(JMods.forVar(0), type, name, null);
  }

  public IJExpression expr() {
    return m_oExpr;
  }

  public JVar var() {
    return m_oVar;
  }

  @Override
  public void generate(@NonNull IJFormatter f) {
    f.print('(').generable(m_oExpr).print("instanceof").var(m_oVar).print(')');
  }

}
