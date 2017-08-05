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
   * @param rhs
   *        value to add
   * @return <code>[this]+[rhs]</code>.
   */
  @Nonnull
  default IJExpression plus (@Nonnull final IJExpression rhs)
  {
    return JOp.plus (this, rhs);
  }

  /**
   * @param rhs
   *        value to add
   * @return <code>[this]+[rhs]</code>.
   */
  @Nonnull
  default IJExpression plus (final double rhs)
  {
    return plus (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        value to add
   * @return <code>[this]+[rhs]</code>.
   */
  @Nonnull
  default IJExpression plus (final float rhs)
  {
    return plus (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        value to add
   * @return <code>[this]+[rhs]</code>.
   */
  @Nonnull
  default IJExpression plus (final int rhs)
  {
    return plus (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        value to add
   * @return <code>[this]+[rhs]</code>.
   */
  @Nonnull
  default IJExpression plus (final long rhs)
  {
    return plus (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        value to add
   * @return <code>[this]+[rhs]</code>.
   */
  @Nonnull
  default IJExpression plus (@Nonnull final String rhs)
  {
    return plus (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        value to add
   * @return <code>[this]-[rhs]</code>.
   */
  @Nonnull
  default IJExpression minus (@Nonnull final IJExpression rhs)
  {
    return JOp.minus (this, rhs);
  }

  /**
   * @param rhs
   *        value to subtract
   * @return <code>[this]-[rhs]</code>.
   */
  @Nonnull
  default IJExpression minus (final double rhs)
  {
    return minus (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        value to subtract
   * @return <code>[this]-[rhs]</code>.
   */
  @Nonnull
  default IJExpression minus (final float rhs)
  {
    return minus (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        value to subtract
   * @return <code>[this]-[rhs]</code>.
   */
  @Nonnull
  default IJExpression minus (final int rhs)
  {
    return minus (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        value to subtract
   * @return <code>[this]-[rhs]</code>.
   */
  @Nonnull
  default IJExpression minus (final long rhs)
  {
    return minus (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        value to multiply
   * @return <code>[this]*[rhs]</code>.
   */
  @Nonnull
  default IJExpression mul (@Nonnull final IJExpression rhs)
  {
    return JOp.mul (this, rhs);
  }

  /**
   * @param rhs
   *        value to multiply
   * @return <code>[this]*[rhs]</code>.
   */
  @Nonnull
  default IJExpression mul (final double rhs)
  {
    return mul (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        value to multiply
   * @return <code>[this]*[rhs]</code>.
   */
  @Nonnull
  default IJExpression mul (final float rhs)
  {
    return mul (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        value to multiply
   * @return <code>[this]*[rhs]</code>.
   */
  @Nonnull
  default IJExpression mul (final int rhs)
  {
    return mul (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        value to multiply
   * @return <code>[this]*[rhs]</code>.
   */
  @Nonnull
  default IJExpression mul (final long rhs)
  {
    return mul (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        value to divide through
   * @return <code>[this]/[rhs]</code>.
   */
  @Nonnull
  default IJExpression div (@Nonnull final IJExpression rhs)
  {
    return JOp.div (this, rhs);
  }

  /**
   * @param rhs
   *        value to divide through
   * @return <code>[this]/[rhs]</code>.
   */
  @Nonnull
  default IJExpression div (final double rhs)
  {
    return div (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        value to divide through
   * @return <code>[this]/[rhs]</code>.
   */
  @Nonnull
  default IJExpression div (final float rhs)
  {
    return div (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        value to divide through
   * @return <code>[this]/[rhs]</code>.
   */
  @Nonnull
  default IJExpression div (final int rhs)
  {
    return div (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        value to divide through
   * @return <code>[this]/[rhs]</code>.
   */
  @Nonnull
  default IJExpression div (final long rhs)
  {
    return div (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        value to mod with
   * @return <code>[this]%[rhs]</code>.
   */
  @Nonnull
  default IJExpression mod (@Nonnull final IJExpression rhs)
  {
    return JOp.mod (this, rhs);
  }

  /**
   * @param rhs
   *        value to mod with
   * @return <code>[this]%[rhs]</code>.
   */
  @Nonnull
  default IJExpression mod (final int rhs)
  {
    return mod (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        value to mod with
   * @return <code>[this]%[rhs]</code>.
   */
  @Nonnull
  default IJExpression mod (final long rhs)
  {
    return mod (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        number of bits to shift
   * @return <code>[this]&lt;&lt;[rhs]</code>.
   */
  @Nonnull
  default IJExpression shl (@Nonnull final IJExpression rhs)
  {
    return JOp.shl (this, rhs);
  }

  /**
   * @param rhs
   *        number of bits to shift
   * @return <code>[this]&lt;&lt;[rhs]</code>.
   */
  @Nonnull
  default IJExpression shl (final int rhs)
  {
    return shl (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        number of bits to shift
   * @return <code>[this] &gt;&gt; [rhs]</code>.
   */
  @Nonnull
  default IJExpression shr (@Nonnull final IJExpression rhs)
  {
    return JOp.shr (this, rhs);
  }

  /**
   * @param rhs
   *        number of bits to shift
   * @return <code>[this] &gt;&gt; [rhs]</code>.
   */
  @Nonnull
  default IJExpression shr (final int rhs)
  {
    return shr (JExpr.lit (rhs));
  }

  /**
   * @param rhs
   *        number of bits to shift
   * @return <code>[this] &gt;&gt;&gt; [rhs]</code>.
   */
  @Nonnull
  default IJExpression shrz (@Nonnull final IJExpression rhs)
  {
    return JOp.shrz (this, rhs);
  }

  /**
   * @param rhs
   *        number of bits to shift
   * @return <code>[this] &gt;&gt;&gt; [rhs]</code>.
   */
  @Nonnull
  default IJExpression shrz (final int rhs)
  {
    return shrz (JExpr.lit (rhs));
  }

  /**
   * Bit-wise AND '&amp;'.
   *
   * @param rhs
   *        value to combine with
   * @return <code>[this] &amp; [rhs]</code>.
   */
  @Nonnull
  default IJExpression band (@Nonnull final IJExpression rhs)
  {
    return JOp.band (this, rhs);
  }

  /**
   * Bit-wise OR '|'.
   *
   * @param rhs
   *        value to combine with
   * @return <code>[this] | [rhs]</code>.
   */
  @Nonnull
  default IJExpression bor (@Nonnull final IJExpression rhs)
  {
    return JOp.bor (this, rhs);
  }

  /**
   * Logical AND '&amp;&amp;'.
   *
   * @param rhs
   *        value to combine with
   * @return <code>[this] &amp;&amp; [rhs]</code>.
   */
  @Nonnull
  default IJExpression cand (@Nonnull final IJExpression rhs)
  {
    return JOp.cand (this, rhs);
  }

  /**
   * Logical OR '||'.
   *
   * @param rhs
   *        value to combine with
   * @return <code>[this] || [rhs]</code>.
   */
  @Nonnull
  default IJExpression cor (@Nonnull final IJExpression rhs)
  {
    return JOp.cor (this, rhs);
  }

  /**
   * @param rhs
   *        value to combine with
   * @return <code>[this] ^ [rhs]</code>.
   */
  @Nonnull
  default IJExpression xor (@Nonnull final IJExpression rhs)
  {
    return JOp.xor (this, rhs);
  }

  /**
   * @param rhs
   *        value to compare to
   * @return <code>[this] &lt; [rhs]</code>.
   */
  @Nonnull
  default IJExpression lt (@Nonnull final IJExpression rhs)
  {
    return JOp.lt (this, rhs);
  }

  /**
   * @param rhs
   *        value to compare to
   * @return <code>[this] &lt; [rhs]</code>.
   */
  @Nonnull
  default IJExpression lt (final int rhs)
  {
    return lt (JExpr.lit (rhs));
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
   * @param rhs
   *        value to compare to
   * @return <code>[this] &lt;= [rhs]</code>.
   */
  @Nonnull
  default IJExpression lte (@Nonnull final IJExpression rhs)
  {
    return JOp.lte (this, rhs);
  }

  /**
   * @param rhs
   *        value to compare to
   * @return <code>[this] &lt;= [rhs]</code>.
   */
  @Nonnull
  default IJExpression lte (final int rhs)
  {
    return lte (JExpr.lit (rhs));
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
   * @param rhs
   *        value to compare to
   * @return <code>[this] &gt; [rhs]</code>.
   */
  @Nonnull
  default IJExpression gt (@Nonnull final IJExpression rhs)
  {
    return JOp.gt (this, rhs);
  }

  /**
   * @param rhs
   *        value to compare to
   * @return <code>[this] &gt; [rhs]</code>.
   */
  @Nonnull
  default IJExpression gt (final int rhs)
  {
    return gt (JExpr.lit (rhs));
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
   * @param rhs
   *        value to compare to
   * @return <code>[this] &gt;= [rhs]</code>.
   */
  @Nonnull
  default IJExpression gte (@Nonnull final IJExpression rhs)
  {
    return JOp.gte (this, rhs);
  }

  /**
   * @param rhs
   *        value to compare to
   * @return <code>[this] &gt;= [rhs]</code>.
   */
  @Nonnull
  default IJExpression gte (final int rhs)
  {
    return gte (JExpr.lit (rhs));
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
   * @param rhs
   *        expression to compare to
   * @return <code><em>expr</em> == <em>rhs</em></code>
   */
  @Nonnull
  default IJExpression eq (@Nonnull final IJExpression rhs)
  {
    return JOp.eq (this, rhs);
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
   * @param rhs
   *        expression to compare to
   * @return <code><em>expr</em> != <em>rhs</em></code>
   */
  @Nonnull
  default IJExpression ne (@Nonnull final IJExpression rhs)
  {
    return JOp.ne (this, rhs);
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
   * @param rhs
   *        type to check
   * @return <code>[this] instanceof [rhs]</code>.
   */
  @Nonnull
  default IJExpression _instanceof (@Nonnull final AbstractJType rhs)
  {
    return JOp._instanceof (this, rhs);
  }

  /**
   * @param aMethod
   *        Method to be invoked
   * @return <code>[this].[method]</code>. Arguments shall be added to the
   *         returned {@link JInvocation} object.
   */
  @Nonnull
  default JInvocation invoke (@Nonnull final JMethod aMethod)
  {
    return JExpr.invoke (this, aMethod);
  }

  /**
   * @param sMethod
   *        name of the method to invoke
   * @return <code>[this].[method]</code>. Arguments shall be added to the
   *         returned {@link JInvocation} object.
   */
  @Nonnull
  default JInvocation invoke (@Nonnull final String sMethod)
  {
    return JExpr.invoke (this, sMethod);
  }

  @Nonnull
  default JFieldRef ref (@Nonnull final JVar aField)
  {
    return JExpr.ref (this, aField);
  }

  @Nonnull
  default JFieldRef ref (@Nonnull final String sField)
  {
    return JExpr.ref (this, sField);
  }

  /**
   * @param aIndex
   *        array index
   * @return <code>[this] [ [index] ]</code>
   */
  @Nonnull
  default JArrayCompRef component (@Nonnull final IJExpression aIndex)
  {
    return JExpr.component (this, aIndex);
  }

  /**
   * @param nIndex
   *        array index
   * @return <code>[this] [ [index] ]</code>
   */
  @Nonnull
  default JArrayCompRef component (final int nIndex)
  {
    return component (JExpr.lit (nIndex));
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
