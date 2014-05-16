/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2011 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
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
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.helger.jcodemodel.util.NameUtilities;

/**
 * Represents an annotation on a program element. TODO How to add enums to the
 * annotations
 * 
 * @author Bhakti Mehta (bhakti.mehta@sun.com)
 */
public class JAnnotationUse extends AbstractJAnnotationValue implements IJOwned
{
  /**
   * The {@link Annotation} class
   */
  private final AbstractJClass _clazz;

  /**
   * Map of member values.
   */
  private Map <String, AbstractJAnnotationValue> _memberValues;

  protected JAnnotationUse (@Nonnull final AbstractJClass clazz)
  {
    this._clazz = clazz;
  }

  @Nonnull
  public AbstractJClass getAnnotationClass ()
  {
    return _clazz;
  }

  @Nonnull
  public Map <String, AbstractJAnnotationValue> getAnnotationMembers ()
  {
    return Collections.unmodifiableMap (_memberValues);
  }

  @Nonnull
  public JCodeModel owner ()
  {
    return _clazz.owner ();
  }

  private void _addValue (final String name, final AbstractJAnnotationValue annotationValue)
  {
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
   * @return The JAnnotationUse. More member value pairs can be added to it
   *         using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (final String name, final boolean value)
  {
    _addValue (name, new JAnnotationStringValue (JExpr.lit (value)));
    return this;
  }

  /**
   * Adds a member value pair to this annotation
   * 
   * @param name
   *        The simple name for this annotation
   * @param value
   *        The byte member value for this annotation
   * @return The JAnnotationUse. More member value pairs can be added to it
   *         using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (final String name, final byte value)
  {
    _addValue (name, new JAnnotationStringValue (JExpr.lit (value)));
    return this;
  }

  /**
   * Adds a member value pair to this annotation
   * 
   * @param name
   *        The simple name for this annotation
   * @param value
   *        The char member value for this annotation
   * @return The JAnnotationUse. More member value pairs can be added to it
   *         using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (final String name, final char value)
  {
    _addValue (name, new JAnnotationStringValue (JExpr.lit (value)));
    return this;
  }

  /**
   * Adds a member value pair to this annotation
   * 
   * @param name
   *        The simple name for this annotation
   * @param value
   *        The double member value for this annotation
   * @return The JAnnotationUse. More member value pairs can be added to it
   *         using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (final String name, final double value)
  {
    _addValue (name, new JAnnotationStringValue (JExpr.lit (value)));
    return this;
  }

  /**
   * Adds a member value pair to this annotation
   * 
   * @param name
   *        The simple name for this annotation
   * @param value
   *        The float member value for this annotation
   * @return The JAnnotationUse. More member value pairs can be added to it
   *         using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (final String name, final float value)
  {
    _addValue (name, new JAnnotationStringValue (JExpr.lit (value)));
    return this;
  }

  /**
   * Adds a member value pair to this annotation
   * 
   * @param name
   *        The simple name for this annotation
   * @param value
   *        The long member value for this annotation
   * @return The JAnnotationUse. More member value pairs can be added to it
   *         using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (final String name, final long value)
  {
    _addValue (name, new JAnnotationStringValue (JExpr.lit (value)));
    return this;
  }

  /**
   * Adds a member value pair to this annotation
   * 
   * @param name
   *        The simple name for this annotation
   * @param value
   *        The short member value for this annotation
   * @return The JAnnotationUse. More member value pairs can be added to it
   *         using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (final String name, final short value)
  {
    _addValue (name, new JAnnotationStringValue (JExpr.lit (value)));
    return this;
  }

  /**
   * Adds a member value pair to this annotation
   * 
   * @param name
   *        The simple name for this annotation
   * @param value
   *        The int member value for this annotation
   * @return The JAnnotationUse. More member value pairs can be added to it
   *         using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (final String name, final int value)
  {
    _addValue (name, new JAnnotationStringValue (JExpr.lit (value)));
    return this;
  }

  /**
   * Adds a member value pair to this annotation
   * 
   * @param name
   *        The simple name for this annotation
   * @param value
   *        The String member value for this annotation
   * @return The JAnnotationUse. More member value pairs can be added to it
   *         using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (final String name, final String value)
  {
    // Escape string values with quotes so that they can
    // be generated accordingly
    _addValue (name, new JAnnotationStringValue (JExpr.lit (value)));
    return this;
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
   * @return The JAnnotationUse. More member value pairs can be added to it
   *         using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse annotationParam (final String name, final Class <? extends Annotation> value)
  {
    final JAnnotationUse annotationUse = new JAnnotationUse (owner ().ref (value));
    _addValue (name, annotationUse);
    return annotationUse;
  }

  /**
   * Adds a member value pair to this annotation
   * 
   * @param name
   *        The simple name for this annotation
   * @param value
   *        The enum class which is member value for this annotation
   * @return The JAnnotationUse. More member value pairs can be added to it
   *         using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (final String name, final Enum <?> value)
  {
    _addValue (name, new AbstractJAnnotationValue ()
    {
      public void generate (final JFormatter f)
      {
        f.type (owner ().ref (value.getDeclaringClass ())).print ('.').print (value.name ());
      }
    });
    return this;
  }

  /**
   * Adds a member value pair to this annotation
   * 
   * @param name
   *        The simple name for this annotation
   * @param value
   *        The JEnumConstant which is member value for this annotation
   * @return The JAnnotationUse. More member value pairs can be added to it
   *         using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (final String name, final JEnumConstant value)
  {
    _addValue (name, new JAnnotationStringValue (value));
    return this;
  }

  /**
   * Adds a member value pair to this annotation This can be used for e.g to
   * specify
   * 
   * <pre>
   * &#64;XmlCollectionItem(type=Integer.class);
   *    *
   * </pre>
   * 
   * For adding a value of Class<? extends Annotation>
   * {@link #annotationParam(String, Class)}
   * 
   * @param name
   *        The simple name for this annotation param
   * @param value
   *        The class type of the param
   * @return The JAnnotationUse. More member value pairs can be added to it
   *         using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (final String name, final Class <?> value)
  {
    _addValue (name, new JAnnotationStringValue (new AbstractJExpressionImpl ()
    {
      public void generate (@Nonnull final JFormatter f)
      {
        f.print (NameUtilities.getFullName (value));
        f.print (".class");
      }
    }));
    return this;
  }

  /**
   * Adds a member value pair to this annotation based on the type represented
   * by the given JType
   * 
   * @param name
   *        The simple name for this annotation param
   * @param type
   *        the JType representing the actual type
   * @return The JAnnotationUse. More member value pairs can be added to it
   *         using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (final String name, @Nonnull final AbstractJType type)
  {
    final AbstractJClass c = type.boxify ();
    _addValue (name, new JAnnotationStringValue (c.dotclass ()));
    return this;
  }

  /**
   * Adds a member value pair to this annotation.
   * 
   * @param name
   *        The simple name for this annotation
   * @param value
   *        The JExpression which provides the contant value for this annotation
   * @return The JAnnotationUse. More member value pairs can be added to it
   *         using the same or the overloaded methods.
   */
  @Nonnull
  public JAnnotationUse param (final String name, final IJExpression value)
  {
    _addValue (name, new JAnnotationStringValue (value));
    return this;
  }

  /**
   * Adds a member value pair which is of type array to this annotation
   * 
   * @param name
   *        The simple name for this annotation
   * @return The JAnnotationArrayMember. For adding array values
   * @see JAnnotationArrayMember
   */
  @Nonnull
  public JAnnotationArrayMember paramArray (final String name)
  {
    final JAnnotationArrayMember arrayMember = new JAnnotationArrayMember (owner ());
    _addValue (name, arrayMember);
    return arrayMember;
  }

  public void generate (final JFormatter f)
  {
    f.print ('@').generable (_clazz);
    if (_memberValues != null)
    {
      f.print ('(');
      boolean first = true;

      if (_isOptimizable ())
      {
        // short form
        f.generable (_memberValues.get ("value"));
      }
      else
      {
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

  private boolean _isOptimizable ()
  {
    return _memberValues.size () == 1 && _memberValues.containsKey ("value");
  }
}
