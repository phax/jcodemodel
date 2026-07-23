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
package com.helger.jcodemodel.tests.arrayinit;

import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.vars.JFieldVar;

@TestJCM

public class ArrayInitTestGen {

  public void simpleClass(final JPackage root) throws JCodeModelException {
    final JDefinedClass cl = root._class("ArrayInitSimpleClass");

    JFieldVar i = cl.field(JMod.PUBLIC_STATIC_FINAL, cl.owner().INT.array(), "i", JExpr.arrayInit(0, 1, 2, 3, 4, 5));
    JFieldVar i0 = cl.field(JMod.PUBLIC_STATIC_FINAL, cl.owner().INT.array(), "i0", JExpr.arrayInit());
    JFieldVar c = cl.field(JMod.PUBLIC_STATIC_FINAL, cl.owner().CHAR.array(), "c", JExpr.arrayInit('a'));
    JFieldVar d = cl.field(JMod.PUBLIC_STATIC_FINAL, cl.owner().DOUBLE.array(), "d", JExpr.arrayInit(.0, .1, .2, .3));
    JFieldVar s =
        cl.field(JMod.PUBLIC_STATIC_FINAL, cl.owner().ref(String.class).array(), "s",
        JExpr.arrayInit(null, JExpr.lit("s"), JExpr.lit("sss")));
    cl.field(JMod.PUBLIC_STATIC_FINAL, cl.owner().ref(Object.class).array(), "o",
        JExpr.arrayInit(i, i0, c, d, s));

  }

}
