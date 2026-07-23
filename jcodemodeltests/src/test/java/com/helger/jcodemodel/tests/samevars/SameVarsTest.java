package com.helger.jcodemodel.tests.samevars;

import org.junit.Assert;
import org.junit.Test;

public class SameVarsTest {

  @Test
  public void testSimpleClass() {
    Assert.assertEquals(3, SameVarsSimpleClass.i);
    Assert.assertEquals(0, SameVarsSimpleClass.j);
    Assert.assertEquals(1, SameVarsSimpleClass.k);

    SameVarsSimpleClass.a1 = new char[] { 'a' };
    SameVarsSimpleClass.a2 = new char[] { 'a' };
    SameVarsSimpleClass.a3 = new char[][] { { 'a' } };

  }

}
