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
package com.helger.jcodemodel;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.enforce.ValueEnforcer;
import com.helger.jcodemodel.vars.JForEachVar;

/**
 * ForEach Statement This will generate the code for statement based on the new
 * j2se 1.5 j.l.s.
 *
 * @author Bhakti
 */
public class JForEach implements IJStatement
{
  private JBlock m_aBody; // lazily created
  private final JForEachVar m_aLoopVar;

  public JForEach (@NonNull final JMods aMods,
      @Nullable final AbstractJType aVarType,
                   @NonNull final String sVarName,
                   @NonNull final IJExpression aCollection)
  {
    ValueEnforcer.notNull (aMods, "Mods");
    ValueEnforcer.notNull (sVarName, "VarName");
    ValueEnforcer.notNull (aCollection, "Collection");
    m_aLoopVar = new JForEachVar((aMods.getValue() | JMod.FINAL) > 0, aVarType, sVarName, aCollection);
  }

  /**
   * @return the current modifiers of this method. Always return non-null valid
   *         object.
   */
  @NonNull
  public JMods mods ()
  {
    return m_aLoopVar.mods();
  }

  @NonNull
  public AbstractJType type ()
  {
    return m_aLoopVar.type();
  }

  /**
   * @return a reference to the loop variable.
   */
  @NonNull
  public JVar var ()
  {
    return m_aLoopVar;
  }

  @NonNull
  public IJExpression collection ()
  {
    return m_aLoopVar.collection();
  }

  @NonNull
  public JBlock body ()
  {
    if (m_aBody == null) {
      m_aBody = new JBlock ();
    }
    return m_aBody;
  }

  @Override
  public void state (@NonNull final IJFormatter f)
  {
    f.print ("for (");
    f.var(m_aLoopVar);
    // f.generable (m_aMods).generable (m_aType).id (m_sVarName).print (":
    // ").generable (m_aCollection);
    f.print (')');
    if (m_aBody != null) {
      f.generable (m_aBody);
    } else {
      f.print (';');
    }
    f.newline ();
  }
}
