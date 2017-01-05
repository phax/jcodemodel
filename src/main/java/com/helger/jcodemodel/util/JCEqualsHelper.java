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
package com.helger.jcodemodel.util;

import java.util.Arrays;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A small helper class that provides helper methods for easy
 * <code>equals</code> method generation
 *
 * @author Philip Helger
 */
@Immutable
public final class JCEqualsHelper
{
  private JCEqualsHelper ()
  {}

  public static boolean isEqual (final boolean aObj1, final boolean aObj2)
  {
    return aObj1 == aObj2;
  }

  public static boolean isEqual (final byte aObj1, final byte aObj2)
  {
    return aObj1 == aObj2;
  }

  public static boolean isEqual (final char aObj1, final char aObj2)
  {
    return aObj1 == aObj2;
  }

  /**
   * Check if two double values are equal. This is necessary, because in some
   * cases, the "==" operator returns wrong results.
   *
   * @param aObj1
   *        First double
   * @param aObj2
   *        Second double
   * @return <code>true</code> if they are equal.
   */
  public static boolean isEqual (final double aObj1, final double aObj2)
  {
    // ESCA-JAVA0078:
    // Special overload for "double" required!
    return (aObj1 == aObj2) || (Double.doubleToLongBits (aObj1) == Double.doubleToLongBits (aObj2));
  }

  /**
   * Check if two float values are equal. This is necessary, because in some
   * cases, the "==" operator returns wrong results.
   *
   * @param aObj1
   *        First float
   * @param aObj2
   *        Second float
   * @return <code>true</code> if they are equal.
   */
  public static boolean isEqual (final float aObj1, final float aObj2)
  {
    // ESCA-JAVA0078:
    // Special overload for "float" required!
    return (aObj1 == aObj2) || (Float.floatToIntBits (aObj1) == Float.floatToIntBits (aObj2));
  }

  public static boolean isEqual (final int aObj1, final int aObj2)
  {
    return aObj1 == aObj2;
  }

  public static boolean isEqual (final long aObj1, final long aObj2)
  {
    return aObj1 == aObj2;
  }

  public static boolean isEqual (final short aObj1, final short aObj2)
  {
    return aObj1 == aObj2;
  }

  public static boolean isEqual (@Nullable final Object aObj1, @Nullable final Object aObj2)
  {
    // Same object - check first
    if (aObj1 == aObj2)
      return true;

    // Is only one value null?
    if (aObj1 == null || aObj2 == null)
      return false;

    // Check whether the implementation classes are identical
    final Class <?> aClass1 = aObj1.getClass ();
    final Class <?> aClass2 = aObj2.getClass ();
    if (!aClass1.equals (aClass2))
    {
      // Not the same class -> not equal!
      return false;
    }

    if (aClass1.isArray ())
    {
      // Special handling for arrays
      final Object [] aArray1 = (Object []) aObj1;
      final Object [] aArray2 = (Object []) aObj2;
      // Size check
      final int nLength = aArray1.length;
      if (nLength != aArray2.length)
        return false;
      // Content check
      for (int i = 0; i < nLength; i++)
        if (!isEqual (aArray1[i], aArray2[i]))
          return false;
      return true;
    }

    // Primitive arrays
    if (aClass1.equals (boolean [].class))
      return Arrays.equals ((boolean []) aObj1, (boolean []) aObj2);
    if (aClass1.equals (byte [].class))
      return Arrays.equals ((byte []) aObj1, (byte []) aObj2);
    if (aClass1.equals (char [].class))
      return Arrays.equals ((char []) aObj1, (char []) aObj2);
    if (aClass1.equals (double [].class))
      return Arrays.equals ((double []) aObj1, (double []) aObj2);
    if (aClass1.equals (float [].class))
      return Arrays.equals ((float []) aObj1, (float []) aObj2);
    if (aClass1.equals (int [].class))
      return Arrays.equals ((int []) aObj1, (int []) aObj2);
    if (aClass1.equals (long [].class))
      return Arrays.equals ((long []) aObj1, (long []) aObj2);
    if (aClass1.equals (short [].class))
      return Arrays.equals ((short []) aObj1, (short []) aObj2);

    // Non-array
    return aObj1.equals (aObj2);
  }

  @SuppressFBWarnings ({ "ES_COMPARING_PARAMETER_STRING_WITH_EQ" })
  public static boolean nullSafeEqualsIgnoreCase (@Nullable final String sObj1, @Nullable final String sObj2)
  {
    return sObj1 == null ? sObj2 == null : sObj1.equalsIgnoreCase (sObj2);
  }
}
