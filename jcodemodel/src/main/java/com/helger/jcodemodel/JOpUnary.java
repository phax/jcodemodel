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

public class JOpUnary implements IJExpression
{
  /// unary operators, their print string and their precedence
  public static enum UnaryOp
  {
    BITWISE_NOT ("~", Precedence.UNARY, true),
    LOGICAL_NOT ("!", Precedence.UNARY, true),
    MINUS ("-", Precedence.UNARY, true),
    POST_DECR ("--", Precedence.POSTFIX, false),
    POST_INCR ("++", Precedence.POSTFIX, false),
    PRE_DECR ("--", Precedence.POSTFIX, true),
    PRE_INCR ("++", Precedence.POSTFIX, true),
    ;

    public final String print;
    public final Precedence precedence;
    public final boolean prefix;

    UnaryOp (String print, Precedence precedence, boolean prefix)
    {
      this.print = print;
      this.precedence = precedence;
      this.prefix = prefix;
    }

  }

  private final UnaryOp m_aOperator;
  private final IJExpression m_aExpr;

  /**
   * Constructor for operator before expression
   *
   * @param sOperator
   *        operator
   * @param aExpr
   *        expression
   */
  protected JOpUnary (@NonNull final UnaryOp aOperator, @NonNull final IJExpression aExpr)
  {
    m_aOperator = ValueEnforcer.notNull (aOperator, "Operator");
    m_aExpr = ValueEnforcer.notNull (aExpr, "Expression");
  }

  @NonNull
  public String op ()
  {
    return m_aOperator.print;
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
    return m_aOperator.prefix;
  }

  public void generate (@NonNull final IJFormatter f)
  {
    boolean parentheses = true;
    switch (f.settings ().parentheses.global)
    {
      case ALWAYS ->
      {
        parentheses = true;
      }
      case NOTOKEN ->
      {
        parentheses = m_aExpr.operatorPrecedence () != Precedence.TOKEN;
      }
      case REQUIRED ->
      {
        parentheses = m_aOperator.precedence.higherThan (m_aExpr.operatorPrecedence ());
      }
      default -> throw new IllegalArgumentException ("Unexpected value: " + f.settings ().parentheses.global);
    }
    if (m_aOperator.prefix)
      f.print (m_aOperator.print);
    if (parentheses)
      f.print ('(');
    f.generable (m_aExpr);
    if (parentheses)
      f.print (')');
    if (!m_aOperator.prefix)
      f.print (m_aOperator.print);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || getClass () != o.getClass ())
      return false;
    final JOpUnary rhs = (JOpUnary) o;
    return EqualsHelper.equals (m_aOperator, rhs.m_aOperator) && EqualsHelper.equals (m_aExpr, rhs.m_aExpr);
  }

  @Override
  public int hashCode ()
  {
    return getHashCode (this, m_aOperator, m_aExpr);
  }

  @Override
  public Precedence operatorPrecedence ()
  {
    return m_aOperator.precedence;
  }
}
