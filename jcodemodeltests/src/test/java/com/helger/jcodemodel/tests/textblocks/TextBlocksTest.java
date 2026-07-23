/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.jcodemodel.tests.textblocks;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TextBlocksTest
{

  @Test
  public void testBasicExamples ()
  {
    assertEquals ("", TextBlocksExample.EMPTY);
    assertEquals ("a", TextBlocksExample.ONE_LINE);
    assertEquals ("a\nb", TextBlocksExample.TWO_LINES);
    assertEquals ("\"\"\n\"\"", TextBlocksExample.TWO_DOUBLE_DQUOTES_LINES);
    assertEquals ("\"\"\"\n\"\"\"", TextBlocksExample.TWO_TRIPLE_DQUOTES_LINES);
    assertEquals ("\"\"\"\"\"", TextBlocksExample.FIVE_DQUOTES);
    assertEquals ("\"\"\"\"\"\n\"\"\"\"\"", TextBlocksExample.TWO_FIVE_DQUOTES_LINES);
    assertEquals ("a", TextBlocksExample.ONE_LINE_ENDSPACE);
    assertEquals ("a", TextBlocksExample.ONE_LINE_ENDTAB);
    assertEquals ("a\nb", TextBlocksExample.TWO_LINES_ENDTAB);
  }

  @Test
  public void testKeepWhiteSpaces ()
  {
    assertEquals ("  a  ", TextBlocksKeepWhiteSpaces.ONE_LINE_SPACES);
    assertEquals ("	a	", TextBlocksKeepWhiteSpaces.ONE_LINE_TABS);
    assertEquals ("  \n", TextBlocksKeepWhiteSpaces.SPACES_EMPTYLINE);
    assertEquals (" a \n b \n c  ", TextBlocksKeepWhiteSpaces.THREE_LINES_SPACES);
    assertEquals ("\n  a", TextBlocksKeepWhiteSpaces.EMPTY_THEN_SPACED);
    assertEquals ("\n\n  \n   ", TextBlocksKeepWhiteSpaces.TWO_EMPTY_LINES_2SPACES_3SPACES);
  }
}
