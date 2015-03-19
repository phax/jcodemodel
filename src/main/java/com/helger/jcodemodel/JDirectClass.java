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
 * A special {@link AbstractJClass} that represents an unknown class (except its
 * name.)
 *
 * @author Kohsuke Kawaguchi
 * @see JCodeModel#directClass(String)
 */
public class JDirectClass extends AbstractJClass
{
  private final String m_sFullName;

  protected JDirectClass (@Nonnull final JCodeModel aOwner, @Nonnull final String sFullName)
  {
    super (aOwner);
    m_sFullName = sFullName;
  }

  @Override
  @Nonnull
  public String name ()
  {
    final int i = m_sFullName.lastIndexOf ('.');
    if (i >= 0)
      return m_sFullName.substring (i + 1);
    return m_sFullName;
  }

  @Override
  @Nonnull
  public String fullName ()
  {
    return m_sFullName;
  }

  @Override
  @Nonnull
  public JPackage _package ()
  {
    final int i = m_sFullName.lastIndexOf ('.');
    if (i >= 0)
      return owner ()._package (m_sFullName.substring (0, i));
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
  @Nonnull
  protected AbstractJClass substituteParams (final JTypeVar [] variables, final List <? extends AbstractJClass> bindings)
  {
    return this;
  }
}
