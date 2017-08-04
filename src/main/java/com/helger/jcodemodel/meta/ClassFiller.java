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

import java.util.List;

import javax.annotation.Nonnull;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JTypeVar;
import com.helger.jcodemodel.JVar;

/**
 * @author Victor Nazarov &lt;asviraspossible@gmail.com&gt;
 */
class ClassFiller
{
  private final JDefinedClass m_aNewClass;
  private final JCodeModel m_aCodeModel;
  private final DecidedErrorTypesModelsAdapter m_aModelsAdapter;

  ClassFiller (final JCodeModel codeModel,
               final DecidedErrorTypesModelsAdapter modelsAdapter,
               final JDefinedClass newClass)
  {
    m_aCodeModel = codeModel;
    m_aModelsAdapter = modelsAdapter;
    m_aNewClass = newClass;
  }

  void fillClass (@Nonnull final TypeElement element,
                  @Nonnull final TypeEnvironment environment) throws CodeModelBuildingException, ErrorTypeFound
  {
    m_aNewClass.hide ();
    final Annotator classAnnotator = new Annotator (m_aModelsAdapter, m_aNewClass, environment);
    classAnnotator.annotate (element.getAnnotationMirrors ());
    for (final TypeParameterElement parameter : element.getTypeParameters ())
    {
      final JTypeVar typeVariable = m_aNewClass.generify (parameter.getSimpleName ().toString ());
      environment.put (typeVariable.name (), typeVariable);
      for (final TypeMirror type : parameter.getBounds ())
      {
        typeVariable.bound ((AbstractJClass) m_aModelsAdapter.toJType (type, environment));
      }
    }
    final TypeMirror superclass = element.getSuperclass ();
    if (superclass != null && superclass.getKind () != TypeKind.NONE)
    {
      m_aNewClass._extends ((AbstractJClass) m_aModelsAdapter.toJType (superclass, environment));
    }
    for (final TypeMirror iface : element.getInterfaces ())
    {
      m_aNewClass._implements ((AbstractJClass) m_aModelsAdapter.toJType (iface, environment));
    }
    for (final Element enclosedElement : element.getEnclosedElements ())
    {
      if (enclosedElement.getKind ().equals (ElementKind.INTERFACE) ||
          enclosedElement.getKind ().equals (ElementKind.CLASS))
      {
        final TypeElement innerClassElement = (TypeElement) enclosedElement;
        m_aModelsAdapter.defineInnerClass (m_aNewClass, innerClassElement, environment.enclosed ());
      }
      else
        if (enclosedElement.getKind ().equals (ElementKind.METHOD))
        {
          final ExecutableElement executable = (ExecutableElement) enclosedElement;
          final JMethod method = m_aNewClass.method (DecidedErrorTypesModelsAdapter.toJMod (executable.getModifiers ()),
                                                     m_aCodeModel.VOID,
                                                     executable.getSimpleName ().toString ());
          final TypeEnvironment methodEnvironment = environment.enclosed ();
          final Annotator methodAnnotator = new Annotator (m_aModelsAdapter, method, environment);
          methodAnnotator.annotate (executable.getAnnotationMirrors ());
          for (final TypeParameterElement parameter : executable.getTypeParameters ())
          {
            final JTypeVar typeVariable = method.generify (parameter.getSimpleName ().toString ());
            methodEnvironment.put (typeVariable.name (), typeVariable);
            for (final TypeMirror type : parameter.getBounds ())
            {
              typeVariable.bound ((AbstractJClass) m_aModelsAdapter.toJType (type, methodEnvironment));
            }
          }
          method.type (m_aModelsAdapter.toJType (executable.getReturnType (), methodEnvironment));
          for (final TypeMirror type : executable.getThrownTypes ())
          {
            final AbstractJClass throwable = (AbstractJClass) m_aModelsAdapter.toJType (type, methodEnvironment);
            method._throws (throwable);
          }
          final List <? extends VariableElement> parameters = executable.getParameters ();
          int n = 0;
          for (final VariableElement variable : parameters)
          {
            final String parameterName = variable.getSimpleName ().toString ();
            final TypeMirror parameterTypeMirror = variable.asType ();
            final AbstractJType parameterType = m_aModelsAdapter.toJType (parameterTypeMirror, methodEnvironment);
            JVar param;
            if (executable.isVarArgs () && n == parameters.size () - 1)
            {
              param = method.varParam (DecidedErrorTypesModelsAdapter.toJMod (variable.getModifiers ()),
                                       parameterType.elementType (),
                                       parameterName);
            }
            else
            {
              param = method.param (DecidedErrorTypesModelsAdapter.toJMod (variable.getModifiers ()),
                                    parameterType,
                                    parameterName);
            }
            final Annotator parametorAnnotator = new Annotator (m_aModelsAdapter, param, methodEnvironment);
            parametorAnnotator.annotate (variable.getAnnotationMirrors ());
            n++;
          }
        }
    }
  }

}
