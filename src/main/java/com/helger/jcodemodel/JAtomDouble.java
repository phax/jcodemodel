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
 * A special atom for double values
 */
public class JAtomDouble implements IJExpression
{
  public static final String JAVA_LANG_DOUBLE_NEGATIVE_INFINITY = "java.lang.Double.NEGATIVE_INFINITY";
  public static final String JAVA_LANG_DOUBLE_POSITIVE_INFINITY = "java.lang.Double.POSITIVE_INFINITY";
  public static final String JAVA_LANG_DOUBLE_NAN = "java.lang.Double.NaN";

  private final double m_dWhat;

  protected JAtomDouble (final double dWhat)
  {
    m_dWhat = dWhat;
  }

  public double what ()
  {
    return m_dWhat;
  }

  public void generate (@Nonnull final IJFormatter f)
  {
    if (m_dWhat == Double.NEGATIVE_INFINITY)
      f.print (JAVA_LANG_DOUBLE_NEGATIVE_INFINITY);
    else
      if (m_dWhat == Double.POSITIVE_INFINITY)
        f.print (JAVA_LANG_DOUBLE_POSITIVE_INFINITY);
      else
        if (Double.isNaN (m_dWhat))
          f.print (JAVA_LANG_DOUBLE_NAN);
        else
          f.print (Double.toString (m_dWhat));
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || getClass () != o.getClass ())
      return false;
    final JAtomDouble rhs = (JAtomDouble) o;
    return isEqual (m_dWhat, rhs.m_dWhat);
  }

  @Override
  public int hashCode ()
  {
    return getHashCode (this, Double.valueOf (m_dWhat));
  }
}
