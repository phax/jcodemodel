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

import java.util.Locale;

import javax.annotation.CheckForSigned;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class JCStringHelper
{
  /** Constant empty String array */
  public static final String [] EMPTY_STRING_ARRAY = new String [0];

  /**
   * The constant to be returned if an String.indexOf call did not find a match!
   */
  public static final int STRING_NOT_FOUND = -1;

  /**
   * Represents an illegal character (\0).
   */
  public static final char ILLEGAL_CHAR = '\0';

  private JCStringHelper ()
  {}

  /**
   * Get the length of the passed character sequence.
   *
   * @param aCS
   *        The character sequence who's length is to be determined. May be
   *        <code>null</code>.
   * @return 0 if the parameter is <code>null</code>, its length otherwise.
   * @see CharSequence#length()
   */
  @Nonnegative
  public static int getLength (@Nullable final CharSequence aCS)
  {
    return aCS == null ? 0 : aCS.length ();
  }

  /**
   * Check if the string contains any char.
   *
   * @param aCS
   *        The character sequence to check. May be <code>null</code>.
   * @return <code>true</code> if the string contains at least one,
   *         <code>false</code> otherwise
   */
  public static boolean hasText (@Nullable final CharSequence aCS)
  {
    return aCS != null && aCS.length () > 0;
  }

  /**
   * Check if the string is <code>null</code> or empty.
   *
   * @param sStr
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the string is <code>null</code> or empty,
   *         <code>false</code> otherwise
   */
  public static boolean hasNoText (@Nullable final String sStr)
  {
    return sStr == null || sStr.isEmpty ();
  }

  @Nonnegative
  public static int getCharCount (@Nullable final String s, final char cSearch)
  {
    return s == null ? 0 : getCharCount (s.toCharArray (), cSearch);
  }

  @Nonnegative
  public static int getCharCount (@Nullable final char [] aChars, final char cSearch)
  {
    int ret = 0;
    if (aChars != null)
      for (final char c : aChars)
        if (c == cSearch)
          ++ret;
    return ret;
  }

  /**
   * Take a concatenated String and return the passed String array of all
   * elements in the passed string, using specified separator char.
   *
   * @param cSep
   *        The separator to use.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or
   *        empty.
   * @return The passed collection and never <code>null</code>.
   */
  @Nonnull
  public static String [] getExplodedArray (final char cSep, @Nullable final String sElements)
  {
    return getExplodedArray (cSep, sElements, -1);
  }

  /**
   * Take a concatenated String and return the passed String array of all
   * elements in the passed string, using specified separator char.
   *
   * @param cSep
   *        The separator to use.
   * @param sElements
   *        The concatenated String to convert. May be <code>null</code> or
   *        empty.
   * @param nMaxItems
   *        The maximum number of items to explode. If the passed value is &le;
   *        0 all items are used. If max items is 1, than the result string is
   *        returned as is. If max items is larger than the number of elements
   *        found, it has no effect.
   * @return The passed collection and never <code>null</code>.
   */
  @Nonnull
  public static String [] getExplodedArray (final char cSep,
                                            @Nullable final String sElements,
                                            @CheckForSigned final int nMaxItems)
  {
    if (nMaxItems == 1)
      return new String [] { sElements };
    if (hasNoText (sElements))
      return EMPTY_STRING_ARRAY;

    final int nMaxResultElements = 1 + getCharCount (sElements, cSep);
    if (nMaxResultElements == 1)
    {
      // Separator not found
      return new String [] { sElements };
    }
    final String [] ret = new String [nMaxItems < 1 ? nMaxResultElements : Math.min (nMaxResultElements, nMaxItems)];

    // Do not use RegExCache.stringReplacePattern because of package
    // dependencies
    // Do not use String.split because it trims empty tokens from the end
    int nStartIndex = 0;
    int nItemsAdded = 0;
    while (true)
    {
      final int nMatchIndex = sElements.indexOf (cSep, nStartIndex);
      if (nMatchIndex < 0)
        break;

      ret[nItemsAdded++] = sElements.substring (nStartIndex, nMatchIndex);
      // 1 == length of separator char
      nStartIndex = nMatchIndex + 1;
      if (nMaxItems > 0 && nItemsAdded == nMaxItems - 1)
      {
        // We have exactly one item the left: the rest of the string
        break;
      }
    }
    ret[nItemsAdded++] = sElements.substring (nStartIndex);
    if (nItemsAdded != ret.length)
      throw new IllegalStateException ("Added " + nItemsAdded + " but expected " + ret.length);
    return ret;
  }

  /**
   * This is a fast replacement for {@link String#replace(char, char)} for
   * characters. The problem with the mentioned String method is, that is uses
   * internally regular expressions which use a synchronized block to compile
   * the patterns. This method is inherently thread safe since {@link String} is
   * immutable and we're operating on different temporary {@link StringBuilder}
   * objects.
   *
   * @param sInputString
   *        The input string where the text should be replace. If this parameter
   *        is <code>null</code> or empty, no replacement is done.
   * @param cSearchChar
   *        The character to be replaced.
   * @param cReplacementChar
   *        The character with the replacement.
   * @return The input string as is, if the input string is empty or if the
   *         search pattern and the replacement are equal or if the string to be
   *         replaced is not contained.
   */
  @Nullable
  public static String replaceAll (@Nullable final String sInputString,
                                   final char cSearchChar,
                                   final char cReplacementChar)
  {
    // Is input string empty?
    if (hasNoText (sInputString))
      return sInputString;

    // Replace old with the same new?
    if (cSearchChar == cReplacementChar)
      return sInputString;

    // Does the old text occur anywhere?
    int nIndex = sInputString.indexOf (cSearchChar, 0);
    if (nIndex == STRING_NOT_FOUND)
      return sInputString;

    // build output buffer
    final StringBuilder ret = new StringBuilder (sInputString.length ());
    int nOldIndex = 0;
    do
    {
      ret.append (sInputString, nOldIndex, nIndex).append (cReplacementChar);
      nIndex++;
      nOldIndex = nIndex;
      nIndex = sInputString.indexOf (cSearchChar, nIndex);
    } while (nIndex != STRING_NOT_FOUND);
    ret.append (sInputString, nOldIndex, sInputString.length ());
    return ret.toString ();
  }

  /**
   * Just calls <code>replaceAll</code> as long as there are still replacements
   * found
   *
   * @param sInputString
   *        The input string where the text should be replace. If this parameter
   *        is <code>null</code> or empty, no replacement is done.
   * @param sSearchText
   *        The string to be replaced. May neither be <code>null</code> nor
   *        empty.
   * @param sReplacementText
   *        The string with the replacement. May not be <code>null</code> but
   *        may be empty.
   * @return The input string as is, if the input string is empty or if the
   *         string to be replaced is not contained.
   */
  @Nullable
  public static String replaceAllRepeatedly (@Nullable final String sInputString,
                                             @Nonnull final String sSearchText,
                                             @Nonnull final String sReplacementText)
  {
    JCValueEnforcer.notEmpty (sSearchText, "SearchText");
    JCValueEnforcer.notNull (sReplacementText, "ReplacementText");
    JCValueEnforcer.isFalse (sReplacementText.contains (sSearchText),
                             "Loop detection: replacementText must not contain searchText");

    // Is input string empty?
    if (hasNoText (sInputString))
      return sInputString;

    String sRet = sInputString;
    String sLastLiteral;
    do
    {
      sLastLiteral = sRet;
      sRet = sRet.replace (sSearchText, sReplacementText);
    } while (!sLastLiteral.equals (sRet));
    return sRet;
  }

  /**
   * Get the first character of the passed character sequence
   *
   * @param aCS
   *        The source character sequence
   * @return {@link #ILLEGAL_CHAR} if the passed sequence was empty
   */
  public static char getFirstChar (@Nullable final CharSequence aCS)
  {
    return hasText (aCS) ? aCS.charAt (0) : ILLEGAL_CHAR;
  }

  /**
   * Get the last character of the passed character sequence
   *
   * @param aCS
   *        The source character sequence
   * @return {@link #ILLEGAL_CHAR} if the passed sequence was empty
   */
  public static char getLastChar (@Nullable final CharSequence aCS)
  {
    final int nLength = getLength (aCS);
    return nLength > 0 ? aCS.charAt (nLength - 1) : ILLEGAL_CHAR;
  }

  public static boolean endsWithAny (@Nullable final CharSequence aCS, @Nullable final char [] aChars)
  {
    if (hasText (aCS) && aChars != null)
      if (JCArrayHelper.contains (aChars, getLastChar (aCS)))
        return true;
    return false;
  }

  public static boolean endsWithCaseInsensitive (@Nullable final String sSrc, @Nullable final String sExpectedEnd)
  {
    return hasText (sSrc) &&
           hasText (sExpectedEnd) &&
           sSrc.toUpperCase (Locale.ROOT).endsWith (sExpectedEnd.toUpperCase (Locale.ROOT));
  }
}
