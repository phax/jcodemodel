package com.helger.jcodemodel;

import org.junit.Assert;
import org.junit.Test;

import com.helger.jcodemodel.literals.EIntegerBase;
import com.helger.jcodemodel.literals.IntegerRepresentation;
import com.helger.jcodemodel.util.CodeModelTestsHelper;

public class JAtomIntTest
{
  @Test
  public void testRepresentationDefault ()
  {
    // basic representation
    {
      JAtomInt i42 = new JAtomInt (42);
      Assert.assertEquals ("0b101010", CodeModelTestsHelper.toString (i42.binary ()));
      Assert.assertEquals ("42", CodeModelTestsHelper.toString (i42.decimal ()));
      Assert.assertEquals ("0x2A", CodeModelTestsHelper.toString (i42.hexadecimal ()));
      Assert.assertEquals ("052", CodeModelTestsHelper.toString (i42.octal ()));
    }
    {
      JAtomInt i0 = new JAtomInt (0);
      Assert.assertEquals ("0b0", CodeModelTestsHelper.toString (i0.binary ()));
      Assert.assertEquals ("0", CodeModelTestsHelper.toString (i0.decimal ()));
      Assert.assertEquals ("0x0", CodeModelTestsHelper.toString (i0.hexadecimal ()));
      Assert.assertEquals ("00", CodeModelTestsHelper.toString (i0.octal ()));
    }
    {
      JAtomInt iNeg2 = new JAtomInt (-2);
      Assert.assertEquals ("-0b10", CodeModelTestsHelper.toString (iNeg2.binary ()));
      Assert.assertEquals ("-2", CodeModelTestsHelper.toString (iNeg2.decimal ()));
      Assert.assertEquals ("-0x2", CodeModelTestsHelper.toString (iNeg2.hexadecimal ()));
      Assert.assertEquals ("-02", CodeModelTestsHelper.toString (iNeg2.octal ()));
    }
  }

  @Test
  public void testRepresentationSeparator ()
  {
    {
      IntegerRepresentation r = IntegerRepresentation.DEFAULT.separatorSize (2)
                                                             .separateEvery (2)
                                                             .base (EIntegerBase.DECIMAL);
      Assert.assertEquals ("-1__23__45__67", CodeModelTestsHelper.toString (new JAtomInt (-1234567, r)));
      Assert.assertEquals ("-12__34__56__78", CodeModelTestsHelper.toString (new JAtomInt (-12345678, r)));
    }
    {
      IntegerRepresentation r = IntegerRepresentation.DEFAULT.separatorSize (1)
                                                             .separateEvery (3)
                                                             .base (EIntegerBase.BINARY);
      Assert.assertEquals ("-0b1_010", CodeModelTestsHelper.toString (new JAtomInt (-10, r)));
      Assert.assertEquals ("0b100", CodeModelTestsHelper.toString (new JAtomInt (4, r)));
      Assert.assertEquals ("0b1_000", CodeModelTestsHelper.toString (new JAtomInt (8, r)));
    }
  }

  @Test
  public void testRepresentationPadding ()
  {
    JAtomInt ia = new JAtomInt (42).separateEvery (0).padding (5);
    Assert.assertEquals ("0b101010", CodeModelTestsHelper.toString (ia.binary ()));
    Assert.assertEquals ("42", CodeModelTestsHelper.toString (ia.decimal ()));
    Assert.assertEquals ("0x0002A", CodeModelTestsHelper.toString (ia.hexadecimal ()));
    Assert.assertEquals ("000052", CodeModelTestsHelper.toString (ia.octal ()));
  }

  @Test
  public void testRepresentationSign ()
  {
    JAtomInt ja = new JAtomInt (42);
    for (boolean positiveSign : new boolean [] { true, false })
      for (boolean prefixUpper : new boolean [] { true, false })
      {
        for (boolean suffixUpper : new boolean [] { true, false })
        {
          ja.representation (ja.representation ()
                               .with (ir->{
                                 ir.positiveSign=positiveSign;
                                 ir.prefixUpper = prefixUpper;
                                 ir.suffixUpper = suffixUpper;
                               }));
          Assert.assertEquals (positiveSign ? "+42" : "42", CodeModelTestsHelper.toString (ja));
        }
      }
  }

}
