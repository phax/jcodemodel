/*
 * Copyright (c) 2015, Victor Nazarov <asviraspossible@gmail.com>
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

import java.util.List;

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
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
class ClassFiller
{
  private final JDefinedClass _newClass;
  private final JCodeModel _codeModel;
  private final DecidedErrorTypesModelsAdapter _modelsAdapter;

  ClassFiller (final JCodeModel codeModel,
               final DecidedErrorTypesModelsAdapter modelsAdapter,
               final JDefinedClass newClass)
  {
    this._codeModel = codeModel;
    this._modelsAdapter = modelsAdapter;
    this._newClass = newClass;
  }

  void fillClass (final TypeElement element, final TypeEnvironment environment) throws CodeModelBuildingException, ErrorTypeFound
  {
    _newClass.hide ();
    final Annotator classAnnotator = new Annotator (_modelsAdapter, _newClass, environment);
    classAnnotator.annotate (element.getAnnotationMirrors ());
    for (final TypeParameterElement parameter : element.getTypeParameters ())
    {
      final JTypeVar typeVariable = _newClass.generify (parameter.getSimpleName ().toString ());
      environment.put (typeVariable.name (), typeVariable);
      for (final TypeMirror type : parameter.getBounds ())
      {
        typeVariable.bound ((AbstractJClass) _modelsAdapter.toJType (type, environment));
      }
    }
    final TypeMirror superclass = element.getSuperclass ();
    if (superclass != null && superclass.getKind () != TypeKind.NONE)
    {
      _newClass._extends ((AbstractJClass) _modelsAdapter.toJType (superclass, environment));
    }
    for (final TypeMirror iface : element.getInterfaces ())
    {
      _newClass._implements ((AbstractJClass) _modelsAdapter.toJType (iface, environment));
    }
    for (final Element enclosedElement : element.getEnclosedElements ())
    {
      if (enclosedElement.getKind ().equals (ElementKind.INTERFACE) ||
          enclosedElement.getKind ().equals (ElementKind.CLASS))
      {
        final TypeElement innerClassElement = (TypeElement) enclosedElement;
        _modelsAdapter.defineInnerClass (_newClass, innerClassElement, environment.enclosed ());
      }
      else
        if (enclosedElement.getKind ().equals (ElementKind.METHOD))
        {
          final ExecutableElement executable = (ExecutableElement) enclosedElement;
          final JMethod method = _newClass.method (DecidedErrorTypesModelsAdapter.toJMod (executable.getModifiers ()),
                                                   _codeModel.VOID,
                                                   executable.getSimpleName ().toString ());
          final TypeEnvironment methodEnvironment = environment.enclosed ();
          final Annotator methodAnnotator = new Annotator (_modelsAdapter, method, environment);
          methodAnnotator.annotate (executable.getAnnotationMirrors ());
          for (final TypeParameterElement parameter : executable.getTypeParameters ())
          {
            final JTypeVar typeVariable = method.generify (parameter.getSimpleName ().toString ());
            methodEnvironment.put (typeVariable.name (), typeVariable);
            for (final TypeMirror type : parameter.getBounds ())
            {
              typeVariable.bound ((AbstractJClass) _modelsAdapter.toJType (type, methodEnvironment));
            }
          }
          method.type (_modelsAdapter.toJType (executable.getReturnType (), methodEnvironment));
          for (final TypeMirror type : executable.getThrownTypes ())
          {
            final AbstractJClass throwable = (AbstractJClass) _modelsAdapter.toJType (type, methodEnvironment);
            method._throws (throwable);
          }
          final List <? extends VariableElement> parameters = executable.getParameters ();
          int n = 0;
          for (final VariableElement variable : parameters)
          {
            final String parameterName = variable.getSimpleName ().toString ();
            final TypeMirror parameterTypeMirror = variable.asType ();
            final AbstractJType parameterType = _modelsAdapter.toJType (parameterTypeMirror, methodEnvironment);
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
            final Annotator parametorAnnotator = new Annotator (_modelsAdapter, param, methodEnvironment);
            parametorAnnotator.annotate (variable.getAnnotationMirrors ());
            n++;
          }
        }
    }
  }

}
