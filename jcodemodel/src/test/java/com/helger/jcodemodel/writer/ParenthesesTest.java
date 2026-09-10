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
package com.helger.jcodemodel.writer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.jspecify.annotations.NonNull;
import org.junit.Test;

import com.helger.base.io.nonblocking.NonBlockingStringWriter;
import com.helger.jcodemodel.IJAssignmentTarget;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.IJFormatter;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JOp;
import com.helger.jcodemodel.writer.settings.Parentheses.EParenthesesStrategy;

/**
 * Test the parentheses that are printed around the operands of an operator. Every expression in
 * here must keep its meaning under all three {@link EParenthesesStrategy} values - see
 * <code>OperatorTestGen</code> in the <code>jcodemodeltests</code> module for the tests that
 * additionally compile and execute the generated code.
 */
public final class ParenthesesTest
{
  private static final JCodeModel CM = new JCodeModel ();
  private static final IJExpression A = JExpr.ref ("a");
  private static final IJExpression B = JExpr.ref ("b");
  private static final IJExpression C = JExpr.ref ("c");
  private static final IJAssignmentTarget TA = JExpr.ref ("a");

  @NonNull
  private static String _render (@NonNull final EParenthesesStrategy eStrategy, @NonNull final IJExpression aExpr)
  {
    final FormatterSettings aSettings = new FormatterSettings ();
    aSettings.parentheses.global = eStrategy;
    try (final NonBlockingStringWriter aSW = new NonBlockingStringWriter ();
         final IJFormatter aFormatter = new JFormatter (new SourcePrintWriter (aSW, JCMWriter.DEFAULT_NEW_LINE),
                                                        aSettings))
    {
      aExpr.generate (aFormatter);
      return aSW.getAsString ();
    }
    catch (final IOException ex)
    {
      throw new UncheckedIOException (ex);
    }
  }

  private static void _assertRequired (@NonNull final String sExpected, @NonNull final IJExpression aExpr)
  {
    assertEquals (sExpected, _render (EParenthesesStrategy.REQUIRED, aExpr));
  }

  private static void _assertNoToken (@NonNull final String sExpected, @NonNull final IJExpression aExpr)
  {
    assertEquals (sExpected, _render (EParenthesesStrategy.NOTOKEN, aExpr));
  }

  private static void _assertAlways (@NonNull final String sExpected, @NonNull final IJExpression aExpr)
  {
    assertEquals (sExpected, _render (EParenthesesStrategy.ALWAYS, aExpr));
  }

  /**
   * A left associative operator keeps its right hand operand parenthesized on equal precedence,
   * because <code>a-(b-c)</code> is not <code>a-b-c</code>.
   */
  @Test
  public void testBinaryAssociativity ()
  {
    _assertRequired ("a -(b -c)", JOp.minus (A, JOp.minus (B, C)));
    _assertRequired ("a -b -c", JOp.minus (JOp.minus (A, B), C));
    _assertRequired ("a -(b + c)", JOp.minus (A, JOp.plus (B, C)));
    _assertRequired ("a/(b/c)", JOp.div (A, JOp.div (B, C)));
    _assertRequired ("a/(b*c)", JOp.div (A, JOp.mul (B, C)));
    _assertRequired ("a%(b%c)", JOp.mod (A, JOp.mod (B, C)));
    _assertRequired ("a<<(b<<c)", JOp.shl (A, JOp.shl (B, C)));
    _assertRequired ("a >>(b >>c)", JOp.shr (A, JOp.shr (B, C)));
    // A tighter binding operand never needs parentheses
    _assertRequired ("a*b + c", JOp.plus (JOp.mul (A, B), C));
    _assertRequired ("a + b*c", JOp.plus (A, JOp.mul (B, C)));
    _assertRequired ("a&&b||c", JOp.cor (JOp.cand (A, B), C));
    _assertRequired ("a&&(b||c)", JOp.cand (A, JOp.cor (B, C)));
  }

  /**
   * An operator expression used as the target of a dereference must be parenthesized, because
   * <code>.</code> and <code>[]</code> bind tighter than any operator.
   */
  @Test
  public void testDereferenceTarget ()
  {
    _assertRequired ("(a + b).toString()", JOp.plus (A, B).invoke ("toString"));
    _assertRequired ("(a + b).length", JOp.plus (A, B).ref ("length"));
    _assertRequired ("(a + b)[c]", JOp.plus (A, B).component (C));
    _assertRequired ("(a?b:c).hashCode()", JExpr.cond (A, B, C).invoke ("hashCode"));
    _assertRequired ("(a = b).toString()", JExpr.assign (TA, B).invoke ("toString"));
    _assertRequired ("(a++).toString()", JOp.postincr (A).invoke ("toString"));
    _assertRequired ("((java.lang.String) a).length()", JExpr.cast (CM.ref (String.class), A).invoke ("length"));
    _assertRequired ("((int[]) a).length", JExpr.cast (CM.INT.array (), A).ref ("length"));
    // Chained dereferences are left associative, so no parentheses are needed
    _assertRequired ("a.b().c()", A.invoke ("b").invoke ("c"));
    _assertRequired ("a.b.c", A.ref ("b").ref ("c"));
    _assertRequired ("a[b][c]", A.component (B).component (C));
  }

