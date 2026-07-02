package com.helger.jcodemodel.writer;

import org.junit.Assert;
import org.junit.Test;

public class JFormatterTest {

  @Test
  public void testSizeWithTabsExpanded() {
    Assert.assertEquals(2, JFormatter.sizeWithTabsExpanded("\t", 2));
    Assert.assertEquals(4, JFormatter.sizeWithTabsExpanded("\t", 4));
    Assert.assertEquals(4, JFormatter.sizeWithTabsExpanded("a\t", 4));
    Assert.assertEquals(4, JFormatter.sizeWithTabsExpanded("ab\t", 4));
    Assert.assertEquals(4, JFormatter.sizeWithTabsExpanded("abc\t", 4));
    Assert.assertEquals(8, JFormatter.sizeWithTabsExpanded("abcd\t", 4));
    Assert.assertEquals(8, JFormatter.sizeWithTabsExpanded("abcde\t", 4));
    Assert.assertEquals(8, JFormatter.sizeWithTabsExpanded("abcdef\t", 4));
    Assert.assertEquals(8, JFormatter.sizeWithTabsExpanded("abcdefg\t", 4));
    Assert.assertEquals(12, JFormatter.sizeWithTabsExpanded("abcdefgh\t", 4));
    Assert.assertEquals(8, JFormatter.sizeWithTabsExpanded("\t\t", 4));
    Assert.assertEquals(8, JFormatter.sizeWithTabsExpanded("\tab\t", 4));
  }

}
