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

/**
 * A special atom for float values
 */
public class JAtomFloat implements IJExpression
{
  public static final String JAVA_LANG_FLOAT_NEGATIVE_INFINITY = "java.lang.Float.NEGATIVE_INFINITY";
  public static final String JAVA_LANG_FLOAT_POSITIVE_INFINITY = "java.lang.Float.POSITIVE_INFINITY";
  public static final String JAVA_LANG_FLOAT_NAN = "java.lang.Float.NaN";
  public static final String SUFFIX_FLOAT = "F";

  private final float m_fWhat;

  protected JAtomFloat (final float fWhat)
  {
    m_fWhat = fWhat;
  }

  public float what ()
  {
    return m_fWhat;
  }

  public void generate (@Nonnull final JFormatter f)
  {
    if (m_fWhat == Float.NEGATIVE_INFINITY)
      f.print (JAVA_LANG_FLOAT_NEGATIVE_INFINITY);
    else
      if (m_fWhat == Float.POSITIVE_INFINITY)
        f.print (JAVA_LANG_FLOAT_POSITIVE_INFINITY);
      else
        if (Float.isNaN (m_fWhat))
          f.print (JAVA_LANG_FLOAT_NAN);
        else
          f.print (Float.toString (m_fWhat) + SUFFIX_FLOAT);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || getClass () != o.getClass ())
      return false;
    final JAtomFloat rhs = (JAtomFloat) o;
    return isEqual (m_fWhat, rhs.m_fWhat);
  }

  @Override
  public int hashCode ()
  {
    return getHashCode (this, Float.valueOf (m_fWhat));
  }
}
