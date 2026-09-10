/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2026 Philip Helger + contributors
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package com.helger.jcodemodel;

import static com.helger.jcodemodel.util.JCHashCodeGenerator.getHashCode;

import org.jspecify.annotations.NonNull;

import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.jcodemodel.JOp.EPrecedence;
import com.helger.jcodemodel.JOp.ESide;

public class JOpTernary implements IJExpression
{

  /**
   * Ternary operators and the textual representation of their two separators.
   */
  public static enum ETernaryOp
  {
    TERN_COND ("?", ":", EPrecedence.TERNARY);

    private final String m_sLeftPrint;
    private final String m_sRightPrint;
    private final EPrecedence m_aPrecedence;

    ETernaryOp (@NonNull final String sLeftPrint,
                @NonNull final String sRightPrint,
                @NonNull final EPrecedence aPrecedence)
    {
      m_sLeftPrint = sLeftPrint;
      m_sRightPrint = sRightPrint;
      m_aPrecedence = aPrecedence;
    }

    /**
     * @return The separator printed between the first and the second operand. Neither
     *         <code>null</code> nor empty.
     */
    @NonNull
    public String leftPrint ()
    {
      return m_sLeftPrint;
    }

    /**
     * @return The separator printed between the second and the third operand. Neither
     *         <code>null</code> nor empty.
     */
    @NonNull
    public String rightPrint ()
    {
      return m_sRightPrint;
    }

    /**
     * @return The binding strength of this operator. Never <code>null</code>.
     */
    @NonNull
    public EPrecedence precedence ()
    {
      return m_aPrecedence;
    }
  }

  private final ETernaryOp m_aOperator;
  private final IJExpression m_aExpr1;
  private final IJExpression m_aExpr2;
  private final IJExpression m_aExpr3;

  protected JOpTernary (@NonNull final ETernaryOp aOperator,
                        @NonNull final IJExpression aExpr1,
                        @NonNull final IJExpression aExpr2,
                        @NonNull final IJExpression aExpr3)
  {
    m_aOperator = ValueEnforcer.notNull (aOperator, "Operator");
    m_aExpr1 = ValueEnforcer.notNull (aExpr1, "Expr1");
    m_aExpr2 = ValueEnforcer.notNull (aExpr2, "Expr2");
    m_aExpr3 = ValueEnforcer.notNull (aExpr3, "Expr3");
  }

  @NonNull
  public IJExpression expr1 ()
  {
    return m_aExpr1;
  }

  @NonNull
  public String op1 ()
  {
    return m_aOperator.leftPrint ();
  }

  @NonNull
  public IJGenerable expr2 ()
  {
    return m_aExpr2;
  }

  @NonNull
  public String op2 ()
  {
    return m_aOperator.rightPrint ();
  }

  @NonNull
  public IJGenerable expr3 ()
  {
    return m_aExpr3;
  }

  public void generate (@NonNull final IJFormatter f)
  {
    final EPrecedence aOp = m_aOperator.precedence ();
    // Only the condition can become ambiguous. The second operand is enclosed by "?" and ":" and
    // the third one is the last thing in the expression, so both accept any expression - see JLS
    // 15.25.
    final boolean bLeftParentheses = JOp.needsParentheses (f.settings ().parentheses.global,
                                                           aOp,
                                                           m_aExpr1.operatorPrecedence (),
                                                           ESide.LEFT);
    final boolean bMidParentheses = JOp.needsParentheses (f.settings ().parentheses.global,
                                                          aOp,
                                                          m_aExpr2.operatorPrecedence (),
                                                          ESide.NONE);
    final boolean bRightParentheses = JOp.needsParentheses (f.settings ().parentheses.global,
                                                            aOp,
                                                            m_aExpr3.operatorPrecedence (),
                                                            ESide.RIGHT);

    if (bLeftParentheses)
      f.print ('(');
    f.generable (m_aExpr1);
    if (bLeftParentheses)
      f.print (')');

    f.print (m_aOperator.leftPrint ());

    if (bMidParentheses)
      f.print ('(');
    f.generable (m_aExpr2);
    if (bMidParentheses)
      f.print (')');

    f.print (m_aOperator.rightPrint ());

    if (bRightParentheses)
      f.print ('(');
    f.generable (m_aExpr3);
    if (bRightParentheses)
      f.print (')');
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || getClass () != o.getClass ())
      return false;
    final JOpTernary rhs = (JOpTernary) o;
    return EqualsHelper.equals (m_aOperator, rhs.m_aOperator) &&
           EqualsHelper.equals (m_aExpr1, rhs.m_aExpr1) &&
           EqualsHelper.equals (m_aExpr2, rhs.m_aExpr2) &&
           EqualsHelper.equals (m_aExpr3, rhs.m_aExpr3);
  }

  @Override
  public int hashCode ()
  {
    return getHashCode (this, m_aExpr1, m_aOperator, m_aExpr2, m_aExpr3);
  }

  @Override
  @NonNull
  public EPrecedence operatorPrecedence ()
  {
    return m_aOperator.precedence ();
  }
}
