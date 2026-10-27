package com.helger.jcodemodel.tests.expressions.typed;

import org.junit.Assert;
import org.junit.Test;

public class StringCharAtTest
{

  @FunctionalInterface
  public static interface FI1
  {
    char charAt (String str, int index);
  }

  @Test
  public void test ()
  {
    for (FI1 test : new FI1 [] { StringCharAtIJExpression::charAt, StringCharAtTypedExpression::charAt })
    {
      Assert.assertEquals ('a', test.charAt ("abc", 0));
      Assert.assertEquals ('a', test.charAt ("abc", 3));
      Assert.assertEquals ('a', test.charAt ("abc", -3));
      Assert.assertEquals ('c', test.charAt ("abc", 2));
      Assert.assertEquals ('c', test.charAt ("abc", -1));
      Assert.assertEquals ('c', test.charAt ("abc", -4));
    }
  }

}
