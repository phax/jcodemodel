/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2016 Philip Helger + contributors
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

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.AbstractTypeVisitor6;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JTypeWildcard;

/**
 * @author Victor Nazarov &lt;asviraspossible@gmail.com&gt;
 */
class TypeMirrorToJTypeVisitor extends AbstractTypeVisitor6 <AbstractJType, Void>
{
  private final ErrorTypePolicy _errorTypePolicy;
  private final TypeEnvironment _environment;
  private final JCodeModel _codeModel;
  private final DecidedErrorTypesModelsAdapter _modelsAdapter;

  public TypeMirrorToJTypeVisitor (final JCodeModel codeModel,
                                   final DecidedErrorTypesModelsAdapter modelsAdapter,
                                   final ErrorTypePolicy errorTypePolicy,
                                   final TypeEnvironment environment)
  {
    this._codeModel = codeModel;
    this._modelsAdapter = modelsAdapter;
    this._errorTypePolicy = errorTypePolicy;
    this._environment = environment;
  }

  @Override
  public AbstractJType visitPrimitive (final PrimitiveType t, final Void p)
  {
    switch (t.getKind ())
    {
      case BOOLEAN:
        return _codeModel.BOOLEAN;
      case BYTE:
        return _codeModel.BYTE;
      case CHAR:
        return _codeModel.CHAR;
      case INT:
        return _codeModel.INT;
      case LONG:
        return _codeModel.LONG;
      case FLOAT:
        return _codeModel.FLOAT;
      case DOUBLE:
        return _codeModel.DOUBLE;
      case SHORT:
        return _codeModel.SHORT;
      default:
        throw new IllegalArgumentException ("Unrecognized primitive " + t.getKind ());
    }
  }

  @Override
  public AbstractJType visitNull (final NullType t, final Void p)
  {
    // To change body of generated methods, choose Tools | Templates.
    throw new IllegalArgumentException ("null can't be JClass.");
  }

  @Override
  public AbstractJType visitArray (final ArrayType t, final Void p)
  {
    try
    {
      final AbstractJType componentType = _modelsAdapter.toJType (t.getComponentType (), _environment);
      return componentType.array ();
    }
    catch (final CodeModelBuildingException ex)
    {
      throw new RuntimeCodeModelBuildingException (ex);
    }
    catch (final ErrorTypeFound ex)
    {
      throw new RuntimeErrorTypeFound (ex);
    }
  }

  @Override
  public AbstractJType visitDeclared (final DeclaredType t, final Void p)
  {
    try
    {
      final TypeElement element = (TypeElement) t.asElement ();
      AbstractJClass _class = _modelsAdapter.ref (element);
      for (final TypeMirror typeArgument : t.getTypeArguments ())
      {
        _class = _class.narrow (_modelsAdapter.toJType (typeArgument, _environment));
      }
      return _class;
    }
    catch (final CodeModelBuildingException ex)
    {
      throw new RuntimeCodeModelBuildingException (ex);
    }
    catch (final ErrorTypeFound ex)
    {
      throw new RuntimeErrorTypeFound (ex);
    }
  }

  @Override
  public AbstractJType visitError (final ErrorType t, final Void p)
  {
    final String typeName = t.asElement ().getSimpleName ().toString ();
    final String fullTypeName = _environment.packageName () + "." + typeName;
    final JDefinedClass jCodeModelClass = _codeModel._getClass (fullTypeName);
    if (jCodeModelClass != null)
    {
      final List <? extends TypeMirror> typeArguments = t.getTypeArguments ();
      if (typeArguments.isEmpty ())
        return jCodeModelClass;
      final List <AbstractJClass> jArguments = new ArrayList <> (typeArguments.size ());
      for (final TypeMirror typeArgument : typeArguments)
      {
        try
        {
          jArguments.add ((AbstractJClass) _modelsAdapter.toJType (typeArgument, _environment));
        }
        catch (final CodeModelBuildingException ex)
        {
          throw new RuntimeCodeModelBuildingException (ex);
        }
        catch (final ErrorTypeFound ex)
        {
          throw new RuntimeErrorTypeFound (ex);
        }
      }
      return jCodeModelClass.narrow (jArguments);
    }
    if (_errorTypePolicy.action () == ErrorTypePolicy.EAction.CREATE_ERROR_TYPE)
      return _codeModel.errorClass (typeName +
                                    " in annotated source code",
                                    typeName.equals ("<any>") ? null : typeName);
    try
    {
      throw new ErrorTypeFound (typeName + " in annotated source code");
    }
    catch (final ErrorTypeFound ex)
    {
      throw new RuntimeErrorTypeFound (ex);
    }
  }

  @Override
  public AbstractJType visitTypeVariable (final TypeVariable t, final Void p)
  {
    final String typeName = t.asElement ().getSimpleName ().toString ();
    final AbstractJType result = _environment.get (typeName);
    if (result != null)
      return result;
    if (_errorTypePolicy.action () == ErrorTypePolicy.EAction.CREATE_ERROR_TYPE)
      return _codeModel.errorClass ("Missing type-variable " + typeName + " in annotated source code");
    try
    {
      throw new ErrorTypeFound ("Missing type-variable " + typeName + " in annotated source code");
    }
    catch (final ErrorTypeFound ex)
    {
      throw new RuntimeErrorTypeFound (ex);
    }
  }

  @Override
  public AbstractJType visitWildcard (final WildcardType t, final Void p)
  {
    try
    {
      final TypeMirror extendsBoundMirror = t.getExtendsBound ();
      if (extendsBoundMirror != null)
      {
        final AbstractJClass extendsBound = (AbstractJClass) _modelsAdapter.toJType (extendsBoundMirror, _environment);
        return extendsBound.wildcard (JTypeWildcard.EBoundMode.EXTENDS);
      }
      final TypeMirror superBoundMirror = t.getSuperBound ();
      if (superBoundMirror != null)
      {
        final AbstractJClass superBound = (AbstractJClass) _modelsAdapter.toJType (superBoundMirror, _environment);
        return superBound.wildcard (JTypeWildcard.EBoundMode.SUPER);
      }
      return _codeModel.wildcard ();
    }
    catch (final CodeModelBuildingException ex)
    {
      throw new RuntimeCodeModelBuildingException (ex);
    }
    catch (final ErrorTypeFound ex)
    {
      throw new RuntimeErrorTypeFound (ex);
    }
  }

  @Override
  public AbstractJType visitExecutable (final ExecutableType t, final Void p)
  {
    throw new IllegalArgumentException ("executable can't be JClass.");
  }

  @Override
  public AbstractJType visitNoType (final NoType t, final Void p)
  {
    if (_errorTypePolicy.action () == ErrorTypePolicy.EAction.CREATE_ERROR_TYPE)
      return _codeModel.errorClass ("'no type' in annotated source code");

    try
    {
      throw new ErrorTypeFound ("'no type' in annotated source code");
    }
    catch (final ErrorTypeFound ex)
    {
      throw new RuntimeErrorTypeFound (ex);
    }
  }

  @Override
  public AbstractJType visitUnknown (final TypeMirror t, final Void p)
  {
    if (_errorTypePolicy.action () == ErrorTypePolicy.EAction.CREATE_ERROR_TYPE)
      return _codeModel.errorClass ("'unknown type' in annotated source code");

    try
    {
      throw new ErrorTypeFound ("'unknown type' in annotated source code");
    }
    catch (final ErrorTypeFound ex)
    {
      throw new RuntimeErrorTypeFound (ex);
    }
  }
}
