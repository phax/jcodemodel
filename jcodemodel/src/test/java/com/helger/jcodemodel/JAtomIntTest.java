package com.helger.jcodemodel;

import org.junit.Assert;
import org.junit.Test;

import com.helger.jcodemodel.util.CodeModelTestsHelper;

public class JAtomIntTest
{
  @Test
  public void testRepresentation ()
  {
    JAtomInt i42 = new JAtomInt (42);
    Assert.assertEquals ("0b101010", CodeModelTestsHelper.toString (i42.binary ()));
    Assert.assertEquals ("42", CodeModelTestsHelper.toString (i42.decimal ()));
    Assert.assertEquals ("0x2a", CodeModelTestsHelper.toString (i42.hex ()));
    Assert.assertEquals ("052", CodeModelTestsHelper.toString (i42.octal ()));

    JAtomInt iNeg2 = new JAtomInt (-2);
    Assert.assertEquals ("-0b10", CodeModelTestsHelper.toString (iNeg2.binary ()));
    Assert.assertEquals ("-2", CodeModelTestsHelper.toString (iNeg2.decimal ()));
    Assert.assertEquals ("-0x2", CodeModelTestsHelper.toString (iNeg2.hex ()));
    Assert.assertEquals ("-02", CodeModelTestsHelper.toString (iNeg2.octal ()));
  }

}
