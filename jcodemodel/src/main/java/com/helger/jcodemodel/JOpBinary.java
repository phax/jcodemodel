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
import com.helger.jcodemodel.JOp.Precedence;

public class JOpBinary implements IJExpression
{

  /// binary operators, their print string and their precedence
  ///
  /// use BinaryOp since BinaryOperator exists in the jdk.
  public static enum BinaryOp
  {
    ADD ("+", Precedence.ADDITIVE),
    BITWISE_AND ("&", Precedence.BITWISE_AND),
    BITWISE_OR ("|", Precedence.BITWISE_OR),
    BITWISE_XOR ("^", Precedence.BITWISE_XOR),
    DIVIDE ("/", Precedence.MULTIPLICATIVE),
    EQUALS ("==", Precedence.EQUALITY),
    GREATER (">", Precedence.RELATIONAL),
    GREATER_EQUAL (">=", Precedence.RELATIONAL),
    INSTANCE_OF ("instanceof", Precedence.RELATIONAL),
    LOGICAL_AND ("&&", Precedence.LOGICAL_AND),
    LOGICAL_OR ("||", Precedence.LOGICAL_OR),
    LOWER ("<", Precedence.RELATIONAL),
    LOWER_EQUAL ("<=", Precedence.RELATIONAL),
    MODULUS ("%", Precedence.MULTIPLICATIVE),
    MULTIPLY ("*", Precedence.MULTIPLICATIVE),
    NOT_EQUALS ("!=", Precedence.EQUALITY),
    SHIFT_LEFT ("<<", Precedence.SHIFT),
    SHIFT_RIGHT (">>", Precedence.SHIFT),
    SHIFT_RIGHT_ZERO (">>>", Precedence.SHIFT),
    SUBSTRACT ("-", Precedence.ADDITIVE);

    public final String print;
    public final Precedence precedence;

    BinaryOp (String print, Precedence precedence)
    {
      this.print = print;
      this.precedence = precedence;
    }
  }

  private final IJExpression m_aLeft;
  @NonNull
  private final BinaryOp m_aOperator;
  private final IJGenerable m_aRight;

  protected JOpBinary (@NonNull final IJExpression aLeft,
                       @NonNull final BinaryOp aOperator,
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
    return m_aOperator.print;
  }

  @NonNull
  public IJGenerable right ()
  {
    return m_aRight;
  }

  public void generate (@NonNull final IJFormatter f)
  {
    boolean leftParentheses = true, rightParentheses = true;
    switch(f.settings ().parentheses.global) {
      case ALWAYS ->
      {
        leftParentheses = true;
        rightParentheses = true;
      }
      case NOTOKEN ->
      {
        leftParentheses = m_aLeft.operatorPrecedence () != Precedence.TOKEN;
        rightParentheses = m_aRight.operatorPrecedence () != Precedence.TOKEN;
      }
      case REQUIRED ->
          {
            leftParentheses = m_aOperator.precedence.higherThan (m_aLeft.operatorPrecedence ());
            rightParentheses = m_aOperator.precedence.higherThan (m_aRight.operatorPrecedence ());
          }
          default -> throw new IllegalArgumentException ("Unexpected value: " + f.settings ().parentheses.global);
    }
    if (leftParentheses)
      f.print ('(');
    f.generable (m_aLeft);
    if (leftParentheses)
      f.print (')');
    f.print (m_aOperator.print);
    if (rightParentheses)
      f.print ('(');
    f.generable (m_aRight);
    if (rightParentheses)
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
  public Precedence operatorPrecedence ()
  {
    return m_aOperator.precedence;
  }
}
