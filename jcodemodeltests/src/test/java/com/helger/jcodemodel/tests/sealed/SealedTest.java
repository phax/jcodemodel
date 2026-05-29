package com.helger.jcodemodel.tests.sealed;

import org.junit.Assert;
import org.junit.Test;

public class SealedTest {

  @Test
  public void testBasicExample() {
    Assert.assertTrue(SealedInterface.class.isSealed());
    Assert.assertFalse(ImplFinal.class.isSealed());
    Assert.assertFalse(ImplNonsealed.class.isSealed());
    Assert.assertTrue(ImplSealed.class.isSealed());
    Assert.assertFalse(ImplSealedChildFinal.class.isSealed());
    Assert.assertFalse(ImplSealedChildNonsealed.class.isSealed());
  }

}
