/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2025 Philip Helger + contributors
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

import org.jspecify.annotations.NonNull;

/**
 * Marker interface for code components that can be placed to the left of '=' in
 * an assignment. A left hand value can always be a right hand value, so this
 * interface derives from {@link IJExpression}.
 */
public interface IJAssignmentTarget extends IJExpression
{
  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this = <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assign (@NonNull final IJExpression rhs)
  {
    return JExpr.assign (this, rhs);
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this = <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assign (final boolean rhs)
  {
    return assign (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this = <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assign (final char rhs)
  {
    return assign (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this = <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assign (final double rhs)
  {
    return assign (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this = <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assign (final float rhs)
  {
    return assign (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this = <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assign (final int rhs)
  {
    return assign (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this = <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assign (final long rhs)
  {
    return assign (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this = <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assign (@NonNull final String rhs)
  {
    return assign (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this += <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assignPlus (@NonNull final IJExpression rhs)
  {
    return JExpr.assignPlus (this, rhs);
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this += <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assignPlus (@NonNull final char rhs)
  {
    return assignPlus (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this += <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assignPlus (@NonNull final double rhs)
  {
    return assignPlus (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this += <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assignPlus (@NonNull final float rhs)
  {
    return assignPlus (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this += <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assignPlus (@NonNull final int rhs)
  {
    return assignPlus (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this += <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assignPlus (@NonNull final long rhs)
  {
    return assignPlus (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this += <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assignPlus (@NonNull final String rhs)
  {
    return assignPlus (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this -= <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assignMinus (@NonNull final IJExpression rhs)
  {
    return JExpr.assignMinus (this, rhs);
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this -= <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assignMinus (final double rhs)
  {
    return assignMinus (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this -= <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assignMinus (final float rhs)
  {
    return assignMinus (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this -= <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assignMinus (final int rhs)
  {
    return assignMinus (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this -= <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assignMinus (final long rhs)
  {
    return assignMinus (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this *= <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assignTimes (@NonNull final IJExpression rhs)
  {
    return JExpr.assignTimes (this, rhs);
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this *= <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assignTimes (final double rhs)
  {
    return assignTimes (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this *= <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assignTimes (final float rhs)
  {
    return assignTimes (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this *= <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assignTimes (final int rhs)
  {
    return assignTimes (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this *= <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assignTimes (final long rhs)
  {
    return assignTimes (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this /= <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assignDivide (@NonNull final IJExpression rhs)
  {
    return JExpr.assignDivide (this, rhs);
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this /= <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assignDivide (final double rhs)
  {
    return assignDivide (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this /= <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assignDivide (final float rhs)
  {
    return assignDivide (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this /= <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assignDivide (final int rhs)
  {
    return assignDivide (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this /= <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assignDivide (final long rhs)
  {
    return assignDivide (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this &lt;&lt;= <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assignShl (@NonNull final IJExpression rhs)
  {
    return JExpr.assignShl (this, rhs);
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this &lt;&lt;= <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assignShl (final int rhs)
  {
    return assignShl (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this &gt;&gt;= <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assignShr (@NonNull final IJExpression rhs)
  {
    return JExpr.assignShr (this, rhs);
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this &gt;&gt;= <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assignShr (final int rhs)
  {
    return assignShr (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this &gt;&gt;&gt;= <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assignShrz (@NonNull final IJExpression rhs)
  {
    return JExpr.assignShrz (this, rhs);
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this &gt;&gt;&gt;= <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assignShrz (final int rhs)
  {
    return assignShrz (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this &amp;= <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assignBand (@NonNull final IJExpression rhs)
  {
    return JExpr.assignBand (this, rhs);
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this |= <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assignBor (@NonNull final IJExpression rhs)
  {
    return JExpr.assignBor (this, rhs);
  }

  /**
   * @param rhs
   *        Expression to assign
   * @return <code>this ^= <em>rhs</em></code>
   */
  @NonNull
  default JAssignment assignXor (@NonNull final IJExpression rhs)
  {
    return JExpr.assignXor (this, rhs);
  }
}
