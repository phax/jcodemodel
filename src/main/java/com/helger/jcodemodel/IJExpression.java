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
  default IJExpression minus ()
  {
    return JOp.minus (this);
  }

  /**
   * Logical 'not' <tt>'!x'</tt>.
   *
   * @return <code>![this]" from "[this]</code>.
   */
  @Nonnull
  default IJExpression not ()
  {
    return JOp.not (this);
  }

  /**
   * @return <code>~[this]" from "[this]</code>.
   */
  @Nonnull
  default IJExpression complement ()
  {
    return JOp.complement (this);
  }

  /**
   * @return <code>[this]++" from "[this]</code>.
   */
  @Nonnull
  default IJExpression incr ()
  {
    return postincr ();
  }

  /**
   * @return <code>[this]++" from "[this]</code>.
   */
  @Nonnull
  default IJExpression postincr ()
  {
    return JOp.postincr (this);
  }

  /**
   * @return <code>++[this]" from "[this]</code>.
   */
  @Nonnull
  default IJExpression preincr ()
  {
    return JOp.preincr (this);
  }

  /**
   * @return <code>[this]--" from "[this]</code>.
   */
  @Nonnull
  default IJExpression decr ()
  {
    return postdecr ();
  }

  /**
   * @return <code>[this]--" from "[this]</code>.
   */
  @Nonnull
  default IJExpression postdecr ()
  {
    return JOp.postdecr (this);
  }

  /**
   * @return <code>--[this]" from "[this]</code>.
   */
  @Nonnull
  default IJExpression predecr ()
  {
    return JOp.predecr (this);
  }

  /**
   * @param right
   *        value to add
   * @return <code>[this]+[right]</code>.
   */
  @Nonnull
  default IJExpression plus (@Nonnull final IJExpression right)
  {
    return JOp.plus (this, right);
  }

  /**
   * @param right
   *        value to add
   * @return <code>[this]+[right]</code>.
   */
  @Nonnull
  default IJExpression plus (final double right)
  {
    return plus (JExpr.lit (right));
  }

  /**
   * @param right
   *        value to add
   * @return <code>[this]+[right]</code>.
   */
  @Nonnull
  default IJExpression plus (final float right)
  {
    return plus (JExpr.lit (right));
  }

  /**
   * @param right
   *        value to add
   * @return <code>[this]+[right]</code>.
   */
  @Nonnull
  default IJExpression plus (final int right)
  {
    return plus (JExpr.lit (right));
  }

  /**
   * @param right
   *        value to add
   * @return <code>[this]+[right]</code>.
   */
  @Nonnull
  default IJExpression plus (final long right)
  {
    return plus (JExpr.lit (right));
  }

  /**
   * @param right
   *        value to add
   * @return <code>[this]+[right]</code>.
   */
  @Nonnull
  default IJExpression plus (@Nonnull final String right)
  {
    return plus (JExpr.lit (right));
  }

  /**
   * @param right
   *        value to add
   * @return <code>[this]-[right]</code>.
   */
  @Nonnull
  default IJExpression minus (@Nonnull final IJExpression right)
  {
    return JOp.minus (this, right);
  }

  /**
   * @param right
   *        value to subtract
   * @return <code>[this]-[right]</code>.
   */
  @Nonnull
  default IJExpression minus (final double right)
  {
    return minus (JExpr.lit (right));
  }

  /**
   * @param right
   *        value to subtract
   * @return <code>[this]-[right]</code>.
   */
  @Nonnull
  default IJExpression minus (final float right)
  {
    return minus (JExpr.lit (right));
  }

  /**
   * @param right
   *        value to subtract
   * @return <code>[this]-[right]</code>.
   */
  @Nonnull
  default IJExpression minus (final int right)
  {
    return minus (JExpr.lit (right));
  }

  /**
   * @param right
   *        value to subtract
   * @return <code>[this]-[right]</code>.
   */
  @Nonnull
  default IJExpression minus (final long right)
  {
    return minus (JExpr.lit (right));
  }

  /**
   * @param right
   *        value to multiply
   * @return <code>[this]*[right]</code>.
   */
  @Nonnull
  default IJExpression mul (@Nonnull final IJExpression right)
  {
    return JOp.mul (this, right);
  }

  /**
   * @param right
   *        value to multiply
   * @return <code>[this]*[right]</code>.
   */
  @Nonnull
  default IJExpression mul (final double right)
  {
    return mul (JExpr.lit (right));
  }

  /**
   * @param right
   *        value to multiply
   * @return <code>[this]*[right]</code>.
   */
  @Nonnull
  default IJExpression mul (final float right)
  {
    return mul (JExpr.lit (right));
  }

  /**
   * @param right
   *        value to multiply
   * @return <code>[this]*[right]</code>.
   */
  @Nonnull
  default IJExpression mul (final int right)
  {
    return mul (JExpr.lit (right));
  }

  /**
   * @param right
   *        value to multiply
   * @return <code>[this]*[right]</code>.
   */
  @Nonnull
  default IJExpression mul (final long right)
  {
    return mul (JExpr.lit (right));
  }

  /**
   * @param right
   *        value to divide through
   * @return <code>[this]/[right]</code>.
   */
  @Nonnull
  default IJExpression div (@Nonnull final IJExpression right)
  {
    return JOp.div (this, right);
  }

  /**
   * @param right
   *        value to divide through
   * @return <code>[this]/[right]</code>.
   */
  @Nonnull
  default IJExpression div (final double right)
  {
    return div (JExpr.lit (right));
  }

  /**
   * @param right
   *        value to divide through
   * @return <code>[this]/[right]</code>.
   */
  @Nonnull
  default IJExpression div (final float right)
  {
    return div (JExpr.lit (right));
  }

  /**
   * @param right
   *        value to divide through
   * @return <code>[this]/[right]</code>.
   */
  @Nonnull
  default IJExpression div (final int right)
  {
    return div (JExpr.lit (right));
  }

  /**
   * @param right
   *        value to divide through
   * @return <code>[this]/[right]</code>.
   */
  @Nonnull
  default IJExpression div (final long right)
  {
    return div (JExpr.lit (right));
  }

  /**
   * @param right
   *        value to mod with
   * @return <code>[this]%[right]</code>.
   */
  @Nonnull
  default IJExpression mod (@Nonnull final IJExpression right)
  {
    return JOp.mod (this, right);
  }

  /**
   * @param right
   *        value to mod with
   * @return <code>[this]%[right]</code>.
   */
  @Nonnull
  default IJExpression mod (final int right)
  {
    return mod (JExpr.lit (right));
  }

  /**
   * @param right
   *        value to mod with
   * @return <code>[this]%[right]</code>.
   */
  @Nonnull
  default IJExpression mod (final long right)
  {
    return mod (JExpr.lit (right));
  }

  /**
   * @param right
   *        number of bits to shift
   * @return <code>[this]&lt;&lt;[right]</code>.
   */
  @Nonnull
  default IJExpression shl (@Nonnull final IJExpression right)
  {
    return JOp.shl (this, right);
  }

  /**
   * @param right
   *        number of bits to shift
   * @return <code>[this]&lt;&lt;[right]</code>.
   */
  @Nonnull
  default IJExpression shl (final int right)
  {
    return shl (JExpr.lit (right));
  }

  /**
   * @param right
   *        number of bits to shift
   * @return <code>[this] &gt;&gt; [right]</code>.
   */
  @Nonnull
  default IJExpression shr (@Nonnull final IJExpression right)
  {
    return JOp.shr (this, right);
  }

  /**
   * @param right
   *        number of bits to shift
   * @return <code>[this] &gt;&gt; [right]</code>.
   */
  @Nonnull
  default IJExpression shr (final int right)
  {
    return shr (JExpr.lit (right));
  }

  /**
   * @param right
   *        number of bits to shift
   * @return <code>[this] &gt;&gt;&gt; [right]</code>.
   */
  @Nonnull
  default IJExpression shrz (@Nonnull final IJExpression right)
  {
    return JOp.shrz (this, right);
  }

  /**
   * @param right
   *        number of bits to shift
   * @return <code>[this] &gt;&gt;&gt; [right]</code>.
   */
  @Nonnull
  default IJExpression shrz (final int right)
  {
    return shrz (JExpr.lit (right));
  }

  /**
   * Bit-wise AND '&amp;'.
   *
   * @param right
   *        value to combine with
   * @return <code>[this] &amp; [right]</code>.
   */
  @Nonnull
  default IJExpression band (@Nonnull final IJExpression right)
  {
    return JOp.band (this, right);
  }

  /**
   * Bit-wise OR '|'.
   *
   * @param right
   *        value to combine with
   * @return <code>[this] | [right]</code>.
   */
  @Nonnull
  default IJExpression bor (@Nonnull final IJExpression right)
  {
    return JOp.bor (this, right);
  }

  /**
   * Logical AND '&amp;&amp;'.
   *
   * @param right
   *        value to combine with
   * @return <code>[this] &amp;&amp; [right]</code>.
   */
  @Nonnull
  default IJExpression cand (@Nonnull final IJExpression right)
  {
    return JOp.cand (this, right);
  }

  /**
   * Logical OR '||'.
   *
   * @param right
   *        value to combine with
   * @return <code>[this] || [right]</code>.
   */
  @Nonnull
  default IJExpression cor (@Nonnull final IJExpression right)
  {
    return JOp.cor (this, right);
  }

  /**
   * @param right
   *        value to combine with
   * @return <code>[this] ^ [right]</code>.
   */
  @Nonnull
  default IJExpression xor (@Nonnull final IJExpression right)
  {
    return JOp.xor (this, right);
  }

  /**
   * @param right
   *        value to compare to
   * @return <code>[this] &lt; [right]</code>.
   */
  @Nonnull
  default IJExpression lt (@Nonnull final IJExpression right)
  {
    return JOp.lt (this, right);
  }

  /**
   * @param right
   *        value to compare to
   * @return <code>[this] &lt; [right]</code>.
   */
  @Nonnull
  default IJExpression lt (final int right)
  {
    return lt (JExpr.lit (right));
  }

  /**
   * @return <code>[this] &lt; 0</code>.
   */
  @Nonnull
  default IJExpression lt0 ()
  {
    return lt (0);
  }

  /**
   * @param right
   *        value to compare to
   * @return <code>[this] &lt;= [right]</code>.
   */
  @Nonnull
  default IJExpression lte (@Nonnull final IJExpression right)
  {
    return JOp.lte (this, right);
  }

  /**
   * @param right
   *        value to compare to
   * @return <code>[this] &lt;= [right]</code>.
   */
  @Nonnull
  default IJExpression lte (final int right)
  {
    return lte (JExpr.lit (right));
  }

  /**
   * @return <code>[this] &lt;= 0</code>.
   */
  @Nonnull
  default IJExpression lte0 ()
  {
    return lte (0);
  }

  /**
   * @param right
   *        value to compare to
   * @return <code>[this] &gt; [right]</code>.
   */
  @Nonnull
  default IJExpression gt (@Nonnull final IJExpression right)
  {
    return JOp.gt (this, right);
  }

  /**
   * @param right
   *        value to compare to
   * @return <code>[this] &gt; [right]</code>.
   */
  @Nonnull
  default IJExpression gt (final int right)
  {
    return gt (JExpr.lit (right));
  }

  /**
   * @return <code>[this] &gt; 0</code>.
   */
  @Nonnull
  default IJExpression gt0 ()
  {
    return gt (0);
  }

  /**
   * @param right
   *        value to compare to
   * @return <code>[this] &gt;= [right]</code>.
   */
  @Nonnull
  default IJExpression gte (@Nonnull final IJExpression right)
  {
    return JOp.gte (this, right);
  }

  /**
   * @param right
   *        value to compare to
   * @return <code>[this] &gt;= [right]</code>.
   */
  @Nonnull
  default IJExpression gte (final int right)
  {
    return gte (JExpr.lit (right));
  }

  /**
   * @return <code>[this] &gt;= 0</code>.
   */
  @Nonnull
  default IJExpression gte0 ()
  {
    return gte (0);
  }

  /**
   * Equals
   *
   * @param right
   *        expression to compare to
   * @return <code><em>expr</em> == <em>right</em></code>
   */
  @Nonnull
  default IJExpression eq (@Nonnull final IJExpression right)
  {
    return JOp.eq (this, right);
  }

  /**
   * Shortcut for <code>eq (JExpr._null ())</code>
   *
   * @return <code><em>expr</em> == null</code>
   */
  @Nonnull
  default IJExpression eqNull ()
  {
    return eq (JExpr._null ());
  }

  /**
   * Shortcut for <code>eq (JExpr.lit (0))</code>
   *
   * @return <code><em>expr</em> == 0</code>
   */
  @Nonnull
  default IJExpression eq0 ()
  {
    return eq (JExpr.lit (0));
  }

  /**
   * Not equals
   *
   * @param right
   *        expression to compare to
   * @return <code><em>expr</em> != <em>right</em></code>
   */
  @Nonnull
  default IJExpression ne (@Nonnull final IJExpression right)
  {
    return JOp.ne (this, right);
  }

  /**
   * Shortcut for <code>ne (JExpr._null ())</code>
   *
   * @return Never <code><em>expr</em> != null</code>
   */
  @Nonnull
  default IJExpression neNull ()
  {
    return ne (JExpr._null ());
  }

  /**
   * Shortcut for <code>ne (JExpr.lit (0))</code>
   *
   * @return Never <code><em>expr</em> != 0</code>
   */
  @Nonnull
  default IJExpression ne0 ()
  {
    return ne (JExpr.lit (0));
  }

  /**
   * @param right
   *        type to check
   * @return <code>[this] instanceof [right]</code>.
   */
  @Nonnull
  default IJExpression _instanceof (@Nonnull final AbstractJType right)
  {
    return JOp._instanceof (this, right);
  }

  /**
   * @param method
   *        Method to be invoked
   * @return <code>[this].[method]</code>. Arguments shall be added to the
   *         returned {@link JInvocation} object.
   */
  @Nonnull
  default JInvocation invoke (@Nonnull final JMethod method)
  {
    return JExpr.invoke (this, method);
  }

  /**
   * @param method
   *        name of the method to invoke
   * @return <code>[this].[method]</code>. Arguments shall be added to the
   *         returned {@link JInvocation} object.
   */
  @Nonnull
  default JInvocation invoke (@Nonnull final String method)
  {
    return JExpr.invoke (this, method);
  }

  @Nonnull
  default JFieldRef ref (@Nonnull final JVar field)
  {
    return JExpr.ref (this, field);
  }

  @Nonnull
  default JFieldRef ref (@Nonnull final String field)
  {
    return JExpr.ref (this, field);
  }

  /**
   * @param index
   *        array index
   * @return <code>[this] [ [index] ]</code>
   */
  @Nonnull
  default JArrayCompRef component (@Nonnull final IJExpression index)
  {
    return JExpr.component (this, index);
  }

  /**
   * @param index
   *        array index
   * @return <code>[this] [ [index] ]</code>
   */
  @Nonnull
  default JArrayCompRef component (final int index)
  {
    return component (JExpr.lit (index));
  }

  /**
   * @return <code>[this] [0]</code>
   */
  @Nonnull
  default JArrayCompRef component0 ()
  {
    return component (0);
  }
}
