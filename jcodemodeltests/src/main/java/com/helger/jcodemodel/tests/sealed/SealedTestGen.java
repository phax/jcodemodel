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
package com.helger.jcodemodel.tests.sealed;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;

@TestJCM
public class SealedTestGen {

  public void basicExample(JPackage root) throws JCodeModelException {
    @NonNull
    JDefinedClass itf = root._interface("SealedInterface");
    itf.mods().setSealed(true);
    JDefinedClass implFinal = root._class(JMod.PUBLIC | JMod.FINAL, "ImplFinal")._implements(itf);
    JDefinedClass implSealed = root._class(JMod.PUBLIC | JMod.SEALED, "ImplSealed")._implements(itf);
    JDefinedClass implNonsealed = root._class(JMod.PUBLIC | JMod.NONSEALED, "ImplNonsealed")._implements(itf);
    itf.permits(implFinal, implSealed, implNonsealed);
    JDefinedClass implSealedChildNonSealed =
        root._class(JMod.PUBLIC | JMod.NONSEALED, "ImplSealedChildNonsealed")._extends(implSealed);
    JDefinedClass implSealedChildFinal =
        root._class(JMod.PUBLIC | JMod.FINAL, "ImplSealedChildFinal")._extends(implSealed);
    implSealed.permits(implSealedChildNonSealed, implSealedChildFinal);
  }

}
