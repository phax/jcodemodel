/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2014 Philip Helger
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
package com.helger.jcodemodel;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents an annotation on a program element. TODO How to add enums to the
 * annotations
 *
 * @author Bhakti Mehta (bhakti.mehta@sun.com)
 */
public class JAnnotationUse extends AbstractJAnnotationValueOwned
{
  /**
   * The special parameter name that can be optimized away if used without any
   * other parameter
   */
  public static final String SPECIAL_KEY_VALUE = "value";

  /**
   * The {@link Annotation} class
   */
  private final AbstractJClass _clazz;

  /**
   * Map of member values.
   */
  private Map <String, AbstractJAnnotationValue> _memberValues;

  public JAnnotationUse (@Nonnull final AbstractJClass clazz)
  {
    if (clazz == null)
      throw new NullPointerException ("clazz");
    _clazz = clazz;
  }

  @Nonnull
  public AbstractJClass getAnnotationClass ()
  {
    return _clazz;
  }

  @Nonnull
  public JCodeModel owner ()
  {
    return _clazz.owner ();
  }

  @Nonnull
  public Map <String, AbstractJAnnotationValue> getAnnotationMembers ()
  {
    return _memberValues == null ? new HashMap <String, AbstractJAnnotationValue> ()
                                : Collections.unmodifiableMap (_memberValues);
  }

  public boolean hasAnnotationMembers ()
  {
    return _memberValues != null && !_memberValues.isEmpty ();
  }

  @Nullable
  public AbstractJAnnotationValue getParam (@Nullable final String sName)
  {
    return _memberValues == null ? null : _memberValues.get (sName);
  }

  @Nullable
  public JAnnotationStringValue getConstantParam (@Nullable final String sName)
  {
    final AbstractJAnnotationValue aParam = getParam (sName);
    return aParam instanceof JAnnotationStringValue ? (JAnnotationStringValue) aParam : null;
  }

  @Nullable
  public IJExpression getConstantParamValue (@Nullable final String sName)
  {
    final JAnnotationStringValue aParam = getConstantParam (sName);
    return aParam != null ? aParam.value () : null;
  }

  private void _addValue (@Nonnull final String name, @Nonnull final AbstractJAnnotationValue annotationValue)
  {
    if (name == null || name.length () == 0)
      throw new IllegalArgumentException ("Name must not be null or empty");
    if (annotationValue == null)
      throw new NullPointerException ("annotationValue may not be null!");

    // Use ordered map to keep the code generation the same on any JVM.
    // Lazily created.
    if (_memberValues == null)
      _memberValues = new LinkedHashMap <String, AbstractJAnnotationValue> ();
    _memberValues.put (name, annotationValue);
  }

