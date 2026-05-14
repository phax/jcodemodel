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
package com.helger.jcodemodel.tests.unnamed;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;

@TestJCM
public class UnnamedTestGen {

  public void basic(JPackage rootPck, JCodeModel jcm) throws JCodeModelException {
    JDefinedClass cl = rootPck._class("SimpleUnnamed");
    JMethod eq = cl.method(JMod.PUBLIC, jcm.BOOLEAN, "equals");
    // TODO use unnamed variable name later (when avail in the project)
    eq.param(jcm.ref(Object.class), "_unnamed");
    eq.body()._return(JExpr.FALSE);
  }

}
