package com.helger.jcodemodel.literals;

import org.junit.Assert;
import org.junit.Test;

public class EIntegerBaseTest
{

  static void checkFormat (String expected, String source, String format, boolean allowLeadingSep)
  {
    StringBuilder sb = new StringBuilder ();
    EIntegerBase.addSep (source, format, allowLeadingSep, 0, 0, sb);
    Assert.assertEquals (expected, sb.toString ());
  }

  /// some tests on adding separators to an int representation using [EIntegerBase#addSep]
  @Test
  public void testAddSep ()
  {
    {
      checkFormat ("_0", "0", "_ ", true);
      checkFormat ("__0", "0", "__ ", true);
      checkFormat ("0", "0", "__ ", false);
      checkFormat ("012__3", "0123", "__ ", true);

      checkFormat ("_0_12_3", "0123", "_c_cc_c", true);
      checkFormat ("0_12_3", "0123", "_c_cc_c", false);
      checkFormat ("___0_12_3", "0123", "___c_cc_c", true);
      checkFormat ("0_12_3", "0123", "___c_cc_c", false);

      checkFormat ("01__2_3", "0123", "__d_d", true);
      checkFormat ("01__2_3", "0123", "__d__", true);
      checkFormat ("01234_56_789", "0123456789", "_dd_ddd", true);
    }
  }

}
