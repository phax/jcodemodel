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

}
