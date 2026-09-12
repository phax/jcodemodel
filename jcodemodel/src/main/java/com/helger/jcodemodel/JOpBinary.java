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
import com.helger.jcodemodel.writer.settings.Parentheses.EParenthesesStrategy;

public class JOpBinary implements IJExpression
{

  /**
   * Binary operators, their textual representation and their precedence.<br>
   * Named <code>EBinaryOp</code> because <code>BinaryOperator</code> already exists in the JDK.
   */
  public static enum EBinaryOp
  {
    ADD ("+", EPrecedence.ADDITIVE),
    BITWISE_AND ("&", EPrecedence.BITWISE_AND),
    BITWISE_OR ("|", EPrecedence.BITWISE_OR),
    BITWISE_XOR ("^", EPrecedence.BITWISE_XOR),
    DIVIDE ("/", EPrecedence.MULTIPLICATIVE),
    EQUALS ("==", EPrecedence.EQUALITY),
    GREATER (">", EPrecedence.RELATIONAL),
    GREATER_EQUAL (">=", EPrecedence.RELATIONAL),
    INSTANCE_OF ("instanceof", EPrecedence.RELATIONAL),
    LOGICAL_AND ("&&", EPrecedence.LOGICAL_AND),
    LOGICAL_OR ("||", EPrecedence.LOGICAL_OR),
    LOWER ("<", EPrecedence.RELATIONAL),
    LOWER_EQUAL ("<=", EPrecedence.RELATIONAL),
    MODULUS ("%", EPrecedence.MULTIPLICATIVE),
    MULTIPLY ("*", EPrecedence.MULTIPLICATIVE),
    NOT_EQUALS ("!=", EPrecedence.EQUALITY),
    SHIFT_LEFT ("<<", EPrecedence.SHIFT),
    SHIFT_RIGHT (">>", EPrecedence.SHIFT),
    SHIFT_RIGHT_ZERO (">>>", EPrecedence.SHIFT),
    SUBTRACT ("-", EPrecedence.ADDITIVE);

    private final String m_sPrint;
    private final EPrecedence m_aPrecedence;

    EBinaryOp (@NonNull final String sPrint, @NonNull final EPrecedence aPrecedence)
    {
      m_sPrint = sPrint;
      m_aPrecedence = aPrecedence;
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
  }

  private final IJExpression m_aLeft;
  private final EBinaryOp m_aOperator;
  private final IJGenerable m_aRight;

  protected JOpBinary (@NonNull final IJExpression aLeft,
                       @NonNull final EBinaryOp aOperator,
                       @NonNull final IJGenerable aRight)
  {
    m_aLeft = ValueEnforcer.notNull (aLeft, "Left");
    m_aOperator = ValueEnforcer.notNull (aOperator, "Operator");
    m_aRight = ValueEnforcer.notNull (aRight, "Right");
  }

  @NonNull
  public IJExpression left ()
  {
    return m_aLeft;
  }

  @NonNull
  public String op ()
  {
    return m_aOperator.print ();
  }

  @NonNull
  public IJGenerable right ()
  {
    return m_aRight;
  }

  public void generate (@NonNull final IJFormatter f)
  {
    final EParenthesesStrategy eStrategy = f.settings ().parentheses.global;
    final EPrecedence aOp = m_aOperator.precedence ();
    final boolean bLeftParentheses = JOp.needsParentheses (eStrategy, aOp, m_aLeft.operatorPrecedence (), ESide.LEFT);
    // The right hand side of "instanceof" is a type, and a type may never be parenthesized
    final boolean bRightParentheses = !(m_aRight instanceof AbstractJType) &&
                                      JOp.needsParentheses (eStrategy,
                                                            aOp,
                                                            m_aRight.operatorPrecedence (),
                                                            ESide.RIGHT);

    if (bLeftParentheses)
      f.print ('(');
    f.generable (m_aLeft);
    if (bLeftParentheses)
      f.print (')');

    f.print (m_aOperator.print ());

    if (bRightParentheses)
      f.print ('(');
    f.generable (m_aRight);
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
    final JOpBinary rhs = (JOpBinary) o;
    return EqualsHelper.equals (m_aLeft, rhs.m_aLeft) &&
           EqualsHelper.equals (m_aOperator, rhs.m_aOperator) &&
           EqualsHelper.equals (m_aRight, rhs.m_aRight);
  }

  @Override
  public int hashCode ()
  {
    return getHashCode (this, m_aLeft, m_aOperator, m_aRight);
  }

  @Override
  @NonNull
  public EPrecedence operatorPrecedence ()
  {
    return m_aOperator.precedence ();
  }
}
