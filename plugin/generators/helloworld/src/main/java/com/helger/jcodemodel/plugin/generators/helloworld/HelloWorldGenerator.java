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
package com.helger.jcodemodel.plugin.generators.helloworld;

import java.io.InputStream;
import java.util.Map;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.plugin.maven.ICodeModelBuilder;
import com.helger.jcodemodel.plugin.maven.generators.JCMGen;

@JCMGen
public class HelloWorldGenerator implements ICodeModelBuilder
{

  protected String m_sRootPackage = "com.helger.tests.helloworld";
  protected String className = "Hello";
  protected String value = "world";

  public void configure (final Map <String, String> params)
  {
    className = params.getOrDefault ("name", className);
    value = params.getOrDefault ("value", value);
  }

  public void build (final JCodeModel model, final InputStream source) throws JCodeModelException
  {
    final JDefinedClass cl = model._class (expandClassName (className));
    cl.field (JMod.PUBLIC, model._ref (String.class), "value", JExpr.lit (value));
  }

  public void setRootPackage (final String rootPackage)
  {
    m_sRootPackage = rootPackage;
  }

  public String getRootPackage ()
  {
    return m_sRootPackage;
  }

}
