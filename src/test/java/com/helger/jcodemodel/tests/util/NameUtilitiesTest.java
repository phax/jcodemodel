package com.helger.jcodemodel.tests.util;

import org.junit.Assert;
import org.junit.Test;

import com.helger.jcodemodel.util.NameUtilities;

/**
 * @author Ben Fagin
 * @version 2013-04-01
 */
public class NameUtilitiesTest
{
  public static class Inner
  {}

  @Test
  public void testInnerClassNaming ()
  {
    final String expected = NameUtilitiesTest.class.getPackage ().getName () +
                            "." +
                            NameUtilitiesTest.class.getSimpleName () +
                            "." +
                            "Inner";

    final String name = NameUtilities.getFullName (Inner.class);
    Assert.assertEquals (expected, name);
  }
}
