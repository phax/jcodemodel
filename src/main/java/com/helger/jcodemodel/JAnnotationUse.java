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
package com.helger.jcodemodel;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.jcodemodel.util.JCValueEnforcer;

/**
 * Represents an annotation on a program element.
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
  private final AbstractJClass m_aAnnotationClass;

  /**
   * Map of member aValues.
   */
  private Map <String, AbstractJAnnotationValue> m_aMemberValues;

  public JAnnotationUse (@Nonnull final AbstractJClass aAnnotationClass)
  {
    m_aAnnotationClass = JCValueEnforcer.notNull (aAnnotationClass, "AnnotationClass");
  }

  @Nonnull
  public AbstractJClass getAnnotationClass ()
  {
    return m_aAnnotationClass;
  }

  @Nonnull
  public JCodeModel owner ()
  {
    return m_aAnnotationClass.owner ();
  }

  @Nonnull
  public Map <String, AbstractJAnnotationValue> getAnnotationMembers ()
  {
    return m_aMemberValues == null ? new HashMap <> () : Collections.unmodifiableMap (m_aMemberValues);
  }

  public boolean hasAnnotationMembers ()
  {
    return m_aMemberValues != null && !m_aMemberValues.isEmpty ();
  }

  @Nullable
  public AbstractJAnnotationValue getParam (@Nullable final String sName)
  {
    return m_aMemberValues == null ? null : m_aMemberValues.get (sName);
  }

  @SuppressWarnings ("unchecked")
  private static <T> T _castAnnotationArgument (@Nullable final AbstractJAnnotationValue aValue,
                                                @Nonnull final Class <T> aClazz) throws ClassCastException
  {
    if (!aClazz.isArray ())
    {
      if (aValue == null)
        throw new ClassCastException ("Can't cast null annotation value to " + aClazz + " class");

      if (JAnnotationUse.class.isAssignableFrom (aClazz))
        return aClazz.cast (aValue);

      if (!(aValue instanceof JAnnotationStringValue))
        throw new ClassCastException ("Can't cast " + aValue + " annotation value to " + aClazz + " class");

      final JAnnotationStringValue aStringValue = (JAnnotationStringValue) aValue;
      return (T) aStringValue.nativeValue ();
    }

    // It's any array
    if (aValue == null)
      return (T) Array.newInstance (aClazz.getComponentType (), 0);

    final JAnnotationArrayMember jarray = (JAnnotationArrayMember) aValue;
    final Collection <AbstractJAnnotationValue> interfaceJArray = jarray.getAllAnnotations ();
    final Object [] result = (Object []) Array.newInstance (aClazz.getComponentType (), interfaceJArray.size ());
    final Iterator <AbstractJAnnotationValue> iterator = interfaceJArray.iterator ();
    for (int i = 0; iterator.hasNext (); i++)
    {
      result[i] = _castAnnotationArgument (iterator.next (), aClazz.getComponentType ());
    }
    return (T) result;
  }

  /**
   * Return annotation argument represented as required type. Return type is
   * chosen to conform to given klass argument.
   * <p>
   * For example, you can have annotation parameter named 'value' of type
   * String.
   * <p>
   * You can write {@code getParam("value", String.class)} to get raw
   * string-value. You can write
   * {@code getParam("value", AbstractJAnnotationValue.class)} to get
   * AbstractJAnnotationValue for this argument.
   * <p>
   * Arrays are supported as a result type.
   *
   * @param <T>
   *        return type
   * @param sName
   *        annotation parameter name
   * @param aClazz
   *        type to use as a return type
   * @return annotation argument represented as required type
   */
  @Nullable
  public <T> T getParam (@Nonnull final String sName, @Nonnull final Class <T> aClazz) throws ClassCastException
  {
    final AbstractJAnnotationValue value = getParam (sName);
    return _castAnnotationArgument (value, aClazz);
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

  @Nonnull
  private JAnnotationUse _addValue (@Nonnull final String sName,
                                    @Nonnull final AbstractJAnnotationValue aAnnotationValue)
  {
    JCValueEnforcer.notEmpty (sName, "Name");
    JCValueEnforcer.notNull (aAnnotationValue, "AnnotationValue");

    // Use ordered map to keep the code generation the same on any JVM.
    // Lazily created.
    if (m_aMemberValues == null)
      m_aMemberValues = new LinkedHashMap <> ();
    m_aMemberValues.put (sName, aAnnotationValue);

    return this;
  }

  /**
   * Adds a member value pair to this annotation
   *
   * @param sName
   *        The simple name for this annotation
   * @param bValue
   *        The boolean value for this annotation
   * @return The {@link JAnnotationUse}. More member value pairs can be added to
   *         it using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (@Nonnull final String sName, final boolean bValue)
  {
    return _addValue (sName, wrap (bValue));
  }

  @Nonnull
  public JAnnotationUse paramArray (@Nonnull final String sName, @Nonnull final boolean... aValues)
  {
    paramArray (sName).params (aValues);
    return this;
  }

  /**
   * Adds a member value pair to this annotation
   *
   * @param sName
   *        The simple name for this annotation
   * @param nValue
   *        The byte member value for this annotation
   * @return The {@link JAnnotationUse}. More member value pairs can be added to
   *         it using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (@Nonnull final String sName, final byte nValue)
  {
    return _addValue (sName, wrap (nValue));
  }

  @Nonnull
  public JAnnotationUse paramArray (@Nonnull final String sName, @Nonnull final byte... aValues)
  {
    paramArray (sName).params (aValues);
    return this;
  }

  /**
   * Adds a member value pair to this annotation
   *
   * @param sName
   *        The simple name for this annotation
   * @param cValue
   *        The char member value for this annotation
   * @return The {@link JAnnotationUse}. More member value pairs can be added to
   *         it using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (@Nonnull final String sName, final char cValue)
  {
    return _addValue (sName, wrap (cValue));
  }

  @Nonnull
  public JAnnotationUse paramArray (@Nonnull final String sName, @Nonnull final char... aValues)
  {
    paramArray (sName).params (aValues);
    return this;
  }

  /**
   * Adds a member value pair to this annotation
   *
   * @param sName
   *        The simple name for this annotation
   * @param dValue
   *        The double member value for this annotation
   * @return The {@link JAnnotationUse}. More member value pairs can be added to
   *         it using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (@Nonnull final String sName, final double dValue)
  {
    return _addValue (sName, wrap (dValue));
  }

  @Nonnull
  public JAnnotationUse paramArray (@Nonnull final String sName, @Nonnull final double... aValues)
  {
    paramArray (sName).params (aValues);
    return this;
  }

  /**
   * Adds a member value pair to this annotation
   *
   * @param sName
   *        The simple name for this annotation
   * @param fValue
   *        The float member value for this annotation
   * @return The {@link JAnnotationUse}. More member value pairs can be added to
   *         it using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (@Nonnull final String sName, final float fValue)
  {
    return _addValue (sName, wrap (fValue));
  }

  @Nonnull
  public JAnnotationUse paramArray (@Nonnull final String sName, @Nonnull final float... aValues)
  {
    paramArray (sName).params (aValues);
    return this;
  }

  /**
   * Adds a member value pair to this annotation
   *
   * @param sName
   *        The simple name for this annotation
   * @param nValue
   *        The long member value for this annotation
   * @return The {@link JAnnotationUse}. More member value pairs can be added to
   *         it using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (@Nonnull final String sName, final long nValue)
  {
    return _addValue (sName, wrap (nValue));
  }

  @Nonnull
  public JAnnotationUse paramArray (@Nonnull final String sName, @Nonnull final long... aValues)
  {
    paramArray (sName).params (aValues);
    return this;
  }

  /**
   * Adds a member value pair to this annotation
   *
   * @param sName
   *        The simple name for this annotation
   * @param nValue
   *        The short member value for this annotation
   * @return The {@link JAnnotationUse}. More member value pairs can be added to
   *         it using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (@Nonnull final String sName, final short nValue)
  {
    return _addValue (sName, wrap (nValue));
  }

  @Nonnull
  public JAnnotationUse paramArray (@Nonnull final String sName, @Nonnull final short... aValues)
  {
    paramArray (sName).params (aValues);
    return this;
  }

  /**
   * Adds a member value pair to this annotation
   *
   * @param sName
   *        The simple name for this annotation
   * @param nValue
   *        The int member value for this annotation
   * @return The {@link JAnnotationUse}. More member value pairs can be added to
   *         it using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (@Nonnull final String sName, final int nValue)
  {
    return _addValue (sName, wrap (nValue));
  }

  @Nonnull
  public JAnnotationUse paramArray (@Nonnull final String sName, @Nonnull final int... aValues)
  {
    paramArray (sName).params (aValues);
    return this;
  }

  /**
   * Adds a member value pair to this annotation
   *
   * @param sName
   *        The simple name for this annotation
   * @param sValue
   *        The String member value for this annotation
   * @return The {@link JAnnotationUse}. More member value pairs can be added to
   *         it using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (@Nonnull final String sName, final String sValue)
  {
    return _addValue (sName, wrap (sValue));
  }

  @Nonnull
  public JAnnotationUse paramArray (@Nonnull final String sName, @Nonnull final String... aValues)
  {
    paramArray (sName).params (aValues);
    return this;
  }

  /**
   * Adds a member value pair to this annotation
   *
   * @param sName
   *        The simple name for this annotation
   * @param aValue
   *        The enum class which is member value for this annotation
   * @return The {@link JAnnotationUse}. More member value pairs can be added to
   *         it using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (@Nonnull final String sName, @Nonnull final Enum <?> aValue)
  {
    return _addValue (sName, wrap (aValue));
  }

  @Nonnull
  public JAnnotationUse paramArray (@Nonnull final String sName, @Nonnull final Enum <?>... aValues)
  {
    paramArray (sName).params (aValues);
    return this;
  }

  /**
   * Adds a member value pair to this annotation
   *
   * @param sName
   *        The simple name for this annotation
   * @param aValue
   *        The {@link JEnumConstant} which is member value for this annotation
   * @return The {@link JAnnotationUse}. More member value pairs can be added to
   *         it using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (@Nonnull final String sName, @Nonnull final JEnumConstant aValue)
  {
    return _addValue (sName, wrap (aValue));
  }

  @Nonnull
  public JAnnotationUse paramArray (@Nonnull final String sName, @Nonnull final JEnumConstant... aValues)
  {
    paramArray (sName).params (aValues);
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
   * @param sName
   *        The simple name for this annotation param
   * @param aValue
   *        The class type of the param
   * @return The {@link JAnnotationUse}. More member value pairs can be added to
   *         it using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (@Nonnull final String sName, @Nonnull final Class <?> aValue)
  {
    return _addValue (sName, wrap (aValue));
  }

  @Nonnull
  public JAnnotationUse paramArray (@Nonnull final String sName, @Nonnull final Class <?>... aValues)
  {
    paramArray (sName).params (aValues);
    return this;
  }

  /**
   * Adds a member value pair to this annotation based on the type represented
   * by the given {@link AbstractJType}
   *
   * @param sName
   *        The simple name for this annotation param
   * @param aValue
   *        the {@link AbstractJType} representing the actual type
   * @return The {@link JAnnotationUse}. More member value pairs can be added to
   *         it using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (@Nonnull final String sName, @Nonnull final AbstractJType aValue)
  {
    return _addValue (sName, wrap (aValue));
  }

  @Nonnull
  public JAnnotationUse paramArray (@Nonnull final String sName, @Nonnull final AbstractJType... aValues)
  {
    paramArray (sName).params (aValues);
    return this;
  }

  /**
   * Adds a member value pair to this annotation.
   *
   * @param sName
   *        The simple name for this annotation
   * @param aValue
   *        The {@link IJExpression} which provides the content value for this
   *        annotation
   * @return The {@link JAnnotationUse}. More member value pairs can be added to
   *         it using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (@Nonnull final String sName, @Nonnull final IJExpression aValue)
  {
    return _addValue (sName, wrap (aValue));
  }

  @Nonnull
  public JAnnotationUse paramArray (@Nonnull final String sName, @Nonnull final IJExpression... aValues)
  {
    paramArray (sName).params (aValues);
    return this;
  }

  /**
   * Adds a member value pair which is of type array to this annotation
   *
   * @param sName
   *        The simple name for this annotation
   * @return The {@link JAnnotationArrayMember}. For adding array aValues
   * @see JAnnotationArrayMember
   */
  @Nonnull
  public JAnnotationArrayMember paramArray (@Nonnull final String sName)
  {
    final JAnnotationArrayMember aArrayMember = new JAnnotationArrayMember (owner ());
    _addValue (sName, aArrayMember);
    return aArrayMember;
  }

  /**
   * Adds a member value pair to this annotation For adding class values as
   * param
   *
   * @see #param(String, Class)
   * @param sName
   *        The simple name for this annotation
   * @param aValue
   *        The annotation class which is member value for this annotation
   * @return The {@link JAnnotationUse}. More member value pairs can be added to
   *         it using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse annotationParam (@Nonnull final String sName, @Nonnull final Class <? extends Annotation> aValue)
  {
    return annotationParam (sName, owner ().ref (aValue));
  }

  /**
   * Adds a member value pair to this annotation For adding class values as
   * param
   *
   * @see #param(String, Class)
   * @param sName
   *        The simple name for this annotation
   * @param aValue
   *        The annotation class which is member value for this annotation
   * @return The {@link JAnnotationUse}. More member value pairs can be added to
   *         it using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse annotationParam (@Nonnull final String sName, @Nonnull final AbstractJClass aValue)
  {
    final JAnnotationUse annotationUse = new JAnnotationUse (aValue);
    _addValue (sName, annotationUse);
    return annotationUse;
  }

  @Nonnegative
  public int size ()
  {
    return m_aMemberValues.size ();
  }

  private boolean _isOptimizable ()
  {
    return m_aMemberValues.size () == 1 && m_aMemberValues.containsKey (SPECIAL_KEY_VALUE);
  }

  public void generate (final JFormatter f)
  {
    f.print ('@').generable (m_aAnnotationClass);
    if (m_aMemberValues != null && !m_aMemberValues.isEmpty ())
    {
      f.print ('(');
      if (_isOptimizable ())
      {
        // short form
        f.generable (m_aMemberValues.get (SPECIAL_KEY_VALUE));
      }
      else
      {
        boolean bFirst = true;
        for (final Map.Entry <String, AbstractJAnnotationValue> mapEntry : m_aMemberValues.entrySet ())
        {
          if (!bFirst)
            f.print (',');
          f.print (mapEntry.getKey ()).print ('=').generable (mapEntry.getValue ());
          bFirst = false;
        }
      }
      f.print (')');
    }
  }
}
