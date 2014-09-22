/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
public final class EqualsUtils
{
  private EqualsUtils ()
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
