package com.helger.jcodemodel.patterns;

import org.junit.Assert;
import org.junit.Test;

public class JLazyTest {

  @Test
  public void testExtractFieldName() {

    // standard cases

    Assert.assertEquals("x", JLazy.extractFieldName("getX"));
    Assert.assertEquals("x", JLazy.extractFieldName("getx"));
    Assert.assertEquals("myFieldName", JLazy.extractFieldName("myFieldName"));
    Assert.assertEquals("xy", JLazy.extractFieldName("getXy"));
    Assert.assertEquals("xy", JLazy.extractFieldName("getxy"));
    Assert.assertEquals("gETget", JLazy.extractFieldName("getGETget"));

    // edge cases

    // null/blank are kept as is
    Assert.assertEquals(null, JLazy.extractFieldName(null));
    Assert.assertEquals("  ", JLazy.extractFieldName("  "));
    Assert.assertEquals("\t", JLazy.extractFieldName("\t"));
    // too short are kept
    Assert.assertEquals("get", JLazy.extractFieldName("get"));
    Assert.assertEquals("x", JLazy.extractFieldName("x"));
    // reserved keywords have _ appended
    Assert.assertEquals("int_", JLazy.extractFieldName("int"));
    Assert.assertEquals("class_", JLazy.extractFieldName("class"));
  }

}
