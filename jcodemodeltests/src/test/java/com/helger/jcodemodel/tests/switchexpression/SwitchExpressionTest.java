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
