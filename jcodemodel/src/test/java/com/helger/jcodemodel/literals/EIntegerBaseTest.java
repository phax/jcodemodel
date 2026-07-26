package com.helger.jcodemodel.literals;

import org.junit.Assert;
import org.junit.Test;

public class EIntegerBaseTest
{

  void testFormat (String expected, String source, String format, boolean allowLeadingSep)
  {
    StringBuilder sb = new StringBuilder ();
    EIntegerBase.addSep (source, format, allowLeadingSep, 0, 0, sb);
    Assert.assertEquals (expected, sb.toString ());
  }

  @Test
  public void addSep ()
  {
    {
      testFormat ("_0", "0", "_ ", true);
      testFormat ("__0", "0", "__ ", true);
      testFormat ("0", "0", "__ ", false);
      testFormat ("012__3", "0123", "__ ", true);
      testFormat ("_0_12_3", "0123", "_c_cc_c", true);
      testFormat ("0_12_3", "0123", "_c_cc_c", false);
      testFormat ("01__2_3", "0123", "__ _ ", true);
      testFormat ("01__2_3", "0123", "__ __", true);
      testFormat ("01234_56_789", "0123456789", "_  _   ", true);
    }
  }

}
