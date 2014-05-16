/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.helger.jcodemodel.JTypeVar.EBoundMode;

/**
 * Implementation of {@link IJGenerifiable}.
 * 
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public abstract class AbstractJGenerifiableImpl implements IJGenerifiable, IJDeclaration, IJOwned
{
  /**
   * Lazily created list of {@link JTypeVar}s.
   */
  private Map <String, JTypeVar> _typeVariables;

  public void declare (@Nonnull final JFormatter f)
  {
    if (_typeVariables != null && !_typeVariables.isEmpty ())
    {
      f.print ('<');
      int nIndex = 0;
      for (final JTypeVar aTypeVar : _typeVariables.values ())
      {
        if (nIndex++ > 0)
          f.print (',');
        f.declaration (aTypeVar);
      }
      f.print (JFormatter.CLOSE_TYPE_ARGS);
    }
  }

  @Nonnull
  public JTypeVar generify (@Nonnull final String name)
  {
    final JTypeVar v = new JTypeVar (owner (), name);
    if (_typeVariables == null)
      _typeVariables = new LinkedHashMap <String, JTypeVar> (3);
    else
      if (_typeVariables.containsKey (name))
        throw new IllegalArgumentException ("A type parameter with name '" + name + "' is already present!");
    _typeVariables.put (name, v);
    return v;
  }

  @Nonnull
  public JTypeVar generify (@Nonnull final String name, @Nonnull final Class <?> _extends)
  {
    return generify (name, owner ().ref (_extends));
  }

  @Nonnull
  public JTypeVar generify (@Nonnull final String name,
                            @Nonnull final Class <?> _extends,
                            @Nonnull final EBoundMode eMode)
  {
    return generify (name, owner ().ref (_extends), eMode);
  }

  @Nonnull
  public JTypeVar generify (@Nonnull final String name, @Nonnull final AbstractJClass _extends)
  {
    return generify (name).bound (_extends);
  }

  @Nonnull
  public JTypeVar generify (@Nonnull final String name,
                            @Nonnull final AbstractJClass _extends,
                            @Nonnull final EBoundMode eMode)
  {
    return generify (name).bound (_extends, eMode);
  }

  @Nonnull
  public JTypeVar [] typeParams ()
  {
    if (_typeVariables == null)
      return AbstractJClass.EMPTY_ARRAY;
    return _typeVariables.values ().toArray (new JTypeVar [_typeVariables.size ()]);
  }

  @Nonnull
  public List <JTypeVar> typeParamList ()
  {
    if (_typeVariables == null)
      return Collections.<JTypeVar> emptyList ();
    return new ArrayList <JTypeVar> (_typeVariables.values ());
  }
}
