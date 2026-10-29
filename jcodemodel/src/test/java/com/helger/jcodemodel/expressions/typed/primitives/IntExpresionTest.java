package com.helger.jcodemodel.expressions.typed.primitives;

import org.junit.Assert;
import org.junit.Test;

import com.helger.jcodemodel.JExpr;

public class IntExpresionTest
{

  @Test
  public void testCache() {
    
    // check that requesting it directly, twice, gives the same value ; then not for 130+
    Assert.assertTrue (IntExpression.of (5) == IntExpression.of (5));
    Assert.assertTrue (IntExpression.of (-1) == IntExpression.of (-1));
    Assert.assertFalse (IntExpression.of (-1) == IntExpression.of (5));
    Assert.assertFalse (IntExpression.of (135) == IntExpression.of (135));

    // check for 128 and -127 => ok, 129 and -128 => not okay
    Assert.assertTrue (IntExpression.of (128) == IntExpression.of (128));
    Assert.assertTrue (IntExpression.of (-127) == IntExpression.of (-127));
    Assert.assertFalse (IntExpression.of (129) == IntExpression.of (129));
    Assert.assertFalse (IntExpression.of (-128) == IntExpression.of (-128));

    // now using IJExpression + direct
    Assert.assertTrue (IntExpression.of (JExpr.lit (10)) == IntExpression.of (10));
    Assert.assertFalse (IntExpression.of (JExpr.lit (140)) == IntExpression.of (140));

    // now using IJExpression twice
    Assert.assertTrue (IntExpression.of (JExpr.lit (11)) == IntExpression.of (JExpr.lit (11)));
    Assert.assertFalse (IntExpression.of (JExpr.lit (141)) == IntExpression.of (JExpr.lit (141)));

  }

}
