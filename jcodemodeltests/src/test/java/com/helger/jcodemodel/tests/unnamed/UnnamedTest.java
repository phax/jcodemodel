package com.helger.jcodemodel.tests.unnamed;

import org.junit.Assert;
import org.junit.Test;

public class UnnamedTest {

  @Test
  public void testEquals() {
    SimpleUnnamed test = new SimpleUnnamed();
    Assert.assertFalse(test.equals(test));
  }

}
