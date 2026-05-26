package com.helger.jcodemodel;

import org.junit.Assert;
import org.junit.Test;

public class JTextBlockTest {

  @Test
  public void testFormatLine() {
    // 6 parenthesis should have the 3rd and 6th one escaped
    Assert.assertEquals("\"\"\\\"\"\"\\\"", JTextBlock.formatLine("\"".repeat(6)));
    Assert.assertEquals("\"\"\\\"?\"\"\\\"", JTextBlock.formatLine("\"".repeat(3) + "?" + "\"".repeat(3)));
  }

  @Test
  public void testEscapeLastIfDoubleQuote() {
    Assert.assertEquals("", JTextBlock.escapeLastIfDoubleQuote(""));
    Assert.assertEquals("\\\"", JTextBlock.escapeLastIfDoubleQuote("\""));
    Assert.assertEquals("\"\\\"", JTextBlock.escapeLastIfDoubleQuote("\"\""));
  }

  @Test
  public void testConstruction() {
    JTextBlock test = new JTextBlock();
    test.add("a\n b ");
    Assert.assertTrue(test.lines().filter(l -> l.equals("a")).findAny().isPresent());
    Assert.assertTrue(test.lines().filter(l -> l.equals(" b ")).findAny().isPresent());
    Assert.assertEquals(2L, test.lines().count());
  }

}
