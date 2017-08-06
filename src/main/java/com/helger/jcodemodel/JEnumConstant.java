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

import com.helger.jcodemodel.util.JCValueEnforcer;

/**
 * Enum Constant. When used as an {@link IJExpression}, this object represents a
 * reference to the enum constant.
 *
 * @author Bhakti Mehta (Bhakti.Mehta@sun.com)
 */
public class JEnumConstant implements IJExpression, IJDeclaration, IJAnnotatable, IJDocCommentable
{
  /**
   * The enum class.
   */
  private final AbstractJClass m_aType;

  /**
   * The constant.
   */
  private final String m_sName;

  /**
   * javadoc comments, if any.
   */
  private JDocComment m_aJavaDoc;

  /**
   * Annotations on this variable. Lazily created.
   */
  private List <JAnnotationUse> m_aAnnotations;

  /**
   * List of the constructor argument expressions. Lazily constructed.
   */
  private List <IJExpression> m_aArgs;

  protected JEnumConstant (@Nonnull final AbstractJClass aType, @Nonnull final String sName)
  {
    m_aType = JCValueEnforcer.notNull (aType, "Type");
    m_sName = JCValueEnforcer.notNull (sName, "Name");
  }

  @Nonnull
  public AbstractJClass type ()
  {
    return m_aType;
  }

  /**
   * @return The plain name of the enum constant, without any type prefix
   */
  @Nonnull
  public String name ()
  {
    return m_sName;
  }

  /**
   * Add an expression to this constructor's argument list
   *
   * @param aArg
   *        Argument to add to argument list
   * @return this for chaining
   */
  @Nonnull
  public JEnumConstant arg (@Nonnull final IJExpression aArg)
  {
    JCValueEnforcer.notNull (aArg, "Arg");
    if (m_aArgs == null)
      m_aArgs = new ArrayList <> ();
    m_aArgs.add (aArg);
    return this;
  }

  @Nonnull
  public List <IJExpression> args ()
  {
    if (m_aArgs == null)
      m_aArgs = new ArrayList <> ();
    return Collections.unmodifiableList (m_aArgs);
  }

  public boolean hasArgs ()
  {
    return m_aArgs != null && !m_aArgs.isEmpty ();
  }

  /**
   * Returns the name of this constant including the type name
   *
   * @return never null.
   */
  @Nonnull
  public String getName ()
  {
    return m_aType.fullName () + '.' + m_sName;
  }

  @Nonnull
  public JDocComment javadoc ()
  {
    if (m_aJavaDoc == null)
      m_aJavaDoc = new JDocComment (m_aType.owner ());
    return m_aJavaDoc;
  }

  /**
   * Adds an annotation to this variable.
   *
   * @param aClazz
   *        The annotation class to annotate the field with
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
   */
  @Nonnull
  public JAnnotationUse annotate (@Nonnull final Class <? extends Annotation> aClazz)
  {
    return annotate (m_aType.owner ().ref (aClazz));
  }

  /**
   * {@link IJAnnotatable#annotations()}
   */
  @Nonnull
  public Collection <JAnnotationUse> annotations ()
  {
    if (m_aAnnotations == null)
      m_aAnnotations = new ArrayList <> ();
    return Collections.unmodifiableList (m_aAnnotations);
  }

  public void declare (@Nonnull final JFormatter f)
  {
    if (m_aJavaDoc != null)
      f.newline ().generable (m_aJavaDoc);
    if (m_aAnnotations != null)
      for (final JAnnotationUse annotation : m_aAnnotations)
        f.generable (annotation).newline ();
    f.id (m_sName);
    if (m_aArgs != null)
      f.print ('(').generable (m_aArgs).print (')');
  }

  public void generate (@Nonnull final JFormatter f)
  {
    f.type (m_aType).print ('.').print (m_sName);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || getClass () != o.getClass ())
      return false;
    final JEnumConstant rhs = (JEnumConstant) o;
    return isEqual (m_aType.fullName (), rhs.m_aType.fullName ()) && isEqual (m_sName, rhs.m_sName);
  }

  @Override
  public int hashCode ()
  {
    return getHashCode (this, m_aType.fullName (), m_sName);
  }
}
