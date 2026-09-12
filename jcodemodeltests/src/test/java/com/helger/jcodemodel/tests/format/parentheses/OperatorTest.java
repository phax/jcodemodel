/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.jcodemodel.tests.format.parentheses;

import org.junit.Assert;
import org.junit.Test;

/// results should be the same for each implementation
public class OperatorTest {

  @FunctionalInterface
  interface IF1 {
    int multIfSameOddityElseAdd(int a, int b);
  }

  @Test
  public void testMultIfSameOddityElseAdd() {
    for (IF1 f : new IF1[] {
        OperatorParenthesesAlways::multIfSameOddityElseAdd,
        OperatorParenthesesNoToken::multIfSameOddityElseAdd,
        OperatorParenthesesRequired::multIfSameOddityElseAdd
    }) {
      Assert.assertEquals(1, f.multIfSameOddityElseAdd(1, 1));
      Assert.assertEquals(4, f.multIfSameOddityElseAdd(2, 2));
      Assert.assertEquals(5, f.multIfSameOddityElseAdd(2, 3));
      Assert.assertEquals(8, f.multIfSameOddityElseAdd(2, 4));
      Assert.assertEquals(7, f.multIfSameOddityElseAdd(2, 5));
    }
  }

  @FunctionalInterface
  interface IF2 {
    char representBools(boolean a, boolean b);
  }

  @Test
  public void testRepresentBools() {
    for (IF2 f : new IF2[] {
        OperatorParenthesesAlways::representBools,
        OperatorParenthesesNoToken::representBools,
        OperatorParenthesesRequired::representBools
    }) {
      Assert.assertEquals('0', f.representBools(false, false));
      Assert.assertEquals('1', f.representBools(false, true));
      Assert.assertEquals('2', f.representBools(true, false));
      Assert.assertEquals('3', f.representBools(true, true));
    }
  }

  @FunctionalInterface
  interface IF3 {
    String concat(String a, String b);
  }

  @Test
  public void testConcat() {
    for (IF3 f : new IF3[] {
        OperatorParenthesesAlways::concat,
        OperatorParenthesesNoToken::concat,
        OperatorParenthesesRequired::concat
    }) {
      Assert.assertEquals(null, f.concat(null, null));
      Assert.assertEquals("a", f.concat("a", null));
      Assert.assertEquals("b", f.concat(null, "b"));
      Assert.assertEquals("ab", f.concat("a", "b"));
    }
  }

  @FunctionalInterface
  interface IF4 {
    int bitwiseImply(int a, int b);
  }

  @Test
  public void testBitwiseImply() {
    for (IF4 f : new IF4[] {
        OperatorParenthesesAlways::bitwiseImply,
        OperatorParenthesesNoToken::bitwiseImply,
        OperatorParenthesesRequired::bitwiseImply
    }) {
      Assert.assertEquals(0xffffffff, f.bitwiseImply(0, 1));
    }
  }

  @FunctionalInterface
  interface IF5
  {
    boolean isSortedAsc (int [] arr);
  }

  @Test
  public void testIsSortedAsc ()
  {
    for (IF5 f : new IF5 [] { OperatorParenthesesAlways::isSortedAsc,
                              OperatorParenthesesNoToken::isSortedAsc,
                              OperatorParenthesesRequired::isSortedAsc })
    {
      Assert.assertTrue (f.isSortedAsc (null));
      Assert.assertTrue (f.isSortedAsc (new int [] { 0 }));
      Assert.assertTrue (f.isSortedAsc (new int [] { 0, 1 }));
      Assert.assertTrue (f.isSortedAsc (new int [] { 0, 1, 2, 3, 4 }));
      Assert.assertFalse (f.isSortedAsc (new int [] { 0, 1, 3, 2, 4 }));
    }
  }

  @FunctionalInterface
  interface IF6 {
    int apply(int a, int b, int c);
  }

  /// a left associative operator must keep its right operand grouped
  @Test
  public void testOperatorAssociativity() {
    for (IF6 f : new IF6[] {
        OperatorParenthesesAlways::subChain,
        OperatorParenthesesNoToken::subChain,
        OperatorParenthesesRequired::subChain
    }) {
      Assert.assertEquals(7, f.apply(10, 5, 2));
      Assert.assertEquals(0, f.apply(0, 0, 0));
      Assert.assertEquals(-4, f.apply(1, 8, 3));
    }
    for (IF6 f : new IF6[] {
        OperatorParenthesesAlways::divChain,
        OperatorParenthesesNoToken::divChain,
        OperatorParenthesesRequired::divChain
    }) {
      Assert.assertEquals(10, f.apply(100, 20, 2));
      Assert.assertEquals(3, f.apply(12, 8, 2));
    }
    for (IF6 f : new IF6[] {
        OperatorParenthesesAlways::shiftChain,
        OperatorParenthesesNoToken::shiftChain,
        OperatorParenthesesRequired::shiftChain
    }) {
      Assert.assertEquals(256, f.apply(1024, 4, 1));
      Assert.assertEquals(512, f.apply(1024, 3, 1));
    }
  }

