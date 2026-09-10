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

import static com.helger.jcodemodel.util.JCHashCodeGenerator.getHashCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.equals.EqualsHelper;

/**
 * array creation and initialization.
 */
public class JArray implements IJExpression
{
  private final AbstractJType m_aType;
  private final IJExpression m_aSize;
  private List <IJExpression> m_aExprs;

  protected JArray (@NonNull final AbstractJType aType, @Nullable final IJExpression aSize)
  {
    m_aType = aType;
    m_aSize = aSize;
  }

  @NonNull
  public AbstractJType type ()
  {
    return m_aType;
  }

  @Nullable
  public IJExpression size ()
  {
    return m_aSize;
  }

  /**
   * Add an element to the array initializer
   *
   * @param aExpr
   *        Expression to be added to the array
   * @return this
   */
  @NonNull
  public JArray add (@NonNull final IJExpression aExpr)
  {
    if (m_aExprs == null)
      m_aExprs = new ArrayList <> ();
    m_aExprs.add (aExpr);
    return this;
  }

  /**
   * Remove all elements from the array initializer
   *
   * @return this
   */
  @NonNull
  public JArray removeAll ()
  {
    m_aExprs = null;
    return this;
  }

  @NonNull
  public List <IJExpression> exprsMutable ()
  {
    if (m_aExprs == null)
      m_aExprs = new ArrayList <> ();
    return m_aExprs;
  }

  @NonNull
  public List <IJExpression> exprs ()
  {
    return Collections.unmodifiableList (exprsMutable ());
  }

  public boolean hasExprs ()
  {
    return m_aExprs != null && !m_aExprs.isEmpty ();
  }

  public void generate (@NonNull final IJFormatter f)
  {
    // generally we produce new T[x], but when T is an array type (T=T'[])
    // then new T'[][x] is wrong. It has to be new T'[x][].
    int arrayCount = 0;
    AbstractJType t = m_aType;
    final boolean hasExprs = hasExprs ();

    while (t.isArray ())
    {
      t = t.elementType ();
      arrayCount++;
    }

    f.print ("new").generable (t).print ('[');
    if (m_aSize != null)
      f.generable (m_aSize);
    f.print (']');

    for (int i = 0; i < arrayCount; i++)
      f.print ("[]");

    if (m_aSize == null || hasExprs)
      f.print ('{');
    if (hasExprs)
      f.generable (m_aExprs);
    else
      f.print (' ');
    if (m_aSize == null || hasExprs)
      f.print ('}');
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || getClass () != o.getClass ())
      return false;
    final JArray rhs = (JArray) o;
    return EqualsHelper.equals (m_aType.fullName (), rhs.m_aType.fullName ()) &&
           EqualsHelper.equals (m_aSize, rhs.m_aSize) &&
           EqualsHelper.equals (m_aExprs, rhs.m_aExprs);
  }

  @Override
  public int hashCode ()
  {
    return getHashCode (this, m_aType.fullName (), m_aSize, m_aExprs);
  }
}
