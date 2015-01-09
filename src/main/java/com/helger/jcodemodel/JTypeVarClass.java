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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A special type variable that is used inside {@link JInvocation} objects if
 * the parameter type is an {@link AbstractJClass}
 * 
 * @author Philip Helger
 */
public class JTypeVarClass extends JTypeVar
{
  private final AbstractJClass _cls;

  protected JTypeVarClass (@Nonnull final AbstractJClass cls)
  {
    super (cls.owner (), cls.name ());
    _cls = cls;
  }

  @Override
  @Nonnull
  public String name ()
  {
    // This method is used for the main printing
    if (_cls instanceof JDefinedClass)
    {
      final List <JTypeVar> aTypeParams = ((JDefinedClass) _cls).typeParamList ();
      if (!aTypeParams.isEmpty ())
      {
        // We need the type params here!
        return new JNarrowedClass (_cls, aTypeParams).name ();
      }
    }
    return _cls.name ();
  }

  @Override
  @Nonnull
  public String fullName ()
  {
    // This method is e.g. used for import statements
    if (_cls instanceof JNarrowedClass)
    {
      // Avoid the type parameters
      return ((JNarrowedClass) _cls).erasure ().fullName ();
    }
    return _cls.fullName ();
  }

  @Override
  @Nullable
  public JPackage _package ()
  {
    return _cls._package ();
  }
}
