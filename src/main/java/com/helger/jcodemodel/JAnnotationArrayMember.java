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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.jcodemodel.util.JCValueEnforcer;

/**
 * Represents an arrays as annotation members
 * <p>
 * This class implements {@link IJAnnotatable} to allow new annotations to be
 * added as a member of the array.
 *
 * @author Bhakti Mehta (bhakti.mehta@sun.com)
 */
public class JAnnotationArrayMember extends AbstractJAnnotationValueOwned implements IJAnnotatable
{
  private final JCodeModel m_aOwner;
  private final List <AbstractJAnnotationValue> m_aValues = new ArrayList <> ();

  public JAnnotationArrayMember (@Nonnull final JCodeModel aOwner)
  {
    JCValueEnforcer.notNull (aOwner, "Owner");
    m_aOwner = aOwner;
  }

  @Nonnull
  public JCodeModel owner ()
  {
    return m_aOwner;
  }

  /**
   * Adds an array member to this annotation
   *
   * @param sValue
   *        Adds a string value to the array member
   * @return The {@link JAnnotationArrayMember}. More elements can be added by
   *         calling the same method multiple times
   */
  @Nonnull
  public JAnnotationArrayMember param (@Nonnull final String sValue)
  {
    m_aValues.add (wrap (sValue));
    return this;
  }

  @Nonnull
  public JAnnotationArrayMember params (@Nonnull final String... aValues)
  {
    for (final String sValue : aValues)
      m_aValues.add (wrap (sValue));
    return this;
  }

  /**
   * Adds an array member to this annotation
   *
   * @param bValue
   *        Adds a boolean value to the array member
   * @return The {@link JAnnotationArrayMember}. More elements can be added by
   *         calling the same method multiple times
   */
  @Nonnull
  public JAnnotationArrayMember param (final boolean bValue)
  {
    m_aValues.add (wrap (bValue));
    return this;
  }

  @Nonnull
  public JAnnotationArrayMember params (@Nonnull final boolean... aValues)
  {
    for (final boolean value : aValues)
      m_aValues.add (wrap (value));
    return this;
  }

  /**
   * Adds an array member to this annotation
   *
   * @param nValue
   *        Adds a byte value to the array member
   * @return The {@link JAnnotationArrayMember}. More elements can be added by
   *         calling the same method multiple times
   */
  @Nonnull
  public JAnnotationArrayMember param (final byte nValue)
  {
    m_aValues.add (wrap (nValue));
    return this;
  }

  @Nonnull
  public JAnnotationArrayMember params (@Nonnull final byte... aValues)
  {
    for (final byte value : aValues)
      m_aValues.add (wrap (value));
    return this;
  }

  /**
   * Adds an array member to this annotation
   *
   * @param cValue
   *        Adds a char value to the array member
   * @return The {@link JAnnotationArrayMember}. More elements can be added by
   *         calling the same method multiple times
   */
  @Nonnull
  public JAnnotationArrayMember param (final char cValue)
  {
    m_aValues.add (wrap (cValue));
    return this;
  }

  @Nonnull
  public JAnnotationArrayMember params (@Nonnull final char... aValues)
  {
    for (final char value : aValues)
      m_aValues.add (wrap (value));
    return this;
  }

  /**
   * Adds an array member to this annotation
   *
   * @param dValue
   *        Adds a double value to the array member
   * @return The {@link JAnnotationArrayMember}. More elements can be added by
   *         calling the same method multiple times
   */
  @Nonnull
  public JAnnotationArrayMember param (final double dValue)
  {
    m_aValues.add (wrap (dValue));
    return this;
  }

  @Nonnull
  public JAnnotationArrayMember params (@Nonnull final double... aValues)
  {
    for (final double value : aValues)
      m_aValues.add (wrap (value));
    return this;
  }

  /**
   * Adds an array member to this annotation
   *
   * @param nValue
   *        Adds a long value to the array member
   * @return The {@link JAnnotationArrayMember}. More elements can be added by
   *         calling the same method multiple times
   */
  @Nonnull
  public JAnnotationArrayMember param (final long nValue)
  {
    m_aValues.add (wrap (nValue));
    return this;
  }

  @Nonnull
  public JAnnotationArrayMember params (@Nonnull final long... aValues)
  {
    for (final long value : aValues)
      m_aValues.add (wrap (value));
    return this;
  }

  /**
   * Adds an array member to this annotation
   *
   * @param nValue
   *        Adds a short value to the array member
   * @return The {@link JAnnotationArrayMember}. More elements can be added by
   *         calling the same method multiple times
   */
  @Nonnull
  public JAnnotationArrayMember param (final short nValue)
  {
    m_aValues.add (wrap (nValue));
    return this;
  }

  @Nonnull
  public JAnnotationArrayMember params (@Nonnull final short... aValues)
  {
    for (final short value : aValues)
      m_aValues.add (wrap (value));
    return this;
  }

  /**
   * Adds an array member to this annotation
   *
   * @param nValue
   *        Adds an int value to the array member
   * @return The {@link JAnnotationArrayMember}. More elements can be added by
   *         calling the same method multiple times
   */
  @Nonnull
  public JAnnotationArrayMember param (final int nValue)
  {
    m_aValues.add (wrap (nValue));
    return this;
  }

