package com.helger.jcodemodel.tests.expressions.typed;

import org.junit.Assert;
import org.junit.Test;

public class TypedExpressionTest
{

  @FunctionalInterface
  public static interface FI1
  {
    boolean test (String str);
  }

  @Test
  public void testPalyndrom ()
  {
    for (FI1 fi : new FI1 [] { PalyndromIJExpression::test, PalyndromTypedExpression::test })
    {
      Assert.assertTrue (fi.test (null));
      Assert.assertTrue (fi.test ("a"));
      Assert.assertTrue (fi.test ("aa"));
      Assert.assertFalse (fi.test ("ab"));
      Assert.assertTrue (fi.test ("aaa"));
      Assert.assertTrue (fi.test ("aba"));
      Assert.assertFalse (fi.test ("abc"));
      Assert.assertTrue (fi.test ("aaaa"));
      Assert.assertTrue (fi.test ("abba"));
      Assert.assertFalse (fi.test ("abcd"));
    }
  }

}
