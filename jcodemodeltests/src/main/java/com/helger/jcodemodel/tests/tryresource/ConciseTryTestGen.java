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
package com.helger.jcodemodel.tests.tryresource;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.JVar;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;

@TestJCM
public class ConciseTryTestGen {

  public interface NoErrorCloseable extends AutoCloseable {

    /**
     * doesn't throw an ioexception
     */
    @Override
    void close();
  }

  public void basicTry(JPackage root, JCodeModel jcm) throws JCodeModelException {
    JDefinedClass cl = root._class("BasicTry");
    JMethod m = cl.method(JMod.PUBLIC, jcm.VOID, "close");
    JVar p = m.param(NoErrorCloseable.class, "p");
    m.body()._try().withResource(p);
  }

}
