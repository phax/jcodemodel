/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
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

import javax.annotation.Nonnull;

/**
 * A Java expression.
 * <p>
 * Unlike most of CodeModel, JExpressions are built bottom-up ( meaning you
 * start from leaves and then gradually build compliated expressions by
 * combining them.)
 * <p>
 * {@link IJExpression} defines a series of composer methods, which returns a
 * complicated expression (by often taking other {@link IJExpression}s as
 * parameters. For example, you can build "5+2" by
 * <tt>JExpr.lit(5).add(JExpr.lit(2))</tt>
 */
public interface IJExpression extends IJGenerable
{
  /**
   * Returns "-[this]" from "[this]".
   */
  @Nonnull
  IJExpression minus ();

  /**
   * Returns "![this]" from "[this]".
   */
  @Nonnull
  IJExpression not ();

  /**
   * Returns "~[this]" from "[this]".
   */
  @Nonnull
  IJExpression complement ();

  /**
   * Returns "[this]++" from "[this]".
   */
  @Nonnull
  IJExpression incr ();

  /**
   * Returns "++[this]" from "[this]".
   */
  @Nonnull
  IJExpression preincr ();

  /**
   * Returns "[this]--" from "[this]".
   */
  @Nonnull
  IJExpression decr ();

  /**
   * Returns "--[this]" from "[this]".
   */
  @Nonnull
  IJExpression predecr ();

  /**
   * Returns "[this]+[right]"
   */
  @Nonnull
  IJExpression plus (@Nonnull IJExpression right);

  /**
   * Returns "[this]+[right]"
   */
  @Nonnull
  IJExpression plus (double right);

  /**
   * Returns "[this]+[right]"
   */
  @Nonnull
  IJExpression plus (float right);

  /**
   * Returns "[this]+[right]"
   */
  @Nonnull
  IJExpression plus (int right);

  /**
   * Returns "[this]+[right]"
   */
  @Nonnull
  IJExpression plus (long right);

  /**
   * Returns "[this]+[right]"
   */
  @Nonnull
  IJExpression plus (@Nonnull String right);

  /**
   * Returns "[this]-[right]"
   */
  @Nonnull
  IJExpression minus (@Nonnull IJExpression right);

  /**
   * Returns "[this]-[right]"
   */
  @Nonnull
  IJExpression minus (double right);

  /**
   * Returns "[this]-[right]"
   */
  @Nonnull
  IJExpression minus (float right);

  /**
   * Returns "[this]-[right]"
   */
  @Nonnull
  IJExpression minus (int right);

  /**
   * Returns "[this]-[right]"
   */
  @Nonnull
  IJExpression minus (long right);

  /**
   * Returns "[this]*[right]"
   */
  @Nonnull
  IJExpression mul (@Nonnull IJExpression right);

  /**
   * Returns "[this]*[right]"
   */
  @Nonnull
  IJExpression mul (double right);

  /**
   * Returns "[this]*[right]"
   */
  @Nonnull
  IJExpression mul (float right);

  /**
   * Returns "[this]*[right]"
   */
  @Nonnull
  IJExpression mul (int right);

  /**
   * Returns "[this]*[right]"
   */
  @Nonnull
  IJExpression mul (long right);

  /**
   * Returns "[this]/[right]"
   */
  @Nonnull
  IJExpression div (@Nonnull IJExpression right);

  /**
   * Returns "[this]/[right]"
   */
  @Nonnull
  IJExpression div (double right);

  /**
   * Returns "[this]/[right]"
   */
  @Nonnull
  IJExpression div (float right);

  /**
   * Returns "[this]/[right]"
   */
  @Nonnull
  IJExpression div (int right);

  /**
   * Returns "[this]/[right]"
   */
  @Nonnull
  IJExpression div (long right);

  /**
   * Returns "[this]%[right]"
   */
  @Nonnull
  IJExpression mod (@Nonnull IJExpression right);

  /**
   * Returns "[this]&lt;&lt;[right]"
   */
  @Nonnull
  IJExpression shl (@Nonnull IJExpression right);

  /**
   * Returns "[this]>>[right]"
   */
  @Nonnull
  IJExpression shr (@Nonnull IJExpression right);

  /**
   * Returns "[this]>>>[right]"
   */
  @Nonnull
  IJExpression shrz (@Nonnull IJExpression right);

  /** Bit-wise AND '&amp;'. */
  @Nonnull
  IJExpression band (@Nonnull IJExpression right);

  /** Bit-wise OR '|'. */
  @Nonnull
  IJExpression bor (@Nonnull IJExpression right);

  /** Logical AND '&amp;&amp;'. */
  @Nonnull
  IJExpression cand (@Nonnull IJExpression right);

  /** Logical OR '||'. */
  @Nonnull
  IJExpression cor (@Nonnull IJExpression right);

  @Nonnull
  IJExpression xor (@Nonnull IJExpression right);

  @Nonnull
  IJExpression lt (@Nonnull IJExpression right);

  @Nonnull
  IJExpression lte (@Nonnull IJExpression right);

  @Nonnull
  IJExpression gt (@Nonnull IJExpression right);

  @Nonnull
  IJExpression gte (@Nonnull IJExpression right);

  @Nonnull
  IJExpression eq (@Nonnull IJExpression right);

  @Nonnull
  IJExpression ne (@Nonnull IJExpression right);

  /**
   * Returns "[this] instanceof [right]"
   */
  @Nonnull
  IJExpression _instanceof (@Nonnull AbstractJType right);

  /**
   * Returns "[this].[method]". Arguments shall be added to the returned
   * {@link JInvocation} object.
   */
  @Nonnull
  JInvocation invoke (@Nonnull JMethod method);

  /**
   * Returns "[this].[method]". Arguments shall be added to the returned
   * {@link JInvocation} object.
   */
  @Nonnull
  JInvocation invoke (@Nonnull String method);

  @Nonnull
  JFieldRef ref (@Nonnull JVar field);

  @Nonnull
  JFieldRef ref (@Nonnull String field);

  @Nonnull
  JArrayCompRef component (@Nonnull IJExpression index);

  @Nonnull
  JArrayCompRef component (int index);

  @Nonnull
  JArrayCompRef component (long index);

  @Nonnull
  JArrayCompRef component0 ();
}
