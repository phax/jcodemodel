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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Case statement
 */
public class JCase implements IJStatement
{
  /**
   * label part of the case statement
   */
  private final IJExpression m_aLabelExpr;

  /**
   * is this a regular case statement or a default case statement?
   */
  private boolean m_bIsDefaultCase = false;

  /**
   * JBlock of statements which makes up body of this While statement
   */
  private JBlock m_aBody;

  /**
   * Construct a case statement
   *
   * @param aLabel
   *        Label name. May not be <code>null</code>.
   */
  public JCase (@Nonnull final IJExpression aLabel)
  {
    this (aLabel, false);
  }

  /**
   * Construct a case statement. If isDefaultCase is true, then label should be
   * null since default cases don't have a label.
   *
   * @param aLabel
   *        Label name. May be <code>null</code> for the default case.
   * @param bIsDefaultCase
   *        <code>true</code> if this is the <code>default:</code> case which
   *        renders differently.
   */
  public JCase (@Nullable final IJExpression aLabel, final boolean bIsDefaultCase)
  {
    m_aLabelExpr = aLabel;
    m_bIsDefaultCase = bIsDefaultCase;
  }

  @Nullable
  public IJExpression label ()
  {
    return m_aLabelExpr;
  }

  public boolean isDefaultCase ()
  {
    return m_bIsDefaultCase;
  }

  @Nonnull
  public JBlock body ()
  {
    if (m_aBody == null)
      m_aBody = new JBlock ();
    return m_aBody;
  }

  public void state (@Nonnull final JFormatter f)
  {
    f.indent ();
    if (m_bIsDefaultCase)
    {
      f.print ("default:").newline ();
    }
    else
    {
      // Hack for #41 :)
      IJExpression aLabelName;
      if (m_aLabelExpr instanceof JEnumConstant)
      {
        // Just use the name, but not the type of the enum
        aLabelName = f1 -> f1.print (((JEnumConstant) m_aLabelExpr).name ());
      }
      else
        aLabelName = m_aLabelExpr;

      f.print ("case ").generable (aLabelName).print (':').newline ();
    }
    if (m_aBody != null)
      f.statement (m_aBody);
    f.outdent ();
  }
}