  /**
   * Adds a member value pair to this annotation
   *
   * @param name
   *        The simple name for this annotation
   * @param value
   *        The boolean value for this annotation
   * @return The {@link JAnnotationUse}. More member value pairs can be added to
   *         it using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (@Nonnull final String name, final boolean value)
  {
    _addValue (name, wrap (value));
    return this;
  }

  @Nonnull
  public JAnnotationUse paramArray (@Nonnull final String name, @Nonnull final boolean... values)
  {
    paramArray (name).params (values);
    return this;
  }

  /**
   * Adds a member value pair to this annotation
   *
   * @param name
   *        The simple name for this annotation
   * @param value
   *        The byte member value for this annotation
   * @return The {@link JAnnotationUse}. More member value pairs can be added to
   *         it using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (@Nonnull final String name, final byte value)
  {
    _addValue (name, wrap (value));
    return this;
  }

  @Nonnull
  public JAnnotationUse paramArray (@Nonnull final String name, @Nonnull final byte... values)
  {
    paramArray (name).params (values);
    return this;
  }

  /**
   * Adds a member value pair to this annotation
   *
   * @param name
   *        The simple name for this annotation
   * @param value
   *        The char member value for this annotation
   * @return The {@link JAnnotationUse}. More member value pairs can be added to
   *         it using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (@Nonnull final String name, final char value)
  {
    _addValue (name, wrap (value));
    return this;
  }

  @Nonnull
  public JAnnotationUse paramArray (@Nonnull final String name, @Nonnull final char... values)
  {
    paramArray (name).params (values);
    return this;
  }

  /**
   * Adds a member value pair to this annotation
   *
   * @param name
   *        The simple name for this annotation
   * @param value
   *        The double member value for this annotation
   * @return The {@link JAnnotationUse}. More member value pairs can be added to
   *         it using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (@Nonnull final String name, final double value)
  {
    _addValue (name, wrap (value));
    return this;
  }

  @Nonnull
  public JAnnotationUse paramArray (@Nonnull final String name, @Nonnull final double... values)
  {
    paramArray (name).params (values);
    return this;
  }

  /**
   * Adds a member value pair to this annotation
   *
   * @param name
   *        The simple name for this annotation
   * @param value
   *        The float member value for this annotation
   * @return The {@link JAnnotationUse}. More member value pairs can be added to
   *         it using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (@Nonnull final String name, final float value)
  {
    _addValue (name, wrap (value));
    return this;
  }

  @Nonnull
  public JAnnotationUse paramArray (@Nonnull final String name, @Nonnull final float... values)
  {
    paramArray (name).params (values);
    return this;
  }

  /**
   * Adds a member value pair to this annotation
   *
   * @param name
   *        The simple name for this annotation
   * @param value
   *        The long member value for this annotation
   * @return The {@link JAnnotationUse}. More member value pairs can be added to
   *         it using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (@Nonnull final String name, final long value)
  {
    _addValue (name, wrap (value));
    return this;
  }

  @Nonnull
  public JAnnotationUse paramArray (@Nonnull final String name, @Nonnull final long... values)
  {
    paramArray (name).params (values);
    return this;
  }

  /**
   * Adds a member value pair to this annotation
   *
   * @param name
   *        The simple name for this annotation
   * @param value
   *        The short member value for this annotation
   * @return The {@link JAnnotationUse}. More member value pairs can be added to
   *         it using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (@Nonnull final String name, final short value)
  {
    _addValue (name, wrap (value));
    return this;
  }

  @Nonnull
  public JAnnotationUse paramArray (@Nonnull final String name, @Nonnull final short... values)
  {
    paramArray (name).params (values);
    return this;
  }

  /**
   * Adds a member value pair to this annotation
   *
   * @param name
   *        The simple name for this annotation
   * @param value
   *        The int member value for this annotation
   * @return The {@link JAnnotationUse}. More member value pairs can be added to
   *         it using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (@Nonnull final String name, final int value)
  {
    _addValue (name, wrap (value));
    return this;
  }

  @Nonnull
  public JAnnotationUse paramArray (@Nonnull final String name, @Nonnull final int... values)
  {
    paramArray (name).params (values);
    return this;
  }

  /**
   * Adds a member value pair to this annotation
   *
   * @param name
   *        The simple name for this annotation
   * @param value
   *        The String member value for this annotation
   * @return The {@link JAnnotationUse}. More member value pairs can be added to
   *         it using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (@Nonnull final String name, final String value)
  {
    _addValue (name, wrap (value));
    return this;
  }

  @Nonnull
  public JAnnotationUse paramArray (@Nonnull final String name, @Nonnull final String... values)
  {
    paramArray (name).params (values);
    return this;
  }

  /**
   * Adds a member value pair to this annotation
   *
   * @param name
   *        The simple name for this annotation
   * @param value
   *        The enum class which is member value for this annotation
   * @return The {@link JAnnotationUse}. More member value pairs can be added to
   *         it using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (@Nonnull final String name, @Nonnull final Enum <?> value)
  {
    _addValue (name, wrap (value));
    return this;
  }

  @Nonnull
  public JAnnotationUse paramArray (@Nonnull final String name, @Nonnull final Enum <?>... values)
  {
    paramArray (name).params (values);
    return this;
  }

  /**
   * Adds a member value pair to this annotation
   *
   * @param name
   *        The simple name for this annotation
   * @param value
   *        The {@link JEnumConstant} which is member value for this annotation
   * @return The {@link JAnnotationUse}. More member value pairs can be added to
   *         it using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (@Nonnull final String name, @Nonnull final JEnumConstant value)
  {
    _addValue (name, wrap (value));
    return this;
  }

  @Nonnull
  public JAnnotationUse paramArray (@Nonnull final String name, @Nonnull final JEnumConstant... values)
  {
    paramArray (name).params (values);
    return this;
  }

  /**
   * Adds a member value pair to this annotation This can be used for e.g to
   * specify
   *
   * <pre>
   * &#64;XmlCollectionItem(type=Integer.class);
   * </pre>
   *
   * For adding a value of Class&lt;? extends Annotation&gt;
   * {@link #annotationParam(String, Class)}
   *
   * @param name
   *        The simple name for this annotation param
   * @param value
   *        The class type of the param
   * @return The {@link JAnnotationUse}. More member value pairs can be added to
   *         it using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (@Nonnull final String name, @Nonnull final Class <?> value)
  {
    _addValue (name, wrap (value));
    return this;
  }

  @Nonnull
  public JAnnotationUse paramArray (@Nonnull final String name, @Nonnull final Class <?>... values)
  {
    paramArray (name).params (values);
    return this;
  }

  /**
   * Adds a member value pair to this annotation based on the type represented
   * by the given {@link AbstractJType}
   *
   * @param name
   *        The simple name for this annotation param
   * @param type
   *        the {@link AbstractJType} representing the actual type
   * @return The {@link JAnnotationUse}. More member value pairs can be added to
   *         it using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (@Nonnull final String name, @Nonnull final AbstractJType type)
  {
    _addValue (name, wrap (type));
    return this;
  }

  @Nonnull
  public JAnnotationUse paramArray (@Nonnull final String name, @Nonnull final AbstractJType... values)
  {
    paramArray (name).params (values);
    return this;
  }

  /**
   * Adds a member value pair to this annotation.
   *
   * @param name
   *        The simple name for this annotation
   * @param value
   *        The {@link IJExpression} which provides the content value for this
   *        annotation
   * @return The {@link JAnnotationUse}. More member value pairs can be added to
   *         it using the same or the overloaded methods.
   */
  @Nonnull
  @Deprecated
  public JAnnotationUse param (@Nonnull final String name, @Nonnull final IJExpression value)
  {
    _addValue (name, wrap (value));
    return this;
  }

