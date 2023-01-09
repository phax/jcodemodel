/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2020 Philip Helger + contributors
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

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A generated Java class/interface/enum/annotation<br>
 * This class models a declaration, and since a declaration can be always used
 * as a reference, it inherits {@link AbstractJClass}.
 *
 * @author Philip Helger
 * @param <CLASSTYPE>
 *        Implementation type
 */
public abstract class AbstractJClassContainer <CLASSTYPE extends AbstractJClassContainer <CLASSTYPE>> extends
                                              AbstractJClass implements
                                              IJClassContainer <CLASSTYPE>
{
  /**
   * If this is a package-member class, this is {@link JPackage}. If this is a
   * nested class, this is {@link AbstractJClassContainer}. If this is an
   * anonymous class, this constructor shouldn't be used.
   */
  private final IJClassContainer <?> m_aOuter;

  /**
   * Default value is class or interface or annotationTypeDeclaration or enum
   */
  private final EClassType m_eClassType;

  /**
   * Name of this class. <code>null</code> if anonymous.
   */
  private final String m_sName;

  /**
   * Nested classes as a map from name to a defined class. The name is all
   * capitalized in a case sensitive file system to avoid conflicts. Lazily
   * created to save footprint.
   */
  protected Map <String, CLASSTYPE> m_aClasses;

  /**
   * JClass constructor
   *
   * @param aOwner
   *        Owning code model
   * @param aOuter
   *        Optional outer class container
   * @param eClassType
   *        Class type to use
   * @param sName
   *        Name of this class
   */
  protected AbstractJClassContainer (@Nonnull final JCodeModel aOwner,
                                     @Nullable final IJClassContainer <?> aOuter,
                                     @Nonnull final EClassType eClassType,
                                     @Nullable final String sName)
  {
    super (aOwner);
    m_aOuter = aOuter;
    m_eClassType = eClassType;
    m_sName = sName;
  }

  @Nullable
  public final IJClassContainer <?> getOuter ()
  {
    return m_aOuter;
  }

  @Override
  @Nullable
  public final AbstractJClass outer ()
  {
    if (m_aOuter != null && m_aOuter.isClass ())
      return (AbstractJClass) m_aOuter;
    return null;
  }

  @Nonnull
  public final EClassType getClassType ()
  {
    return m_eClassType;
  }

  @Override
  public final boolean isInterface ()
  {
    return m_eClassType == EClassType.INTERFACE;
  }

  /**
   * This method indicates if the interface is an annotationTypeDeclaration
   *
   * @return <code>true</code> if this an annotation type declaration
   */
  public final boolean isAnnotationTypeDeclaration ()
  {
    return m_eClassType == EClassType.ANNOTATION_TYPE_DECL;
  }

  /**
   * Class name accessor. <br>
   * For example, for <code>java.util.List</code>, this method returns
   * <code>"List"</code>"
   *
   * @return Name of this class
   */
  @Override
  @Nullable
  public String name ()
  {
    return m_sName;
  }

  /**
   * Gets the fully qualified name of this class.
   */
  @Override
  @Nullable
  public String fullName ()
  {
    if (getOuter () instanceof AbstractJClassContainer <?>)
      return ((AbstractJClassContainer <?>) getOuter ()).fullName () + '.' + name ();

    final JPackage aPkg = _package ();
    if (aPkg.isUnnamed ())
      return name ();
    return aPkg.name () + '.' + name ();
  }

  /**
   * @return <code>true</code> if this is an anonymous class. Note: this applies
   *         only to classes.
   */
  public final boolean isAnonymous ()
  {
    return m_sName == null;
  }

  public final boolean isClass ()
  {
    return true;
  }

  public final boolean isPackage ()
  {
    return false;
  }

  @SuppressWarnings ("unchecked")
  @Nonnull
  protected final CLASSTYPE thisAsT ()
  {
    return (CLASSTYPE) this;
  }

  @Nonnull
  public final IJClassContainer <?> parentContainer ()
  {
    return m_aOuter;
  }

  public final JPackage getPackage ()
  {
    return parentContainer ().getPackage ();
  }

  @Nonnull
  protected abstract CLASSTYPE createInnerClass (final int nMods,
                                                 @Nonnull final EClassType eClassType,
                                                 @Nonnull final String sName);

  @Nonnull
  public final CLASSTYPE _class (final int nMods,
                                 @Nonnull final String sName,
                                 @Nonnull final EClassType eClassType) throws JCodeModelException
  {
    final String sRealName;
    if (owner ().getFileSystemConvention ().isCaseSensistive ())
      sRealName = sName;
    else
      sRealName = sName.toUpperCase (Locale.ROOT);

    // Existing class?
    if (m_aClasses != null)
    {
      final CLASSTYPE aExistingClass = m_aClasses.get (sRealName);
      if (aExistingClass != null)
        throw new JClassAlreadyExistsException (aExistingClass);
    }
    else
      m_aClasses = new TreeMap <> ();

    // Create and add inner class
    final CLASSTYPE c = createInnerClass (nMods, eClassType, sName);
    m_aClasses.put (sRealName, c);
    return c;
  }

  /**
   * Returns an iterator that walks the nested classes defined in this class.
   * Don't modify the returned collection!
   */
  @Nonnull
  public final Collection <CLASSTYPE> classes ()
  {
    if (m_aClasses == null)
      return Collections.emptyList ();
    return m_aClasses.values ();
  }
}
