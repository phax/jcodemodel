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

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * A special {@link AbstractJClass} that represents an error class.
 * <p>
 * Error-types represents holes or placeholders that can't be filled.
 * {@code JErrorClass} differs from {@code JDirectClass} class in that it should
 * never be used in generated code. References to error-classes can be used in
 * hidden class-models. Such classes should never be actually written but can be
 * somehow used during code generation. Use
 * {@code JCodeModel#buildsErrorTypeRefs} method to test if your generated
 * Java-sources contains references to error-types.
 * <p>
 * You should probably always check generated code with
 * {@code JCodeModel#buildsErrorTypeRefs} method if you use any error-types.
 * <p>
 * Most of {@code JErrorClass} methods throws {@code JErrorClassUsedException}
 * unchecked exceptions. Be careful and use {@link AbstractJType#isError()
 * AbstractJType#isError} method to check for error-types before actually using
 * it's methods.
 *
 * @see JCodeModel#buildsErrorTypeRefs()
 * @see JCodeModel#errorClass(String)
 * @author Victor Nazarov
 */
public class JErrorClass extends AbstractJClass
{
  private final String m_sMessage;

  protected JErrorClass (@Nonnull final JCodeModel _owner, @Nonnull final String sMessage)
  {
    super (_owner);
    m_sMessage = sMessage;
  }

  @Override
  @Nonnull
  public String name ()
  {
    throw new JErrorClassUsedException (m_sMessage);
  }

  @Override
  @Nonnull
  public String fullName ()
  {
    throw new JErrorClassUsedException (m_sMessage);
  }

  @Override
  @Nonnull
  public JPackage _package ()
  {
    throw new JErrorClassUsedException (m_sMessage);
  }

  @Override
  @Nonnull
  public AbstractJClass _extends ()
  {
    throw new JErrorClassUsedException (m_sMessage);
  }

  @Override
  @Nonnull
  public Iterator <AbstractJClass> _implements ()
  {
    throw new JErrorClassUsedException (m_sMessage);
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
    return true;
  }

  @Nonnull
  public String getMessage ()
  {
    return m_sMessage;
  }

  @Override
  @Nonnull
  protected AbstractJClass substituteParams (final JTypeVar [] variables, final List <? extends AbstractJClass> bindings)
  {
    return this;
  }
}
