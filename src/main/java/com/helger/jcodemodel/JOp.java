/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2016 Philip Helger + contributors
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
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * Class for generating expressions containing operators
 *
 * @author Philip Helger et al
 */
@Immutable
public final class JOp
{
  private JOp ()
  {}

  /**
   * Determine whether the top level of an expression involves an operator.
   *
   * @param e
   *        Expression to evaluate
   * @return <code>true</code> of a top level operator is present
   */
  public static boolean hasTopOp (@Nullable final IJExpression e)
  {
    return (e instanceof JOpUnary) || (e instanceof JOpBinary);
  }

  /* -- Unary operators -- */

  @Nonnull
  public static JOpUnary minus (@Nonnull final IJExpression e)
  {
    return new JOpUnary ("-", e);
  }

  /**
   * Logical not <tt>'!x'</tt>.
   *
   * @param e
   *        Expression to invert
   * @return Inverted expression
   */
  @Nonnull
  public static IJExpression not (@Nonnull final IJExpression e)
  {
    // Inline optimizations :)
    if (e == JExpr.TRUE)
      return JExpr.FALSE;
    if (e == JExpr.FALSE)
      return JExpr.TRUE;
    return new JOpUnary ("!", e);
  }

  @Nonnull
  public static JOpUnary complement (@Nonnull final IJExpression e)
  {
    return new JOpUnary ("~", e);
  }

  /**
   * Post increment
   *
   * @param e
   *        expression
   * @return <code><em>e</em>++</code>
   * @deprecated Use {@link #postincr(IJExpression)} instead
   */
  @Deprecated
  @Nonnull
  public static JOpUnaryTight incr (@Nonnull final IJExpression e)
  {
    return postincr (e);
  }

  /**
   * Post increment
   *
   * @param e
   *        expression
   * @return <code><em>e</em>++</code>
   */
  @Nonnull
  public static JOpUnaryTight postincr (@Nonnull final IJExpression e)
  {
    return new JOpUnaryTight (e, "++");
  }

  /**
   * Pre increment
   *
   * @param e
   *        expression
   * @return <code>++<em>e</em></code>
   */
  @Nonnull
  public static JOpUnaryTight preincr (@Nonnull final IJExpression e)
  {
    return new JOpUnaryTight ("++", e);
  }

  /**
   * Post decrement
   *
   * @param e
   *        expression
   * @return <code><em>e</em>--</code>
   * @deprecated Use {@link #postdecr(IJExpression)} instead
   */
  @Deprecated
  @Nonnull
  public static JOpUnaryTight decr (@Nonnull final IJExpression e)
  {
    return postdecr (e);
  }

  /**
   * Post decrement
   *
   * @param e
   *        expression
   * @return <code><em>e</em>--</code>
   */
  @Nonnull
  public static JOpUnaryTight postdecr (@Nonnull final IJExpression e)
  {
    return new JOpUnaryTight (e, "--");
  }

  /**
   * Pre decrement
   *
   * @param e
   *        expression
   * @return <code>--<em>e</em></code>
   */
  @Nonnull
  public static JOpUnaryTight predecr (@Nonnull final IJExpression e)
  {
    return new JOpUnaryTight ("--", e);
  }

  /* -- Binary operators -- */

  @Nonnull
  public static JOpBinary plus (@Nonnull final IJExpression left, @Nonnull final IJExpression right)
  {
    return new JOpBinary (left, "+", right);
  }

  @Nonnull
  public static JOpBinary minus (@Nonnull final IJExpression left, @Nonnull final IJExpression right)
  {
    return new JOpBinary (left, "-", right);
  }

  @Nonnull
  public static JOpBinary mul (@Nonnull final IJExpression left, @Nonnull final IJExpression right)
  {
    return new JOpBinary (left, "*", right);
  }

  @Nonnull
  public static JOpBinary div (@Nonnull final IJExpression left, @Nonnull final IJExpression right)
  {
    return new JOpBinary (left, "/", right);
  }

