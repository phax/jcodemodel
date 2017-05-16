/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2017 Philip Helger + contributors
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

import static com.helger.jcodemodel.util.JCEqualsHelper.isEqual;
import static com.helger.jcodemodel.util.JCHashCodeGenerator.getHashCode;

import javax.annotation.Nonnull;

import com.helger.jcodemodel.util.JCValueEnforcer;

public class JOpTernary implements IJExpression
{
  private final IJExpression m_aExpr1;
  private final String m_sOperator1;
  private final IJExpression m_aExpr2;
  private final String m_sOperator2;
  private final IJExpression m_aExpr3;

  protected JOpTernary (@Nonnull final IJExpression aExpr1,
                        @Nonnull final String sOperator1,
                        @Nonnull final IJExpression aExpr2,
                        @Nonnull final String sOperator2,
                        @Nonnull final IJExpression aExpr3)
  {
    m_aExpr1 = JCValueEnforcer.notNull (aExpr1, "Expr1");
    m_sOperator1 = JCValueEnforcer.notNull (sOperator1, "Operator1");
    m_aExpr2 = JCValueEnforcer.notNull (aExpr2, "Expr2");
    m_sOperator2 = JCValueEnforcer.notNull (sOperator2, "Operator2");
    m_aExpr3 = JCValueEnforcer.notNull (aExpr3, "Expr3");
  }

  @Nonnull
  public IJExpression expr1 ()
  {
    return m_aExpr1;
  }

  @Nonnull
  public String op1 ()
  {
    return m_sOperator1;
  }

  @Nonnull
  public IJGenerable expr2 ()
  {
    return m_aExpr2;
  }

  @Nonnull
  public String op2 ()
  {
    return m_sOperator2;
  }

  @Nonnull
  public IJGenerable expr3 ()
  {
    return m_aExpr3;
  }

  public void generate (@Nonnull final JFormatter f)
  {
    f.print ('(')
     .generable (m_aExpr1)
     .print (m_sOperator1)
     .generable (m_aExpr2)
     .print (m_sOperator2)
     .generable (m_aExpr3)
     .print (')');
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || getClass () != o.getClass ())
      return false;
    final JOpTernary rhs = (JOpTernary) o;
    return isEqual (m_aExpr1, rhs.m_aExpr1) &&
           isEqual (m_sOperator1, rhs.m_sOperator1) &&
           isEqual (m_aExpr2, rhs.m_aExpr2) &&
           isEqual (m_sOperator2, rhs.m_sOperator2) &&
           isEqual (m_aExpr3, rhs.m_aExpr3);
  }

  @Override
  public int hashCode ()
  {
    return getHashCode (this, m_aExpr1, m_sOperator1, m_aExpr2, m_sOperator2, m_aExpr3);
  }
}
