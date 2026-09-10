/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2026 Philip Helger + contributors
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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.jcodemodel.JOpBinary.BinaryOp;
import com.helger.jcodemodel.JOpTernary.TernaryOp;
import com.helger.jcodemodel.JOpUnary.UnaryOp;

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

  /// expression precedence. Lower position means higher priority.
  ///
  /// @see https://docs.oracle.com/javase/tutorial/java/nutsandbolts/operators.html
  /// @see https://introcs.cs.princeton.edu/java/11precedence/
  public static enum Precedence
  {
    TOKEN, // 12, myVar . Not an operator, but required for JExpr precedence.
    DEREF, // (a), [a], a.b
    POSTFIX, // a ++
    UNARY, // ++a, ! a, -a
    CAST, // (a) b
    MULTIPLICATIVE, // a * b, a / b
    ADDITIVE, // a + b
    SHIFT, // a >> b
    RELATIONAL, // a > b, a instanceof b
    EQUALITY, // a == b, a != b
    BITWISE_AND, // a & b
    BITWISE_XOR, // a ^ b
    BITWISE_OR, // a | b
    LOGICAL_AND, // a && b
    LOGICAL_OR, // a || b
    TERNAY, // a ? b : c
    ASSIGNMENT, // a=b, a += b
    LAMBDA // a -> b
    ;

    public Precedence lowest (Precedence other)
    {
      if (other == null)
        return this;
      if (other.ordinal () < ordinal ())
        return other;
      return this;
    }

    public boolean higherThan (@NonNull Precedence other)
    {
      return ordinal () < other.ordinal ();
    }
  }

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

  @NonNull
  public static JOpUnary minus (@NonNull final IJExpression aExpr)
  {
    return new JOpUnary (UnaryOp.MINUS, aExpr);
  }

  /**
   * Logical not <code>'!x'</code>.
   *
   * @param aExpr
   *        Expression to invert
   * @return Inverted expression
   */
  @NonNull
  public static IJExpression not (@NonNull final IJExpression aExpr)
  {
    // Inline optimizations :)
    if (aExpr == JExpr.TRUE)
      return JExpr.FALSE;
    if (aExpr == JExpr.FALSE)
      return JExpr.TRUE;
    return new JOpUnary (UnaryOp.LOGICAL_NOT, aExpr);
  }

  @NonNull
  public static JOpUnary complement (@NonNull final IJExpression aExpr)
  {
    return new JOpUnary (UnaryOp.BITWISE_NOT, aExpr);
  }

  /**
   * Post increment
   *
   * @param aExpr
   *        expression
   * @return <code><em>aExpr</em>++</code>
   */
  @NonNull
  public static JOpUnary postincr (@NonNull final IJExpression aExpr)
  {
    return new JOpUnary (UnaryOp.POST_INCR, aExpr);
  }

  /**
   * Pre increment
   *
   * @param aExpr
   *        expression
   * @return <code>++<em>aExpr</em></code>
   */
  @NonNull
  public static JOpUnary preincr (@NonNull final IJExpression aExpr)
  {
    return new JOpUnary (UnaryOp.PRE_INCR, aExpr);
  }

  /**
   * Post decrement
   *
   * @param aExpr
   *        expression
   * @return <code><em>aExpr</em>--</code>
   */
  @NonNull
  public static JOpUnary postdecr (@NonNull final IJExpression aExpr)
  {
    return new JOpUnary (UnaryOp.POST_DECR, aExpr);
  }

  /**
   * Pre decrement
   *
   * @param aExpr
   *        expression
   * @return <code>--<em>aExpr</em></code>
   */
  @NonNull
  public static JOpUnary predecr (@NonNull final IJExpression aExpr)
  {
    return new JOpUnary (UnaryOp.PRE_DECR, aExpr);
  }

  /* -- Binary operators -- */

  @NonNull
  public static JOpBinary plus (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, BinaryOp.ADD, aRhs);
  }

  @NonNull
  public static JOpBinary minus (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, BinaryOp.SUBSTRACT, aRhs);
  }

  @NonNull
  public static JOpBinary mul (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, BinaryOp.MULTIPLY, aRhs);
  }

  @NonNull
  public static JOpBinary div (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, BinaryOp.DIVIDE, aRhs);
  }

  @NonNull
  public static JOpBinary mod (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, BinaryOp.MODULUS, aRhs);
  }

  @NonNull
  public static JOpBinary shl (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, BinaryOp.SHIFT_LEFT, aRhs);
  }

  @NonNull
  public static JOpBinary shr (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, BinaryOp.SHIFT_RIGHT, aRhs);
  }

  @NonNull
  public static JOpBinary shrz (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, BinaryOp.SHIFT_RIGHT_ZERO, aRhs);
  }

  @NonNull
  public static JOpBinary band (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, BinaryOp.BITWISE_AND, aRhs);
  }

  @NonNull
  public static JOpBinary bor (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, BinaryOp.BITWISE_OR, aRhs);
  }

  @NonNull
  public static IJExpression cand (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
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
    return new JOpBinary (aLhs, BinaryOp.LOGICAL_AND, aRhs);
  }

  @NonNull
  public static IJExpression cor (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    if (aLhs == JExpr.TRUE)
      return aLhs; // JExpr.TRUE
    if (aRhs == JExpr.TRUE)
      return aRhs; // JExpr.FALSE
    if (aLhs == JExpr.FALSE)
      return aRhs;
    if (aRhs == JExpr.FALSE)
      return aLhs;
    return new JOpBinary (aLhs, BinaryOp.LOGICAL_OR, aRhs);
  }

  @NonNull
  public static JOpBinary xor (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, BinaryOp.BITWISE_XOR, aRhs);
  }

  @NonNull
  public static JOpBinary lt (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, BinaryOp.LOWER, aRhs);
  }

  @NonNull
  public static JOpBinary lte (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, BinaryOp.LOWER_EQUAL, aRhs);
  }

  @NonNull
  public static JOpBinary gt (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, BinaryOp.GREATER, aRhs);
  }

  @NonNull
  public static JOpBinary gte (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, BinaryOp.GREATER_EQUAL, aRhs);
  }

  @NonNull
  public static JOpBinary eq (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, BinaryOp.EQUALS, aRhs);
  }

  @NonNull
  public static JOpBinary ne (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, BinaryOp.NOT_EQUALS, aRhs);
  }

  @NonNull
  public static JOpBinary _instanceof (@NonNull final IJExpression aLhs, @NonNull final AbstractJType aRhs)
  {
    return new JOpBinary (aLhs, BinaryOp.INSTANCE_OF, aRhs);
  }

  /* -- Ternary operators -- */

  @NonNull
  public static JOpTernary cond (@NonNull final IJExpression aCond,
                                 @NonNull final IJExpression aIfTrue,
                                 @NonNull final IJExpression aIfFalse)
  {
    return new JOpTernary (TernaryOp.TERN_COND, aCond, aIfTrue, aIfFalse);
  }
}
