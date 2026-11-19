package com.helger.jcodemodel.expressions.typed.primitives;

import org.junit.Assert;
import org.junit.Test;

import com.helger.jcodemodel.util.CodeModelTestsHelper;

/// If a test fail, it may be because default spacing/casing changed . If that's the case, just replace the expected result
/// 
/// The test are only really failing, when numbers are badly represented
public class PrimitiveOperatorTest
{

  @Test
  public void testDiv ()
  {
    DblExpression d = DblExpression.of (5);
    char op = '/';
    Assert.assertEquals ("5.0 " + op + " 5.0", CodeModelTestsHelper.generate (d.div (d).raw ()));
    Assert.assertEquals ("5.0 " + op + " 3.0", CodeModelTestsHelper.generate (d.div (3.0).raw ()));
    Assert.assertEquals ("5.0 " + op + " 3.0F", CodeModelTestsHelper.generate (d.div (3.0f).raw ()));
    Assert.assertEquals ("5.0 " + op + " 3L", CodeModelTestsHelper.generate (d.div (3l).raw ()));
    Assert.assertEquals ("5.0 " + op + " 3", CodeModelTestsHelper.generate (d.div (3).raw ()));
    Assert.assertEquals ("5.0 " + op + " 3", CodeModelTestsHelper.generate (d.div ((short) 3).raw ()));
    Assert.assertEquals ("5.0 " + op + "'\\u0003'", CodeModelTestsHelper.generate (d.div ('\3').raw ()));
  }

  @Test
  public void testMod ()
  {
    DblExpression d = DblExpression.of (5);
    char op = '%';
    Assert.assertEquals ("5.0 " + op + " 5.0", CodeModelTestsHelper.generate (d.mod (d).raw ()));
    Assert.assertEquals ("5.0 " + op + " 3.0", CodeModelTestsHelper.generate (d.mod (3.0).raw ()));
    Assert.assertEquals ("5.0 " + op + " 3.0F", CodeModelTestsHelper.generate (d.mod (3.0f).raw ()));
    Assert.assertEquals ("5.0 " + op + " 3L", CodeModelTestsHelper.generate (d.mod (3l).raw ()));
    Assert.assertEquals ("5.0 " + op + " 3", CodeModelTestsHelper.generate (d.mod (3).raw ()));
    Assert.assertEquals ("5.0 " + op + " 3", CodeModelTestsHelper.generate (d.mod ((short) 3).raw ()));
    Assert.assertEquals ("5.0 " + op + "'\\u0003'", CodeModelTestsHelper.generate (d.mod ('\3').raw ()));
  }

  @Test
  public void testMult ()
  {
    DblExpression d = DblExpression.of (5);
    char op = '*';
    Assert.assertEquals ("5.0 " + op + " 5.0", CodeModelTestsHelper.generate (d.mult (d).raw ()));
    Assert.assertEquals ("5.0 " + op + " 3.0", CodeModelTestsHelper.generate (d.mult (3.0).raw ()));
    Assert.assertEquals ("5.0 " + op + " 3.0F", CodeModelTestsHelper.generate (d.mult (3.0f).raw ()));
    Assert.assertEquals ("5.0 " + op + " 3L", CodeModelTestsHelper.generate (d.mult (3l).raw ()));
    Assert.assertEquals ("5.0 " + op + " 3", CodeModelTestsHelper.generate (d.mult (3).raw ()));
    Assert.assertEquals ("5.0 " + op + " 3", CodeModelTestsHelper.generate (d.mult ((short) 3).raw ()));
    Assert.assertEquals ("5.0 " + op + "'\\u0003'", CodeModelTestsHelper.generate (d.mult ('\3').raw ()));
  }

  @Test
  public void testPlus ()
  {
    DblExpression d = DblExpression.of (5);
    char op = '+';
    Assert.assertEquals ("5.0 " + op + " 5.0", CodeModelTestsHelper.generate (d.plus (d).raw ()));
    Assert.assertEquals ("5.0 " + op + " 3.0", CodeModelTestsHelper.generate (d.plus (3.0).raw ()));
    Assert.assertEquals ("5.0 " + op + " 3.0F", CodeModelTestsHelper.generate (d.plus (3.0f).raw ()));
    Assert.assertEquals ("5.0 " + op + " 3L", CodeModelTestsHelper.generate (d.plus (3l).raw ()));
    Assert.assertEquals ("5.0 " + op + " 3", CodeModelTestsHelper.generate (d.plus (3).raw ()));
    Assert.assertEquals ("5.0 " + op + " 3", CodeModelTestsHelper.generate (d.plus ((short) 3).raw ()));
    Assert.assertEquals ("5.0 " + op + "'\\u0003'", CodeModelTestsHelper.generate (d.plus ('\3').raw ()));
  }

  @Test
  public void testSub ()
  {
    DblExpression d = DblExpression.of (5);
    char op = '-';
    Assert.assertEquals ("5.0 " + op + " 5.0", CodeModelTestsHelper.generate (d.sub (d).raw ()));
    Assert.assertEquals ("5.0 " + op + " 3.0", CodeModelTestsHelper.generate (d.sub (3.0).raw ()));
    Assert.assertEquals ("5.0 " + op + " 3.0F", CodeModelTestsHelper.generate (d.sub (3.0f).raw ()));
    Assert.assertEquals ("5.0 " + op + " 3L", CodeModelTestsHelper.generate (d.sub (3l).raw ()));
    Assert.assertEquals ("5.0 " + op + " 3", CodeModelTestsHelper.generate (d.sub (3).raw ()));
    Assert.assertEquals ("5.0 " + op + " 3", CodeModelTestsHelper.generate (d.sub ((short) 3).raw ()));
    Assert.assertEquals ("5.0 " + op + "'\\u0003'", CodeModelTestsHelper.generate (d.sub ('\3').raw ()));
  }
}
