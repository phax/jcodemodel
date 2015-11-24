/*
 * Copyright (c) 2014, Victor Nazarov <asviraspossible@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation and/or
 *     other materials provided with the distribution.
 *
 *  3. Neither the name of the copyright holder nor the names of its contributors
 *     may be used to endorse or promote products derived from this software
 *     without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *  THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *   LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 *  EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.helger.jcodemodel.meta;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import javax.annotation.Nonnull;

public class JCodeModelJavaxLangModelAdapter
{
  private final JCodeModel _codeModel;
  private final Elements _elementUtils;

  /**
   * Creates new instance of JCodeModelJavaxLangModelAdapter.
   */
  public JCodeModelJavaxLangModelAdapter (@Nonnull final JCodeModel codeModel, @Nonnull final Elements elementUtils)
  {
    this._codeModel = codeModel;
    this._elementUtils = elementUtils;
  }

  /**
   * Returns jcodemodel class definition for given element.
   *
   * @param element
   *        element to convert to class definition
   * @return jcodemodel class definition for given element
   * @throws ErrorTypeFound
   *         if {@code element} argument contains references to so called
   *         "error"-types.
   * @throws CodeModelBuildingException
   *         when operation can't be performed.
   *         For example, when given class already exists.
   */
  @Nonnull
  public JDefinedClass getClass (@Nonnull final TypeElement element) throws ErrorTypeFound, CodeModelBuildingException
  {
    ErrorTypePolicy policy = new ErrorTypePolicy(ErrorTypePolicy.Action.THROW_EXCEPTION, true);
    return getClass(element, policy);
  }

  /**
   * Returns jcodemodel class definition for given element.
   * <p>
   * This method result-class definition can include references to
   * "error"-types. Error-types are used only if they are present in
   * {@code element} argument
   *
   * @param element
   *        element to convert to class definition
   * @return jcodemodel class definition for given element.
   * @throws CodeModelBuildingException
   *         when operation can't be performed.
   *         For example, when given class already exists.
   */
  @Nonnull
  public JDefinedClass getClassWithErrorTypes (@Nonnull final TypeElement element) throws CodeModelBuildingException
  {
    ErrorTypePolicy policy = new ErrorTypePolicy(ErrorTypePolicy.Action.CREATE_ERROR_TYPE, true);
      try {
          return getClass(element, policy);
      } catch (ErrorTypeFound ex) {
          throw new RuntimeException("ErrorTypeFound exception is disabled and shouldn't be thrown here", ex);
      }
  }

  /**
   * Returns jcodemodel class definition for given element.
   * <p>
   * This method result-class definition can include references to
   * "error"-types. Error-types are used only if they are present in
   * {@code element} argument
   *
   * @param element
   *        element to convert to class definition
   * @param policy
   *        error type policy
   * @return jcodemodel class definition for given element.
   * @throws ErrorTypeFound
   *         if error type {@code policy} is configured to throw exceptions and
   *         {@code element} argument contains references to so called
   *         "error"-types
   * @throws CodeModelBuildingException
   *         when operation can't be performed.
   *         For example, when given class already exists.
   */
  @Nonnull
  public JDefinedClass getClass (@Nonnull final TypeElement element, @Nonnull ErrorTypePolicy policy) throws ErrorTypeFound, CodeModelBuildingException
  {
    final DecidedErrorTypesModelsAdapter errorTypeDecision = new DecidedErrorTypesModelsAdapter (_codeModel,
                                                                                                 _elementUtils,
                                                                                                 policy);
    return errorTypeDecision.getClass (element);
  }
}
