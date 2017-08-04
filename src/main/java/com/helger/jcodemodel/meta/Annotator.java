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

import javax.annotation.Nonnull;
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
  private final DecidedErrorTypesModelsAdapter m_aModelsAdapter;
  private final IJAnnotatable m_aAnnotatable;
  private final TypeEnvironment m_aTypeEnvironment;

  public Annotator (final DecidedErrorTypesModelsAdapter modelsAdapter,
                    final IJAnnotatable annotatable,
                    final TypeEnvironment typeEnvironment)
  {
    m_aModelsAdapter = modelsAdapter;
    m_aAnnotatable = annotatable;
    m_aTypeEnvironment = typeEnvironment;
  }

  void annotate (@Nonnull final List <? extends AnnotationMirror> annotationMirrors) throws CodeModelBuildingException,
                                                                                     IllegalStateException,
                                                                                     ErrorTypeFound
  {
    for (final AnnotationMirror annotation : annotationMirrors)
    {
      _annotate (annotation);
    }
  }

  private void _annotate (@Nonnull final AnnotationMirror annotation) throws CodeModelBuildingException,
                                                                      IllegalStateException,
                                                                      ErrorTypeFound
  {
    final JAnnotationUse annotationUse = m_aAnnotatable.annotate ((AbstractJClass) m_aModelsAdapter.toJType (annotation.getAnnotationType (),
                                                                                                             m_aTypeEnvironment));
    final ArgumentAdder reader = new ArgumentAdder (annotationUse);
    reader.addArguments (annotation);
  }

  class ArgumentAdder
  {
    private final JAnnotationUse m_aAnnotationUse;

    public ArgumentAdder (@Nonnull final JAnnotationUse annotationUse)
    {
      m_aAnnotationUse = annotationUse;
    }

    void addArguments (final AnnotationMirror annotation) throws CodeModelBuildingException,
                                                          IllegalStateException,
                                                          ErrorTypeFound
    {
      final Map <? extends ExecutableElement, ? extends AnnotationValue> annotationArguments = m_aModelsAdapter.getElementValuesWithDefaults (annotation);
      for (final Map.Entry <? extends ExecutableElement, ? extends AnnotationValue> annotationValueAssignment : annotationArguments.entrySet ())
      {
        final String name = annotationValueAssignment.getKey ().getSimpleName ().toString ();
        final Object value = annotationValueAssignment.getValue ().getValue ();
        _addArgument (name, value);
      }
    }

    private void _addArgument (final String name, final Object value) throws IllegalStateException,
                                                                      CodeModelBuildingException,
                                                                      ErrorTypeFound
    {
      if (value instanceof String)
        m_aAnnotationUse.param (name, (String) value);
      else
        if (value instanceof Integer)
          m_aAnnotationUse.param (name, ((Integer) value).intValue ());
        else
          if (value instanceof Long)
            m_aAnnotationUse.param (name, ((Long) value).longValue ());
          else
            if (value instanceof Short)
              m_aAnnotationUse.param (name, ((Short) value).shortValue ());
            else
              if (value instanceof Float)
                m_aAnnotationUse.param (name, ((Float) value).floatValue ());
              else
                if (value instanceof Double)
                  m_aAnnotationUse.param (name, ((Double) value).doubleValue ());
                else
                  if (value instanceof Byte)
                    m_aAnnotationUse.param (name, ((Byte) value).byteValue ());
                  else
                    if (value instanceof Character)
                      m_aAnnotationUse.param (name, ((Character) value).charValue ());
                    else
                      if (value instanceof Boolean)
                        m_aAnnotationUse.param (name, ((Boolean) value).booleanValue ());
                      else
                        if (value instanceof Class)
                          m_aAnnotationUse.param (name, (Class <?>) value);
                        else
                          if (value instanceof DeclaredType)
                          {
                            m_aAnnotationUse.param (name,
                                                    m_aModelsAdapter.toJType ((DeclaredType) value,
                                                                              m_aTypeEnvironment));
                          }
                          else
                            if (value instanceof VariableElement)
                            {
                              try
                              {
                                m_aAnnotationUse.param (name, _actualEnumConstantValue ((VariableElement) value));
                              }
                              catch (final ClassNotFoundException ex)
                              {
                                Logger.getLogger (Annotator.class.getName ())
                                      .log (Level.WARNING,
                                            "Not processing annotation argument: {0}: {1}",
                                            new Object [] { name, value });
                              }
                            }
                            else
                              if (value instanceof AnnotationMirror)
                              {
                                final AnnotationMirror annotation = (AnnotationMirror) value;
                                final AbstractJClass annotationClass = (AbstractJClass) m_aModelsAdapter.toJType (annotation.getAnnotationType (),
                                                                                                                  m_aTypeEnvironment);
                                final JAnnotationUse annotationParam = m_aAnnotationUse.annotationParam (name,
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
                                      m_aAnnotationUse.paramArray (name, elements);
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
                                        m_aAnnotationUse.paramArray (name, elements);
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
                                          m_aAnnotationUse.paramArray (name, elements);
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
                                            m_aAnnotationUse.paramArray (name, elements);
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
                                              m_aAnnotationUse.paramArray (name, elements);
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
                                                m_aAnnotationUse.paramArray (name, elements);
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
                                                  m_aAnnotationUse.paramArray (name, elements);
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
                                                    m_aAnnotationUse.paramArray (name, elements);
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
                                                      m_aAnnotationUse.paramArray (name, elements);
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
                                                        m_aAnnotationUse.paramArray (name, elements);
                                                      }
                                                      else
                                                        if (element instanceof DeclaredType)
                                                        {
                                                          final AbstractJType [] elements = new AbstractJType [list.size ()];
                                                          int i = 0;
                                                          for (final AnnotationValue elementValue : list)
                                                          {
                                                            elements[i] = m_aModelsAdapter.toJType ((DeclaredType) elementValue.getValue (),
                                                                                                    m_aTypeEnvironment);
                                                            i++;
                                                          }
                                                          m_aAnnotationUse.paramArray (name, elements);
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
                                                                elements[i] = _actualEnumConstantValue ((VariableElement) elementValue.getValue ());
                                                                i++;
                                                              }
                                                              m_aAnnotationUse.paramArray (name, elements);
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
                                                              final JAnnotationArrayMember paramArray = m_aAnnotationUse.paramArray (name);
                                                              for (final AnnotationValue elementValue : list)
                                                              {
                                                                final AnnotationMirror annotation = (AnnotationMirror) elementValue.getValue ();
                                                                final AbstractJClass annotationClass = (AbstractJClass) m_aModelsAdapter.toJType (annotation.getAnnotationType (),
                                                                                                                                                  m_aTypeEnvironment);
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

    private Enum <?> _actualEnumConstantValue (final VariableElement variableElement) throws ClassNotFoundException
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
        throw new IllegalStateException ("Unable to load enum constant: " +
                                         enumClassElement.getQualifiedName ().toString () +
                                         "." +
                                         variableElement.getSimpleName ().toString (),
                                         ex);
      }
      catch (final SecurityException ex)
      {
        throw new IllegalStateException ("Unable to load enum constant: " +
                                         enumClassElement.getQualifiedName ().toString () +
                                         "" +
                                         "." +
                                         variableElement.getSimpleName ().toString (),
                                         ex);
      }
      Enum <?> enumValue;
      try
      {
        enumValue = (Enum <?>) enumConstantField.get (null);
      }
      catch (final IllegalArgumentException ex)
      {
        throw new IllegalStateException ("Unable to load enum constant actual value: " +
                                         enumClassElement.getQualifiedName ().toString () +
                                         "." +
                                         variableElement.getSimpleName ().toString (),
                                         ex);
      }
      catch (final IllegalAccessException ex)
      {
        throw new IllegalStateException ("Unable to load enum constant actual value:" +
                                         enumClassElement.getQualifiedName ().toString () +
                                         "." +
                                         variableElement.getSimpleName ().toString (),
                                         ex);
      }
      return enumValue;
    }

  }
}
