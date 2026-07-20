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

import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;

@TestJCM
public class TextBlocksTestGen
{

  public void basicExamples (final JPackage root) throws JCodeModelException
  {
    final JDefinedClass cl = root._class ("TextBlocksExample");
    cl.field (JMod.PUBLIC | JMod.STATIC | JMod.FINAL, String.class, "EMPTY", JExpr.textBlock ());
    cl.field (JMod.PUBLIC | JMod.STATIC | JMod.FINAL, String.class, "ONE_LINE", JExpr.textBlock ().add ("a"));
    cl.field (JMod.PUBLIC | JMod.STATIC | JMod.FINAL, String.class, "TWO_LINES", JExpr.textBlock ().add ("a\nb"));

    cl.field (JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
              String.class,
              "TWO_DOUBLE_DQUOTES_LINES",
              JExpr.textBlock ().add ("\"\"\n\"\""));
    cl.field (JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
              String.class,
              "TWO_TRIPLE_DQUOTES_LINES",
              JExpr.textBlock ().add ("\"\"\"\n\"\"\""));
    cl.field (JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
              String.class,
              "FIVE_DQUOTES",
              JExpr.textBlock ().add ("\"".repeat (5)));
    cl.field (JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
              String.class,
              "TWO_FIVE_DQUOTES_LINES",
              JExpr.textBlock ().add ("\"".repeat (5)).add ("\"".repeat (5)));

    cl.field (JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
              String.class,
              "ONE_LINE_ENDSPACE",
              JExpr.textBlock ().add ("a  "));
    cl.field (JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
              String.class,
              "ONE_LINE_ENDTAB",
              JExpr.textBlock ().add ("a\t\t"));
    cl.field (JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
              String.class,
              "TWO_LINES_ENDTAB",
              JExpr.textBlock ().add ("a\t\t").add ("b\t\t"));
  }

  public void keepWhiteSpaces (final JPackage root) throws JCodeModelException
  {
    final JDefinedClass cl = root._class ("TextBlocksKeepWhiteSpaces");
    cl.field (JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
              String.class,
              "ONE_LINE_SPACES",
              JExpr.textBlock ().keepWhitespaces (true).add ("  a  "));
    cl.field (JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
              String.class,
              "ONE_LINE_TABS",
              JExpr.textBlock ().keepWhitespaces (true).add ("	a	"));
    cl.field (JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
              String.class,
              "SPACES_EMPTYLINE",
              JExpr.textBlock ().keepWhitespaces (true).add ("  ").newline ());
    cl.field (JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
              String.class,
              "THREE_LINES_SPACES",
              JExpr.textBlock ().keepWhitespaces (true).add (" a ").add (" b ").add (" c  "));
    cl.field (JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
              String.class,
              "EMPTY_THEN_SPACED",
              JExpr.textBlock ().keepWhitespaces (true).newline ().add ("  a"));
    cl.field (JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
              String.class,
              "TWO_EMPTY_LINES_2SPACES_3SPACES",
              JExpr.textBlock ().keepWhitespaces (true).newline ().newline ().add ("  ").add ("   "));
  }
}
