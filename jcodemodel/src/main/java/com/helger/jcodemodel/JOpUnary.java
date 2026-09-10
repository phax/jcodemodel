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

public class JOpUnary implements IJExpression
{
  /**
   * Unary operators, their textual representation, their precedence and whether they are printed
   * before or after their operand.
   */
  public enum EUnaryOp
  {
    BITWISE_NOT ("~", EPrecedence.UNARY, true),
    LOGICAL_NOT ("!", EPrecedence.UNARY, true),
    MINUS ("-", EPrecedence.UNARY, true),
    POST_DECR ("--", EPrecedence.POSTFIX, false),
    POST_INCR ("++", EPrecedence.POSTFIX, false),
    PRE_DECR ("--", EPrecedence.UNARY, true),
    PRE_INCR ("++", EPrecedence.UNARY, true);

    private final String m_sPrint;
    private final EPrecedence m_aPrecedence;
    private final boolean m_bPrefix;

    EUnaryOp (@NonNull final String sPrint, @NonNull final EPrecedence aPrecedence, final boolean bPrefix)
    {
      m_sPrint = sPrint;
      m_aPrecedence = aPrecedence;
      m_bPrefix = bPrefix;
    }

    /**
     * @return The textual representation of this operator. Neither <code>null</code> nor empty.
     */
    @NonNull
    public String print ()
    {
      return m_sPrint;
    }

    /**
     * @return The binding strength of this operator. Never <code>null</code>.
     */
    @NonNull
    public EPrecedence precedence ()
    {
      return m_aPrecedence;
    }

    /**
     * @return <code>true</code> if the operator is printed before its operand.
     */
    public boolean prefix ()
    {
      return m_bPrefix;
    }
  }

  private final EUnaryOp m_eOperator;
  private final IJExpression m_aExpr;

  /**
   * Constructor for operator before expression
   *
   * @param eOperator
   *        operator
   * @param aExpr
   *        expression
   */
  protected JOpUnary (@NonNull final EUnaryOp eOperator, @NonNull final IJExpression aExpr)
  {
    m_eOperator = ValueEnforcer.notNull (eOperator, "Operator");
    m_aExpr = ValueEnforcer.notNull (aExpr, "Expression");
  }

  @NonNull
  public String op ()
  {
    return m_eOperator.print ();
  }

  @NonNull
  public IJExpression expr ()
  {
    return m_aExpr;
  }

  /**
   * @return <code>true</code> if the operator comes first, <code>false</code> if the operator comes
   *         last
   */
  public boolean opFirst ()
  {
    return m_eOperator.prefix ();
  }

  public void generate (@NonNull final IJFormatter f)
  {
    // A prefix operator has its operand on the right, a postfix operator on the left
    final ESide eOperandSide = m_eOperator.prefix () ? ESide.RIGHT : ESide.LEFT;
    final boolean bParentheses = JOp.needsParentheses (f.settings ().parentheses.global,
                                                       m_eOperator.precedence (),
                                                       m_aExpr.operatorPrecedence (),
                                                       eOperandSide);

    if (m_eOperator.prefix ())
      f.print (m_eOperator.print ());
    if (bParentheses)
      f.print ('(');
    f.generable (m_aExpr);
    if (bParentheses)
      f.print (')');
    if (!m_eOperator.prefix ())
    {
      // A postfix operator stays attached to its operand: "a++" and not "a ++"
      f.printNoSpace (m_eOperator.print ());
    }
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || getClass () != o.getClass ())
      return false;
    final JOpUnary rhs = (JOpUnary) o;
    return EqualsHelper.equals (m_eOperator, rhs.m_eOperator) && EqualsHelper.equals (m_aExpr, rhs.m_aExpr);
  }

  @Override
  public int hashCode ()
  {
    return getHashCode (this, m_eOperator, m_aExpr);
  }

  @Override
  @NonNull
  public EPrecedence operatorPrecedence ()
  {
    return m_eOperator.precedence ();
  }
}