  @Nonnull
  @Deprecated
  public JAnnotationUse paramArray (@Nonnull final String name, @Nonnull final IJExpression... values)
  {
    paramArray (name).params (values);
    return this;
  }

  /**
   * Adds a member value pair which is of type array to this annotation
   *
   * @param name
   *        The simple name for this annotation
   * @return The {@link JAnnotationArrayMember}. For adding array values
   * @see JAnnotationArrayMember
   */
  @Nonnull
  public JAnnotationArrayMember paramArray (@Nonnull final String name)
  {
    final JAnnotationArrayMember arrayMember = new JAnnotationArrayMember (owner ());
    _addValue (name, arrayMember);
    return arrayMember;
  }

  /**
   * Adds a member value pair to this annotation For adding class values as
   * param
   *
   * @see #param(String, Class)
   * @param name
   *        The simple name for this annotation
   * @param value
   *        The annotation class which is member value for this annotation
   * @return The {@link JAnnotationUse}. More member value pairs can be added to
   *         it using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse annotationParam (@Nonnull final String name, @Nonnull final Class <? extends Annotation> value)
  {
    return annotationParam (name, owner ().ref (value));
  }

  /**
   * Adds a member value pair to this annotation For adding class values as
   * param
   *
   * @see #param(String, Class)
   * @param name
   *        The simple name for this annotation
   * @param value
   *        The annotation class which is member value for this annotation
   * @return The {@link JAnnotationUse}. More member value pairs can be added to
   *         it using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse annotationParam (@Nonnull final String name, @Nonnull final AbstractJClass value)
  {
    final JAnnotationUse annotationUse = new JAnnotationUse (value);
    _addValue (name, annotationUse);
    return annotationUse;
  }

  @Nonnegative
  public int size ()
  {
    return _memberValues.size ();
  }

  private boolean _isOptimizable ()
  {
    return _memberValues.size () == 1 && _memberValues.containsKey (SPECIAL_KEY_VALUE);
  }

  public void generate (final JFormatter f)
  {
    f.print ('@').generable (_clazz);
    if (_memberValues != null && !_memberValues.isEmpty ())
    {
      f.print ('(');
      if (_isOptimizable ())
      {
        // short form
        f.generable (_memberValues.get (SPECIAL_KEY_VALUE));
      }
      else
      {
        boolean first = true;
        for (final Map.Entry <String, AbstractJAnnotationValue> mapEntry : _memberValues.entrySet ())
        {
          if (!first)
            f.print (',');
          f.print (mapEntry.getKey ()).print ('=').generable (mapEntry.getValue ());
          first = false;
        }
      }
      f.print (')');
    }
  }
}