  /**
   * A type may never be parenthesized, not even in the strictest strategy.
   */
  @Test
  public void testTypeOperandIsNeverParenthesized ()
  {
    final IJExpression aInstanceOf = JOp._instanceof (A, CM.ref (String.class));
    _assertRequired ("a instanceof java.lang.String", aInstanceOf);
    _assertNoToken ("a instanceof java.lang.String", aInstanceOf);
    _assertAlways ("(a) instanceof java.lang.String", aInstanceOf);

    final IJExpression aStatic = CM.ref (Math.class).staticInvoke ("max").arg (A).arg (B);
    _assertRequired ("java.lang.Math.max(a, b)", aStatic);
    _assertNoToken ("java.lang.Math.max(a, b)", aStatic);
    _assertAlways ("java.lang.Math.max(a, b)", aStatic);
  }

  /**
   * Stacked unary operators and negative literals must never be glued into a different token.
   */
  @Test
  public void testUnaryTokensAreNotGlued ()
  {
    _assertRequired ("-(-a)", JOp.minus (JOp.minus (A)));
    _assertRequired ("-(--a)", JOp.minus (JOp.predecr (A)));
    _assertRequired ("-a--", JOp.minus (JOp.postdecr (A)));
    _assertRequired ("!(!a)", JOp.not (JOp.not (A)));
    // A negative literal is a plain token, so only the formatter can keep the tokens apart
    _assertRequired ("a - -1", JOp.minus (A, JExpr.lit (-1)));
    _assertRequired ("a +-1", JOp.plus (A, JExpr.lit (-1)));
    _assertRequired ("a*-1", JOp.mul (A, JExpr.lit (-1)));

    // "-a--" groups as "-(a--)", and the postfix operator yields the value before the decrement
    int a = 5;
    final int b = -a--;
    assertEquals (-5, b);
    assertEquals (4, a);
  }

  /**
   * A cast is deliberately never treated as associative, because <code>(T) -a</code> is parsed as a
   * subtraction when <code>T</code> is a reference type.
   */
  @Test
  public void testCast ()
  {
    _assertRequired ("(A)(-a)", JExpr.cast (CM.ref ("A"), JOp.minus (A)));
    _assertRequired ("(A)((B) a)", JExpr.cast (CM.ref ("A"), JExpr.cast (CM.ref ("B"), A)));
    _assertRequired ("(A)(a + b)", JExpr.cast (CM.ref ("A"), JOp.plus (A, B)));
    _assertRequired ("-((A) a)", JOp.minus (JExpr.cast (CM.ref ("A"), A)));
    // A tighter binding operand needs no parentheses
    _assertRequired ("(A) a.b()", JExpr.cast (CM.ref ("A"), A.invoke ("b")));
  }

  /**
   * Only the condition of a ternary operator can become ambiguous - the second operand is enclosed
   * by <code>?</code> and <code>:</code> and the third one is right associative.
   */
  @Test
  public void testTernary ()
  {
    _assertRequired ("(a?b:c)?a:b", JExpr.cond (JExpr.cond (A, B, C), A, B));
    _assertRequired ("a?b:c?a:b", JExpr.cond (A, B, JExpr.cond (C, A, B)));
    _assertRequired ("a?c?a:b:b", JExpr.cond (A, JExpr.cond (C, A, B), B));
    _assertRequired ("(a = b)?a:b", JExpr.cond (JExpr.assign (TA, B), A, B));
    _assertRequired ("a == b?a:b", JExpr.cond (JOp.eq (A, B), A, B));
  }

  /**
   * An assignment is right associative and its left hand side is always an assignment target.
   */
  @Test
  public void testAssignment ()
  {
    _assertRequired ("a = b = c", JExpr.assign (TA, JExpr.assign ((IJAssignmentTarget) B, C)));
    _assertRequired ("a = b + c", JExpr.assign (TA, JOp.plus (B, C)));
    _assertRequired ("a = b?a:c", JExpr.assign (TA, JExpr.cond (B, A, C)));
  }

  /**
   * The looser strategies must stay legal Java as well - they may only add parentheses where they
   * are allowed.
   */
  @Test
  public void testLooserStrategiesKeepTheMeaning ()
  {
    final IJExpression aExpr = JOp.plus (A, B).invoke ("toString");
    _assertNoToken ("(a + b).toString()", aExpr);
    _assertAlways ("((a)+(b)).toString()", aExpr);

    final IJExpression aNested = JOp.minus (A, JOp.minus (B, C));
    _assertNoToken ("a -(b -c)", aNested);
    _assertAlways ("(a)-((b)-(c))", aNested);

    final IJExpression aCast = JExpr.cast (CM.ref (String.class), A).invoke ("length");
    _assertNoToken ("((java.lang.String) a).length()", aCast);
    _assertAlways ("((java.lang.String)(a)).length()", aCast);
  }
}
