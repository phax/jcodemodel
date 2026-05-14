package com.helger.jcodemodel.tests.switchexpression;

import org.junit.Assert;
import org.junit.Test;

public class SwitchExpressionTest {

  @Test
  public void basicSwitch() {
    BasicSwitch test = new BasicSwitch();
    Assert.assertEquals(2, test.plus1(1));
    Assert.assertEquals(4, test.plus1('3'));
    Assert.assertEquals(1, test.plus1(null));
    Assert.assertEquals(2, test.plus1(null));

  }

  @Test
  public void switchEnum() {
    Assert.assertEquals(31, ESwitch.daysIn(EnumMonths.JAN));
    Assert.assertEquals(7, ESwitch.daysIn(EPeriod.WEEK));
  }

}
