package com.helger.jcodemodel.tests.switchexpression;

import org.junit.Assert;
import org.junit.Test;

public class SwitchExpressionTest {

  @Test
  public void basicSwitch() {
    Assert.assertFalse(BasicSwitch.isOdd(0));
    Assert.assertTrue(BasicSwitch.isOdd(1));
    Assert.assertFalse(BasicSwitch.isOdd(6));
    Assert.assertThrows(UnsupportedOperationException.class, () -> BasicSwitch.isOdd(42));
  }

  @Test
  public void switchEnum() {
    Assert.assertEquals(31, ESwitch.daysIn(EnumMonths.JAN));
    Assert.assertEquals(28, ESwitch.daysIn(EnumMonths.FEB));

    Assert.assertEquals(7, ESwitch.daysIn(EPeriod.WEEK));
    Assert.assertEquals(365, ESwitch.daysIn(EPeriod.YEAR));
    Assert.assertThrows(UnsupportedOperationException.class, () -> ESwitch.daysIn(EPeriod.MONTH));
  }

}
