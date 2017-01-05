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

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * This class provides the hash code generation for different data types.
 *
 * @author Philip Helger
 */
@Immutable
public final class JCHashCodeCalculator
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
  private static final JCHashCodeCalculator s_aInstance = new JCHashCodeCalculator ();

  private JCHashCodeCalculator ()
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
