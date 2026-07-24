package com.helger.jcodemodel;

import org.junit.Assert;
import org.junit.Test;

import com.helger.jcodemodel.util.CodeModelTestsHelper;

public class JAtomIntTest
{
  @Test
  public void testRepresentation ()
  {
    {
      JAtomInt i42 = new JAtomInt (42);
      Assert.assertEquals ("0b101010", CodeModelTestsHelper.toString (i42.binary ()));
      Assert.assertEquals ("42", CodeModelTestsHelper.toString (i42.decimal ()));
      Assert.assertEquals ("0x2a", CodeModelTestsHelper.toString (i42.hex ()));
      Assert.assertEquals ("052", CodeModelTestsHelper.toString (i42.octal ()));
    }
    {
      JAtomInt iNeg2 = new JAtomInt (-2);
      Assert.assertEquals ("-0b10", CodeModelTestsHelper.toString (iNeg2.binary ()));
      Assert.assertEquals ("-2", CodeModelTestsHelper.toString (iNeg2.decimal ()));
      Assert.assertEquals ("-0x2", CodeModelTestsHelper.toString (iNeg2.hex ()));
      Assert.assertEquals ("-02", CodeModelTestsHelper.toString (iNeg2.octal ()));
    }
    {
      JAtomInt ia = new JAtomInt (-1234567).separatorSize (2).separateEvery (2);
      Assert.assertEquals ("-1__23__45__67", CodeModelTestsHelper.toString (ia.decimal ()));
    }
    {
      JAtomInt ia = new JAtomInt (-12345678).separatorSize (2).separateEvery (2);
      Assert.assertEquals ("-12__34__56__78", CodeModelTestsHelper.toString (ia.decimal ()));
    }
    {
      JAtomInt ia = new JAtomInt (-10).separatorSize (1).separateEvery (3);
      Assert.assertEquals ("-0b1_010", CodeModelTestsHelper.toString (ia.binary ()));
    }
  }

}
