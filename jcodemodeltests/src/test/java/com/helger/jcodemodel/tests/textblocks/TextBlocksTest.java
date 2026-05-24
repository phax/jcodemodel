package com.helger.jcodemodel.tests.textblocks;

import org.junit.Assert;
import org.junit.Test;

public class TextBlocksTest {

  @Test
  public void staticBlocks() {
    Assert.assertEquals("", TextBlocks.EMPTY);
    Assert.assertEquals("a", TextBlocks.ONE_LINE);
    Assert.assertEquals("a\nb", TextBlocks.TWO_LINES);
    Assert.assertEquals("\"\"\"\n\"\"\"", TextBlocks.TWO_TRIPLEQUOTES_LINES);
    Assert.assertEquals("a  ", TextBlocks.ONE_LINE_ENDSPACE);
    Assert.assertEquals("a\t\t", TextBlocks.ONE_LINE_ENDTAB);
    Assert.assertEquals("a\t\t\nb\t\t", TextBlocks.TWO_LINES_ENDTAB);
  }

}
