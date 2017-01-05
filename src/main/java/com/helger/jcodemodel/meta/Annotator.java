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

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJAnnotatable;
import com.helger.jcodemodel.JAnnotationArrayMember;
import com.helger.jcodemodel.JAnnotationUse;

/**
 * @author Victor Nazarov &lt;asviraspossible@gmail.com&gt;
 */
class Annotator
{
  private final DecidedErrorTypesModelsAdapter _modelsAdapter;

  private final IJAnnotatable _annotatable;
  private final TypeEnvironment _typeEnvironment;

  public Annotator (final DecidedErrorTypesModelsAdapter modelsAdapter,
                    final IJAnnotatable annotatable,
                    final TypeEnvironment typeEnvironment)
  {
    this._modelsAdapter = modelsAdapter;
    this._annotatable = annotatable;
    this._typeEnvironment = typeEnvironment;
  }

  void annotate (final List <? extends AnnotationMirror> annotationMirrors) throws CodeModelBuildingException,
                                                                            IllegalStateException,
                                                                            ErrorTypeFound
  {
    for (final AnnotationMirror annotation : annotationMirrors)
    {
      annotate (annotation);
    }
  }

  private void annotate (final AnnotationMirror annotation) throws CodeModelBuildingException,
                                                            IllegalStateException,
                                                            ErrorTypeFound
  {
    final JAnnotationUse annotationUse = _annotatable.annotate ((AbstractJClass) _modelsAdapter.toJType (annotation.getAnnotationType (),
                                                                                                         _typeEnvironment));
    final ArgumentAdder reader = new ArgumentAdder (annotationUse);
    reader.addArguments (annotation);
  }

  class ArgumentAdder
  {
    private final JAnnotationUse _annotationUse;

    public ArgumentAdder (final JAnnotationUse annotationUse)
    {
      this._annotationUse = annotationUse;
    }

    void addArguments (final AnnotationMirror annotation) throws CodeModelBuildingException,
                                                          IllegalStateException,
                                                          ErrorTypeFound
    {
      final Map <? extends ExecutableElement, ? extends AnnotationValue> annotationArguments = _modelsAdapter.getElementValuesWithDefaults (annotation);
      for (final Map.Entry <? extends ExecutableElement, ? extends AnnotationValue> annotationValueAssignment : annotationArguments.entrySet ())
      {
        final String name = annotationValueAssignment.getKey ().getSimpleName ().toString ();
        final Object value = annotationValueAssignment.getValue ().getValue ();
        addArgument (name, value);
      }
    }

