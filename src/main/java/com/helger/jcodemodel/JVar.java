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

import static com.helger.jcodemodel.util.JCEqualsHelper.isEqual;
import static com.helger.jcodemodel.util.JCHashCodeGenerator.getHashCode;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.jcodemodel.util.JCValueEnforcer;

/**
 * Variables and fields.
 */
public class JVar implements IJAssignmentTarget, IJDeclaration, IJAnnotatable
{
  /**
   * Modifiers.
   */
  private final JMods m_aMods;

  /**
   * Type of the variable
   */
  private AbstractJType m_aType;

  /**
   * Name of the variable
   */
  private String m_sName;

  /**
   * Initialization of the variable in its declaration
   */
  private IJExpression m_aInitExpr;

  /**
   * Annotations on this variable. Lazily created.
   */
  private List <JAnnotationUse> m_aAnnotations;

  /**
   * JVar constructor
   *
   * @param aMods
   *        Modifiers to use
   * @param aType
   *        Data type of this variable
   * @param sName
   *        Name of this variable
   * @param aInitExpr
   *        Value to initialize this variable to
   */
  public JVar (@Nonnull final JMods aMods,
               @Nonnull final AbstractJType aType,
               @Nonnull final String sName,
               @Nullable final IJExpression aInitExpr)
  {
    JCValueEnforcer.isTrue (JJavaName.isJavaIdentifier (sName), () -> "Illegal variable name '" + sName + "'");
    m_aMods = aMods;
    m_aType = aType;
    m_sName = sName;
    m_aInitExpr = aInitExpr;
  }

  /**
   * Initialize this variable
   *
   * @param aInitExpr
   *        Expression to be used to initialize this field
   * @return this for chaining
   */
  @Nonnull
  public JVar init (@Nullable final IJExpression aInitExpr)
  {
    m_aInitExpr = aInitExpr;
    return this;
  }

  /**
   * @return The init expression. May be <code>null</code>.
   */
  @Nullable
  public IJExpression init ()
  {
    return m_aInitExpr;
  }

  /**
   * Get the name of this variable
   *
   * @return Name of the variable
   */
  @Nonnull
  public String name ()
  {
    return m_sName;
  }

  /**
   * Changes the name of this variable.
   *
   * @param sName
   *        New name of the variable
   */
  public void name (@Nonnull final String sName)
  {
    JCValueEnforcer.isTrue (JJavaName.isJavaIdentifier (sName), () -> "Illegal variable name '" + sName + "'");
    m_sName = sName;
  }

  /**
   * Return the type of this variable.
   *
   * @return always non-null.
   */
  @Nonnull
  public AbstractJType type ()
  {
    return m_aType;
  }

  /**
   * @return the current modifiers of this method. Always return non-null valid
   *         object.
   */
  @Nonnull
  public JMods mods ()
  {
    return m_aMods;
  }

  /**
   * Sets the type of this variable.
   *
   * @param aNewType
   *        must not be null.
   * @return the old type value. always non-null.
   */
  @Nonnull
  public AbstractJType type (@Nonnull final AbstractJType aNewType)
  {
    JCValueEnforcer.notNull (aNewType, "NewType");
    final AbstractJType aOldType = m_aType;
    m_aType = aNewType;
    return aOldType;
  }

  /**
   * Adds an annotation to this variable.
   *
   * @param aClazz
   *        The annotation class to annotate the field with
   * @return New {@link JAnnotationUse}
   */
  @Nonnull
  public JAnnotationUse annotate (@Nonnull final AbstractJClass aClazz)
  {
    if (m_aAnnotations == null)
      m_aAnnotations = new ArrayList <> ();
    final JAnnotationUse a = new JAnnotationUse (aClazz);
    m_aAnnotations.add (a);
    return a;
  }

  /**
   * Adds an annotation to this variable.
   *
   * @param aClazz
   *        The annotation class to annotate the field with
   * @return New {@link JAnnotationUse}
   */
  @Nonnull
  public JAnnotationUse annotate (@Nonnull final Class <? extends Annotation> aClazz)
  {
    return annotate (m_aType.owner ().ref (aClazz));
  }

  @Nonnull
  public Collection <JAnnotationUse> annotations ()
  {
    if (m_aAnnotations == null)
      m_aAnnotations = new ArrayList <> ();
    return Collections.unmodifiableList (m_aAnnotations);
  }

  protected boolean isAnnotated ()
  {
    return m_aAnnotations != null;
  }

  public void bind (@Nonnull final JFormatter f)
  {
    if (m_aAnnotations != null)
      for (final JAnnotationUse annotation : m_aAnnotations)
        f.generable (annotation).newline ();
    f.generable (m_aMods).generable (m_aType).id (m_sName);
    if (m_aInitExpr != null)
      f.print ('=').generable (m_aInitExpr);
  }

  public void declare (@Nonnull final JFormatter f)
  {
    f.var (this).print (';').newline ();
  }

  public void generate (@Nonnull final JFormatter f)
  {
    f.id (m_sName);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || getClass () != o.getClass ())
      return false;
    final JVar rhs = (JVar) o;
    return isEqual (m_sName, rhs.m_sName);
  }

  @Override
  public int hashCode ()
  {
    return getHashCode (this, m_sName);
  }
}
