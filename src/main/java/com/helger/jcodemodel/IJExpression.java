/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2015 Philip Helger
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

import com.helger.jcodemodel.optimize.ExpressionContainer;

import javax.annotation.Nonnull;

/**
 * A Java expression.
 * <p>
 * Unlike most of CodeModel, JExpressions are built bottom-up ( meaning you
 * start from leaves and then gradually build complicated expressions by
 * combining them.)
 * <p>
 * {@link IJExpression} defines a series of composer methods, which returns a
 * complicated expression (by often taking other {@link IJExpression}s as
 * parameters. For example, you can build "5+2" by
 * <tt>JExpr.lit(5).add(JExpr.lit(2))</tt>
 */
public interface IJExpression extends IJGenerable, ExpressionContainer
{
  /**
   * @return <code>-[this]" from "[this]</code>.
   */
  @Nonnull
  IJExpression minus ();

  /**
   * @return <code>![this]" from "[this]</code>.
   */
  @Nonnull
  IJExpression not ();

  /**
   * @return <code>~[this]" from "[this]</code>.
   */
  @Nonnull
  IJExpression complement ();

  /**
   * @return <code>[this]++" from "[this]</code>.
   */
  @Nonnull
  IJExpression incr ();

  /**
   * @return <code>++[this]" from "[this]</code>.
   */
  @Nonnull
  IJExpression preincr ();

  /**
   * @return <code>[this]--" from "[this]</code>.
   */
  @Nonnull
  IJExpression decr ();

  /**
   * @return <code>--[this]" from "[this]</code>.
   */
  @Nonnull
  IJExpression predecr ();

  /**
   * @return <code>[this]+[right]</code>.
   */
  @Nonnull
  IJExpression plus (@Nonnull IJExpression right);

  /**
   * @return <code>[this]+[right]</code>.
   */
  @Nonnull
  IJExpression plus (double right);

  /**
   * @return <code>[this]+[right]</code>.
   */
  @Nonnull
  IJExpression plus (float right);

  /**
   * @return <code>[this]+[right]</code>.
   */
  @Nonnull
  IJExpression plus (int right);

  /**
   * @return <code>[this]+[right]</code>.
   */
  @Nonnull
  IJExpression plus (long right);

  /**
   * @return <code>[this]+[right]</code>.
   */
  @Nonnull
  IJExpression plus (@Nonnull String right);

  /**
   * @return <code>[this]-[right]</code>.
   */
  @Nonnull
  IJExpression minus (@Nonnull IJExpression right);

  /**
   * @return <code>[this]-[right]</code>.
   */
  @Nonnull
  IJExpression minus (double right);

  /**
   * @return <code>[this]-[right]</code>.
   */
  @Nonnull
  IJExpression minus (float right);

  /**
   * @return <code>[this]-[right]</code>.
   */
  @Nonnull
  IJExpression minus (int right);

  /**
   * @return <code>[this]-[right]</code>.
   */
  @Nonnull
  IJExpression minus (long right);

  /**
   * @return <code>[this]*[right]</code>.
   */
  @Nonnull
  IJExpression mul (@Nonnull IJExpression right);

  /**
   * @return <code>[this]*[right]</code>.
   */
  @Nonnull
  IJExpression mul (double right);

  /**
   * @return <code>[this]*[right]</code>.
   */
  @Nonnull
  IJExpression mul (float right);

  /**
   * @return <code>[this]*[right]</code>.
   */
  @Nonnull
  IJExpression mul (int right);

  /**
   * @return <code>[this]*[right]</code>.
   */
  @Nonnull
  IJExpression mul (long right);

  /**
   * @return <code>[this]/[right]</code>.
   */
  @Nonnull
  IJExpression div (@Nonnull IJExpression right);

  /**
   * @return <code>[this]/[right]</code>.
   */
  @Nonnull
  IJExpression div (double right);

  /**
   * @return <code>[this]/[right]</code>.
   */
  @Nonnull
  IJExpression div (float right);

  /**
   * @return <code>[this]/[right]</code>.
   */
  @Nonnull
  IJExpression div (int right);

  /**
   * @return <code>[this]/[right]</code>.
   */
  @Nonnull
  IJExpression div (long right);

  /**
   * @return <code>[this]%[right]</code>.
   */
  @Nonnull
  IJExpression mod (@Nonnull IJExpression right);

  /**
   * @return <code>[this]&lt;&lt;[right]</code>.
   */
  @Nonnull
  IJExpression shl (@Nonnull IJExpression right);

  /**
   * @return <code>[this]&lt;&lt;[right]</code>.
   */
  @Nonnull
  IJExpression shl (int right);

  /**
   * @return <code>[this] &gt;&gt; [right]</code>.
   */
  @Nonnull
  IJExpression shr (@Nonnull IJExpression right);

  /**
   * @return <code>[this] &gt;&gt; [right]</code>.
   */
  @Nonnull
  IJExpression shr (int right);

  /**
   * @return <code>[this] &gt;&gt;&gt; [right]</code>.
   */
  @Nonnull
  IJExpression shrz (@Nonnull IJExpression right);