    private void addArgument (final String name, final Object value) throws IllegalStateException,
                                                                     CodeModelBuildingException,
                                                                     ErrorTypeFound
    {
      if (value instanceof String)
        _annotationUse.param (name, (String) value);
      else
        if (value instanceof Integer)
          _annotationUse.param (name, ((Integer) value).intValue ());
        else
          if (value instanceof Long)
            _annotationUse.param (name, ((Long) value).longValue ());
          else
            if (value instanceof Short)
              _annotationUse.param (name, ((Short) value).shortValue ());
            else
              if (value instanceof Float)
                _annotationUse.param (name, ((Float) value).floatValue ());
              else
                if (value instanceof Double)
                  _annotationUse.param (name, ((Double) value).doubleValue ());
                else
                  if (value instanceof Byte)
                    _annotationUse.param (name, ((Byte) value).byteValue ());
                  else
                    if (value instanceof Character)
                      _annotationUse.param (name, ((Character) value).charValue ());
                    else
                      if (value instanceof Boolean)
                        _annotationUse.param (name, ((Boolean) value).booleanValue ());
                      else
                        if (value instanceof Class)
                          _annotationUse.param (name, (Class <?>) value);
                        else
                          if (value instanceof DeclaredType)
                          {
                            _annotationUse.param (name,
                                                  _modelsAdapter.toJType ((DeclaredType) value, _typeEnvironment));
                          }
                          else
                            if (value instanceof VariableElement)
                            {
                              try
                              {
                                _annotationUse.param (name, actualEnumConstantValue ((VariableElement) value));
                              }
                              catch (final ClassNotFoundException ex)
                              {
                                Logger.getLogger (Annotator.class.getName ()).log (Level.WARNING,
                                                                                   "Not processing annotation argument: {0}: {1}",
                                                                                   new Object [] { name, value });
                              }
                            }
                            else
                              if (value instanceof AnnotationMirror)
                              {
                                final AnnotationMirror annotation = (AnnotationMirror) value;
                                final AbstractJClass annotationClass = (AbstractJClass) _modelsAdapter.toJType (annotation.getAnnotationType (),
                                                                                                                _typeEnvironment);
                                final JAnnotationUse annotationParam = _annotationUse.annotationParam (name,
                                                                                                       annotationClass);
                                final ArgumentAdder adder = new ArgumentAdder (annotationParam);
                                adder.addArguments (annotation);
                              }
                              else
                                if (value instanceof List)
                                {
                                  @SuppressWarnings (value = "unchecked")
                                  final List <? extends AnnotationValue> list = (List <? extends AnnotationValue>) value;
                                  final Iterator <? extends AnnotationValue> iterator = list.iterator ();
                                  if (iterator.hasNext ())
                                  {
                                    final AnnotationValue firstElementValue = iterator.next ();
                                    final Object element = firstElementValue.getValue ();
                                    if (element instanceof String)
                                    {
                                      final String [] elements = new String [list.size ()];
                                      int i = 0;
                                      for (final AnnotationValue elementValue : list)
                                      {
                                        elements[i] = (String) elementValue.getValue ();
                                        i++;
                                      }
                                      _annotationUse.paramArray (name, elements);
                                    }
                                    else
                                      if (element instanceof Integer)
                                      {
                                        final int [] elements = new int [list.size ()];
                                        int i = 0;
                                        for (final AnnotationValue elementValue : list)
                                        {
                                          elements[i] = ((Integer) elementValue.getValue ()).intValue ();
                                          i++;
                                        }
                                        _annotationUse.paramArray (name, elements);
                                      }
                                      else
                                        if (element instanceof Long)
                                        {
                                          final long [] elements = new long [list.size ()];
                                          int i = 0;
                                          for (final AnnotationValue elementValue : list)
                                          {
                                            elements[i] = ((Long) elementValue.getValue ()).longValue ();
                                            i++;
                                          }
                                          _annotationUse.paramArray (name, elements);
                                        }
                                        else
                                          if (element instanceof Short)
                                          {
                                            final short [] elements = new short [list.size ()];
                                            int i = 0;
                                            for (final AnnotationValue elementValue : list)
                                            {
                                              elements[i] = ((Short) elementValue.getValue ()).shortValue ();
                                              i++;
                                            }
                                            _annotationUse.paramArray (name, elements);
                                          }
                                          else
                                            if (element instanceof Float)
                                            {
                                              final float [] elements = new float [list.size ()];
                                              int i = 0;
                                              for (final AnnotationValue elementValue : list)
                                              {
                                                elements[i] = ((Float) elementValue.getValue ()).floatValue ();
                                                i++;
                                              }
                                              _annotationUse.paramArray (name, elements);
                                            }
                                            else
                                              if (element instanceof Double)
                                              {
                                                final double [] elements = new double [list.size ()];
                                                int i = 0;
                                                for (final AnnotationValue elementValue : list)
                                                {
                                                  elements[i] = ((Double) elementValue.getValue ()).doubleValue ();
                                                  i++;
                                                }
                                                _annotationUse.paramArray (name, elements);
                                              }
                                              else
                                                if (element instanceof Byte)
                                                {
                                                  final byte [] elements = new byte [list.size ()];
                                                  int i = 0;
                                                  for (final AnnotationValue elementValue : list)
                                                  {
                                                    elements[i] = ((Byte) elementValue.getValue ()).byteValue ();
                                                    i++;
                                                  }
                                                  _annotationUse.paramArray (name, elements);
                                                }
                                                else
                                                  if (element instanceof Character)
                                                  {
                                                    final char [] elements = new char [list.size ()];
                                                    int i = 0;
                                                    for (final AnnotationValue elementValue : list)
                                                    {
                                                      elements[i] = ((Character) elementValue.getValue ()).charValue ();
                                                      i++;
                                                    }
                                                    _annotationUse.paramArray (name, elements);
                                                  }
                                                  else
                                                    if (element instanceof Boolean)
                                                    {
                                                      final boolean [] elements = new boolean [list.size ()];
                                                      int i = 0;
                                                      for (final AnnotationValue elementValue : list)
                                                      {
                                                        elements[i] = ((Boolean) elementValue.getValue ()).booleanValue ();
                                                        i++;
                                                      }
                                                      _annotationUse.paramArray (name, elements);
                                                    }
                                                    else
                                                      if (element instanceof Class)
                                                      {
                                                        final Class <?> [] elements = new Class <?> [list.size ()];
                                                        int i = 0;
                                                        for (final AnnotationValue elementValue : list)
                                                        {
                                                          elements[i] = (Class <?>) elementValue.getValue ();
                                                          i++;
                                                        }
                                                        _annotationUse.paramArray (name, elements);
                                                      }
                                                      else
                                                        if (element instanceof DeclaredType)
                                                        {
                                                          final AbstractJType [] elements = new AbstractJType [list.size ()];
                                                          int i = 0;
                                                          for (final AnnotationValue elementValue : list)
                                                          {
                                                            elements[i] = _modelsAdapter.toJType ((DeclaredType) elementValue.getValue (),
                                                                                                  _typeEnvironment);
                                                            i++;
                                                          }
                                                          _annotationUse.paramArray (name, elements);
                                                        }
                                                        else
                                                          if (element instanceof VariableElement)
                                                          {
                                                            try
                                                            {
                                                              final Enum <?> [] elements = new Enum <?> [list.size ()];
                                                              int i = 0;
                                                              for (final AnnotationValue elementValue : list)
                                                              {
                                                                elements[i] = actualEnumConstantValue ((VariableElement) elementValue.getValue ());
                                                                i++;
                                                              }
                                                              _annotationUse.paramArray (name, elements);
                                                            }
                                                            catch (final ClassNotFoundException ex)
                                                            {
                                                              Logger.getLogger (Annotator.class.getName ())
                                                                    .log (Level.WARNING,
                                                                          "Not processing annotation argument: {0}: {1}",
                                                                          new Object [] { name, list });
                                                            }
                                                          }
                                                          else
                                                            if (element instanceof AnnotationMirror)
                                                            {
                                                              final JAnnotationArrayMember paramArray = _annotationUse.paramArray (name);
                                                              for (final AnnotationValue elementValue : list)
                                                              {
                                                                final AnnotationMirror annotation = (AnnotationMirror) elementValue.getValue ();
                                                                final AbstractJClass annotationClass = (AbstractJClass) _modelsAdapter.toJType (annotation.getAnnotationType (),
                                                                                                                                                _typeEnvironment);
                                                                final JAnnotationUse annotationParam = paramArray.annotate (annotationClass);
                                                                final ArgumentAdder adder = new ArgumentAdder (annotationParam);
                                                                adder.addArguments (annotation);
                                                              }
                                                            }
                                                            else
                                                            {
                                                              throw new IllegalStateException (MessageFormat.format ("Unknown annotation array argument: {0}: {1} ({2})",
                                                                                                                     name,
                                                                                                                     element,
                                                                                                                     element.getClass ()));
                                                            }
                                  }
                                }
                                else
                                  throw new IllegalStateException (MessageFormat.format ("Unknown annotation argument: {0}: {1} ({2})",
                                                                                         name,
                                                                                         value,
                                                                                         value.getClass ()));
    }

