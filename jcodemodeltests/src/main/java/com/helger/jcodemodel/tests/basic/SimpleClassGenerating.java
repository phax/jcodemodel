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
package com.helger.jcodemodel.tests.basic;

import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;

@TestJCM
public class SimpleClassGenerating {

  public void createSimple1(JPackage root) throws JCodeModelException {
    root._class("Simple1");
  }


  public void createSimple2(JPackage root, JPackage root2) throws JCodeModelException {
    assert root == root2;
    root._class("Simple2");
  }

  /**
   * protected so should not be selected
   */
  protected void protectedCall(JPackage root) throws JCodeModelException {
    root._class("ERROR");
  }

  /**
   * requires a param too many so should not be selected
   */
  public void invalidParamCall(JPackage root, Object o) throws JCodeModelException {
    root._class("ERROR2");
  }

  public static void staticSimple(JPackage root) throws JCodeModelException {
    root._class("Simple3");
  }

}
