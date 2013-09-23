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
 * Provides default implementations for {@link IJExpression}.
 */
public abstract class AbstractJExpressionImpl implements IJExpression
{
  protected AbstractJExpressionImpl ()
  {}

  // from JOp
  @Nonnull
  public final IJExpression minus ()
  {
    return JOp.minus (this);
  }

  /**
   * Logical 'not' <tt>'!x'</tt>.
   */
  @Nonnull
  public final IJExpression not ()
  {
    return JOp.not (this);
  }

  @Nonnull
  public final IJExpression complement ()
  {
    return JOp.complement (this);
  }

  @Nonnull
  public final IJExpression incr ()
  {
    return JOp.incr (this);
  }

  @Nonnull
  public final IJExpression preincr ()
  {
    return JOp.preincr (this);
  }

  @Nonnull
  public final IJExpression decr ()
  {
    return JOp.decr (this);
  }

  @Nonnull
  public final IJExpression predecr ()
  {
    return JOp.predecr (this);
  }

  @Nonnull
  public final IJExpression plus (@Nonnull final IJExpression right)
  {
    return JOp.plus (this, right);
  }

  @Nonnull
  public final IJExpression minus (@Nonnull final IJExpression right)
  {
    return JOp.minus (this, right);
  }

  @Nonnull
  public final IJExpression mul (@Nonnull final IJExpression right)
  {
    return JOp.mul (this, right);
  }

  @Nonnull
  public final IJExpression div (@Nonnull final IJExpression right)
  {
    return JOp.div (this, right);
  }

  @Nonnull
  public final IJExpression mod (@Nonnull final IJExpression right)
  {
    return JOp.mod (this, right);
  }

  @Nonnull
  public final IJExpression shl (@Nonnull final IJExpression right)
  {
    return JOp.shl (this, right);
  }

  @Nonnull
  public final IJExpression shr (@Nonnull final IJExpression right)
  {
    return JOp.shr (this, right);
  }

  @Nonnull
  public final IJExpression shrz (@Nonnull final IJExpression right)
  {
    return JOp.shrz (this, right);
  }

  @Nonnull
  public final IJExpression band (@Nonnull final IJExpression right)
  {
    return JOp.band (this, right);
  }

  @Nonnull
  public final IJExpression bor (@Nonnull final IJExpression right)
  {
    return JOp.bor (this, right);
  }

  @Nonnull
  public final IJExpression cand (@Nonnull final IJExpression right)
  {
    return JOp.cand (this, right);
  }

  @Nonnull
  public final IJExpression cor (@Nonnull final IJExpression right)
  {
    return JOp.cor (this, right);
  }

  @Nonnull
  public final IJExpression xor (@Nonnull final IJExpression right)
  {
    return JOp.xor (this, right);
  }

  @Nonnull
  public final IJExpression lt (@Nonnull final IJExpression right)
  {
    return JOp.lt (this, right);
  }

  @Nonnull
  public final IJExpression lte (@Nonnull final IJExpression right)
  {
    return JOp.lte (this, right);
  }

  @Nonnull
  public final IJExpression gt (@Nonnull final IJExpression right)
  {
    return JOp.gt (this, right);
  }

  @Nonnull
  public final IJExpression gte (@Nonnull final IJExpression right)
  {
    return JOp.gte (this, right);
  }

  @Nonnull
  public final IJExpression eq (@Nonnull final IJExpression right)
  {
    return JOp.eq (this, right);
  }

  @Nonnull
  public final IJExpression ne (@Nonnull final IJExpression right)
  {
    return JOp.ne (this, right);
  }

  @Nonnull
  public final IJExpression _instanceof (@Nonnull final AbstractJType right)
  {
    return JOp._instanceof (this, right);
  }

  //
  //
  // from JExpr
  //
  //
  @Nonnull
  public final JInvocation invoke (@Nonnull final JMethod method)
  {
    return JExpr.invoke (this, method);
  }

  @Nonnull
  public final JInvocation invoke (@Nonnull final String method)
  {
    return JExpr.invoke (this, method);
  }

  @Nonnull
  public final JFieldRef ref (@Nonnull final JVar field)
  {
    return JExpr.ref (this, field);
  }

  @Nonnull
  public final JFieldRef ref (@Nonnull final String field)
  {
    return JExpr.ref (this, field);
  }

  @Nonnull
  public final JArrayCompRef component (@Nonnull final IJExpression index)
  {
    return JExpr.component (this, index);
  }
}
