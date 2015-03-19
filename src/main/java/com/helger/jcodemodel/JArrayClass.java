/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2015 Philip Helger
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

/**
 * Array class.
 *
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class JArrayClass extends AbstractJClass
{
  // array component type
  private final AbstractJType m_aComponentType;

  protected JArrayClass (@Nonnull final JCodeModel aOwner, @Nonnull final AbstractJType aComponentType)
  {
    super (aOwner);
    m_aComponentType = aComponentType;
  }

  @Override
  @Nonnull
  public String name ()
  {
    return m_aComponentType.name () + "[]";
  }

  @Override
  @Nonnull
  public String fullName ()
  {
    return m_aComponentType.fullName () + "[]";
  }

  @Override
  @Nonnull
  public String binaryName ()
  {
    return m_aComponentType.binaryName () + "[]";
  }

  @Override
  public void generate (final JFormatter f)
  {
    f.generable (m_aComponentType).print ("[]");
  }

  @Override
  @Nonnull
  public JPackage _package ()
  {
    return owner ().rootPackage ();
  }

  @Override
  public AbstractJClass _extends ()
  {
    return owner ().ref (Object.class);
  }

  @Override
  public Iterator <AbstractJClass> _implements ()
  {
    return Collections.<AbstractJClass> emptyList ().iterator ();
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
  public AbstractJType elementType ()
  {
    return m_aComponentType;
  }

  @Override
  public boolean isArray ()
  {
    return true;
  }

  //
  // Equality is based on value
  //

  @Override
  public boolean equals (final Object obj)
  {
    if (obj == this)
      return true;
    if (obj == null || !getClass ().equals (obj.getClass ()))
      return false;

    final JArrayClass rhs = (JArrayClass) obj;
    return m_aComponentType.equals (rhs.m_aComponentType);
  }

  @Override
  public int hashCode ()
  {
    return m_aComponentType.hashCode ();
  }

  @Override
  @Nonnull
  protected AbstractJClass substituteParams (final JTypeVar [] variables, final List <? extends AbstractJClass> bindings)
  {
    if (m_aComponentType.isPrimitive ())
      return this;

    final AbstractJClass c = ((AbstractJClass) m_aComponentType).substituteParams (variables, bindings);
    if (c == m_aComponentType)
      return this;

    return new JArrayClass (owner (), c);
  }
}
