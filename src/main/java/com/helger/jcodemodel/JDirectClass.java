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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.jcodemodel.util.JCHashCodeGenerator;

/**
 * A special {@link AbstractJClass} that represents an unknown class (except its
 * name.)
 *
 * @author Kohsuke Kawaguchi
 * @see JCodeModel#directClass(String)
 */
public class JDirectClass extends AbstractJClassContainer <JDirectClass>
{
  private final String m_sFullName;

  @Deprecated
  protected JDirectClass (@Nonnull final JCodeModel aOwner, @Nonnull final String sFullName)
  {
    this (aOwner, null, EClassType.CLASS, sFullName);
  }

  @Nonnull
  private static String _getName (@Nonnull final String sFullName)
  {
    final int nLast = sFullName.lastIndexOf ('.');
    if (nLast < 0)
      return sFullName;
    return sFullName.substring (nLast + 1);
  }

  protected JDirectClass (@Nonnull final JCodeModel aOwner,
                          @Nullable final IJClassContainer <?> aOuter,
                          @Nonnull final EClassType eClassType,
                          @Nonnull final String sFullName)
  {
    super (aOwner, aOuter, eClassType, _getName (sFullName));
    m_sFullName = sFullName;
  }

  @Override
  @Nonnull
  public String name ()
  {
    return super.name ();
  }

  /**
   * Gets the fully qualified name of this class.
   */
  @Override
  @Nonnull
  public String fullName ()
  {
    if (getOuter () instanceof AbstractJClassContainer <?>)
      return ((AbstractJClassContainer <?>) getOuter ()).fullName () + '.' + m_sFullName;

    // The fully qualified name was already provided in the ctor
    return m_sFullName;
  }

  @Override
  @Nonnull
  public JPackage _package ()
  {
    final IJClassContainer <?> aOuter = getOuter ();
    if (aOuter instanceof AbstractJClassContainer <?>)
      return ((AbstractJClassContainer <?>) aOuter)._package ();
    if (aOuter instanceof JPackage)
      return (JPackage) aOuter;

    // No package present - use name based analysis
    final String sFullName = fullName ();
    final int i = sFullName.lastIndexOf ('.');
    if (i >= 0)
      return owner ()._package (sFullName.substring (0, i));
    return owner ().rootPackage ();
  }

  @Override
  @Nonnull
  public AbstractJClass _extends ()
  {
    return owner ().ref (Object.class);
  }

  @Override
  @Nonnull
  public Iterator <AbstractJClass> _implements ()
  {
    return Collections.<AbstractJClass> emptyList ().iterator ();
  }

  @Override
  public boolean isAbstract ()
  {
    return false;
  }

  @Override
  @Nonnull
  protected AbstractJClass substituteParams (final JTypeVar [] aVariables,
                                             final List <? extends AbstractJClass> aBindings)
  {
    return this;
  }

  @Override
  @Nonnull
  protected JDirectClass createInnerClass (final int nMods, final EClassType eClassType, final String sName)
  {
    return new JDirectClass (owner (), this, eClassType, sName);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;

    if (o == null || !getClass ().equals (o.getClass ()))
      return false;

    final JDirectClass rhs = (JDirectClass) o;
    return m_sFullName.equals (rhs.m_sFullName);
  }

  @Override
  public int hashCode ()
  {
    return JCHashCodeGenerator.getHashCode (this, m_sFullName);
  }
}
