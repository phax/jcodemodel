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
package com.helger.jcodemodel.tests.issues.issue201;

import com.helger.jcodemodel.JAssignment;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.JSwitchExpression;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;

/// https://github.com/phax/jcodemodel/issues/201
@TestJCM
public class Issue201TestGen
{
  public void generate (JPackage root) throws JCodeModelException
  {
    var cl = root._class ("Issue201");
    var jm = cl.method (JMod.PUBLIC | JMod.STATIC, root.owner ().VOID, "test");
    var param = jm.param (root.owner ().INT, "i");

    JSwitchExpression sw = JExpr._switch (param);
    jm.body ().add (sw);
    sw._case (JExpr.lit (1)).getBlock ().assign (param, JExpr.lit (42));
    sw._case (JExpr.lit (2)).getBlock ().assign (param, JExpr.lit (40)).assign (param, JExpr.lit (40));
    JAssignment iAss = JExpr.assign (param, JExpr.lit (-1));
    for (int i = 0; i < 25; i++)
      iAss = JExpr.assign (param, iAss);
    sw._default ().add (iAss);

    sw = JExpr._switch (param);
    jm.body ().assign (param, sw);
    sw._case (JExpr.lit (1)).yield (JExpr.lit (42));
    sw._case (JExpr.lit (2)).add (JExpr.assign (param, JExpr.incr (param))).yield (JExpr.lit (40));
    sw._default ().yield (param);
  }

}
