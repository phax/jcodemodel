package com.helger.jcodemodel;

import org.junit.Assert;
import org.junit.Test;

import com.helger.jcodemodel.literals.IntegerRepresentation;
import com.helger.jcodemodel.util.CodeModelTestsHelper;

public class JAtomLongTest
{

  @Test
  public void testRepresentationBasic ()
  {
    Assert.assertEquals ("42L", CodeModelTestsHelper.toString (new JAtomLong (42, IntegerRepresentation.DEFAULT)));
    Assert.assertEquals ("0b1_00000001L",
                         CodeModelTestsHelper.toString (new JAtomLong (257, IntegerRepresentation.BIN)));
    Assert.assertEquals ("1_024L", CodeModelTestsHelper.toString (new JAtomLong (1024, IntegerRepresentation.DEC)));
    Assert.assertEquals ("0x2AL", CodeModelTestsHelper.toString (new JAtomLong (42, IntegerRepresentation.HEX)));
    Assert.assertEquals ("-052L", CodeModelTestsHelper.toString (new JAtomLong (-42, IntegerRepresentation.OCT)));
  }

}
