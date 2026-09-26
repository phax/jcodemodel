package com.helger.jcodemodel.expressions.typed.primitives;

import org.junit.Assert;
import org.junit.Test;

import com.helger.jcodemodel.util.CodeModelTestsHelper;

public class BoolExpressionTest
{

  @Test
  public void testTernary ()
  {
    // random code but we need to check that the ternary is the common lower type
    ASubShortExpression <?, ?, ?> ternary = BoolExpression.true_ ()
                                                          .ternary (ShortExpression.of ((short) 5),
                                                                    ByteExpression.of ((byte) 3));
    IntExpression test = ternary.plus (CharExpression.of ('a'));
    Assert.assertEquals ("(true? 5 : 3)+'a'", CodeModelTestsHelper.generate (test.raw ()));
    
  }

}
