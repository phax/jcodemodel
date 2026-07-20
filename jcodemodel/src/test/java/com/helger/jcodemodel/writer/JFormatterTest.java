package com.helger.jcodemodel.writer;

import org.junit.Assert;
import org.junit.Test;

import com.helger.jcodemodel.util.NullWriter;
import com.helger.jcodemodel.writer.JFormatter.FormatterContext;
import com.helger.jcodemodel.writer.JFormatter.WriteContext;

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

  @Test
  public void testContexts() {
    FormatterSettings settings = new FormatterSettings();
    settings.indent.useSpaces(1);
    try (JFormatter test =
        new JFormatter(new SourcePrintWriter(NullWriter.getInstance(), JCMWriter.DEFAULT_NEW_LINE), settings);) {
      WriteContext root = test.asContext;

      Assert.assertEquals(0, test.indentLevel());
      Assert.assertEquals(0, root.getIndentLevel());
      Assert.assertEquals("", root.getCurrentLine());
      Assert.assertEquals(0, root.getLastChar());

      test.print("test");
      Assert.assertEquals(0, test.indentLevel());
      Assert.assertEquals(0, root.getIndentLevel());
      Assert.assertEquals("test", root.getCurrentLine());
      Assert.assertEquals('t', root.getLastChar());

      test.indent(2);
      Assert.assertEquals(2, test.indentLevel());
      Assert.assertEquals(2, root.getIndentLevel());
      Assert.assertEquals("test", root.getCurrentLine());
      Assert.assertEquals('t', root.getLastChar());

      test.newline();
      Assert.assertEquals(2, test.indentLevel());
      Assert.assertEquals(2, root.getIndentLevel());
      Assert.assertEquals("", root.getCurrentLine());
      Assert.assertEquals(0, root.getLastChar());

      test.print('o');
      Assert.assertEquals(2, test.indentLevel());
      Assert.assertEquals(2, root.getIndentLevel());
      Assert.assertEquals("  o", root.getCurrentLine());
      Assert.assertEquals('o', root.getLastChar());

      test.outdent(1);
      Assert.assertEquals(1, test.indentLevel());
      Assert.assertEquals(1, root.getIndentLevel());
      Assert.assertEquals("  o", root.getCurrentLine());
      Assert.assertEquals('o', root.getLastChar());

      test.newline();
      Assert.assertEquals(1, test.indentLevel());
      Assert.assertEquals(1, root.getIndentLevel());
      Assert.assertEquals("", root.getCurrentLine());
      Assert.assertEquals(0, root.getLastChar());

      test.print("sterone");
      Assert.assertEquals(1, test.indentLevel());
      Assert.assertEquals(1, root.getIndentLevel());
      Assert.assertEquals(" sterone", root.getCurrentLine());
      Assert.assertEquals('e', root.getLastChar());

      FormatterContext layer = (FormatterContext) test.addContextLayer();
      Assert.assertEquals(1, test.indentLevel());
      Assert.assertEquals(1, root.getIndentLevel());
      Assert.assertEquals(" sterone", root.getCurrentLine());
      Assert.assertEquals('e', root.getLastChar());
      Assert.assertEquals(1, layer.getIndentLevel());
      Assert.assertEquals(" sterone", layer.getCurrentLine());
      Assert.assertEquals('e', layer.getLastChar());

      test.indent(2);
      Assert.assertEquals(3, test.indentLevel());
      Assert.assertEquals(1, root.getIndentLevel());
      Assert.assertEquals(" sterone", root.getCurrentLine());
      Assert.assertEquals('e', root.getLastChar());
      Assert.assertEquals(3, layer.getIndentLevel());
      Assert.assertEquals(" sterone", layer.getCurrentLine());
      Assert.assertEquals('e', layer.getLastChar());

      test.newline();
      Assert.assertEquals(3, test.indentLevel());
      Assert.assertEquals(1, root.getIndentLevel());
      Assert.assertEquals(" sterone", root.getCurrentLine());
      Assert.assertEquals('e', root.getLastChar());
      Assert.assertEquals(3, layer.getIndentLevel());
      Assert.assertEquals("", layer.getCurrentLine());
      Assert.assertEquals(0, layer.getLastChar());

      test.print("oid");
      Assert.assertEquals(3, test.indentLevel());
      Assert.assertEquals(1, root.getIndentLevel());
      Assert.assertEquals(" sterone", root.getCurrentLine());
      Assert.assertEquals('e', root.getLastChar());
      Assert.assertEquals(3, layer.getIndentLevel());
      Assert.assertEquals("   oid", layer.getCurrentLine());
      Assert.assertEquals('d', layer.getLastChar());

      layer.commit();
      Assert.assertEquals(3, test.indentLevel());
      Assert.assertEquals(3, root.getIndentLevel());
      Assert.assertEquals("   oid", root.getCurrentLine());
      Assert.assertEquals('d', root.getLastChar());

    }

  }

}
