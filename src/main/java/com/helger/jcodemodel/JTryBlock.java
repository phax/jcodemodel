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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * Try statement with Catch and/or Finally clause
 */
public class JTryBlock implements IJStatement
{
  private final JBlock m_aBody = new JBlock ();
  private final List <JCatchBlock> m_aCatches = new ArrayList <> ();
  private JBlock m_aFinally;

  protected JTryBlock ()
  {}

  @Nonnull
  public JBlock body ()
  {
    return m_aBody;
  }

  @Nonnull
  public JCatchBlock _catch (@Nonnull final AbstractJClass aException)
  {
    final JCatchBlock cb = new JCatchBlock (aException);
    m_aCatches.add (cb);
    return cb;
  }

  @Nonnull
  public List <JCatchBlock> catches ()
  {
    return Collections.unmodifiableList (m_aCatches);
  }

  @Nonnull
  public JBlock _finally ()
  {
    if (m_aFinally == null)
      m_aFinally = new JBlock ();
    return m_aFinally;
  }

  public void state (@Nonnull final JFormatter f)
  {
    f.print ("try").generable (m_aBody);
    for (final JCatchBlock cb : m_aCatches)
      f.generable (cb);
    if (m_aFinally != null)
      f.print ("finally").generable (m_aFinally);
    f.newline ();
  }
}
