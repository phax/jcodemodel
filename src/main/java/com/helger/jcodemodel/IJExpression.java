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
public interface IJExpression extends IJGenerable
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
   * @param right
   *        value to add
   * @return <code>[this]+[right]</code>.
   */
  @Nonnull
  IJExpression plus (@Nonnull IJExpression right);

  /**
   * @param right
   *        value to add
   * @return <code>[this]+[right]</code>.
   */
  @Nonnull
  IJExpression plus (double right);

  /**
   * @param right
   *        value to add
   * @return <code>[this]+[right]</code>.
   */
  @Nonnull
  IJExpression plus (float right);

  /**
   * @param right
   *        value to add
   * @return <code>[this]+[right]</code>.
   */
  @Nonnull
  IJExpression plus (int right);

  /**
   * @param right
   *        value to add
   * @return <code>[this]+[right]</code>.
   */
  @Nonnull
  IJExpression plus (long right);

  /**
   * @param right
   *        value to add
   * @return <code>[this]+[right]</code>.
   */
  @Nonnull
  IJExpression plus (@Nonnull String right);

  /**
   * @param right
   *        value to add
   * @return <code>[this]-[right]</code>.
   */
  @Nonnull
  IJExpression minus (@Nonnull IJExpression right);

  /**
   * @param right
   *        value to subtract
   * @return <code>[this]-[right]</code>.
   */
  @Nonnull
  IJExpression minus (double right);

  /**
   * @param right
   *        value to subtract
   * @return <code>[this]-[right]</code>.
   */
  @Nonnull
  IJExpression minus (float right);

  /**
   * @param right
   *        value to subtract
   * @return <code>[this]-[right]</code>.
   */
  @Nonnull
  IJExpression minus (int right);

  /**
   * @param right
   *        value to subtract
   * @return <code>[this]-[right]</code>.
   */
  @Nonnull
  IJExpression minus (long right);

  /**
   * @param right
   *        value to multiply
   * @return <code>[this]*[right]</code>.
   */
  @Nonnull
  IJExpression mul (@Nonnull IJExpression right);

  /**
   * @param right
   *        value to multiply
   * @return <code>[this]*[right]</code>.
   */
  @Nonnull
  IJExpression mul (double right);

  /**
   * @param right
   *        value to multiply
   * @return <code>[this]*[right]</code>.
   */
  @Nonnull
  IJExpression mul (float right);

  /**
   * @param right
   *        value to multiply
   * @return <code>[this]*[right]</code>.
   */
  @Nonnull
  IJExpression mul (int right);

  /**
   * @param right
   *        value to multiply
   * @return <code>[this]*[right]</code>.
   */
  @Nonnull
  IJExpression mul (long right);

  /**
   * @param right
   *        value to divide through
   * @return <code>[this]/[right]</code>.
   */
  @Nonnull
  IJExpression div (@Nonnull IJExpression right);

  /**
   * @param right
   *        value to divide through
   * @return <code>[this]/[right]</code>.
   */
  @Nonnull
  IJExpression div (double right);

  /**
   * @param right
   *        value to divide through
   * @return <code>[this]/[right]</code>.
   */
  @Nonnull
  IJExpression div (float right);

  /**
   * @param right
   *        value to divide through
   * @return <code>[this]/[right]</code>.
   */
  @Nonnull
  IJExpression div (int right);

  /**
   * @param right
   *        value to divide through
   * @return <code>[this]/[right]</code>.
   */
  @Nonnull
  IJExpression div (long right);

  /**
   * @param right
   *        value to mod with
   * @return <code>[this]%[right]</code>.
   */
  @Nonnull
  IJExpression mod (@Nonnull IJExpression right);

  /**
   * @param right
   *        number of bits to shift
   * @return <code>[this]&lt;&lt;[right]</code>.
   */
  @Nonnull
  IJExpression shl (@Nonnull IJExpression right);

  /**
   * @param right
   *        number of bits to shift
   * @return <code>[this]&lt;&lt;[right]</code>.
   */
  @Nonnull
  IJExpression shl (int right);

  /**
   * @param right
   *        number of bits to shift
   * @return <code>[this] &gt;&gt; [right]</code>.
   */
  @Nonnull
  IJExpression shr (@Nonnull IJExpression right);

  /**
   * @param right
   *        number of bits to shift
   * @return <code>[this] &gt;&gt; [right]</code>.
   */
  @Nonnull
  IJExpression shr (int right);

  /**
   * @param right
   *        number of bits to shift
   * @return <code>[this] &gt;&gt;&gt; [right]</code>.
   */
  @Nonnull
  IJExpression shrz (@Nonnull IJExpression right);

  /**
   * @param right
   *        number of bits to shift
   * @return <code>[this] &gt;&gt;&gt; [right]</code>.
   */
  @Nonnull
  IJExpression shrz (int right);

  /**
   * Bit-wise AND '&amp;'.
   *
   * @param right
   *        value to combine with
   * @return <code>[this] &amp; [right]</code>.
   */
  @Nonnull
  IJExpression band (@Nonnull IJExpression right);

  /**
   * Bit-wise OR '|'.
   *
   * @param right
   *        value to combine with
   * @return <code>[this] | [right]</code>.
   */
  @Nonnull
  IJExpression bor (@Nonnull IJExpression right);

  /**
   * Logical AND '&amp;&amp;'.
   *
   * @param right
   *        value to combine with
   * @return <code>[this] &amp;&amp; [right]</code>.
   */
  @Nonnull
  IJExpression cand (@Nonnull IJExpression right);

  /**
   * Logical OR '||'.
   *
   * @param right
   *        value to combine with
   * @return <code>[this] || [right]</code>.
   */
  @Nonnull
  IJExpression cor (@Nonnull IJExpression right);

  /**
   * @param right
   *        value to combine with
   * @return <code>[this] ^ [right]</code>.
   */
  @Nonnull
  IJExpression xor (@Nonnull IJExpression right);

  /**
   * @param right
   *        value to compare to
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
   * @param right
   *        value to compare to
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
   * @param right
   *        value to compare to
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
   * @param right
   *        value to compare to
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
   * @param right
   *        type to check
   * @return <code>[this] instanceof [right]</code>.
   */
  @Nonnull
  IJExpression _instanceof (@Nonnull AbstractJType right);

  /**
   * @param method
   *        Method to be invoked
   * @return <code>[this].[method]</code>. Arguments shall be added to the
   *         returned {@link JInvocation} object.
   */
  @Nonnull
  JInvocation invoke (@Nonnull JMethod method);

  /**
   * @param method
   *        name of the method to invoke
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
}
