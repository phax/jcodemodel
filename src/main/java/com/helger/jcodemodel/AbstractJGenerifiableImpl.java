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

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Implementation of {@link IJGenerifiable}.
 *
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public abstract class AbstractJGenerifiableImpl implements IJGenerifiable
{
  /**
   * Lazily created list of {@link JTypeVar}s.
   */
  private Map <String, JTypeVar> m_aTypeVariables;

  public void declare (@Nonnull final JFormatter f)
  {
    if (m_aTypeVariables != null && !m_aTypeVariables.isEmpty ())
    {
      f.print ('<');
      int nIndex = 0;
      for (final JTypeVar aTypeVar : m_aTypeVariables.values ())
      {
        if (nIndex++ > 0)
          f.print (',');
        f.declaration (aTypeVar);
      }
      f.print (JFormatter.CLOSE_TYPE_ARGS);
    }
  }

  @Nonnull
  public JTypeVar generify (@Nonnull final String sName)
  {
    final JTypeVar v = new JTypeVar (owner (), sName);
    if (m_aTypeVariables == null)
      m_aTypeVariables = new LinkedHashMap <> (3);
    else
      if (m_aTypeVariables.containsKey (sName))
        throw new IllegalArgumentException ("A type parameter with name '" + sName + "' is already present!");
    m_aTypeVariables.put (sName, v);
    return v;
  }

  @Nonnull
  public JTypeVar [] typeParams ()
  {
    if (m_aTypeVariables == null)
      return AbstractJClass.EMPTY_ARRAY;
    return m_aTypeVariables.values ().toArray (new JTypeVar [m_aTypeVariables.size ()]);
  }
}
