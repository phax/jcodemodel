/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2020 Philip Helger + contributors
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

import javax.annotation.Nonnull;

import com.helger.jcodemodel.util.JCValueEnforcer;

/**
 * ForEach Statement This will generate the code for statement based on the new
 * j2se 1.5 j.l.s.
 *
 * @author Bhakti
 */
public class JForEach implements IJStatement
{
  private final JMods m_aMods;
  private final AbstractJType m_aType;
  private final String m_sVarName;
  private JBlock m_aBody; // lazily created
  private final IJExpression m_aCollection;
  private final JVar m_aLoopVar;

  public JForEach (@Nonnull final JMods aMods,
                   @Nonnull final AbstractJType aVarType,
                   @Nonnull final String sVarName,
                   @Nonnull final IJExpression aCollection)
  {
    JCValueEnforcer.notNull (aMods, "Mods");
    JCValueEnforcer.notNull (aVarType, "VarType");
    JCValueEnforcer.notNull (sVarName, "VarName");
    JCValueEnforcer.notNull (aCollection, "Collection");

    m_aMods = aMods;
    m_aType = aVarType;
    m_sVarName = sVarName;
    m_aCollection = aCollection;
    m_aLoopVar = new JVar (JMods.forVar (JMod.FINAL), m_aType, m_sVarName, aCollection);
  }

  /**
   * @return the current modifiers of this method. Always return non-null valid
   *         object.
   */
  @Nonnull
  public JMods mods ()
  {
    return m_aMods;
  }

  @Nonnull
  public AbstractJType type ()
  {
    return m_aType;
  }

  /**
   * @return a reference to the loop variable.
   */
  @Nonnull
  public JVar var ()
  {
    return m_aLoopVar;
  }

  @Nonnull
  public IJExpression collection ()
  {
    return m_aCollection;
  }

  @Nonnull
  public JBlock body ()
  {
    if (m_aBody == null)
      m_aBody = new JBlock ();
    return m_aBody;
  }

  public void state (@Nonnull final IJFormatter f)
  {
    f.print ("for (");
    f.generable (m_aMods).generable (m_aType).id (m_sVarName).print (": ").generable (m_aCollection);
    f.print (')');
    if (m_aBody != null)
      f.generable (m_aBody);
    else
      f.print (';');
    f.newline ();
  }
}
