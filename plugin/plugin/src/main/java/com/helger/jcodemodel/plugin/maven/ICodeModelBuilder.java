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
package com.helger.jcodemodel.plugin.maven;

import java.io.InputStream;
import java.util.Map;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.exceptions.JCodeModelException;

import jakarta.annotation.Nullable;

/**
 * implementation of a jcodemodel builder (AKA generator)
 */
public interface ICodeModelBuilder
{
  /**
   * called by the plugin after creating the generator, with the plugin "params" configuration.
   * Override to handle generator-specific parameters
   * 
   * @param params
   *        Parameters
   */
  default void configure (final Map <String, String> params)
  {}

  /**
   * asking the generator to build a model.
   *
   * @param model
   *        the model to build into.
   * @param source
   *        inputstream deduced by the plugin. May be <code>null</code>.
   * @throws JCodeModelException
   *         in case of creation error
   */
  void build (JCodeModel model, @Nullable InputStream source) throws JCodeModelException;

  /**
   * shortcut to {@link #build(JCodeModel, InputStream)} with null values.
   * 
   * @param model
   *        the model to build into.
   * @throws JCodeModelException
   *         in case of creation error
   */
  default void build (final JCodeModel model) throws JCodeModelException
  {
    build (model, null);
  }

  void setRootPackage (String rootPackage);

  String getRootPackage ();

  /**
   * @param localPath
   *        class we want to create, eg "pck.MyClass"
   * @return localpath prefixed by rootpackage and "." if needed.
   */
  default String expandClassName (final String localPath)
  {
    final String rootPackage = getRootPackage ();
    return rootPackage == null || rootPackage.isBlank () ? localPath : rootPackage + "." + localPath;
  }

}
