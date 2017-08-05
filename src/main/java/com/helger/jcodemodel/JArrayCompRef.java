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

import static com.helger.jcodemodel.util.JCEqualsHelper.isEqual;
import static com.helger.jcodemodel.util.JCHashCodeGenerator.getHashCode;

import javax.annotation.Nonnull;

import com.helger.jcodemodel.util.JCValueEnforcer;

/**
 * array component reference.
 */
public class JArrayCompRef implements IJAssignmentTarget
{
  /**
   * JArray expression upon which this component will be accessed.
   */
  private final IJExpression m_aArray;

  /**
   * Integer expression representing index of the component
   */
  private final IJExpression m_aIndex;

  /**
   * JArray component reference constructor given an array expression and index.
   *
   * @param aArray
   *        JExpression for the array upon which the component will be accessed,
   * @param aIndex
   *        JExpression for index of component to access
   */
  protected JArrayCompRef (@Nonnull final IJExpression aArray, @Nonnull final IJExpression aIndex)
  {
    JCValueEnforcer.notNull (aArray, "Array");
    JCValueEnforcer.notNull (aIndex, "Index");
    m_aArray = aArray;
    m_aIndex = aIndex;
  }

  @Nonnull
  public IJExpression array ()
  {
    return m_aArray;
  }

  @Nonnull
  public IJExpression index ()
  {
    return m_aIndex;
  }

  public void generate (@Nonnull final JFormatter f)
  {
    f.generable (m_aArray).print ('[').generable (m_aIndex).print (']');
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || getClass () != o.getClass ())
      return false;
    final JArrayCompRef rhs = (JArrayCompRef) o;
    return isEqual (m_aArray, rhs.m_aArray) && isEqual (m_aIndex, rhs.m_aIndex);
  }

  @Override
  public int hashCode ()
  {
    return getHashCode (this, m_aArray, m_aIndex);
  }
}
