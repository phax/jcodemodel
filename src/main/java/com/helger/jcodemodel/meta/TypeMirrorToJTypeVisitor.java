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
import com.helger.jcodemodel.EWildcardBoundMode;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;

/**
 * @author Victor Nazarov &lt;asviraspossible@gmail.com&gt;
 */
class TypeMirrorToJTypeVisitor extends AbstractTypeVisitor6 <AbstractJType, Void>
{
  private final ErrorTypePolicy m_aErrorTypePolicy;
  private final TypeEnvironment m_aEnvironment;
  private final JCodeModel m_aCodeModel;
  private final DecidedErrorTypesModelsAdapter m_aModelsAdapter;

  public TypeMirrorToJTypeVisitor (final JCodeModel codeModel,
                                   final DecidedErrorTypesModelsAdapter modelsAdapter,
                                   final ErrorTypePolicy errorTypePolicy,
                                   final TypeEnvironment environment)
  {
    this.m_aCodeModel = codeModel;
    this.m_aModelsAdapter = modelsAdapter;
    this.m_aErrorTypePolicy = errorTypePolicy;
    this.m_aEnvironment = environment;
  }

  @Override
  public AbstractJType visitPrimitive (final PrimitiveType t, final Void p)
  {
    switch (t.getKind ())
    {
      case BOOLEAN:
        return m_aCodeModel.BOOLEAN;
      case BYTE:
        return m_aCodeModel.BYTE;
      case CHAR:
        return m_aCodeModel.CHAR;
      case INT:
        return m_aCodeModel.INT;
      case LONG:
        return m_aCodeModel.LONG;
      case FLOAT:
        return m_aCodeModel.FLOAT;
      case DOUBLE:
        return m_aCodeModel.DOUBLE;
      case SHORT:
        return m_aCodeModel.SHORT;
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
      final AbstractJType componentType = m_aModelsAdapter.toJType (t.getComponentType (), m_aEnvironment);
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
      AbstractJClass _class = m_aModelsAdapter.ref (element);
      for (final TypeMirror typeArgument : t.getTypeArguments ())
      {
        _class = _class.narrow (m_aModelsAdapter.toJType (typeArgument, m_aEnvironment));
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
    final String fullTypeName = m_aEnvironment.packageName () + "." + typeName;
    final JDefinedClass jCodeModelClass = m_aCodeModel._getClass (fullTypeName);
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
          jArguments.add ((AbstractJClass) m_aModelsAdapter.toJType (typeArgument, m_aEnvironment));
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
    if (m_aErrorTypePolicy.action () == ErrorTypePolicy.EAction.CREATE_ERROR_TYPE)
      return m_aCodeModel.errorClass (typeName +
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
    final AbstractJType result = m_aEnvironment.get (typeName);
    if (result != null)
      return result;
    if (m_aErrorTypePolicy.action () == ErrorTypePolicy.EAction.CREATE_ERROR_TYPE)
      return m_aCodeModel.errorClass ("Missing type-variable " + typeName + " in annotated source code");
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
        final AbstractJClass extendsBound = (AbstractJClass) m_aModelsAdapter.toJType (extendsBoundMirror, m_aEnvironment);
        return extendsBound.wildcard (EWildcardBoundMode.EXTENDS);
      }
      final TypeMirror superBoundMirror = t.getSuperBound ();
      if (superBoundMirror != null)
      {
        final AbstractJClass superBound = (AbstractJClass) m_aModelsAdapter.toJType (superBoundMirror, m_aEnvironment);
        return superBound.wildcard (EWildcardBoundMode.SUPER);
      }
      return m_aCodeModel.wildcard ();
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
    if (m_aErrorTypePolicy.action () == ErrorTypePolicy.EAction.CREATE_ERROR_TYPE)
      return m_aCodeModel.errorClass ("'no type' in annotated source code");

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
    if (m_aErrorTypePolicy.action () == ErrorTypePolicy.EAction.CREATE_ERROR_TYPE)
      return m_aCodeModel.errorClass ("'unknown type' in annotated source code");

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