    private Enum <?> actualEnumConstantValue (final VariableElement variableElement) throws ClassNotFoundException
    {
      final TypeElement enumClassElement = (TypeElement) variableElement.getEnclosingElement ();
      final Class <?> enumClass = Class.forName (enumClassElement.getQualifiedName ().toString ());
      Field enumConstantField;
      try
      {
        enumConstantField = enumClass.getField (variableElement.getSimpleName ().toString ());
      }
      catch (final NoSuchFieldException ex)
      {
        throw new IllegalStateException (MessageFormat.format ("Unable to load enum constant: {0}.{1}",
                                                               enumClassElement.getQualifiedName ().toString (),
                                                               variableElement.getSimpleName ().toString ()),
                                         ex);
      }
      catch (final SecurityException ex)
      {
        throw new IllegalStateException (MessageFormat.format ("Unable to load enum constant: {0}.{1}",
                                                               enumClassElement.getQualifiedName ().toString (),
                                                               variableElement.getSimpleName ().toString ()),
                                         ex);
      }
      Enum <?> enumValue;
      try
      {
        enumValue = (Enum <?>) enumConstantField.get (null);
      }
      catch (final IllegalArgumentException ex)
      {
        throw new IllegalStateException (MessageFormat.format ("Unable to load enum constant actual value: {0}.{1}",
                                                               enumClassElement.getQualifiedName ().toString (),
                                                               variableElement.getSimpleName ().toString ()),
                                         ex);
      }
      catch (final IllegalAccessException ex)
      {
        throw new IllegalStateException (MessageFormat.format ("Unable to load enum constant actual value: {0}.{1}",
                                                               enumClassElement.getQualifiedName ().toString (),
                                                               variableElement.getSimpleName ().toString ()),
                                         ex);
      }
      return enumValue;
    }

  }
}
