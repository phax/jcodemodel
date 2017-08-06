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

import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.jcodemodel.util.JCNameUtilities;

/**
 * References to existing classes.
 * <p>
 * {@link JReferencedClass} is kept in a pool so that they are shared. There is
 * one pool for each {@link JCodeModel} object.
 * <p>
 * It is impossible to cache JReferencedClass globally only because there is the
 * {@link #_package()} method, which obtains the owner {@link JPackage} object,
 * which is scoped to JCodeModel.
 */
class JReferencedClass extends AbstractJClass implements IJDeclaration
{
  private final Class <?> m_aClass;

  // Cached status vars
  private transient boolean m_bResolvedPrimitive = false;
  private transient JPrimitiveType m_aPrimitiveType;

  JReferencedClass (@Nonnull final JCodeModel aOwner, @Nonnull final Class <?> aClass)
  {
    super (aOwner);
    m_aClass = aClass;
    assert !m_aClass.isArray ();
  }

  @Override
  public String name ()
  {
    return m_aClass.getSimpleName ();
  }

  @Override
  @Nonnull
  public String fullName ()
  {
    return JCNameUtilities.getFullName (m_aClass);
  }

  @Override
  public String binaryName ()
  {
    return m_aClass.getName ();
  }

  @Override
  public AbstractJClass outer ()
  {
    final Class <?> p = m_aClass.getDeclaringClass ();
    if (p == null)
      return null;
    return owner ().ref (p);
  }

  @Override
  @Nonnull
  public JPackage _package ()
  {
    final String name = fullName ();

    // this type is array
    if (name.indexOf ('[') != -1)
      return owner ()._package ("");

    // other normal case
    final int idx = name.lastIndexOf ('.');
    if (idx < 0)
      return owner ()._package ("");
    return owner ()._package (name.substring (0, idx));
  }

  @Override
  public AbstractJClass _extends ()
  {
    final Class <?> sp = m_aClass.getSuperclass ();
    if (sp == null)
    {
      if (isInterface ())
        return owner ().ref (Object.class);
      return null;
    }
    return owner ().ref (sp);
  }

  @Override
  public Iterator <AbstractJClass> _implements ()
  {
    final Class <?> [] aInterfaces = m_aClass.getInterfaces ();
    return new Iterator <AbstractJClass> ()
    {
      private int m_nIdx = 0;

      public boolean hasNext ()
      {
        return m_nIdx < aInterfaces.length;
      }

      @Nonnull
      public AbstractJClass next ()
      {
        return owner ().ref (aInterfaces[m_nIdx++]);
      }

      public void remove ()
      {
        throw new UnsupportedOperationException ();
      }
    };
  }

  @Override
  public boolean isInterface ()
  {
    return m_aClass.isInterface ();
  }

  @Override
  public boolean isAbstract ()
  {
    return Modifier.isAbstract (m_aClass.getModifiers ());
  }

  @Override
  @Nullable
  public final JPrimitiveType getPrimitiveType ()
  {
    // Resolve only once
    if (!m_bResolvedPrimitive)
    {
      final Class <?> v = JCodeModel.boxToPrimitive.get (m_aClass);
      if (v != null)
        m_aPrimitiveType = AbstractJType.parse (owner (), v.getName ());
      else
        m_aPrimitiveType = null;
      m_bResolvedPrimitive = true;
    }
    return m_aPrimitiveType;
  }

  public void declare (final JFormatter f)
  {
    // Nothing to do here...
  }

  @Override
  public JTypeVar [] typeParams ()
  {
    // TODO: does JDK 1.5 reflection provides these information?
    return super.typeParams ();
  }

  @Override
  protected AbstractJClass substituteParams (@Nonnull final JTypeVar [] aVariables,
                                             @Nonnull final List <? extends AbstractJClass> aBindings)
  {
    // TODO: does JDK 1.5 reflection provides these information?
    return this;
  }
}
