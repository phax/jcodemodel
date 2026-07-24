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
package com.helger.jcodemodel.tests.lazy;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.patterns.JLazy;

@TestJCM
public class JLazyTestGen {

  // methods for the tests to call.

  public static int count = 0;

  public static int inc() {
    return ++count;
  }

  public void testLazyClass(JPackage root) throws JCodeModelException {
    JDefinedClass lazyClass = root._class("GeneratedLazyClass");

    var syncinstance =
        new JLazy(lazyClass, Integer.class, "getSyncInstance")
        .init(root.owner().ref(JLazyTestGen.class).staticInvoke("inc"));
    var syncstatic =
        new JLazy(lazyClass, Integer.class, "getSyncStatic")
        ._static()
        .init(root.owner().ref(JLazyTestGen.class).staticInvoke("inc"));
    var asyncinstance =
        new JLazy(lazyClass, Integer.class, "getASyncInstance")
        .sync(false)
        .init(root.owner().ref(JLazyTestGen.class).staticInvoke("inc"));
    var asyncstatic =
        new JLazy(lazyClass, Integer.class, "getASyncStatic")
        ._static()
        .sync(false)
        .init(root.owner().ref(JLazyTestGen.class).staticInvoke("inc"));

    IJExpression sum =
        syncinstance.expr()
        .plus(syncstatic.expr())
        .plus(asyncinstance.expr())
        .plus(asyncstatic.expr());
    lazyClass.method(JMod.PUBLIC, root.owner().INT, "sum").body()._return(sum);
  }

}
