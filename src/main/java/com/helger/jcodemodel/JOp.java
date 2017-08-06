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
   * @param aExpr
   *        Expression to evaluate
   * @return <code>true</code> of a top level operator is present
   */
  public static boolean hasTopOp (@Nullable final IJExpression aExpr)
  {
    return (aExpr instanceof JOpUnary) || (aExpr instanceof JOpBinary);
  }

  /* -- Unary operators -- */

  @Nonnull
  public static JOpUnary minus (@Nonnull final IJExpression aExpr)
  {
    return new JOpUnary ("-", aExpr);
  }

  /**
   * Logical not <tt>'!x'</tt>.
   *
   * @param aExpr
   *        Expression to invert
   * @return Inverted expression
   */
  @Nonnull
  public static IJExpression not (@Nonnull final IJExpression aExpr)
  {
    // Inline optimizations :)
    if (aExpr == JExpr.TRUE)
      return JExpr.FALSE;
    if (aExpr == JExpr.FALSE)
      return JExpr.TRUE;
    return new JOpUnary ("!", aExpr);
  }

  @Nonnull
  public static JOpUnary complement (@Nonnull final IJExpression aExpr)
  {
    return new JOpUnary ("~", aExpr);
  }

  /**
   * Post increment
   *
   * @param aExpr
   *        expression
   * @return <code><em>aExpr</em>++</code>
   * @deprecated Use {@link #postincr(IJExpression)} instead
   */
  @Deprecated
  @Nonnull
  public static JOpUnaryTight incr (@Nonnull final IJExpression aExpr)
  {
    return postincr (aExpr);
  }

  /**
   * Post increment
   *
   * @param aExpr
   *        expression
   * @return <code><em>aExpr</em>++</code>
   */
  @Nonnull
  public static JOpUnaryTight postincr (@Nonnull final IJExpression aExpr)
  {
    return new JOpUnaryTight (aExpr, "++");
  }

  /**
   * Pre increment
   *
   * @param aExpr
   *        expression
   * @return <code>++<em>aExpr</em></code>
   */
  @Nonnull
  public static JOpUnaryTight preincr (@Nonnull final IJExpression aExpr)
  {
    return new JOpUnaryTight ("++", aExpr);
  }

  /**
   * Post decrement
   *
   * @param aExpr
   *        expression
   * @return <code><em>aExpr</em>--</code>
   * @deprecated Use {@link #postdecr(IJExpression)} instead
   */
  @Deprecated
  @Nonnull
  public static JOpUnaryTight decr (@Nonnull final IJExpression aExpr)
  {
    return postdecr (aExpr);
  }

  /**
   * Post decrement
   *
   * @param aExpr
   *        expression
   * @return <code><em>aExpr</em>--</code>
   */
  @Nonnull
  public static JOpUnaryTight postdecr (@Nonnull final IJExpression aExpr)
  {
    return new JOpUnaryTight (aExpr, "--");
  }

  /**
   * Pre decrement
   *
   * @param aExpr
   *        expression
   * @return <code>--<em>aExpr</em></code>
   */
  @Nonnull
  public static JOpUnaryTight predecr (@Nonnull final IJExpression aExpr)
  {
    return new JOpUnaryTight ("--", aExpr);
  }

  /* -- Binary operators -- */

  @Nonnull
  public static JOpBinary plus (@Nonnull final IJExpression aLhs, @Nonnull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, "+", aRhs);
  }

  @Nonnull
  public static JOpBinary minus (@Nonnull final IJExpression aLhs, @Nonnull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, "-", aRhs);
  }

  @Nonnull
  public static JOpBinary mul (@Nonnull final IJExpression aLhs, @Nonnull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, "*", aRhs);
  }

  @Nonnull
  public static JOpBinary div (@Nonnull final IJExpression aLhs, @Nonnull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, "/", aRhs);
  }

  @Nonnull
  public static JOpBinary mod (@Nonnull final IJExpression aLhs, @Nonnull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, "%", aRhs);
  }

  @Nonnull
  public static JOpBinary shl (@Nonnull final IJExpression aLhs, @Nonnull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, "<<", aRhs);
  }

  @Nonnull
  public static JOpBinary shr (@Nonnull final IJExpression aLhs, @Nonnull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, ">>", aRhs);
  }

  @Nonnull
  public static JOpBinary shrz (@Nonnull final IJExpression aLhs, @Nonnull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, ">>>", aRhs);
  }

  @Nonnull
  public static JOpBinary band (@Nonnull final IJExpression aLhs, @Nonnull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, "&", aRhs);
  }

  @Nonnull
  public static JOpBinary bor (@Nonnull final IJExpression aLhs, @Nonnull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, "|", aRhs);
  }

  @Nonnull
  public static IJExpression cand (@Nonnull final IJExpression aLhs, @Nonnull final IJExpression aRhs)
  {
    // Inline optimizations :)
    if (aLhs == JExpr.TRUE)
      return aRhs;
    if (aRhs == JExpr.TRUE)
      return aLhs;
    if (aLhs == JExpr.FALSE)
      return aLhs; // JExpr.FALSE
    if (aRhs == JExpr.FALSE)
      return aRhs; // JExpr.FALSE
    return new JOpBinary (aLhs, "&&", aRhs);
  }

  @Nonnull
  public static IJExpression cor (@Nonnull final IJExpression aLhs, @Nonnull final IJExpression aRhs)
  {
    if (aLhs == JExpr.TRUE)
      return aLhs; // JExpr.TRUE
    if (aRhs == JExpr.TRUE)
      return aRhs; // JExpr.FALSE
    if (aLhs == JExpr.FALSE)
      return aRhs;
    if (aRhs == JExpr.FALSE)
      return aLhs;
    return new JOpBinary (aLhs, "||", aRhs);
  }

  @Nonnull
  public static JOpBinary xor (@Nonnull final IJExpression aLhs, @Nonnull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, "^", aRhs);
  }

  @Nonnull
  public static JOpBinary lt (@Nonnull final IJExpression aLhs, @Nonnull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, "<", aRhs);
  }

  @Nonnull
  public static JOpBinary lte (@Nonnull final IJExpression aLhs, @Nonnull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, "<=", aRhs);
  }

  @Nonnull
  public static JOpBinary gt (@Nonnull final IJExpression aLhs, @Nonnull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, ">", aRhs);
  }

  @Nonnull
  public static JOpBinary gte (@Nonnull final IJExpression aLhs, @Nonnull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, ">=", aRhs);
  }

  @Nonnull
  public static JOpBinary eq (@Nonnull final IJExpression aLhs, @Nonnull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, "==", aRhs);
  }

  @Nonnull
  public static JOpBinary ne (@Nonnull final IJExpression aLhs, @Nonnull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, "!=", aRhs);
  }

  @Nonnull
  public static JOpBinary _instanceof (@Nonnull final IJExpression aLhs, @Nonnull final AbstractJType aRhs)
  {
    return new JOpBinary (aLhs, "instanceof", aRhs);
  }

  /* -- Ternary operators -- */

  @Nonnull
  public static JOpTernary cond (@Nonnull final IJExpression aCond,
                                 @Nonnull final IJExpression aIfTrue,
                                 @Nonnull final IJExpression aIfFalse)
  {
    return new JOpTernary (aCond, "?", aIfTrue, ":", aIfFalse);
  }
}
