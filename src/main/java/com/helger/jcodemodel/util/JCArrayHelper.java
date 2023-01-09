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
package com.helger.jcodemodel.util;

import java.util.function.Predicate;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;

public final class JCArrayHelper
{
  private JCArrayHelper ()
  {}

  /**
   * @param aArray
   *        The array who's size is to be queried. May be <code>null</code>.
   * @return 0 if the passed array is <code>null</code> - it's length otherwise.
   */
  @Nonnegative
  public static int getSize (@Nullable final char... aArray)
  {
    return aArray == null ? 0 : aArray.length;
  }

  /**
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aArray
   *        The array who's size is to be queried. May be <code>null</code>.
   * @return 0 if the passed array is <code>null</code> - it's length otherwise.
   */
  @Nonnegative
  @SafeVarargs
  public static <ELEMENTTYPE> int getSize (@Nullable final ELEMENTTYPE... aArray)
  {
    return aArray == null ? 0 : aArray.length;
  }

  /**
   * @param <ELEMENTTYPE>
   *        Array element type
   * @param aArray
   *        The array to be queried if it is empty. May be <code>null</code>.
   * @return <code>false</code> if the passed array is <code>null</code> or
   *         empty.
   */
  @SafeVarargs
  public static <ELEMENTTYPE> boolean isNotEmpty (@Nullable final ELEMENTTYPE... aArray)
  {
    return getSize (aArray) > 0;
  }

  /**
   * Get the index of the passed search value in the passed value array.
   *
   * @param aValues
   *        The value array to be searched. May be <code>null</code>.
   * @param aSearchValue
   *        The value to be searched. May be <code>null</code>.
   * @return <code>-1</code> if the searched value is not contained, a value
   *         &ge; 0 otherwise.
   */
  public static int getFirstIndex (@Nullable final char [] aValues, final char aSearchValue)
  {
    final int nLength = getSize (aValues);
    if (nLength > 0)
      for (int nIndex = 0; nIndex < nLength; ++nIndex)
        if (JCEqualsHelper.isEqual (aValues[nIndex], aSearchValue))
          return nIndex;
    return -1;
  }

  /**
   * Check if the passed search value is contained in the passed value array.
   *
   * @param aValues
   *        The value array to be searched. May be <code>null</code>.
   * @param aSearchValue
   *        The value to be searched. May be <code>null</code>.
   * @return <code>true</code> if the value array is not empty and the search
   *         value is contained - false otherwise.
   */
  public static boolean contains (@Nullable final char [] aValues, final char aSearchValue)
  {
    return getFirstIndex (aValues, aSearchValue) >= 0;
  }

  public static <ELEMENTTYPE> boolean containsAny (@Nullable final ELEMENTTYPE [] aArray,
                                                   @Nullable final Predicate <? super ELEMENTTYPE> aFilter)
  {
    if (aFilter == null)
      return isNotEmpty (aArray);

    if (isNotEmpty (aArray))
      for (final ELEMENTTYPE aElement : aArray)
        if (aFilter.test (aElement))
          return true;
    return false;
  }
}
