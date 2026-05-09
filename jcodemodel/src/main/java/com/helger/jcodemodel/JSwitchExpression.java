package com.helger.jcodemodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.switchexpression.JCaseArrow;
import com.helger.jcodemodel.switchexpression.JCasePattern;
import com.helger.jcodemodel.switchexpression.JCaseSpecialSelector;
import com.helger.jcodemodel.switchexpression.JCaseStatic;

///
/// A switch whose result can be used as an expression or a statement.
/// basically a copy of [JSwitch]
/// It has two specific blocks : null and default. When writing those, they should be tested for equality.
public class JSwitchExpression implements IJExpressionStatement {

  /**
   * Test part of switch statement.
   */
  private final IJExpression m_aTestExpr;

  /**
   * vector of JCases.
   */
  private final List<JCaseArrow<?>> m_aCases = new ArrayList<>();

  /**
   * a single default block
   */
  private JBlock m_aDefaultBlock;

  @NonNull
  public JBlock defaultBlock() {
    if (m_aDefaultBlock == null) {
      m_aDefaultBlock = new JLambdaBlock();
    }
    return m_aDefaultBlock;
  }

  /**
   * a single null block
   */
  private JBlock m_aNullBlock;

  @NonNull
  public JBlock nullBlock() {
    if (m_aNullBlock == null) {
      m_aNullBlock = new JLambdaBlock();
    }
    return m_aNullBlock;
  }

  /**
   * Construct a switch statement
   *
   * @param aTestExpr
   *                  expression
   */
  public JSwitchExpression(@NonNull final IJExpression aTestExpr) {
    m_aTestExpr = aTestExpr;
  }

  @NonNull
  public IJExpression test() {
    return m_aTestExpr;
  }

  @NonNull
  public Iterator<JCaseArrow<?>> cases() {
    return m_aCases.iterator();
  }

  @NonNull
  public JCaseStatic _case(@NonNull final IJExpression aLabel) {
    JCaseStatic c = new JCaseStatic(this, aLabel);
    m_aCases.add(c);
    return c;
  }

  @NonNull
  public JCasePattern _case(AbstractJType type, String varName) {
    JCasePattern c = new JCasePattern(this, type, varName);
    m_aCases.add(c);
    return c;
  }

  @NonNull
  public JCasePattern _case(JCodeModel jcm, Class<?> cl, String varName) {
    return _case(jcm.ref(cl), varName);
  }

  public JCaseSpecialSelector _null() {
    return new JCaseSpecialSelector(this, true);
  }

  public JCaseSpecialSelector _default() {
    return new JCaseSpecialSelector(this, false);
  }

  @Override
  public void generate(@NonNull IJFormatter f) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException();
  }

  @Override
  public void state(@NonNull IJFormatter f) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException();
  }

}