  @Nonnull
  public JAnnotationArrayMember params (@Nonnull final int... aValues)
  {
    for (final int value : aValues)
      m_aValues.add (wrap (value));
    return this;
  }

  /**
   * Adds an array member to this annotation
   *
   * @param fValue
   *        Adds a float value to the array member
   * @return The {@link JAnnotationArrayMember}. More elements can be added by
   *         calling the same method multiple times
   */
  @Nonnull
  public JAnnotationArrayMember param (final float fValue)
  {
    m_aValues.add (wrap (fValue));
    return this;
  }

  @Nonnull
  public JAnnotationArrayMember params (@Nonnull final float... aValues)
  {
    for (final float value : aValues)
      m_aValues.add (wrap (value));
    return this;
  }

  /**
   * Adds a enum array member to this annotation
   *
   * @param aEnumConstant
   *        Adds a enum value to the array member
   * @return The {@link JAnnotationArrayMember}. More elements can be added by
   *         calling the same method multiple times
   */
  @Nonnull
  public JAnnotationArrayMember param (@Nonnull final Enum <?> aEnumConstant)
  {
    m_aValues.add (wrap (aEnumConstant));
    return this;
  }

  @Nonnull
  public JAnnotationArrayMember params (@Nonnull final Enum <?>... aValues)
  {
    for (final Enum <?> value : aValues)
      m_aValues.add (wrap (value));
    return this;
  }

  /**
   * Adds a enum array member to this annotation
   *
   * @param aValue
   *        Adds a enum value to the array member
   * @return The {@link JAnnotationArrayMember}. More elements can be added by
   *         calling the same method multiple times
   */
  @Nonnull
  public JAnnotationArrayMember param (final JEnumConstant aValue)
  {
    m_aValues.add (wrap (aValue));
    return this;
  }

  @Nonnull
  public JAnnotationArrayMember params (@Nonnull final JEnumConstant... aValues)
  {
    for (final JEnumConstant value : aValues)
      m_aValues.add (wrap (value));
    return this;
  }

  /**
   * Adds an expression array member to this annotation
   *
   * @param aValue
   *        Adds an expression value to the array member
   * @return The JAnnotationArrayMember. More elements can be added by calling
   *         the same method multiple times
   */
  @Nonnull
  public JAnnotationArrayMember param (final IJExpression aValue)
  {
    m_aValues.add (wrap (aValue));
    return this;
  }

  @Nonnull
  public JAnnotationArrayMember params (@Nonnull final IJExpression... aValues)
  {
    for (final IJExpression value : aValues)
      m_aValues.add (wrap (value));
    return this;
  }

  /**
   * Adds a class array member to this annotation
   *
   * @param aValue
   *        Adds a class value to the array member
   * @return The {@link JAnnotationArrayMember}. More elements can be added by
   *         calling the same method multiple times
   */
  @Nonnull
  public JAnnotationArrayMember param (final Class <?> aValue)
  {
    m_aValues.add (wrap (aValue));
    return this;
  }

  @Nonnull
  public JAnnotationArrayMember params (@Nonnull final Class <?>... aValues)
  {
    for (final Class <?> value : aValues)
      m_aValues.add (wrap (value));
    return this;
  }

  @Nonnull
  public JAnnotationArrayMember param (final AbstractJType aType)
  {
    m_aValues.add (wrap (aType));
    return this;
  }

  @Nonnull
  public JAnnotationArrayMember params (@Nonnull final AbstractJType... aValues)
  {
    for (final AbstractJType value : aValues)
      m_aValues.add (wrap (value));
    return this;
  }

  /**
   * Adds a new annotation to the array.
   */
  @Nonnull
  public JAnnotationUse annotate (@Nonnull final Class <? extends Annotation> aClazz)
  {
    return annotate (owner ().ref (aClazz));
  }

  /**
   * Adds a new annotation to the array.
   */
  @Nonnull
  public JAnnotationUse annotate (@Nonnull final AbstractJClass aClazz)
  {
    final JAnnotationUse a = new JAnnotationUse (aClazz);
    m_aValues.add (a);
    return a;
  }

  /**
   * {@link IJAnnotatable#annotations()}
   *
   * @see #getAllAnnotations()
   */
  @SuppressWarnings ({ "unchecked", "rawtypes" })
  @Nonnull
  public Collection <JAnnotationUse> annotations ()
  {
    // FIXME this invocation is invalid if the caller isn't adding annotations
    // into an array so this potentially type-unsafe conversion would be
    // justified.
    return Collections.<JAnnotationUse> unmodifiableList ((List) m_aValues);
  }

  @Nonnull
  public Collection <AbstractJAnnotationValue> getAllAnnotations ()
  {
    return Collections.unmodifiableList (m_aValues);
  }

  @Nonnegative
  public int size ()
  {
    return m_aValues.size ();
  }

  public void generate (@Nonnull final JFormatter f)
  {
    f.print ('{').newline ().indent ();
    boolean first = true;
    for (final AbstractJAnnotationValue aValue : m_aValues)
    {
      if (!first)
        f.print (',').newline ();
      f.generable (aValue);
      first = false;
    }
    f.newline ().outdent ().print ('}');
  }
}
