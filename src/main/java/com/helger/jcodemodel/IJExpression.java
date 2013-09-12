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
   * Returns "[this]--" from "[this]".
   */
  @Nonnull
  IJExpression decr ();

  /**
   * Returns "[this]+[right]"
   */
  @Nonnull
  IJExpression plus (IJExpression right);

  /**
   * Returns "[this]-[right]"
   */
  @Nonnull
  IJExpression minus (IJExpression right);

  /**
   * Returns "[this]*[right]"
   */
  @Nonnull
  IJExpression mul (IJExpression right);

  /**
   * Returns "[this]/[right]"
   */
  @Nonnull
  IJExpression div (IJExpression right);

  /**
   * Returns "[this]%[right]"
   */
  @Nonnull
  IJExpression mod (IJExpression right);

  /**
   * Returns "[this]&lt;&lt;[right]"
   */
  @Nonnull
  IJExpression shl (IJExpression right);

  /**
   * Returns "[this]>>[right]"
   */
  @Nonnull
  IJExpression shr (IJExpression right);

  /**
   * Returns "[this]>>>[right]"
   */
  @Nonnull
  IJExpression shrz (IJExpression right);

  /** Bit-wise AND '&amp;'. */
  @Nonnull
  IJExpression band (IJExpression right);

  /** Bit-wise OR '|'. */
  @Nonnull
  IJExpression bor (IJExpression right);

  /** Logical AND '&amp;&amp;'. */
  @Nonnull
  IJExpression cand (IJExpression right);

  /** Logical OR '||'. */
  @Nonnull
  IJExpression cor (IJExpression right);

  @Nonnull
  IJExpression xor (IJExpression right);

  @Nonnull
  IJExpression lt (IJExpression right);

  @Nonnull
  IJExpression lte (IJExpression right);

  @Nonnull
  IJExpression gt (IJExpression right);

  @Nonnull
  IJExpression gte (IJExpression right);

  @Nonnull
  IJExpression eq (IJExpression right);

  @Nonnull
  IJExpression ne (IJExpression right);

  /**
   * Returns "[this] instanceof [right]"
   */
  @Nonnull
  IJExpression _instanceof (AbstractJType right);

  /**
   * Returns "[this].[method]". Arguments shall be added to the returned
   * {@link JInvocation} object.
   */
  @Nonnull
  JInvocation invoke (JMethod method);

  /**
   * Returns "[this].[method]". Arguments shall be added to the returned
   * {@link JInvocation} object.
   */
  @Nonnull
  JInvocation invoke (String method);

  @Nonnull
  JFieldRef ref (JVar field);

  @Nonnull
  JFieldRef ref (String field);

  @Nonnull
  JArrayCompRef component (IJExpression index);
}
