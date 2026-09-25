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

package com.helger.jcodemodel.plugin.generators.expressions;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import com.helger.base.string.StringHelper;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.plugin.maven.ICodeModelBuilder;
import com.helger.jcodemodel.plugin.maven.ISourcedInputStream;
import com.helger.jcodemodel.plugin.maven.generators.JCMGen;

@JCMGen
public class TypedExpressionsGenerator implements ICodeModelBuilder
{

  private static final Logger log = org.slf4j.LoggerFactory.getLogger (TypedExpressionsGenerator.class);

  public static final String PARAM_CLASSES_KEY = "classes";

  private String m_sClassHeader = "";
  private String m_sRootPackage = "";
  private String classesSpaceSep = "";

  public @Nullable String getClassHeader ()
  {
    return m_sClassHeader;
  }

  public boolean hasClassHeader ()
  {
    return StringHelper.isNotEmpty (m_sClassHeader);
  }

  @Override
  public void setClassHeader (@Nullable final String header)
  {
    m_sClassHeader = header;
  }

  @Override
  public @Nullable String getRootPackage ()
  {
    return m_sRootPackage;
  }

  @Override
  public void setRootPackage (@Nullable final String rootPackage)
  {
    m_sRootPackage = rootPackage;
  }

  @Override
  public void configure (@NonNull Map <String, String> params)
  {
    ICodeModelBuilder.super.configure (params);
    classesSpaceSep = params.getOrDefault (PARAM_CLASSES_KEY, "");
  }

  public Collection <String> listClasses ()
  {
    /// Pattern says \s matches [ \t\n\x0B\f\r] so includes newlines and cr.
    return Stream.of (classesSpaceSep.split ("\\s+")).distinct ().toList ();
  }

  @Override
  public void build (JCodeModel model, @NonNull ISourcedInputStream source) throws JCodeModelException
  {
    Collection <String> classes = listClasses ();
    log.info ("generate expressions for " + classes);
    ExpressionsBuildingProcess process = new ExpressionsBuildingProcess (model, getRootPackage ());
    if (hasClassHeader ())
    {
      process.setClassHeader (getClassHeader ());
    }
    for (String className : classes)
    {
      Class <?> targetClass;
      try
      {
        targetClass = Class.forName (className);
      }
      catch (ClassNotFoundException e)
      {
        throw new IllegalStateException (e);
      }
      process.addTargetClass (targetClass);
    }
    process.processTargets ();
  }
}
