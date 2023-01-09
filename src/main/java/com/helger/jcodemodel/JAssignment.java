/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2020 Philip Helger + contributors
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

/**
 * Assignment statements, which are also expressions.
 */
public class JAssignment implements IJExpressionStatement
{
  private final IJAssignmentTarget m_aLhs;
  private final String m_sOperator;
  private final IJExpression m_aRhs;

  /**
   * Constructor for "=" operator
   *
   * @param lhs
   *        left
   * @param rhs
   *        right
   */
  protected JAssignment (@Nonnull final IJAssignmentTarget lhs, @Nonnull final IJExpression rhs)
  {
    this (lhs, rhs, "");
  }

  /**
   * Constructor for <code>op + "="</code> operator
   *
   * @param lhs
   *        left
   * @param rhs
   *        right
   * @param sOperator
   *        additional operator
   */
  protected JAssignment (@Nonnull final IJAssignmentTarget lhs,
                         @Nonnull final IJExpression rhs,
                         @Nonnull final String sOperator)
  {
    m_aLhs = lhs;
    m_aRhs = rhs;
    m_sOperator = sOperator;
  }

  @Nonnull
  public IJAssignmentTarget lhs ()
  {
    return m_aLhs;
  }

  @Nonnull
  public IJExpression rhs ()
  {
    return m_aRhs;
  }

  /**
   * @return The additional operator (without the "=")
   */
  @Nonnull
  public String op ()
  {
    return m_sOperator;
  }

  /**
   * @return The additional operator (with the "=")
   */
  @Nonnull
  public String opFull ()
  {
    return m_sOperator + '=';
  }

  public void generate (@Nonnull final IJFormatter f)
  {
    f.generable (m_aLhs).print (opFull ()).generable (m_aRhs);
  }

  public void state (@Nonnull final IJFormatter f)
  {
    f.generable (this).print (';').newline ();
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || getClass () != o.getClass ())
      return false;
    final JAssignment rhs = (JAssignment) o;
    return isEqual (m_aLhs, rhs.m_aLhs) && isEqual (m_aRhs, rhs.m_aRhs) && isEqual (m_sOperator, rhs.m_sOperator);
  }

  @Override
  public int hashCode ()
  {
    return getHashCode (this, m_aLhs, m_aRhs, m_sOperator);
  }
}