  /**
   * @return <code>[this] &gt;&gt;&gt; [right]</code>.
   */
  @Nonnull
  IJExpression shrz (int right);

  /**
   * Bit-wise AND '&amp;'.
   *
   * @return <code>[this] &amp; [right]</code>.
   */
  @Nonnull
  IJExpression band (@Nonnull IJExpression right);

  /**
   * Bit-wise OR '|'.
   *
   * @return <code>[this] | [right]</code>.
   */
  @Nonnull
  IJExpression bor (@Nonnull IJExpression right);

  /**
   * Logical AND '&amp;&amp;'.
   *
   * @return <code>[this] &amp;&amp; [right]</code>.
   */
  @Nonnull
  IJExpression cand (@Nonnull IJExpression right);

  /**
   * Logical OR '||'.
   *
   * @return <code>[this] || [right]</code>.
   */
  @Nonnull
  IJExpression cor (@Nonnull IJExpression right);

  /**
   * @return <code>[this] ^ [right]</code>.
   */
  @Nonnull
  IJExpression xor (@Nonnull IJExpression right);

  /**
   * @return <code>[this] &lt; [right]</code>.
   */
  @Nonnull
  IJExpression lt (@Nonnull IJExpression right);

  /**
   * @return <code>[this] &lt; 0</code>.
   */
  @Nonnull
  IJExpression lt0 ();

  /**
   * @return <code>[this] &lt;= [right]</code>.
   */
  @Nonnull
  IJExpression lte (@Nonnull IJExpression right);

  /**
   * @return <code>[this] &lt;= 0</code>.
   */
  @Nonnull
  IJExpression lte0 ();

  /**
   * @return <code>[this] &gt; [right]</code>.
   */
  @Nonnull
  IJExpression gt (@Nonnull IJExpression right);

  /**
   * @return <code>[this] &gt; 0</code>.
   */
  @Nonnull
  IJExpression gt0 ();

  /**
   * @return <code>[this] &gt;= [right]</code>.
   */
  @Nonnull
  IJExpression gte (@Nonnull IJExpression right);

  /**
   * @return <code>[this] &gt;= 0</code>.
   */
  @Nonnull
  IJExpression gte0 ();

  /**
   * Equals
   *
   * @param right
   *        expression to compare to
   * @return <code><em>expr</em> == <em>right</em></code>
   */
  @Nonnull
  IJExpression eq (@Nonnull IJExpression right);

  /**
   * Shortcut for <code>eq (JExpr._null ())</code>
   *
   * @return <code><em>expr</em> == null</code>
   */
  @Nonnull
  IJExpression eqNull ();

  /**
   * Shortcut for <code>eq (JExpr.lit (0))</code>
   *
   * @return <code><em>expr</em> == 0</code>
   */
  @Nonnull
  IJExpression eq0 ();

  /**
   * Not equals
   *
   * @param right
   *        expression to compare to
   * @return <code><em>expr</em> != <em>right</em></code>
   */
  @Nonnull
  IJExpression ne (@Nonnull IJExpression right);

  /**
   * Shortcut for <code>ne (JExpr._null ())</code>
   *
   * @return Never <code><em>expr</em> != null</code>
   */
  @Nonnull
  IJExpression neNull ();

  /**
   * Shortcut for <code>ne (JExpr.lit (0))</code>
   *
   * @return Never <code><em>expr</em> != 0</code>
   */
  @Nonnull
  IJExpression ne0 ();

  /**
   * @return <code>[this] instanceof [right]</code>.
   */
  @Nonnull
  IJExpression _instanceof (@Nonnull AbstractJType right);

  /**
   * @return <code>[this].[method]</code>. Arguments shall be added to the
   *         returned {@link JInvocation} object.
   */
  @Nonnull
  JInvocation invoke (@Nonnull JMethod method);

  /**
   * @return <code>[this].[method]</code>. Arguments shall be added to the
   *         returned {@link JInvocation} object.
   */
  @Nonnull
  JInvocation invoke (@Nonnull String method);

  @Nonnull
  JFieldRef ref (@Nonnull JVar field);

  @Nonnull
  JFieldRef ref (@Nonnull String field);

  /**
   * @param index
   *        array index
   * @return <code>[this] [ [index] ]</code>
   */
  @Nonnull
  JArrayCompRef component (@Nonnull IJExpression index);

  /**
   * @param index
   *        array index
   * @return <code>[this] [ [index] ]</code>
   */
  @Nonnull
  JArrayCompRef component (int index);

  /**
   * @return <code>[this] [0]</code>
   */
  @Nonnull
  JArrayCompRef component0 ();

  /**
   * Returns the unwrapped expression instance. Most implementations returns
   * itself.
   *
   * @return the unwrapped expression
   */
  IJExpression unwrapped ();

  /**
   * Two instances of <code>IJExpression</code> should be considered equal if
   * their unwrapped forms represent expressions eligible for common
   * subexpression elimination.
   */
  boolean equals (Object o);

  String expressionName();

  AbstractJType expressionType();
}
