/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2017 Philip Helger + contributors
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package com.helger.jcodemodel.meta;

import javax.annotation.Nonnull;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;

public class JCodeModelJavaxLangModelAdapter
{
  private final JCodeModel m_aCodeModel;
  private final Elements m_aElementUtils;

  /**
   * Creates new instance of JCodeModelJavaxLangModelAdapter.
   *
   * @param codeModel
   *        Base code model. May not be <code>null</code>.
   * @param elementUtils
   *        Program element utility. May not be <code>null</code>.
   */
  public JCodeModelJavaxLangModelAdapter (@Nonnull final JCodeModel codeModel, @Nonnull final Elements elementUtils)
  {
    this.m_aCodeModel = codeModel;
    this.m_aElementUtils = elementUtils;
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
   *         when operation can't be performed. For example, when given class
   *         already exists.
   */
  @Nonnull
  public JDefinedClass getClass (@Nonnull final TypeElement element) throws ErrorTypeFound, CodeModelBuildingException
  {
    final ErrorTypePolicy policy = new ErrorTypePolicy (ErrorTypePolicy.EAction.THROW_EXCEPTION, true);
    return getClass (element, policy);
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
   *         when operation can't be performed. For example, when given class
   *         already exists.
   */
  @Nonnull
  public JDefinedClass getClassWithErrorTypes (@Nonnull final TypeElement element) throws CodeModelBuildingException
  {
    final ErrorTypePolicy policy = new ErrorTypePolicy (ErrorTypePolicy.EAction.CREATE_ERROR_TYPE, true);
    try
    {
      return getClass (element, policy);
    }
    catch (final ErrorTypeFound ex)
    {
      throw new RuntimeException ("ErrorTypeFound exception is disabled and shouldn't be thrown here", ex);
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
   *         when operation can't be performed. For example, when given class
   *         already exists.
   */
  @Nonnull
  public JDefinedClass getClass (@Nonnull final TypeElement element,
                                 @Nonnull final ErrorTypePolicy policy) throws ErrorTypeFound,
                                                                        CodeModelBuildingException
  {
    final DecidedErrorTypesModelsAdapter errorTypeDecision = new DecidedErrorTypesModelsAdapter (m_aCodeModel,
                                                                                                 m_aElementUtils,
                                                                                                 policy);
    return errorTypeDecision.getClass (element);
  }
}
