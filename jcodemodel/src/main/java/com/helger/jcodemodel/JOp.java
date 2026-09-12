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

import com.helger.annotation.concurrent.Immutable;
import com.helger.jcodemodel.JOpBinary.EBinaryOp;
import com.helger.jcodemodel.JOpTernary.ETernaryOp;
import com.helger.jcodemodel.JOpUnary.EUnaryOp;
import com.helger.jcodemodel.writer.settings.Parentheses.EParenthesesStrategy;

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
   * The position of an operand relative to its operator. The same values are used to express the
   * associativity of an operator, so that both can be compared directly.
   */
  public static enum ESide
  {
    /** Operand left of the operator - as associativity: left associative */
    LEFT,
    /** Operand right of the operator - as associativity: right associative */
    RIGHT,
    /**
     * As operand position: the operand is enclosed by tokens of the operator itself (like the index
     * of an array access) and can therefore never become ambiguous.<br>
     * As associativity: the operator is not associative.
     */
    NONE
  }

  /**
   * The binding strength of an expression, ordered from the tightest to the loosest binding. The
   * ordinal of a constant is its binding strength, so the declaration order - and only it -
   * decides whether an operand has to be surrounded by parentheses.
   *
   * @see <a href="https://docs.oracle.com/javase/specs/jls/se17/html/jls-15.html">JLS 15 -
   *      Expressions</a>
   */
  public static enum EPrecedence
  {
    /** Not an operator at all: a literal, a variable name, a method reference, ... */
    TOKEN (ESide.NONE),
    /** <code>a.b</code>, <code>a ()</code>, <code>a[b]</code> */
    DEREF (ESide.LEFT),
    /** <code>a++</code>, <code>a--</code> */
    POSTFIX (ESide.NONE),
    /**
     * <code>++a</code>, <code>--a</code>, <code>!a</code>, <code>~a</code>, <code>-a</code>.
     * Formally right associative, but deliberately treated as non associative so that stacked
     * operators cannot be glued into a different token - <code>-(-a)</code> must never be printed
     * as <code>--a</code>.
     */
    UNARY (ESide.NONE),
    /**
     * <code>(T) a</code>. Listed after {@link #UNARY} so that a cast used as the operand of a
     * unary operator keeps its parentheses. The operand of the cast itself may not bind looser
     * than a postfix expression - see JLS 15.16 - which {@link JCast} expresses by grouping
     * against {@link #POSTFIX}.
     */
    CAST (ESide.NONE),
    /** <code>a * b</code>, <code>a / b</code>, <code>a % b</code> */
    MULTIPLICATIVE (ESide.LEFT),
    /** <code>a + b</code>, <code>a - b</code> */
    ADDITIVE (ESide.LEFT),
    /** <code>a &lt;&lt; b</code>, <code>a &gt;&gt; b</code>, <code>a &gt;&gt;&gt; b</code> */
    SHIFT (ESide.LEFT),
    /** <code>a &lt; b</code>, <code>a instanceof B</code> */
    RELATIONAL (ESide.LEFT),
    /** <code>a == b</code>, <code>a != b</code> */
    EQUALITY (ESide.LEFT),
    /** <code>a &amp; b</code> */
    BITWISE_AND (ESide.LEFT),
    /** <code>a ^ b</code> */
    BITWISE_XOR (ESide.LEFT),
    /** <code>a | b</code> */
    BITWISE_OR (ESide.LEFT),
    /** <code>a &amp;&amp; b</code> */
    LOGICAL_AND (ESide.LEFT),
    /** <code>a || b</code> */
    LOGICAL_OR (ESide.LEFT),
    /** <code>a ? b : c</code> */
    TERNARY (ESide.RIGHT),
    /** <code>a = b</code>, <code>a += b</code> */
    ASSIGNMENT (ESide.RIGHT),
    /**
     * <code>a -&gt; b</code>. Binds looser than an assignment, because the body of a lambda
     * extends as far to the right as possible: <code>a -&gt; v = a</code> is
     * <code>a -&gt; (v = a)</code> - see JLS 15.27.
     */
    LAMBDA (ESide.RIGHT);

    private final ESide m_eAssociativity;

    EPrecedence (@NonNull final ESide eAssociativity)
    {
      m_eAssociativity = eAssociativity;
    }

    /**
     * @return The side an operand of this precedence is bound to, or {@link ESide#NONE} if the
     *         operator is not associative. Never <code>null</code>.
     */
    @NonNull
    public ESide associativity ()
    {
      return m_eAssociativity;
    }

    /**
     * @param aOther
     *        The precedence to compare to. May not be <code>null</code>.
     * @return <code>true</code> if this precedence binds strictly tighter than the provided one.
     */
    public boolean higherThan (@NonNull final EPrecedence aOther)
    {
      return ordinal () < aOther.ordinal ();
    }
  }

  /**
   * Determine whether an operand of an operator must be surrounded by parentheses. An operand that
   * is a type - like the right hand side of <code>instanceof</code> - may never be parenthesized
   * and must therefore not be passed in here at all.
   *
   * @param eStrategy
   *        The parentheses strategy taken from the formatter settings. May not be
   *        <code>null</code>.
   * @param aOperator
   *        The precedence of the operator the operand belongs to. May not be <code>null</code>.
   * @param aOperand
   *        The precedence of the operand itself. May not be <code>null</code>.
   * @param eOperandSide
   *        The position of the operand relative to the operator. May not be <code>null</code>.
   * @return <code>true</code> if parentheses must be printed around the operand.
   */
  public static boolean needsParentheses (@NonNull final EParenthesesStrategy eStrategy,
                                          @NonNull final EPrecedence aOperator,
                                          @NonNull final EPrecedence aOperand,
                                          @NonNull final ESide eOperandSide)
  {
    return switch (eStrategy)
    {
      case ALWAYS -> true;
      case NOTOKEN -> aOperand != EPrecedence.TOKEN;
      // An operand that is enclosed by the tokens of the operator itself - like the second operand
      // of the ternary operator - can never become ambiguous.
      // A looser binding operand must be parenthesized. On equal binding this is only needed if
      // the operand sits on the side the operator does not associate to: "a-(b-c)" is not "a-b-c",
      // while "(a-b)-c" is.
      case REQUIRED -> eOperandSide != ESide.NONE &&
                       (aOperator.higherThan (aOperand) ||
                        (aOperand == aOperator && aOperator.associativity () != eOperandSide));
    };
  }

  /* -- Unary operators -- */

  @NonNull
  public static JOpUnary minus (@NonNull final IJExpression aExpr)
  {
    return new JOpUnary (EUnaryOp.MINUS, aExpr);
  }

  @NonNull
  public static JOpUnary positive (@NonNull final IJExpression aExpr)
  {
    return new JOpUnary (EUnaryOp.POSITIVE, aExpr);
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
    return new JOpUnary (EUnaryOp.LOGICAL_NOT, aExpr);
  }

  @NonNull
  public static JOpUnary complement (@NonNull final IJExpression aExpr)
  {
    return new JOpUnary (EUnaryOp.BITWISE_NOT, aExpr);
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
    return new JOpUnary (EUnaryOp.POST_INCR, aExpr);
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
    return new JOpUnary (EUnaryOp.PRE_INCR, aExpr);
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
    return new JOpUnary (EUnaryOp.POST_DECR, aExpr);
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
    return new JOpUnary (EUnaryOp.PRE_DECR, aExpr);
  }

  /* -- Binary operators -- */

  @NonNull
  public static JOpBinary plus (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, EBinaryOp.ADD, aRhs);
  }

  @NonNull
  public static JOpBinary minus (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, EBinaryOp.SUBTRACT, aRhs);
  }

  @NonNull
  public static JOpBinary mul (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, EBinaryOp.MULTIPLY, aRhs);
  }

  @NonNull
  public static JOpBinary div (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, EBinaryOp.DIVIDE, aRhs);
  }

  @NonNull
  public static JOpBinary mod (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, EBinaryOp.MODULUS, aRhs);
  }

  @NonNull
  public static JOpBinary shl (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, EBinaryOp.SHIFT_LEFT, aRhs);
  }

  @NonNull
  public static JOpBinary shr (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, EBinaryOp.SHIFT_RIGHT, aRhs);
  }

  @NonNull
  public static JOpBinary shrz (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, EBinaryOp.SHIFT_RIGHT_ZERO, aRhs);
  }

  @NonNull
  public static JOpBinary band (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, EBinaryOp.BITWISE_AND, aRhs);
  }

  @NonNull
  public static JOpBinary bor (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, EBinaryOp.BITWISE_OR, aRhs);
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
    return new JOpBinary (aLhs, EBinaryOp.LOGICAL_AND, aRhs);
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
    return new JOpBinary (aLhs, EBinaryOp.LOGICAL_OR, aRhs);
  }

  @NonNull
  public static JOpBinary xor (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, EBinaryOp.BITWISE_XOR, aRhs);
  }

  @NonNull
  public static JOpBinary lt (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, EBinaryOp.LOWER, aRhs);
  }

  @NonNull
  public static JOpBinary lte (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, EBinaryOp.LOWER_EQUAL, aRhs);
  }

  @NonNull
  public static JOpBinary gt (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, EBinaryOp.GREATER, aRhs);
  }

  @NonNull
  public static JOpBinary gte (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, EBinaryOp.GREATER_EQUAL, aRhs);
  }

  @NonNull
  public static JOpBinary eq (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, EBinaryOp.EQUALS, aRhs);
  }

  @NonNull
  public static JOpBinary ne (@NonNull final IJExpression aLhs, @NonNull final IJExpression aRhs)
  {
    return new JOpBinary (aLhs, EBinaryOp.NOT_EQUALS, aRhs);
  }

  @NonNull
  public static JOpBinary _instanceof (@NonNull final IJExpression aLhs, @NonNull final AbstractJType aRhs)
  {
    return new JOpBinary (aLhs, EBinaryOp.INSTANCE_OF, aRhs);
  }

  /* -- Ternary operators -- */

  @NonNull
  public static JOpTernary cond (@NonNull final IJExpression aCond,
                                 @NonNull final IJExpression aIfTrue,
                                 @NonNull final IJExpression aIfFalse)
  {
    return new JOpTernary (ETernaryOp.TERN_COND, aCond, aIfTrue, aIfFalse);
  }
}
