package com.helger.jcodemodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class JTextBlockTest
{

  @Test
  public void testFormatLine ()
  {
    // 6 parenthesis should have the 3rd and 6th one escaped
    assertEquals ("\"\"\\\"\"\"\\\"", JTextBlock.formatLine ("\"".repeat (6)));
    assertEquals ("\"\"\\\"?\"\"\\\"", JTextBlock.formatLine ("\"".repeat (3) + "?" + "\"".repeat (3)));
  }

  @Test
  public void testEscapeLastIfDoubleQuote ()
  {
    assertEquals ("", JTextBlock.escapeLastIfDoubleQuote (""));
    assertEquals ("\\\"", JTextBlock.escapeLastIfDoubleQuote ("\""));
    assertEquals ("\"\\\"", JTextBlock.escapeLastIfDoubleQuote ("\"\""));
  }

  @Test
  public void testConstruction ()
  {
    final JTextBlock test = new JTextBlock ();
    test.add ("a\n b ");
    assertTrue (test.lines ().filter (l -> l.equals ("a")).findAny ().isPresent ());
    assertTrue (test.lines ().filter (l -> l.equals (" b ")).findAny ().isPresent ());
    assertEquals (2L, test.lines ().count ());
  }

}