  @FunctionalInterface
  interface IF7 {
    int apply(String a, String b);
  }

  @FunctionalInterface
  interface IF8 {
    int apply(Object o);
  }

  @FunctionalInterface
  interface IF9 {
    int apply(boolean t, String a, String b);
  }

  @FunctionalInterface
  interface IF10 {
    int apply(String a);
  }

  @FunctionalInterface
  interface IF11 {
    int apply(Object o, int i);
  }

  /// an expression used as the target of a "." or "[]" must be grouped
  @Test
  public void testDereferenceTarget() {
    for (IF7 f : new IF7[] {
        OperatorParenthesesAlways::sumLength,
        OperatorParenthesesNoToken::sumLength,
        OperatorParenthesesRequired::sumLength
    }) {
      Assert.assertEquals(5, f.apply("ab", "cde"));
      Assert.assertEquals(0, f.apply("", ""));
    }
    for (IF8 f : new IF8[] {
        OperatorParenthesesAlways::castLength,
        OperatorParenthesesNoToken::castLength,
        OperatorParenthesesRequired::castLength
    }) {
      Assert.assertEquals(5, f.apply("hello"));
    }
    for (IF9 f : new IF9[] {
        OperatorParenthesesAlways::condLength,
        OperatorParenthesesNoToken::condLength,
        OperatorParenthesesRequired::condLength
    }) {
      Assert.assertEquals(2, f.apply(true, "ab", "cdef"));
      Assert.assertEquals(4, f.apply(false, "ab", "cdef"));
    }
    for (IF10 f : new IF10[] {
        OperatorParenthesesAlways::assignLength,
        OperatorParenthesesNoToken::assignLength,
        OperatorParenthesesRequired::assignLength
    }) {
      Assert.assertEquals(3, f.apply("abc"));
    }
    for (IF11 f : new IF11[] {
        OperatorParenthesesAlways::castArrayComponent,
        OperatorParenthesesNoToken::castArrayComponent,
        OperatorParenthesesRequired::castArrayComponent
    }) {
      Assert.assertEquals(8, f.apply(new int[] { 7, 8, 9 }, 1));
    }
    for (IF8 f : new IF8[] {
        OperatorParenthesesAlways::castArrayLength,
        OperatorParenthesesNoToken::castArrayLength,
        OperatorParenthesesRequired::castArrayLength
    }) {
      Assert.assertEquals(3, f.apply(new int[] { 7, 8, 9 }));
    }
  }

  @FunctionalInterface
  interface IF12 {
    int apply(int a);
  }

  @FunctionalInterface
  interface IF13 {
    Integer apply(int a);
  }

  /// stacked unary operators and negative literals must not be glued into a single token
  @Test
  public void testUnaryTokens() {
    for (IF12 f : new IF12[] {
        OperatorParenthesesAlways::negNeg,
        OperatorParenthesesNoToken::negNeg,
        OperatorParenthesesRequired::negNeg
    }) {
      Assert.assertEquals(5, f.apply(5));
      Assert.assertEquals(-5, f.apply(-5));
    }
    for (IF12 f : new IF12[] {
        OperatorParenthesesAlways::minusNegLiteral,
        OperatorParenthesesNoToken::minusNegLiteral,
        OperatorParenthesesRequired::minusNegLiteral
    }) {
      Assert.assertEquals(6, f.apply(5));
      Assert.assertEquals(1, f.apply(0));
    }
    for (IF13 f : new IF13[] {
        OperatorParenthesesAlways::castNeg,
        OperatorParenthesesNoToken::castNeg,
        OperatorParenthesesRequired::castNeg
    }) {
      Assert.assertEquals(Integer.valueOf(-5), f.apply(5));
    }
  }

  @FunctionalInterface
  interface IF14 {
    int apply(int a, int b);
  }

  /// the test expression of while, do and switch is always parenthesized
  @Test
  public void testStatementTestExpression() {
    for (IF12 f : new IF12[] {
        OperatorParenthesesAlways::countDownWhile,
        OperatorParenthesesNoToken::countDownWhile,
        OperatorParenthesesRequired::countDownWhile
    }) {
      Assert.assertEquals(0, f.apply(0));
      Assert.assertEquals(4, f.apply(4));
    }
    for (IF12 f : new IF12[] {
        OperatorParenthesesAlways::doubleUntilDo,
        OperatorParenthesesNoToken::doubleUntilDo,
        OperatorParenthesesRequired::doubleUntilDo
    }) {
      Assert.assertEquals(192, f.apply(3));
      // the body of a do loop always runs at least once
      Assert.assertEquals(256, f.apply(128));
    }
    for (IF14 f : new IF14[] {
        OperatorParenthesesAlways::switchOnSum,
        OperatorParenthesesNoToken::switchOnSum,
        OperatorParenthesesRequired::switchOnSum
    }) {
      Assert.assertEquals(10, f.apply(0, 0));
      Assert.assertEquals(11, f.apply(1, 0));
      Assert.assertEquals(11, f.apply(-1, 2));
      Assert.assertEquals(-1, f.apply(5, 5));
    }
  }

}
