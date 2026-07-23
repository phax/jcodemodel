/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2026 Philip Helger + contributors
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
package com.helger.jcodemodel.vars;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.Nonnegative;
import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJFormatter;
import com.helger.jcodemodel.IVariableInitializer;
import com.helger.jcodemodel.JVar;

/// vars that are the same as another one.
///
/// example
/// ```java
/// int i=0, j, k=1;
/// ```
/// Here i is a block variable, j and k are "same" vars.
public class JSameVar extends JVar
{
  private final JVar m_aParent;

  /// number of additional array dimension on top of the parent.
  private final int m_nDim;

  public JSameVar (@NonNull final JVar parent,
                   final String sName,
                   final IVariableInitializer aInitExpr,
                   @Nonnegative final int nDim)
  {
    super (parent.mods (), typeArray (parent.type (), nDim), sName, aInitExpr);
    m_aParent = parent;
    m_nDim = nDim;
  }

  public JSameVar (final JVar parent, final String sName, final IVariableInitializer aInitExpr)
  {
    this (parent, sName, aInitExpr, 0);
  }

  public JVar parentVar ()
  {
    return m_aParent;
  }

  @Override
  public void bind (@NonNull final IJFormatter f)
  {
    f.id (name ());
    for (int i = 0; i < m_nDim; i++)
    {
      f.print ("[]");
    }
    if (init () != null)
    {
      f.print ('=').generable (init ());
    }
  }

  @Override
  public String separator ()
  {
    return ",";
  }

  @NonNull
  static AbstractJType typeArray (@NonNull final AbstractJType type, @Nonnegative final int dim)
  {
    var aCurType = type;
    int nRestDim = dim;
    while (nRestDim > 0)
    {
      aCurType = aCurType.array ();
      nRestDim--;
    }
    return aCurType;
  }
}
