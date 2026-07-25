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
    Assert.assertEquals ("42L", CodeModelTestsHelper.toString (new JAtomLong (42, IntegerRepresentation.DEC)));
    Assert.assertEquals ("0x2aL", CodeModelTestsHelper.toString (new JAtomLong (42, IntegerRepresentation.HEX)));
    Assert.assertEquals ("-052L", CodeModelTestsHelper.toString (new JAtomLong (-42, IntegerRepresentation.OCT)));
  }

}