  @Nonnull
  public static JOpBinary mod (@Nonnull final IJExpression left, @Nonnull final IJExpression right)
  {
    return new JOpBinary (left, "%", right);
  }

  @Nonnull
  public static JOpBinary shl (@Nonnull final IJExpression left, @Nonnull final IJExpression right)
  {
    return new JOpBinary (left, "<<", right);
  }

  @Nonnull
  public static JOpBinary shr (@Nonnull final IJExpression left, @Nonnull final IJExpression right)
  {
    return new JOpBinary (left, ">>", right);
  }

  @Nonnull
  public static JOpBinary shrz (@Nonnull final IJExpression left, @Nonnull final IJExpression right)
  {
    return new JOpBinary (left, ">>>", right);
  }

  @Nonnull
  public static JOpBinary band (@Nonnull final IJExpression left, @Nonnull final IJExpression right)
  {
    return new JOpBinary (left, "&", right);
  }

  @Nonnull
  public static JOpBinary bor (@Nonnull final IJExpression left, @Nonnull final IJExpression right)
  {
    return new JOpBinary (left, "|", right);
  }

  @Nonnull
  public static IJExpression cand (@Nonnull final IJExpression left, @Nonnull final IJExpression right)
  {
    // Inline optimizations :)
    if (left == JExpr.TRUE)
      return right;
    if (right == JExpr.TRUE)
      return left;
    if (left == JExpr.FALSE)
      return left; // JExpr.FALSE
    if (right == JExpr.FALSE)
      return right; // JExpr.FALSE
    return new JOpBinary (left, "&&", right);
  }

  @Nonnull
  public static IJExpression cor (@Nonnull final IJExpression left, @Nonnull final IJExpression right)
  {
    if (left == JExpr.TRUE)
      return left; // JExpr.TRUE
    if (right == JExpr.TRUE)
      return right; // JExpr.FALSE
    if (left == JExpr.FALSE)
      return right;
    if (right == JExpr.FALSE)
      return left;
    return new JOpBinary (left, "||", right);
  }

  @Nonnull
  public static JOpBinary xor (@Nonnull final IJExpression left, @Nonnull final IJExpression right)
  {
    return new JOpBinary (left, "^", right);
  }

  @Nonnull
  public static JOpBinary lt (@Nonnull final IJExpression left, @Nonnull final IJExpression right)
  {
    return new JOpBinary (left, "<", right);
  }

  @Nonnull
  public static JOpBinary lte (@Nonnull final IJExpression left, @Nonnull final IJExpression right)
  {
    return new JOpBinary (left, "<=", right);
  }

  @Nonnull
  public static JOpBinary gt (@Nonnull final IJExpression left, @Nonnull final IJExpression right)
  {
    return new JOpBinary (left, ">", right);
  }

  @Nonnull
  public static JOpBinary gte (@Nonnull final IJExpression left, @Nonnull final IJExpression right)
  {
    return new JOpBinary (left, ">=", right);
  }

  @Nonnull
  public static JOpBinary eq (@Nonnull final IJExpression left, @Nonnull final IJExpression right)
  {
    return new JOpBinary (left, "==", right);
  }

  @Nonnull
  public static JOpBinary ne (@Nonnull final IJExpression left, @Nonnull final IJExpression right)
  {
    return new JOpBinary (left, "!=", right);
  }

  @Nonnull
  public static JOpBinary _instanceof (@Nonnull final IJExpression left, @Nonnull final AbstractJType right)
  {
    return new JOpBinary (left, "instanceof", right);
  }

  /* -- Ternary operators -- */

  @Nonnull
  public static JOpTernary cond (@Nonnull final IJExpression cond,
                                 @Nonnull final IJExpression ifTrue,
                                 @Nonnull final IJExpression ifFalse)
  {
    return new JOpTernary (cond, "?", ifTrue, ":", ifFalse);
  }
}
