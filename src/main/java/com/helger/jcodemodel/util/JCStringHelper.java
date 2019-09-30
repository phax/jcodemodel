package com.helger.jcodemodel.util;

import javax.annotation.CheckForSigned;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class JCStringHelper
{
  /** Constant empty String array */
  public static final String [] EMPTY_STRING_ARRAY = new String [0];

  private JCStringHelper ()
  {}

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
}
