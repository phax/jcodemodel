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

import java.util.Map;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.string.StringHelper;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.exceptions.JCodeModelException;

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
  default void configure (@NonNull final Map <String, String> params)
  {}

  /**
   * @param header
   *        The class header (comment) to be used. May be <code>null</code>.
   */
  default void setClassHeader (@Nullable final String header)
  {}

  /**
   * alter a model according to the specifications in a source.
   *
   * @param model
   *        the model to build into.
   * @param source
   *        inputstream deduced by the plugin. May be <code>null</code>, in which case most
   *        generators would do nothing ; but this allows to have static generators.
   * @throws JCodeModelException
   *         in case of creation error
   */
  void build (JCodeModel model, @NonNull ISourcedInputStream source) throws JCodeModelException;

  /**
   * shortcut to {@link #build(JCodeModel, ISourcedInputStream)} with null values.
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

  @Nullable
  String getRootPackage ();

  /** transmitted by the plugin, specifies when set in which package to add the classes */
  void setRootPackage (@Nullable String rootPackage);

  /**
   * transform a relative path into absolute by using the rootPackage.
   * 
   * @param localPath
   *        class we want to create, eg "pck.MyClass"
   * @return localpath prefixed by rootpackage and "." if needed.
   */
  default String expandClassName (@Nullable final String localPath)
  {
    final String rootPackage = getRootPackage ();
    return StringHelper.isEmpty (rootPackage) ? localPath : rootPackage + "." + localPath;
  }
}
