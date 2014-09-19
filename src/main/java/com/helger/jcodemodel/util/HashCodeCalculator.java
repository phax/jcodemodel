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

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * This class provides the hash code generation for different data types.
 *
 * @author Philip Helger
 */
@Immutable
public final class HashCodeCalculator
{
  /**
   * Each value is multiplied with this value. 31 because it can easily be
   * optimized to <code>(1 &lt;&lt; 5) - 1</code>.
   */
  public static final int MULTIPLIER = 31;

  /**
   * The hash code value to be used for <code>null</code> values. Do not use 0
   * as e.g. <code>BigDecimal ("0")</code> also results in a 0 hash code.
   */
  public static final int HASHCODE_NULL = 129;

  @SuppressWarnings ("unused")
  private static final HashCodeCalculator s_aInstance = new HashCodeCalculator ();

  private HashCodeCalculator ()
  {}

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode, final boolean x)
  {
    return append (nPrevHashCode, x ? 1231 : 1237);
  }

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode, final byte x)
  {
    return append (nPrevHashCode, (int) x);
  }

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode, final char x)
  {
    return append (nPrevHashCode, (int) x);
  }

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode, final double x)
  {
    // ESCA-JAVA0078:
    return append (nPrevHashCode, x == 0.0 ? 0L : Double.doubleToLongBits (x));
  }

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode, final float x)
  {
    // ESCA-JAVA0078:
    return append (nPrevHashCode, x == 0.0F ? 0 : Float.floatToIntBits (x));
  }

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode, final int x)
  {
    return nPrevHashCode * MULTIPLIER + x;
  }

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode, final long x)
  {
    final int nTemp = append (nPrevHashCode, (int) (x >>> 32));
    return append (nTemp, (int) (x & 0xffffffffL));
  }

  /**
   * Atomic type hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Array to add
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode, final short x)
  {
    return append (nPrevHashCode, (int) x);
  }

  /**
   * Object hash code generation.
   *
   * @param nPrevHashCode
   *        The previous hash code used as the basis for calculation
   * @param x
   *        Object to add. May be <code>null</code>.
   * @return The updated hash code
   */
  public static int append (final int nPrevHashCode, @Nullable final Object x)
  {
    return append (nPrevHashCode, x == null ? HASHCODE_NULL : x.hashCode ());
  }
}
