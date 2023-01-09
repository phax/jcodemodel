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

import static com.helger.jcodemodel.util.JCEqualsHelper.isEqual;
import static com.helger.jcodemodel.util.JCHashCodeGenerator.getHashCode;

import javax.annotation.Nonnull;

/**
 * A cast operation.
 */
public class JCast implements IJExpression
{
  /**
   * JType to which the expression is to be cast.
   */
  private final AbstractJType m_aType;

  /**
   * JExpression to be cast.
   */
  private final IJExpression m_aObject;

  /**
   * JCast constructor
   *
   * @param aType
   *        JType to which the expression is cast
   * @param aObject
   *        JExpression for the object upon which the cast is applied
   */
  protected JCast (@Nonnull final AbstractJType aType, @Nonnull final IJExpression aObject)
  {
    m_aType = aType;
    m_aObject = aObject;
  }

  @Nonnull
  public AbstractJType type ()
  {
    return m_aType;
  }

  @Nonnull
  public IJExpression object ()
  {
    return m_aObject;
  }

  public void generate (@Nonnull final IJFormatter f)
  {
    f.print ("((").generable (m_aType).print (')').generable (m_aObject).print (')');
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || getClass () != o.getClass ())
      return false;
    final JCast rhs = (JCast) o;
    return isEqual (m_aType.fullName (), rhs.m_aType.fullName ()) && isEqual (m_aObject, rhs.m_aObject);
  }

  @Override
  public int hashCode ()
  {
    return getHashCode (this, m_aType.fullName (), m_aObject);
  }
}
