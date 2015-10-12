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
import com.helger.jcodemodel.JTypeWildcard;

/**
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
class TypeMirrorToJTypeVisitor extends AbstractTypeVisitor6 <AbstractJType, Void>
{
  private final boolean _includesErrorTypes;
  private final TypeEnvironment _environment;
  private final JCodeModel _codeModel;
  private final DecidedErrorTypesModelsAdapter _modelsAdapter;

  public TypeMirrorToJTypeVisitor (final JCodeModel codeModel,
                                   final DecidedErrorTypesModelsAdapter modelsAdapter,
                                   final boolean includesErrorTypes,
                                   final TypeEnvironment environment)
  {
    this._codeModel = codeModel;
    this._modelsAdapter = modelsAdapter;
    this._includesErrorTypes = includesErrorTypes;
    this._environment = environment;
  }

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

  public AbstractJType visitNull (final NullType t, final Void p)
  {
    // To change body of generated methods, choose Tools | Templates.
    throw new IllegalArgumentException ("null can't be JClass.");
  }

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
  }

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
  }

  public AbstractJType visitError (final ErrorType t, final Void p)
  {
    if (_includesErrorTypes)
      return _codeModel.errorClass (t.asElement ().getSimpleName ().toString () + " in annotated source code");
    try
    {
      throw new ErrorTypeFound ();
    }
    catch (final ErrorTypeFound ex)
    {
      throw new RuntimeErrorTypeFound (ex);
    }
  }

  public AbstractJType visitTypeVariable (final TypeVariable t, final Void p)
  {
    return _environment.get (t.asElement ().getSimpleName ().toString ());
  }

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
  }

  public AbstractJType visitExecutable (final ExecutableType t, final Void p)
  {
    throw new IllegalArgumentException ("executable can't be JClass.");
  }

  public AbstractJType visitNoType (final NoType t, final Void p)
  {
    if (_includesErrorTypes)
      return _codeModel.errorClass ("'no type' in annotated source code");

    try
    {
      throw new ErrorTypeFound ();
    }
    catch (final ErrorTypeFound ex)
    {
      throw new RuntimeErrorTypeFound (ex);
    }
  }

  @Override
  public AbstractJType visitUnknown (final TypeMirror t, final Void p)
  {
    if (_includesErrorTypes)
      return _codeModel.errorClass ("'unknown type' in annotated source code");

    try
    {
      throw new ErrorTypeFound ();
    }
    catch (final ErrorTypeFound ex)
    {
      throw new RuntimeErrorTypeFound (ex);
    }
  }
}
