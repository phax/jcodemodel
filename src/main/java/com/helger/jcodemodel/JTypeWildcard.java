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

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.jcodemodel.util.JCValueEnforcer;

/**
 * Represents a wildcard type like "? extends Foo" or "? super Foo".
 * <p>
 * Instances of this class can be obtained from
 * {@link AbstractJClass#wildcard()}
 * <p>
 * Our modeling of types are starting to look really ugly. ideally it should
 * have been done somewhat like APT, but it's too late now.
 *
 * @author Kohsuke Kawaguchi
 */
public class JTypeWildcard extends AbstractJClass
{
  private final AbstractJClass m_aBoundClass;
  private final EWildcardBoundMode m_eBoundMode;

  protected JTypeWildcard (@Nonnull final AbstractJClass aBoundClass, @Nonnull final EWildcardBoundMode eBoundMode)
  {
    super (aBoundClass.owner ());
    JCValueEnforcer.notNull (eBoundMode, "BoundMode");
    m_aBoundClass = aBoundClass;
    m_eBoundMode = eBoundMode;
  }

  @Nonnull
  public AbstractJClass bound ()
  {
    return m_aBoundClass;
  }

  @Nonnull
  @Override
  public AbstractJType declarable ()
  {
    return m_aBoundClass;
  }

  @Override
  public boolean containsTypeVar (@Nullable final JTypeVar aVar)
  {
    return m_aBoundClass.containsTypeVar (aVar);
  }

  @Nonnull
  public EWildcardBoundMode boundMode ()
  {
    return m_eBoundMode;
  }

  @Override
  @Nonnull
  public String name ()
  {
    return m_eBoundMode.declarationTokens () + m_aBoundClass.name ();
  }

  @Override
  @Nonnull
  public String fullName ()
  {
    return m_eBoundMode.declarationTokens () + m_aBoundClass.fullName ();
  }

  @Override
  @Nullable
  public JPackage _package ()
  {
    return null;
  }

  /**
   * Returns the class bound of this variable.
   * <p>
   * If no bound is given, this method returns {@link Object}.
   */

  @Override
  public AbstractJClass _extends ()
  {
    return m_eBoundMode == EWildcardBoundMode.EXTENDS ? m_aBoundClass : _package ().owner ().ref (Object.class);
  }

  /**
   * Returns the interface bounds of this variable.
   */
  @Override
  @Nonnull
  public Iterator <AbstractJClass> _implements ()
  {
    return m_aBoundClass._implements ();
  }

  @Override
  public boolean isInterface ()
  {
    return false;
  }

  @Override
  public boolean isAbstract ()
  {
    return false;
  }

  @Override
  public boolean isError ()
  {
    return m_aBoundClass.isError ();
  }

  @Override
  @Nonnull
  protected AbstractJClass substituteParams (@Nonnull final JTypeVar [] aVariables,
                                             @Nonnull final List <? extends AbstractJClass> aBindings)
  {
    final AbstractJClass nb = m_aBoundClass.substituteParams (aVariables, aBindings);
    if (nb == m_aBoundClass)
      return this;
    return new JTypeWildcard (nb, EWildcardBoundMode.EXTENDS);
  }

  @Override
  public void generate (@Nonnull final JFormatter f)
  {
    if (m_aBoundClass._extends () == null)
    {
      // instead of "? extends Object" or "? super Object"
      f.print ("?");
    }
    else
      f.print (m_eBoundMode.declarationTokens ()).generable (m_aBoundClass);
  }
}
