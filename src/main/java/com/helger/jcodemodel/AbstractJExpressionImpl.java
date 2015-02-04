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

import com.helger.jcodemodel.optimize.ExpressionAccessor;
import com.helger.jcodemodel.optimize.ExpressionCallback;

/**
 * Provides default implementations for {@link IJExpression}.
 */
public abstract class AbstractJExpressionImpl implements IJExpression
{

  static boolean visitWithSubExpressions (final ExpressionCallback callback, final ExpressionAccessor accessor)
  {
    final IJExpression expression = accessor.get ();
    if (!callback.visitExpression (expression, accessor))
      return false;
    return expression.forAllSubExpressions (callback);
  }

  private AbstractJType _hintType;
  private String _hintName;

  protected AbstractJExpressionImpl ()
  {}

  // from JOp
  @Nonnull
  public final JOpUnary minus ()
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
  public final JOpUnary complement ()
  {
    return JOp.complement (this);
  }

  @Nonnull
  public final JOpUnaryTight incr ()
  {
    return JOp.incr (this);
  }

  @Nonnull
  public final JOpUnaryTight preincr ()
  {
    return JOp.preincr (this);
  }

  @Nonnull
  public final JOpUnaryTight decr ()
  {
    return JOp.decr (this);
  }

  @Nonnull
  public final JOpUnaryTight predecr ()
  {
    return JOp.predecr (this);
  }

  @Nonnull
  public final JOpBinary plus (@Nonnull final IJExpression right)
  {
    return JOp.plus (this, right);
  }

  @Nonnull
  public final JOpBinary plus (final double right)
  {
    return plus (JExpr.lit (right));
  }

  @Nonnull
  public final JOpBinary plus (final float right)
  {
    return plus (JExpr.lit (right));
  }

  @Nonnull
  public final JOpBinary plus (final int right)
  {
    return plus (JExpr.lit (right));
  }

  @Nonnull
  public final JOpBinary plus (final long right)
  {
    return plus (JExpr.lit (right));
  }

  @Nonnull
  public final JOpBinary plus (@Nonnull final String right)
  {
    return plus (JExpr.lit (right));
  }

  @Nonnull
  public final JOpBinary minus (@Nonnull final IJExpression right)
  {
    return JOp.minus (this, right);
  }

  @Nonnull
  public final JOpBinary minus (final double right)
  {
    return minus (JExpr.lit (right));
  }

  @Nonnull
  public final JOpBinary minus (final float right)
  {
    return minus (JExpr.lit (right));
  }

  @Nonnull
  public final JOpBinary minus (final int right)
  {
    return minus (JExpr.lit (right));
  }

  @Nonnull
  public final JOpBinary minus (final long right)
  {
    return minus (JExpr.lit (right));
  }

  @Nonnull
  public final JOpBinary mul (@Nonnull final IJExpression right)
  {
    return JOp.mul (this, right);
  }

  @Nonnull
  public final JOpBinary mul (final double right)
  {
    return mul (JExpr.lit (right));
  }

  @Nonnull
  public final JOpBinary mul (final float right)
  {
    return mul (JExpr.lit (right));
  }

  @Nonnull
  public final JOpBinary mul (final int right)
  {
    return mul (JExpr.lit (right));
  }

  @Nonnull
  public final JOpBinary mul (final long right)
  {
    return mul (JExpr.lit (right));
  }

  @Nonnull
  public final JOpBinary div (@Nonnull final IJExpression right)
  {
    return JOp.div (this, right);
  }

  @Nonnull
  public final JOpBinary div (final double right)
  {
    return div (JExpr.lit (right));
  }

  @Nonnull
  public final JOpBinary div (final float right)
  {
    return div (JExpr.lit (right));
  }

  @Nonnull
  public final JOpBinary div (final int right)
  {
    return div (JExpr.lit (right));
  }

  @Nonnull
  public final JOpBinary div (final long right)
  {
    return div (JExpr.lit (right));
  }

  @Nonnull
  public final JOpBinary mod (@Nonnull final IJExpression right)
  {
    return JOp.mod (this, right);
  }

