package com.helger.jcodemodel.tests.textblocks;

import org.junit.Assert;
import org.junit.Test;

public class TextBlocksTest {

  @Test
  public void testBasicExamples() {
    Assert.assertEquals("", TextBlocksExample.EMPTY);
    Assert.assertEquals("a", TextBlocksExample.ONE_LINE);
    Assert.assertEquals("a\nb", TextBlocksExample.TWO_LINES);
    Assert.assertEquals("\"\"\n\"\"", TextBlocksExample.TWO_DOUBLE_DQUOTES_LINES);
    Assert.assertEquals("\"\"\"\n\"\"\"", TextBlocksExample.TWO_TRIPLE_DQUOTES_LINES);
    Assert.assertEquals("\"\"\"\"\"", TextBlocksExample.FIVE_DQUOTES);
    Assert.assertEquals("\"\"\"\"\"\n\"\"\"\"\"", TextBlocksExample.TWO_FIVE_DQUOTES_LINES);
    Assert.assertEquals("a", TextBlocksExample.ONE_LINE_ENDSPACE);
    Assert.assertEquals("a", TextBlocksExample.ONE_LINE_ENDTAB);
    Assert.assertEquals("a\nb", TextBlocksExample.TWO_LINES_ENDTAB);
  }

  @Test
  public void testKeepWhiteSpaces() {
    Assert.assertEquals("  a  ", TextBlocksKeepWhiteSpaces.ONE_LINE_SPACES);
    Assert.assertEquals("	a	", TextBlocksKeepWhiteSpaces.ONE_LINE_TABS);
    Assert.assertEquals("  \n", TextBlocksKeepWhiteSpaces.SPACES_EMPTYLINE);
  }

}
