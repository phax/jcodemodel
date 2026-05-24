package com.helger.jcodemodel;

import org.junit.Assert;
import org.junit.Test;

public class JTextBlockTest {

  @Test
  public void testFormat() {
    // 6 parenthesis should have the first and 4th one escaped
    Assert.assertEquals("\\\"\"\\\"\\\"\"\\\"", JTextBlock.formatLine("\"".repeat(6)));
    Assert.assertEquals("\\\"\"\\\"?\\\"\"\\\"", JTextBlock.formatLine("\"".repeat(3) + "?" + "\"".repeat(3)));
    // end space is octal space
    Assert.assertEquals("   \\040", JTextBlock.formatLine(" ".repeat(4)));
    // end tab is octal space
    Assert.assertEquals(" \\011", JTextBlock.formatLine(" \t"));
    // same but with alternating space and tabs
    Assert.assertEquals(" \t \t \\011", JTextBlock.formatLine(" \t".repeat(3)));
    // same but with alternating tabs and spaces
    Assert.assertEquals("\t \t \t\\040", JTextBlock.formatLine("\t ".repeat(3)));
  }

  @Test
  public void testConstruction() {
    JTextBlock test = new JTextBlock();
    test.add("a\n b ");
    Assert.assertTrue(test.lines().filter(l -> l.equals("a")).findAny().isPresent());
    Assert.assertTrue(test.lines().filter(l -> l.equals(" b\\040")).findAny().isPresent());
    Assert.assertEquals(2L, test.lines().count());
  }

}