  @Nonnull
  public final JOpBinary shl (@Nonnull final IJExpression right)
  {
    return JOp.shl (this, right);
  }

  @Nonnull
  public final JOpBinary shl (final int right)
  {
    return shl (JExpr.lit (right));
  }

  @Nonnull
  public final JOpBinary shr (@Nonnull final IJExpression right)
  {
    return JOp.shr (this, right);
  }

  @Nonnull
  public final JOpBinary shr (final int right)
  {
    return shr (JExpr.lit (right));
  }

  @Nonnull
  public final JOpBinary shrz (@Nonnull final IJExpression right)
  {
    return JOp.shrz (this, right);
  }

  @Nonnull
  public final JOpBinary shrz (final int right)
  {
    return shrz (JExpr.lit (right));
  }

  @Nonnull
  public final JOpBinary band (@Nonnull final IJExpression right)
  {
    return JOp.band (this, right);
  }

  @Nonnull
  public final JOpBinary bor (@Nonnull final IJExpression right)
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
  public final JOpBinary xor (@Nonnull final IJExpression right)
  {
    return JOp.xor (this, right);
  }

  @Nonnull
  public final JOpBinary lt (@Nonnull final IJExpression right)
  {
    return JOp.lt (this, right);
  }

  @Nonnull
  public final JOpBinary lt0 ()
  {
    return lt (JExpr.lit (0));
  }

  @Nonnull
  public final JOpBinary lte (@Nonnull final IJExpression right)
  {
    return JOp.lte (this, right);
  }

  @Nonnull
  public final JOpBinary lte0 ()
  {
    return lte (JExpr.lit (0));
  }

  @Nonnull
  public final JOpBinary gt (@Nonnull final IJExpression right)
  {
    return JOp.gt (this, right);
  }

  @Nonnull
  public final JOpBinary gt0 ()
  {
    return gt (JExpr.lit (0));
  }

  @Nonnull
  public final JOpBinary gte (@Nonnull final IJExpression right)
  {
    return JOp.gte (this, right);
  }

  @Nonnull
  public final JOpBinary gte0 ()
  {
    return gte (JExpr.lit (0));
  }

  @Nonnull
  public final JOpBinary eq (@Nonnull final IJExpression right)
  {
    return JOp.eq (this, right);
  }

  @Nonnull
  public final JOpBinary eqNull ()
  {
    return eq (JExpr._null ());
  }

  @Nonnull
  public final JOpBinary eq0 ()
  {
    return eq (JExpr.lit (0));
  }

  @Nonnull
  public final JOpBinary ne (@Nonnull final IJExpression right)
  {
    return JOp.ne (this, right);
  }

  @Nonnull
  public final JOpBinary neNull ()
  {
    return ne (JExpr._null ());
  }

  @Nonnull
  public final JOpBinary ne0 ()
  {
    return ne (JExpr.lit (0));
  }

  @Nonnull
  public final JOpBinary _instanceof (@Nonnull final AbstractJType right)
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

  @Nonnull
  public final JArrayCompRef component (final int index)
  {
    return component (JExpr.lit (index));
  }

  @Nonnull
  public final JArrayCompRef component0 ()
  {
    return component (JExpr.lit (0));
  }

  public IJExpression unwrapped ()
  {
    return this;
  }

  public final AbstractJType expressionType ()
  {
    if (_hintType != null)
      return _hintType;
    return derivedType ();
  }

  public final AbstractJExpressionImpl hintType (final AbstractJType hintType)
  {
    _hintType = hintType;
    return this;
  }

  // TODO remove this default and implement for all subclasses!
  AbstractJType derivedType ()
  {
    return null;
  }

  public final String expressionName ()
  {
    if (_hintName != null)
      return _hintName;
    return derivedName ();
  }

  public final AbstractJExpressionImpl hintName (final String hintName)
  {
    _hintName = hintName;
    return this;
  }

  // TODO remove this default and implement for all subclasses!
  String derivedName ()
  {
    return null;
  }

  // TODO remove this default and implement for all subclasses!
  public boolean forAllSubExpressions (final ExpressionCallback callback)
  {
    return true;
  }
}
