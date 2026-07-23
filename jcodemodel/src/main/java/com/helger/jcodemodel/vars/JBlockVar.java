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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJFormatter;
import com.helger.jcodemodel.IVariableInitializer;
import com.helger.jcodemodel.JMods;
import com.helger.jcodemodel.JVar;
import com.helger.jcodemodel.writer.settings.Wrap.ListWrapping;

///
/// A variable that is declared as part of a block.
///
/// example :
/// ```java
/// int i=0;
/// ```
///
public class JBlockVar extends JVar
{
  private final List <JSameVar> m_aChildrenVar = new ArrayList <> ();

  public JBlockVar (@NonNull final JMods aMods,
                    final AbstractJType aType,
                    @NonNull final String sName,
                    @Nullable final IVariableInitializer aInitExpr)
  {
    super (aMods, aType, sName, aInitExpr);
  }

  /// @return a stream of this and children variables.
  public Stream <JVar> streamVars ()
  {
    return Stream.concat (Stream.of (this), m_aChildrenVar.stream ());
  }

  @Override
  public void declare (@NonNull final IJFormatter f)
  {
    if (m_aChildrenVar.isEmpty ())
    {
      super.declare (f);
    }
    else
    {
      f.vars (streamVars ().toList (), extractWrappingOptions (f)).print (';').newline ();
    }
  }

  /// extract the wrapping options for this type of var. Present here to be
  /// overridden in the fieldVar
  protected ListWrapping extractWrappingOptions (@NonNull final IJFormatter f)
  {
    return f.settings ().wrap.variables.block;
  }

  /// add and return a new var with same type and mods, but given name and init.
  ///
  /// Note that the dimension is added on top of this' type. For example
  /// ```java
  /// int [] i={0}, j[][], k;
  /// ```
  /// makes i an int[], j an int[][][] (dim 2), and k an int[] (dim 1).
  ///
  /// @param dim the additional dimension of the array, based on the type of this
  public JSameVar andVar (final String name, final int dim, final IVariableInitializer aInitExpr)
  {
    final JSameVar ret = new JSameVar (this, name, aInitExpr, dim);
    m_aChildrenVar.add (ret);
    return ret;
  }

  /// add and return a new var with same type and mods, but given name and init.
  ///
  /// dimension is set to 0, meaning the new variable type is the same as this.
  public JSameVar andVar (final String name, final IVariableInitializer aInitExpr)
  {
    return andVar (name, 0, aInitExpr);
  }

  /// add and return a new var with same type and mods, but given name and
  /// dimension.
  ///
  /// init is set to null, so nonexistant assignment.
  public JSameVar andVar (final String name, final int dim)
  {
    return andVar (name, dim, null);
  }

  /// add and return a new var with same type, same mods, but no init and given
  /// name. Also with dimension 0
  public JSameVar andVar (final String name)
  {
    return andVar (name, null);
  }

  @Override
  public String separator ()
  {
    return ",";
  }

}
