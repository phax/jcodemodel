/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2016 Philip Helger + contributors
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class JCValueEnforcer
{
  private JCValueEnforcer ()
  {}

  /**
   * Check that the passed value is <code>true</code>.
   *
   * @param bValue
   *        The value to check.
   * @param sMsg
   *        The message to be emitted in case the value is <code>false</code>
   * @throws IllegalArgumentException
   *         if the passed value is not <code>null</code>.
   */
  public static void isTrue (final boolean bValue, final String sMsg)
  {
    if (!bValue)
      throw new IllegalArgumentException ("The expression must be true but it is not: " + sMsg);
  }

  /**
   * Check that the passed value is <code>false</code>.
   *
   * @param bValue
   *        The value to check.
   * @param sMsg
   *        The message to be emitted in case the value is <code>true</code>
   * @throws IllegalArgumentException
   *         if the passed value is not <code>null</code>.
   */
  public static void isFalse (final boolean bValue, final String sMsg)
  {
    if (bValue)
      throw new IllegalArgumentException ("The expression must be false but it is not: " + sMsg);
  }

  /**
   * Check that the passed value is not <code>null</code>.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The value to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value and never <code>null</code>.
   * @throws NullPointerException
   *         if the passed value is <code>null</code>.
   */
  @Nonnull
  public static <T> T notNull (final T aValue, final String sName)
  {
    if (aValue == null)
      throw new NullPointerException ("The value of '" + sName + "' may not be null!");
    return aValue;
  }

  /**
   * Check that the passed value is <code>null</code>.
   *
   * @param aValue
   *        The value to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @throws IllegalArgumentException
   *         if the passed value is not <code>null</code>.
   */
  @Nonnull
  public static void isNull (final Object aValue, final String sName)
  {
    if (aValue != null)
      throw new IllegalArgumentException ("The value of '" + sName + "' must be null but is " + aValue);
  }

  /**
   * Check that the passed String is neither <code>null</code> nor empty.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The String to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value and never <code>null</code>.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  @Nonnull
  public static <T extends CharSequence> T notEmpty (final T aValue, final String sName)
  {
    notNull (aValue, sName);
    if (aValue.length () == 0)
      throw new IllegalArgumentException ("The value of the string '" + sName + "' may not be empty!");
    return aValue;
  }

  /**
   * Check that the passed Array is neither <code>null</code> nor empty.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The Array to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value and never <code>null</code>.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  @Nonnull
  public static <T> T [] notEmpty (final T [] aValue, final String sName)
  {
    notNull (aValue, sName);
    if (aValue.length == 0)
      throw new IllegalArgumentException ("The value of the array '" + sName + "' may not be empty!");
    return aValue;
  }

  /**
   * Check that the passed Array is neither <code>null</code> nor empty.
   *
   * @param aValue
   *        The Array to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value and never <code>null</code>.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  @Nonnull
  public static boolean [] notEmpty (final boolean [] aValue, final String sName)
  {
    notNull (aValue, sName);
    if (aValue.length == 0)
      throw new IllegalArgumentException ("The value of the array '" + sName + "' may not be empty!");
    return aValue;
  }

  /**
   * Check that the passed Array is neither <code>null</code> nor empty.
   *
   * @param aValue
   *        The Array to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value and never <code>null</code>.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  @Nonnull
  public static byte [] notEmpty (final byte [] aValue, final String sName)
  {
    notNull (aValue, sName);
    if (aValue.length == 0)
      throw new IllegalArgumentException ("The value of the array '" + sName + "' may not be empty!");
    return aValue;
  }

  /**
   * Check that the passed Array is neither <code>null</code> nor empty.
   *
   * @param aValue
   *        The Array to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value and never <code>null</code>.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  @Nonnull
  public static char [] notEmpty (final char [] aValue, final String sName)
  {
    notNull (aValue, sName);
    if (aValue.length == 0)
      throw new IllegalArgumentException ("The value of the array '" + sName + "' may not be empty!");
    return aValue;
  }

  /**
   * Check that the passed Array is neither <code>null</code> nor empty.
   *
   * @param aValue
   *        The Array to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value and never <code>null</code>.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  @Nonnull
  public static double [] notEmpty (final double [] aValue, final String sName)
  {
    notNull (aValue, sName);
    if (aValue.length == 0)
      throw new IllegalArgumentException ("The value of the array '" + sName + "' may not be empty!");
    return aValue;
  }

  /**
   * Check that the passed Array is neither <code>null</code> nor empty.
   *
   * @param aValue
   *        The Array to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value and never <code>null</code>.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  @Nonnull
  public static float [] notEmpty (final float [] aValue, final String sName)
  {
    notNull (aValue, sName);
    if (aValue.length == 0)
      throw new IllegalArgumentException ("The value of the array '" + sName + "' may not be empty!");
    return aValue;
  }

  /**
   * Check that the passed Array is neither <code>null</code> nor empty.
   *
   * @param aValue
   *        The Array to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value and never <code>null</code>.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  @Nonnull
  public static int [] notEmpty (final int [] aValue, final String sName)
  {
    notNull (aValue, sName);
    if (aValue.length == 0)
      throw new IllegalArgumentException ("The value of the array '" + sName + "' may not be empty!");
    return aValue;
  }

  /**
   * Check that the passed Array is neither <code>null</code> nor empty.
   *
   * @param aValue
   *        The Array to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value and never <code>null</code>.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  @Nonnull
  public static long [] notEmpty (final long [] aValue, final String sName)
  {
    notNull (aValue, sName);
    if (aValue.length == 0)
      throw new IllegalArgumentException ("The value of the array '" + sName + "' may not be empty!");
    return aValue;
  }

  /**
   * Check that the passed Array is neither <code>null</code> nor empty.
   *
   * @param aValue
   *        The Array to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value and never <code>null</code>.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  @Nonnull
  public static short [] notEmpty (final short [] aValue, final String sName)
  {
    notNull (aValue, sName);
    if (aValue.length == 0)
      throw new IllegalArgumentException ("The value of the array '" + sName + "' may not be empty!");
    return aValue;
  }

  /**
   * Check that the passed {@link Collection} is neither <code>null</code> nor
   * empty.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The String to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value and never <code>null</code>.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  @Nonnull
  public static <T extends Collection <?>> T notEmpty (final T aValue, final String sName)
  {
    notNull (aValue, sName);
    if (aValue.isEmpty ())
      throw new IllegalArgumentException ("The value of the collection '" + sName + "' may not be empty!");
    return aValue;
  }

  /**
   * Check that the passed {@link Iterable} is neither <code>null</code> nor
   * empty.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The String to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value and never <code>null</code>.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  @Nonnull
  public static <T extends Iterable <?>> T notEmpty (final T aValue, final String sName)
  {
    notNull (aValue, sName);
    if (!aValue.iterator ().hasNext ())
      throw new IllegalArgumentException ("The value of the iterable '" + sName + "' may not be empty!");
    return aValue;
  }

  /**
   * Check that the passed Collection is neither <code>null</code> nor empty.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The String to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value and never <code>null</code>.
   * @throws IllegalArgumentException
   *         if the passed value is empty
   */
  @Nonnull
  public static <T extends Map <?, ?>> T notEmpty (final T aValue, final String sName)
  {
    notNull (aValue, sName);
    if (aValue.isEmpty ())
      throw new IllegalArgumentException ("The value of the map '" + sName + "' may not be empty!");
    return aValue;
  }

  /**
   * Check that the passed Array contains no <code>null</code> value. But the
   * whole array can be <code>null</code> or empty.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The Array to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value and never <code>null</code>.
   * @throws IllegalArgumentException
   *         if the passed value is not empty and a <code>null</code> value is
   *         contained
   */
  @Nullable
  public static <T> T [] noNullValue (final T [] aValue, final String sName)
  {
    if (aValue != null)
    {
      int nIndex = 0;
      for (final T aItem : aValue)
      {
        if (aItem == null)
          throw new IllegalArgumentException ("Item " + nIndex + " of array '" + sName + "' may not be null!");
        ++nIndex;
      }
    }
    return aValue;
  }

  /**
   * Check that the passed iterable contains no <code>null</code> value. But the
   * whole iterable can be <code>null</code> or empty.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The collection to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value and never <code>null</code>.
   * @throws IllegalArgumentException
   *         if the passed value is not empty and a <code>null</code> value is
   *         contained
   */
  @Nullable
  public static <T extends Iterable <?>> T noNullValue (final T aValue, final String sName)
  {
    if (aValue != null)
    {
      int nIndex = 0;
      for (final Object aItem : aValue)
      {
        if (aItem == null)
          throw new IllegalArgumentException ("Item " + nIndex + " of iterable '" + sName + "' may not be null!");
        ++nIndex;
      }
    }
    return aValue;
  }

  /**
   * Check that the passed map is neither <code>null</code> nor empty and that
   * no <code>null</code> key or value is contained.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The map to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value and never <code>null</code>.
   * @throws IllegalArgumentException
   *         if the passed value is not empty and a <code>null</code> key or
   *         <code>null</code> value is contained
   */
  @Nullable
  public static <T extends Map <?, ?>> T noNullValue (final T aValue, final String sName)
  {
    if (aValue != null)
    {
      for (final Map.Entry <?, ?> aEntry : aValue.entrySet ())
      {
        if (aEntry.getKey () == null)
          throw new IllegalArgumentException ("Key of map '" + sName + "' may not be null!");
        if (aEntry.getValue () == null)
          throw new IllegalArgumentException ("Value of map '" + sName + "' may not be null!");
      }
    }
    return aValue;
  }

  /**
   * Check that the passed Array is neither <code>null</code> nor empty and that
   * no <code>null</code> value is contained.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The Array to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value and never <code>null</code>.
   * @throws IllegalArgumentException
   *         if the passed value is empty or a <code>null</code> value is
   *         contained
   */
  @Nonnull
  public static <T> T [] notEmptyNoNullValue (final T [] aValue, final String sName)
  {
    notEmpty (aValue, sName);
    noNullValue (aValue, sName);
    return aValue;
  }

  /**
   * Check that the passed collection is neither <code>null</code> nor empty and
   * that no <code>null</code> value is contained.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The collection to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value and never <code>null</code>.
   * @throws IllegalArgumentException
   *         if the passed value is empty or a <code>null</code> value is
   *         contained
   */
  @Nonnull
  public static <T extends Iterable <?>> T notEmptyNoNullValue (final T aValue, final String sName)
  {
    notEmpty (aValue, sName);
    noNullValue (aValue, sName);
    return aValue;
  }

  /**
   * Check that the passed map is neither <code>null</code> nor empty and that
   * no <code>null</code> value is contained.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The map to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @return The passed value and never <code>null</code>.
   * @throws IllegalArgumentException
   *         if the passed value is empty or a <code>null</code> value is
   *         contained
   */
  @Nonnull
  public static <T extends Map <?, ?>> T notEmptyNoNullValue (final T aValue, final String sName)
  {
    notEmpty (aValue, sName);
    noNullValue (aValue, sName);
    return aValue;
  }

  /**
   * Check that the passed value is not <code>null</code> and not equal to the
   * provided value.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The value to check. May not be <code>null</code>.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @param aUnexpectedValue
   *        The value that may not be equal to aValue. May not be
   *        <code>null</code>.
   * @return The passed value and never <code>null</code>.
   */
  @Nonnull
  public static <T> T notNullNotEquals (@Nonnull final T aValue, final String sName, @Nonnull final T aUnexpectedValue)
  {
    notNull (aValue, sName);
    notNull (aUnexpectedValue, "UnexpectedValue");
    if (aValue.equals (aUnexpectedValue))
      throw new IllegalArgumentException ("The value of '" + sName + "' may not be equal to " + aUnexpectedValue + "!");
    return aValue;
  }

  /**
   * Check that the passed value is not <code>null</code> and equal to the
   * provided expected value.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The value to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @param aExpectedValue
   *        The expected value. May not be <code>null</code>.
   * @return The passed value and never <code>null</code>.
   * @throws IllegalArgumentException
   *         if the passed value is not <code>null</code>.
   */
  @Nonnull
  public static <T> T notNullAndEquals (final T aValue, @Nonnull final String sName, @Nonnull final T aExpectedValue)
  {
    notNull (aValue, sName);
    if (!aValue.equals (aExpectedValue))
      throw new IllegalArgumentException ("The value of '" +
                                          sName +
                                          "' does not match the expected value. Passed value: " +
                                          aValue +
                                          " -- Expected value: " +
                                          aExpectedValue);
    return aValue;
  }

  /**
   * Check that the passed value is the same as the provided expected value
   * using <code>==</code> to check comparison.
   *
   * @param <T>
   *        Type to be checked and returned
   * @param aValue
   *        The value to check.
   * @param sName
   *        The name of the value (e.g. the parameter name)
   * @param aExpectedValue
   *        The expected value. May be <code>null</code>.
   * @return The passed value and maybe <code>null</code> if the expected value
   *         is null.
   * @throws IllegalArgumentException
   *         if the passed value is not <code>null</code>.
   */
  @Nullable
  public static <T> T isSame (final T aValue, @Nonnull final String sName, @Nullable final T aExpectedValue)
  {
    if (aValue != aExpectedValue)
      throw new IllegalArgumentException ("The value of '" +
                                          sName +
                                          "' does not match the expected value. Passed value: " +
                                          aValue +
                                          " -- Expected value: " +
                                          aExpectedValue);
    return aValue;
  }

  public static short isGE0 (final short nValue, final String sName)
  {
    if (nValue < 0)
      throw new IllegalArgumentException ("The value of '" + sName + "' must be >= 0! The current value is: " + nValue);
    return nValue;
  }

  public static int isGE0 (final int nValue, final String sName)
  {
    if (nValue < 0)
      throw new IllegalArgumentException ("The value of '" + sName + "' must be >= 0! The current value is: " + nValue);
    return nValue;
  }

  public static long isGE0 (final long nValue, final String sName)
  {
    if (nValue < 0)
      throw new IllegalArgumentException ("The value of '" + sName + "' must be >= 0! The current value is: " + nValue);
    return nValue;
  }

  public static double isGE0 (final double dValue, final String sName)
  {
    if (dValue < 0)
      throw new IllegalArgumentException ("The value of '" + sName + "' must be >= 0! The current value is: " + dValue);
    return dValue;
  }

  public static float isGE0 (final float fValue, final String sName)
  {
    if (fValue < 0)
      throw new IllegalArgumentException ("The value of '" + sName + "' must be >= 0! The current value is: " + fValue);
    return fValue;
  }

  @Nonnull
  public static BigDecimal isGE0 (@Nonnull final BigDecimal aValue, final String sName)
  {
    notNull (aValue, sName);
    if (aValue.compareTo (BigDecimal.ZERO) < 0)
      throw new IllegalArgumentException ("The value of '" + sName + "' must be >= 0! The current value is: " + aValue);
    return aValue;
  }

  @Nonnull
  public static BigInteger isGE0 (@Nonnull final BigInteger aValue, final String sName)
  {
    notNull (aValue, sName);
    if (aValue.compareTo (BigInteger.ZERO) < 0)
      throw new IllegalArgumentException ("The value of '" + sName + "' must be >= 0! The current value is: " + aValue);
    return aValue;
  }

  public static short isGT0 (final short nValue, final String sName)
  {
    if (nValue <= 0)
      throw new IllegalArgumentException ("The value of '" + sName + "' must be > 0! The current value is: " + nValue);
    return nValue;
  }

  public static int isGT0 (final int nValue, final String sName)
  {
    if (nValue <= 0)
      throw new IllegalArgumentException ("The value of '" + sName + "' must be > 0! The current value is: " + nValue);
    return nValue;
  }

  public static long isGT0 (final long nValue, final String sName)
  {
    if (nValue <= 0)
      throw new IllegalArgumentException ("The value of '" + sName + "' must be > 0! The current value is: " + nValue);
    return nValue;
  }

  public static double isGT0 (final double dValue, final String sName)
  {
    if (dValue <= 0)
      throw new IllegalArgumentException ("The value of '" + sName + "' must be > 0! The current value is: " + dValue);
    return dValue;
  }

  public static float isGT0 (final float fValue, final String sName)
  {
    if (fValue <= 0)
      throw new IllegalArgumentException ("The value of '" + sName + "' must be > 0! The current value is: " + fValue);
    return fValue;
  }

  @Nonnull
  public static BigDecimal isGT0 (@Nonnull final BigDecimal aValue, final String sName)
  {
    notNull (aValue, sName);
    if (aValue.compareTo (BigDecimal.ZERO) <= 0)
      throw new IllegalArgumentException ("The value of '" + sName + "' must be > 0! The current value is: " + aValue);
    return aValue;
  }

  @Nonnull
  public static BigInteger isGT0 (@Nonnull final BigInteger aValue, final String sName)
  {
    notNull (aValue, sName);
    if (aValue.compareTo (BigInteger.ZERO) <= 0)
      throw new IllegalArgumentException ("The value of '" + sName + "' must be > 0! The current value is: " + aValue);
    return aValue;
  }

  public static short isLE0 (final short nValue, final String sName)
  {
    if (nValue > 0)
      throw new IllegalArgumentException ("The value of '" + sName + "' must be <= 0! The current value is: " + nValue);
    return nValue;
  }

  public static int isLE0 (final int nValue, final String sName)
  {
    if (nValue > 0)
      throw new IllegalArgumentException ("The value of '" + sName + "' must be <= 0! The current value is: " + nValue);
    return nValue;
  }

  public static long isLE0 (final long nValue, final String sName)
  {
    if (nValue > 0)
      throw new IllegalArgumentException ("The value of '" + sName + "' must be <= 0! The current value is: " + nValue);
    return nValue;
  }

  public static double isLE0 (final double dValue, final String sName)
  {
    if (dValue > 0)
      throw new IllegalArgumentException ("The value of '" + sName + "' must be <= 0! The current value is: " + dValue);
    return dValue;
  }

  public static float isLE0 (final float fValue, final String sName)
  {
    if (fValue > 0)
      throw new IllegalArgumentException ("The value of '" + sName + "' must be <= 0! The current value is: " + fValue);
    return fValue;
  }

  @Nonnull
  public static BigDecimal isLE0 (@Nonnull final BigDecimal aValue, final String sName)
  {
    notNull (aValue, sName);
    if (aValue.compareTo (BigDecimal.ZERO) > 0)
      throw new IllegalArgumentException ("The value of '" + sName + "' must be <= 0! The current value is: " + aValue);
    return aValue;
  }

  @Nonnull
  public static BigInteger isLE0 (@Nonnull final BigInteger aValue, final String sName)
  {
    notNull (aValue, sName);
    if (aValue.compareTo (BigInteger.ZERO) > 0)
      throw new IllegalArgumentException ("The value of '" + sName + "' must be <= 0! The current value is: " + aValue);
    return aValue;
  }

  public static short isLT0 (final short nValue, final String sName)
  {
    if (nValue >= 0)
      throw new IllegalArgumentException ("The value of '" + sName + "' must be < 0! The current value is: " + nValue);
    return nValue;
  }

  public static int isLT0 (final int nValue, final String sName)
  {
    if (nValue >= 0)
      throw new IllegalArgumentException ("The value of '" + sName + "' must be < 0! The current value is: " + nValue);
    return nValue;
  }

  public static long isLT0 (final long nValue, final String sName)
  {
    if (nValue >= 0)
      throw new IllegalArgumentException ("The value of '" + sName + "' must be < 0! The current value is: " + nValue);
    return nValue;
  }

  public static double isLT0 (final double dValue, final String sName)
  {
    if (dValue >= 0)
      throw new IllegalArgumentException ("The value of '" + sName + "' must be < 0! The current value is: " + dValue);
    return dValue;
  }

  public static float isLT0 (final float fValue, final String sName)
  {
    if (fValue >= 0)
      throw new IllegalArgumentException ("The value of '" + sName + "' must be < 0! The current value is: " + fValue);
    return fValue;
  }

  @Nonnull
  public static BigDecimal isLT0 (@Nonnull final BigDecimal aValue, final String sName)
  {
    notNull (aValue, sName);
    if (aValue.compareTo (BigDecimal.ZERO) >= 0)
      throw new IllegalArgumentException ("The value of '" + sName + "' must be < 0! The current value is: " + aValue);
    return aValue;
  }

  @Nonnull
  public static BigInteger isLT0 (@Nonnull final BigInteger aValue, final String sName)
  {
    notNull (aValue, sName);
    if (aValue.compareTo (BigInteger.ZERO) >= 0)
      throw new IllegalArgumentException ("The value of '" + sName + "' must be < 0! The current value is: " + aValue);
    return aValue;
  }

  /**
   * Check if
   * <code>nValue &ge; nLowerBoundInclusive &amp;&amp; nValue &le; nUpperBoundInclusive</code>
   *
   * @param nValue
   *        Value
   * @param sName
   *        Name
   * @param nLowerBoundInclusive
   *        Lower bound
   * @param nUpperBoundInclusive
   *        Upper bound
   * @return The value
   */
  public static short isBetweenInclusive (final short nValue,
                                          final String sName,
                                          final short nLowerBoundInclusive,
                                          final short nUpperBoundInclusive)
  {
    if (nValue < nLowerBoundInclusive || nValue > nUpperBoundInclusive)
      throw new IllegalArgumentException ("The value of '" +
                                          sName +
                                          "' must be >= " +
                                          nLowerBoundInclusive +
                                          " and <= " +
                                          nUpperBoundInclusive +
                                          "! The current value is: " +
                                          nValue);
    return nValue;
  }

  /**
   * Check if
   * <code>nValue &ge; nLowerBoundInclusive &amp;&amp; nValue &le; nUpperBoundInclusive</code>
   *
   * @param nValue
   *        Value
   * @param sName
   *        Name
   * @param nLowerBoundInclusive
   *        Lower bound
   * @param nUpperBoundInclusive
   *        Upper bound
   * @return The value
   */
  public static int isBetweenInclusive (final int nValue,
                                        final String sName,
                                        final int nLowerBoundInclusive,
                                        final int nUpperBoundInclusive)
  {
    if (nValue < nLowerBoundInclusive || nValue > nUpperBoundInclusive)
      throw new IllegalArgumentException ("The value of '" +
                                          sName +
                                          "' must be >= " +
                                          nLowerBoundInclusive +
                                          " and <= " +
                                          nUpperBoundInclusive +
                                          "! The current value is: " +
                                          nValue);
    return nValue;
  }

  /**
   * Check if
   * <code>nValue &ge; nLowerBoundInclusive &amp;&amp; nValue &le; nUpperBoundInclusive</code>
   *
   * @param nValue
   *        Value
   * @param sName
   *        Name
   * @param nLowerBoundInclusive
   *        Lower bound
   * @param nUpperBoundInclusive
   *        Upper bound
   * @return The value
   */
  public static long isBetweenInclusive (final long nValue,
                                         final String sName,
                                         final long nLowerBoundInclusive,
                                         final long nUpperBoundInclusive)
  {
    if (nValue < nLowerBoundInclusive || nValue > nUpperBoundInclusive)
      throw new IllegalArgumentException ("The value of '" +
                                          sName +
                                          "' must be >= " +
                                          nLowerBoundInclusive +
                                          " and <= " +
                                          nUpperBoundInclusive +
                                          "! The current value is: " +
                                          nValue);
    return nValue;
  }

  /**
   * Check if
   * <code>nValue &ge; nLowerBoundInclusive &amp;&amp; nValue &le; nUpperBoundInclusive</code>
   *
   * @param fValue
   *        Value
   * @param sName
   *        Name
   * @param fLowerBoundInclusive
   *        Lower bound
   * @param fUpperBoundInclusive
   *        Upper bound
   * @return The value
   */
  public static float isBetweenInclusive (final float fValue,
                                          final String sName,
                                          final float fLowerBoundInclusive,
                                          final float fUpperBoundInclusive)
  {
    if (fValue < fLowerBoundInclusive || fValue > fUpperBoundInclusive)
      throw new IllegalArgumentException ("The value of '" +
                                          sName +
                                          "' must be >= " +
                                          fLowerBoundInclusive +
                                          " and <= " +
                                          fUpperBoundInclusive +
                                          "! The current value is: " +
                                          fValue);
    return fValue;
  }

  /**
   * Check if
   * <code>nValue &ge; nLowerBoundInclusive &amp;&amp; nValue &le; nUpperBoundInclusive</code>
   *
   * @param dValue
   *        Value
   * @param sName
   *        Name
   * @param dLowerBoundInclusive
   *        Lower bound
   * @param dUpperBoundInclusive
   *        Upper bound
   * @return The value
   */
  public static double isBetweenInclusive (final double dValue,
                                           final String sName,
                                           final double dLowerBoundInclusive,
                                           final double dUpperBoundInclusive)
  {
    if (dValue < dLowerBoundInclusive || dValue > dUpperBoundInclusive)
      throw new IllegalArgumentException ("The value of '" +
                                          sName +
                                          "' must be >= " +
                                          dLowerBoundInclusive +
                                          " and <= " +
                                          dUpperBoundInclusive +
                                          "! The current value is: " +
                                          dValue);
    return dValue;
  }

  /**
   * Check if
   * <code>nValue &ge; nLowerBoundInclusive &amp;&amp; nValue &le; nUpperBoundInclusive</code>
   *
   * @param aValue
   *        Value
   * @param sName
   *        Name
   * @param aLowerBoundInclusive
   *        Lower bound
   * @param aUpperBoundInclusive
   *        Upper bound
   * @return The value
   */
  public static BigDecimal isBetweenInclusive (@Nonnull final BigDecimal aValue,
                                               final String sName,
                                               @Nonnull final BigDecimal aLowerBoundInclusive,
                                               @Nonnull final BigDecimal aUpperBoundInclusive)
  {
    notNull (aValue, sName);
    notNull (aLowerBoundInclusive, "LowerBoundInclusive");
    notNull (aUpperBoundInclusive, "UpperBoundInclusive");
    if (aValue.compareTo (aLowerBoundInclusive) < 0 || aValue.compareTo (aUpperBoundInclusive) > 0)
      throw new IllegalArgumentException ("The value of '" +
                                          sName +
                                          "' must be >= " +
                                          aLowerBoundInclusive +
                                          " and <= " +
                                          aUpperBoundInclusive +
                                          "! The current value is: " +
                                          aValue);
    return aValue;
  }

  /**
   * Check if
   * <code>nValue &ge; nLowerBoundInclusive &amp;&amp; nValue &le; nUpperBoundInclusive</code>
   *
   * @param aValue
   *        Value
   * @param sName
   *        Name
   * @param aLowerBoundInclusive
   *        Lower bound
   * @param aUpperBoundInclusive
   *        Upper bound
   * @return The value
   */
  public static BigInteger isBetweenInclusive (@Nonnull final BigInteger aValue,
                                               final String sName,
                                               @Nonnull final BigInteger aLowerBoundInclusive,
                                               @Nonnull final BigInteger aUpperBoundInclusive)
  {
    notNull (aValue, sName);
    notNull (aLowerBoundInclusive, "LowerBoundInclusive");
    notNull (aUpperBoundInclusive, "UpperBoundInclusive");
    if (aValue.compareTo (aLowerBoundInclusive) < 0 || aValue.compareTo (aUpperBoundInclusive) > 0)
      throw new IllegalArgumentException ("The value of '" +
                                          sName +
                                          "' must be >= " +
                                          aLowerBoundInclusive +
                                          " and <= " +
                                          aUpperBoundInclusive +
                                          "! The current value is: " +
                                          aValue);
    return aValue;
  }

  /**
   * Check if
   * <code>nValue &gt; nLowerBoundInclusive &amp;&amp; nValue &lt; nUpperBoundInclusive</code>
   *
   * @param nValue
   *        Value
   * @param sName
   *        Name
   * @param nLowerBoundExclusive
   *        Lower bound
   * @param nUpperBoundExclusive
   *        Upper bound
   * @return The value
   */
  public static short isBetweenExclusive (final short nValue,
                                          final String sName,
                                          final short nLowerBoundExclusive,
                                          final short nUpperBoundExclusive)
  {
    if (nValue <= nLowerBoundExclusive || nValue >= nUpperBoundExclusive)
      throw new IllegalArgumentException ("The value of '" +
                                          sName +
                                          "' must be > " +
                                          nLowerBoundExclusive +
                                          " and < " +
                                          nUpperBoundExclusive +
                                          "! The current value is: " +
                                          nValue);
    return nValue;
  }

  /**
   * Check if
   * <code>nValue &gt; nLowerBoundInclusive &amp;&amp; nValue &lt; nUpperBoundInclusive</code>
   *
   * @param nValue
   *        Value
   * @param sName
   *        Name
   * @param nLowerBoundExclusive
   *        Lower bound
   * @param nUpperBoundExclusive
   *        Upper bound
   * @return The value
   */
  public static int isBetweenExclusive (final int nValue,
                                        final String sName,
                                        final int nLowerBoundExclusive,
                                        final int nUpperBoundExclusive)
  {
    if (nValue <= nLowerBoundExclusive || nValue >= nUpperBoundExclusive)
      throw new IllegalArgumentException ("The value of '" +
                                          sName +
                                          "' must be > " +
                                          nLowerBoundExclusive +
                                          " and < " +
                                          nUpperBoundExclusive +
                                          "! The current value is: " +
                                          nValue);
    return nValue;
  }

  /**
   * Check if
   * <code>nValue &gt; nLowerBoundInclusive &amp;&amp; nValue &lt; nUpperBoundInclusive</code>
   *
   * @param nValue
   *        Value
   * @param sName
   *        Name
   * @param nLowerBoundExclusive
   *        Lower bound
   * @param nUpperBoundExclusive
   *        Upper bound
   * @return The value
   */
  public static long isBetweenExclusive (final long nValue,
                                         final String sName,
                                         final long nLowerBoundExclusive,
                                         final long nUpperBoundExclusive)
  {
    if (nValue <= nLowerBoundExclusive || nValue >= nUpperBoundExclusive)
      throw new IllegalArgumentException ("The value of '" +
                                          sName +
                                          "' must be > " +
                                          nLowerBoundExclusive +
                                          " and < " +
                                          nUpperBoundExclusive +
                                          "! The current value is: " +
                                          nValue);
    return nValue;
  }

  /**
   * Check if
   * <code>nValue &gt; nLowerBoundInclusive &amp;&amp; nValue &lt; nUpperBoundInclusive</code>
   *
   * @param fValue
   *        Value
   * @param sName
   *        Name
   * @param fLowerBoundExclusive
   *        Lower bound
   * @param fUpperBoundExclusive
   *        Upper bound
   * @return The value
   */
  public static float isBetweenExclusive (final float fValue,
                                          final String sName,
                                          final float fLowerBoundExclusive,
                                          final float fUpperBoundExclusive)
  {
    if (fValue <= fLowerBoundExclusive || fValue >= fUpperBoundExclusive)
      throw new IllegalArgumentException ("The value of '" +
                                          sName +
                                          "' must be > " +
                                          fLowerBoundExclusive +
                                          " and < " +
                                          fUpperBoundExclusive +
                                          "! The current value is: " +
                                          fValue);
    return fValue;
  }

  /**
   * Check if
   * <code>nValue &gt; nLowerBoundInclusive &amp;&amp; nValue &lt; nUpperBoundInclusive</code>
   *
   * @param dValue
   *        Value
   * @param sName
   *        Name
   * @param dLowerBoundExclusive
   *        Lower bound
   * @param dUpperBoundExclusive
   *        Upper bound
   * @return The value
   */
  public static double isBetweenExclusive (final double dValue,
                                           final String sName,
                                           final double dLowerBoundExclusive,
                                           final double dUpperBoundExclusive)
  {
    if (dValue <= dLowerBoundExclusive || dValue >= dUpperBoundExclusive)
      throw new IllegalArgumentException ("The value of '" +
                                          sName +
                                          "' must be > " +
                                          dLowerBoundExclusive +
                                          " and < " +
                                          dUpperBoundExclusive +
                                          "! The current value is: " +
                                          dValue);
    return dValue;
  }

  /**
   * Check if
   * <code>nValue &gt; nLowerBoundInclusive &amp;&amp; nValue &lt; nUpperBoundInclusive</code>
   *
   * @param aValue
   *        Value
   * @param sName
   *        Name
   * @param aLowerBoundExclusive
   *        Lower bound
   * @param aUpperBoundExclusive
   *        Upper bound
   * @return The value
   */
  public static BigDecimal isBetweenExclusive (@Nonnull final BigDecimal aValue,
                                               final String sName,
                                               @Nonnull final BigDecimal aLowerBoundExclusive,
                                               @Nonnull final BigDecimal aUpperBoundExclusive)
  {
    notNull (aValue, sName);
    notNull (aLowerBoundExclusive, "LowerBoundInclusive");
    notNull (aUpperBoundExclusive, "UpperBoundInclusive");
    if (aValue.compareTo (aLowerBoundExclusive) <= 0 || aValue.compareTo (aUpperBoundExclusive) >= 0)
      throw new IllegalArgumentException ("The value of '" +
                                          sName +
                                          "' must be > " +
                                          aLowerBoundExclusive +
                                          " and < " +
                                          aUpperBoundExclusive +
                                          "! The current value is: " +
                                          aValue);
    return aValue;
  }

  /**
   * Check if
   * <code>nValue &gt; nLowerBoundInclusive &amp;&amp; nValue &lt; nUpperBoundInclusive</code>
   *
   * @param aValue
   *        Value
   * @param sName
   *        Name
   * @param aLowerBoundExclusive
   *        Lower bound
   * @param aUpperBoundExclusive
   *        Upper bound
   * @return The value
   */
  public static BigInteger isBetweenExclusive (@Nonnull final BigInteger aValue,
                                               final String sName,
                                               @Nonnull final BigInteger aLowerBoundExclusive,
                                               @Nonnull final BigInteger aUpperBoundExclusive)
  {
    notNull (aValue, sName);
    notNull (aLowerBoundExclusive, "LowerBoundInclusive");
    notNull (aUpperBoundExclusive, "UpperBoundInclusive");
    if (aValue.compareTo (aLowerBoundExclusive) <= 0 || aValue.compareTo (aUpperBoundExclusive) >= 0)
      throw new IllegalArgumentException ("The value of '" +
                                          sName +
                                          "' must be > " +
                                          aLowerBoundExclusive +
                                          " and < " +
                                          aUpperBoundExclusive +
                                          "! The current value is: " +
                                          aValue);
    return aValue;
  }

  private static void _isArrayOfsLen (@Nonnegative final int nArrayLen,
                                      @Nonnegative final int nOfs,
                                      @Nonnegative final int nLen)
  {
    isGE0 (nOfs, "Offset");
    isGE0 (nLen, "Length");
    if ((nOfs + nLen) > nArrayLen)
      throw new IllegalArgumentException ("Offset (" +
                                          nOfs +
                                          ") + length (" +
                                          nLen +
                                          ") exceeds array length (" +
                                          nArrayLen +
                                          ")");

  }

  public static void isArrayOfsLen (@Nonnull final Object [] aArray,
                                    @Nonnegative final int nOfs,
                                    @Nonnegative final int nLen)
  {
    notNull (aArray, "Array");
    _isArrayOfsLen (aArray.length, nOfs, nLen);
  }

  public static void isArrayOfsLen (@Nonnull final boolean [] aArray,
                                    @Nonnegative final int nOfs,
                                    @Nonnegative final int nLen)
  {
    notNull (aArray, "Array");
    _isArrayOfsLen (aArray.length, nOfs, nLen);
  }

  public static void isArrayOfsLen (@Nonnull final byte [] aArray,
                                    @Nonnegative final int nOfs,
                                    @Nonnegative final int nLen)
  {
    notNull (aArray, "Array");
    _isArrayOfsLen (aArray.length, nOfs, nLen);
  }

  public static void isArrayOfsLen (@Nonnull final char [] aArray,
                                    @Nonnegative final int nOfs,
                                    @Nonnegative final int nLen)
  {
    notNull (aArray, "Array");
    _isArrayOfsLen (aArray.length, nOfs, nLen);
  }

  public static void isArrayOfsLen (@Nonnull final double [] aArray,
                                    @Nonnegative final int nOfs,
                                    @Nonnegative final int nLen)
  {
    notNull (aArray, "Array");
    _isArrayOfsLen (aArray.length, nOfs, nLen);
  }

  public static void isArrayOfsLen (@Nonnull final float [] aArray,
                                    @Nonnegative final int nOfs,
                                    @Nonnegative final int nLen)
  {
    notNull (aArray, "Array");
    _isArrayOfsLen (aArray.length, nOfs, nLen);
  }

  public static void isArrayOfsLen (@Nonnull final int [] aArray,
                                    @Nonnegative final int nOfs,
                                    @Nonnegative final int nLen)
  {
    notNull (aArray, "Array");
    _isArrayOfsLen (aArray.length, nOfs, nLen);
  }

  public static void isArrayOfsLen (@Nonnull final long [] aArray,
                                    @Nonnegative final int nOfs,
                                    @Nonnegative final int nLen)
  {
    notNull (aArray, "Array");
    _isArrayOfsLen (aArray.length, nOfs, nLen);
  }

  public static void isArrayOfsLen (@Nonnull final short [] aArray,
                                    @Nonnegative final int nOfs,
                                    @Nonnegative final int nLen)
  {
    notNull (aArray, "Array");
    _isArrayOfsLen (aArray.length, nOfs, nLen);
  }
}
