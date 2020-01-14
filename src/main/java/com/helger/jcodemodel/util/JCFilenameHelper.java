/**
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

import java.io.File;
import java.nio.file.Path;
import java.util.Locale;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * All kind of file name handling stuff. This class gives you platform
 * independent file name handling.
 *
 * @author Philip Helger
 */
@Immutable
public final class JCFilenameHelper
{
  /** The file extension separation character. */
  public static final char EXTENSION_SEPARATOR = '.';

  /** The replacement character used for illegal file name characters. */
  public static final char ILLEGAL_FILENAME_CHAR_REPLACEMENT = '_';

  /** Special name of the current path */
  public static final String PATH_CURRENT = ".";

  /** Special name of the parent path */
  public static final String PATH_PARENT = "..";

  /** The Unix path separator character. */
  public static final char UNIX_SEPARATOR = '/';

  /** The Unix path separator string. */
  public static final String UNIX_SEPARATOR_STR = Character.toString (UNIX_SEPARATOR);

  /** The Windows separator character. */
  public static final char WINDOWS_SEPARATOR = '\\';

  /** The Windows separator string. */
  public static final String WINDOWS_SEPARATOR_STR = Character.toString (WINDOWS_SEPARATOR);

  /** The prefix to identify UNC paths on Unix based systems */
  public static final String UNIX_UNC_PREFIX = "//";

  /** The prefix to identify UNC paths on Windows based systems */
  public static final String WINDOWS_UNC_PREFIX = "\\\\";

  /** The prefix to identify local UNC paths on Windows based systems */
  public static final String WINDOWS_UNC_PREFIX_LOCAL1 = "\\\\.\\";
  /** The prefix to identify local UNC paths on Windows based systems */
  public static final String WINDOWS_UNC_PREFIX_LOCAL2 = "\\\\?\\";

  /** The prefix used for Unix hidden files */
  public static final char HIDDEN_FILE_PREFIX = '.';

  public static final boolean IS_WINDOWS = System.getProperty ("os.name").toLowerCase (Locale.US).contains ("windows");

  /**
   * Illegal characters in Windows file names.<br>
   * see http://en.wikipedia.org/wiki/Filename
   */
  private static final char [] ILLEGAL_CHARACTERS_WINDOWS = { 0, '<', '>', '?', '*', ':', '|', '"' };
  private static final char [] ILLEGAL_CHARACTERS_OTHERS = { 0, '<', '>', '?', '*', '|', '"' };
  // separate by OS - allow ":" as name part on Linux
  private static final char [] ILLEGAL_CHARACTERS = IS_WINDOWS ? ILLEGAL_CHARACTERS_WINDOWS : ILLEGAL_CHARACTERS_OTHERS;

  /**
   * see http://www.w3.org/TR/widgets/#zip-relative <br>
   * see http://forum.java.sun.com/thread.jspa?threadID=544334&tstart=165<br>
   * see http://en.wikipedia.org/wiki/Filename
   */
  private static final String [] ILLEGAL_PREFIXES = { "CLOCK$",
                                                      "CON",
                                                      "PRN",
                                                      "AUX",
                                                      "NUL",
                                                      "COM2",
                                                      "COM3",
                                                      "COM4",
                                                      "COM5",
                                                      "COM6",
                                                      "COM7",
                                                      "COM8",
                                                      "COM9",
                                                      "LPT1",
                                                      "LPT2",
                                                      "LPT3",
                                                      "LPT4",
                                                      "LPT5",
                                                      "LPT6",
                                                      "LPT7",
                                                      "LPT8",
                                                      "LPT9" };

  private static final char [] ILLEGAL_SUFFIXES = new char [] { '.', ' ', '\t' };

  static
  {
    if (!isSecureFilenameCharacter (ILLEGAL_FILENAME_CHAR_REPLACEMENT))
      throw new IllegalStateException ("The illegal filename replacement character must be a valid ASCII character!");
  }

  private JCFilenameHelper ()
  {}

  /**
   * Returns the index of the last extension separator character, which is a
   * dot.
   * <p>
   * This method also checks that there is no directory separator after the last
   * dot. To do this it uses {@link #getIndexOfLastSeparator(String)} which will
   * handle a file in either Unix or Windows format.
   * <p>
   * The output will be the same irrespective of the machine that the code is
   * running on.
   *
   * @param sFilename
   *        The filename to find the last path separator in. May be
   *        <code>null</code>.
   * @return the index of the last separator character, or -1 if there is no
   *         such character or the input parameter is <code>null</code>.
   * @see #getIndexOfLastSeparator(String)
   */
  public static int getIndexOfExtension (@Nullable final String sFilename)
  {
    if (sFilename == null)
      return -1;

    final int nExtensionIndex = sFilename.lastIndexOf (EXTENSION_SEPARATOR);
    final int nLastSepIndex = getIndexOfLastSeparator (sFilename);
    return nLastSepIndex > nExtensionIndex ? -1 : nExtensionIndex;
  }

  /**
   * Get the name of the passed file without the extension. If the file name
   * contains a leading absolute path, the path is returned as well.
   *
   * @param aFile
   *        The file to extract the extension from. May be <code>null</code>.
   * @return An empty string if no extension was found, the extension without
   *         the leading dot otherwise. If the input file is <code>null</code>
   *         the return value is <code>null</code>.
   * @see #getWithoutExtension(String)
   */
  @Nullable
  public static String getWithoutExtension (@Nullable final File aFile)
  {
    return aFile == null ? null : getWithoutExtension (aFile.getPath ());
  }

  /**
   * Get the passed filename without the extension. If the file name contains a
   * leading absolute path, the path is returned as well.
   *
   * @param sFilename
   *        The filename to extract the extension from. May be <code>null</code>
   *        or empty.
   * @return An empty string if no extension was found, the extension without
   *         the leading dot otherwise. If the input string is <code>null</code>
   *         the return value is <code>null</code>.
   * @see #getIndexOfExtension(String)
   */
  @Nullable
  public static String getWithoutExtension (@Nullable final String sFilename)
  {
    final int nIndex = getIndexOfExtension (sFilename);
    return nIndex == -1 ? sFilename : sFilename.substring (0, nIndex);
  }

  /**
   * Get the extension of the passed file.
   *
   * @param aFile
   *        The file to extract the extension from. May be <code>null</code>.
   * @return An empty string if no extension was found, the extension without
   *         the leading dot otherwise. Never <code>null</code>.
   * @see #getExtension(String)
   */
  @Nonnull
  public static String getExtension (@Nullable final File aFile)
  {
    return aFile == null ? "" : getExtension (aFile.getName ());
  }

  /**
   * Get the extension of the passed filename.
   *
   * @param sFilename
   *        The filename to extract the extension from. May be <code>null</code>
   *        or empty.
   * @return An empty string if no extension was found, the extension without
   *         the leading dot otherwise. Never <code>null</code>.
   * @see #getIndexOfExtension(String)
   */
  @Nonnull
  public static String getExtension (@Nullable final String sFilename)
  {
    final int nIndex = getIndexOfExtension (sFilename);
    if (nIndex == -1)
      return "";
    return sFilename.substring (nIndex + 1);
  }

  /**
   * Check if the passed file has one of the passed extensions. The comparison
   * is done case insensitive even on Unix machines.
   *
   * @param aFile
   *        The file to check the extension from. May be <code>null</code> or
   *        empty.
   * @param aExtensions
   *        An array of extensions (without the leading dot) which are matched
   *        case insensitive. May not be <code>null</code>.
   * @return <code>true</code> if the file has one of the passed extensions,
   *         else <code>false</code>.
   * @see #getExtension(File)
   */
  public static boolean hasExtension (@Nullable final File aFile, @Nonnull final String... aExtensions)
  {
    JCValueEnforcer.notNull (aExtensions, "Extensions");

    // determine current extension.
    final String sExt = getExtension (aFile);
    for (final String sExtension : aExtensions)
      if (sExt.equalsIgnoreCase (sExtension))
        return true;
    return false;
  }

  /**
   * Check if the passed filename has one of the passed extensions. The
   * comparison is done case insensitive even on Unix machines.
   *
   * @param sFilename
   *        The filename to check the extension from. May be <code>null</code>
   *        or empty.
   * @param aExtensions
   *        An array of extensions (without the leading dot) which are matched
   *        case insensitive. May not be <code>null</code>.
   * @return <code>true</code> if the filename has one of the passed extensions,
   *         else <code>false</code>.
   * @see #getExtension(String)
   */
  public static boolean hasExtension (@Nullable final String sFilename, @Nonnull final String... aExtensions)
  {
    JCValueEnforcer.notNull (aExtensions, "Extensions");

    // determine current extension.
    final String sExt = getExtension (sFilename);
    for (final String sExtension : aExtensions)
      if (sExt.equalsIgnoreCase (sExtension))
        return true;
    return false;
  }

  /**
   * Returns the index of the last directory separator character. This method
   * will handle a file in either Unix or Windows format. The position of the
   * last forward or backslash is returned. The output will be the same
   * irrespective of the machine that the code is running on.
   *
   * @param sFilename
   *        The filename to find the last path separator in, <code>null</code>
   *        returns -1.
   * @return The index of the last separator character, or -1 if there is no
   *         such character
   */
  public static int getIndexOfLastSeparator (@Nullable final String sFilename)
  {
    return sFilename == null ? -1 : Math.max (sFilename.lastIndexOf (UNIX_SEPARATOR),
                                              sFilename.lastIndexOf (WINDOWS_SEPARATOR));
  }

  /**
   * Get the name of the passed file without any eventually leading path. Note:
   * if the passed file is a directory, the name of the directory is returned.
   *
   * @param aFile
   *        The file. May be <code>null</code>.
   * @return The name only or <code>null</code> if the passed parameter is
   *         <code>null</code>.
   */
  @Nullable
  public static String getWithoutPath (@Nullable final File aFile)
  {
    return aFile == null ? null : aFile.getName ();
  }

  /**
   * Get the name of the passed file without any eventually leading path.
   *
   * @param sAbsoluteFilename
   *        The fully qualified file name. May be <code>null</code>.
   * @return The name only or <code>null</code> if the passed parameter is
   *         <code>null</code>.
   * @see #getIndexOfLastSeparator(String)
   */
  @Nullable
  public static String getWithoutPath (@Nullable final String sAbsoluteFilename)
  {
    /**
     * Note: do not use <code>new File (sFilename).getName ()</code> since this
     * only invokes the underlying FileSystem implementation which handles path
     * handling only correctly on the native platform. Problem arose when
     * running application on a Linux server and making a file upload from a
     * Windows machine.
     */
    if (sAbsoluteFilename == null)
      return null;
    final int nLastSepIndex = getIndexOfLastSeparator (sAbsoluteFilename);
    return nLastSepIndex == -1 ? sAbsoluteFilename : sAbsoluteFilename.substring (nLastSepIndex + 1);
  }

  /**
   * Get the path of the passed file name without any eventually contained
   * filename.
   *
   * @param sAbsoluteFilename
   *        The fully qualified file name. May be <code>null</code>.
   * @return The path only including the last trailing path separator character.
   *         Returns <code>null</code> if the passed parameter is
   *         <code>null</code>.
   * @see #getIndexOfLastSeparator(String)
   */
  @Nullable
  public static String getPath (@Nullable final String sAbsoluteFilename)
  {
    /**
     * Note: do not use <code>new File (sFilename).getPath ()</code> since this
     * only invokes the underlying FileSystem implementation which handles path
     * handling only correctly on the native platform. Problem arose when
     * running application on a Linux server and making a file upload from a
     * Windows machine.
     */
    if (sAbsoluteFilename == null)
      return null;
    final int nLastSepIndex = getIndexOfLastSeparator (sAbsoluteFilename);
    return nLastSepIndex == -1 ? "" : sAbsoluteFilename.substring (0, nLastSepIndex + 1);
  }

  /**
   * Get the passed filename without path and without extension.<br>
   * Example: <code>/dir1/dir2/file.txt</code> becomes <code>file</code>
   *
   * @param aFile
   *        The file to get the base name from. May be <code>null</code>.
   * @return The base name of the passed parameter. May be <code>null</code> if
   *         the parameter was <code>null</code>.
   * @see #getWithoutExtension(String)
   */
  @Nullable
  public static String getBaseName (@Nullable final File aFile)
  {
    return aFile == null ? null : getWithoutExtension (aFile.getName ());
  }

  /**
   * Get the passed filename without path and without extension.<br>
   * Example: <code>/dir1/dir2/file.txt</code> becomes <code>file</code>
   *
   * @param sAbsoluteFilename
   *        The filename to get the base name from. May be <code>null</code>.
   * @return The base name of the passed parameter. May be <code>null</code> if
   *         the parameter was <code>null</code>.
   * @see #getWithoutPath(String)
   * @see #getWithoutExtension(String)
   */
  @Nullable
  public static String getBaseName (@Nullable final String sAbsoluteFilename)
  {
    return getWithoutExtension (getWithoutPath (sAbsoluteFilename));
  }

  /**
   * Ensure that the path (not the absolute path!) of the passed file is using
   * the Unix style separator "/" instead of the Operating System dependent one.
   *
   * @param aFile
   *        The file to use. May be <code>null</code>
   * @return <code>null</code> if the passed file is <code>null</code>.
   * @see #getPathUsingUnixSeparator(String)
   */
  @Nullable
  public static String getPathUsingUnixSeparator (@Nullable final File aFile)
  {
    return aFile == null ? null : getPathUsingUnixSeparator (aFile.getPath ());
  }

  /**
   * Ensure that the passed path is using the Unix style separator "/" instead
   * of the Operating System dependent one.
   *
   * @param sAbsoluteFilename
   *        The file name to use. May be <code>null</code>
   * @return <code>null</code> if the passed path is <code>null</code>.
   * @see #getPathUsingUnixSeparator(File)
   */
  @Nullable
  public static String getPathUsingUnixSeparator (@Nullable final String sAbsoluteFilename)
  {
    return sAbsoluteFilename == null ? null
                                     : JCStringHelper.replaceAll (sAbsoluteFilename, WINDOWS_SEPARATOR, UNIX_SEPARATOR);
  }

  /**
   * Ensure that the path (not the absolute path!) of the passed file is using
   * the Windows style separator "\" instead of the Operating System dependent
   * one.
   *
   * @param aFile
   *        The file to use. May be <code>null</code>
   * @return <code>null</code> if the passed file is <code>null</code>.
   * @see #getPathUsingWindowsSeparator(String)
   */
  @Nullable
  public static String getPathUsingWindowsSeparator (@Nullable final File aFile)
  {
    return aFile == null ? null : getPathUsingWindowsSeparator (aFile.getPath ());
  }

  /**
   * Ensure that the passed path is using the Windows style separator "\"
   * instead of the Operating System dependent one.
   *
   * @param sAbsoluteFilename
   *        The file name to use. May be <code>null</code>
   * @return <code>null</code> if the passed path is <code>null</code>.
   * @see #getPathUsingWindowsSeparator(File)
   */
  @Nullable
  public static String getPathUsingWindowsSeparator (@Nullable final String sAbsoluteFilename)
  {
    return sAbsoluteFilename == null ? null
                                     : JCStringHelper.replaceAll (sAbsoluteFilename, UNIX_SEPARATOR, WINDOWS_SEPARATOR);
  }

  /**
   * Check whether the two passed file names are equal, independent of the used
   * separators (/ or \).
   *
   * @param sAbsoluteFilename1
   *        First file name. May be <code>null</code>.
   * @param sAbsoluteFilename2
   *        Second file name. May be <code>null</code>.
   * @return <code>true</code> if they are equal, <code>false</code> otherwise.
   * @see #getPathUsingUnixSeparator(String)
   */
  public static boolean isEqualIgnoreFileSeparator (@Nullable final String sAbsoluteFilename1,
                                                    @Nullable final String sAbsoluteFilename2)
  {
    return JCEqualsHelper.isEqual (getPathUsingUnixSeparator (sAbsoluteFilename1),
                                   getPathUsingUnixSeparator (sAbsoluteFilename2));
  }

  /**
   * Avoid 0 byte attack. E.g. file name "test.java\u0000.txt" is internally
   * represented as "test.java" but ends with ".txt".<br>
   * Note: the passed file name is <b>NOT</b> decoded (e.g. %20 stays %20 and
   * will not be converted to a space).
   *
   * @param sFilename
   *        The file name to check. May be <code>null</code>.
   * @return <code>null</code> if the input string is <code>null</code> or
   *         everything up to the 0-byte.
   */
  @Nullable
  public static String getSecureFilename (@Nullable final String sFilename)
  {
    if (sFilename == null)
      return null;
    final int nIdx0 = sFilename.indexOf ('\0');
    return nIdx0 == -1 ? sFilename : sFilename.substring (0, nIdx0);
  }

  /**
   * Check if the passed file name is valid. It checks for illegal prefixes that
   * affects compatibility to Windows, illegal characters within a filename and
   * forbidden suffixes. This method fits only for filenames on one level. If
   * you want to check a full path, use
   * {@link #isValidFilenameWithPaths(String)}.
   *
   * @param sFilename
   *        The filename to check. May be <code>null</code>.
   * @return <code>false</code> if the passed filename is <code>null</code> or
   *         empty or invalid. <code>true</code> if the filename is not empty
   *         and valid.
   * @see #containsPathSeparatorChar(String)
   */
  public static boolean isValidFilename (@Nullable final String sFilename)
  {
    // empty not allowed
    if (JCStringHelper.hasNoText (sFilename))
      return false;

    // path separator chars are not allowed in filenames!
    if (containsPathSeparatorChar (sFilename))
      return false;

    // check for illegal last characters
    if (JCStringHelper.endsWithAny (sFilename, ILLEGAL_SUFFIXES))
      return false;

    // Check if file name contains any of the illegal characters
    for (final char cIllegal : ILLEGAL_CHARACTERS)
      if (sFilename.indexOf (cIllegal) != -1)
        return false;

    // check prefixes directly
    for (final String sIllegalPrefix : ILLEGAL_PREFIXES)
      if (sFilename.equalsIgnoreCase (sIllegalPrefix))
        return false;

    // check if filename is prefixed with it
    // Note: we can use the default locale, since all fixed names are pure ANSI
    // names
    final String sUCFilename = sFilename.toUpperCase (Locale.ROOT);
    for (final String sIllegalPrefix : ILLEGAL_PREFIXES)
      if (sUCFilename.startsWith (sIllegalPrefix + "."))
        return false;

    return true;
  }

  /**
   * Check if the passed filename path is valid. In contrast to
   * {@link #isValidFilename(String)} this method can also handle filenames
   * including paths.
   *
   * @param sFilename
   *        The filename to be checked for validity.
   * @return <code>true</code> if all path elements of the filename are valid,
   *         <code>false</code> if at least one element is invalid
   * @see #isValidFilename(String)
   */
  public static boolean isValidFilenameWithPaths (@Nullable final String sFilename)
  {
    if (JCStringHelper.hasNoText (sFilename))
      return false;

    // Iterate filename path by path
    File aFile = new File (sFilename);
    while (aFile != null)
    {
      final String sCurFilename = aFile.getName ();
      final File aParentFile = aFile.getParentFile ();
      if (sCurFilename.length () == 0 && aParentFile == null)
      {
        // The last part of an absolute path can be skipped!
        break;
      }
      if (!isValidFilename (sCurFilename))
        return false;
      aFile = aParentFile;
    }
    return true;
  }

  /**
   * Convert the passed filename into a valid filename by performing the
   * following actions:
   * <ol>
   * <li>Remove everything after a potential \0 character</li>
   * <li>Remove all characters that are invalid at the end of a file name</li>
   * <li>Replace all characters that are invalid inside a filename with a
   * underscore</li>
   * <li>If the filename is invalid on Windows platforms it is prefixed with an
   * underscore.</li>
   * </ol>
   * Note: this method does not handle Windows full path like
   * "c:\autoexec.bat"<br>
   *
   * @param sFilename
   *        The filename to be made value. May be <code>null</code>.
   * @return <code>null</code> if the input filename was <code>null</code> or if
   *         it consisted only of characters invalid for a filename; the
   *         potentially modified filename otherwise but <b>never</b> an empy
   *         string.
   * @see #getSecureFilename(String)
   */
  @Nullable
  public static String getAsSecureValidFilename (@Nullable final String sFilename)
  {
    // First secure it, by cutting everything behind the '\0'
    String ret = getSecureFilename (sFilename);

    // empty not allowed
    if (JCStringHelper.hasText (ret))
    {
      // Remove all trailing invalid suffixes
      while (ret.length () > 0 && JCStringHelper.endsWithAny (ret, ILLEGAL_SUFFIXES))
        ret = ret.substring (0, ret.length () - 1);

      // Replace all characters that are illegal inside a filename
      for (final char cIllegal : ILLEGAL_CHARACTERS)
        ret = JCStringHelper.replaceAll (ret, cIllegal, ILLEGAL_FILENAME_CHAR_REPLACEMENT);

      // Check if a file matches an illegal prefix
      final String sTempRet = ret;
      if (JCArrayHelper.containsAny (ILLEGAL_PREFIXES, sTempRet::equalsIgnoreCase))
        ret = ILLEGAL_FILENAME_CHAR_REPLACEMENT + ret;

      // check if filename is prefixed with an illegal prefix
      // Note: we can use the default locale, since all fixed names are pure
      // ANSI names
      final String sUCFilename = ret.toUpperCase (Locale.ROOT);
      if (JCArrayHelper.containsAny (ILLEGAL_PREFIXES, x -> sUCFilename.startsWith (x + ".")))
        ret = ILLEGAL_FILENAME_CHAR_REPLACEMENT + ret;
    }

    // Avoid returning an empty string as valid file name
    return JCStringHelper.hasNoText (ret) ? null : ret;
  }

  /**
   * Check if the passed character is secure to be used in filenames. Therefore
   * it must be &ge; 0x20 and &lt; 0x80.
   *
   * @param c
   *        The character to check
   * @return <code>true</code> if it is valid, <code>false</code> if not
   */
  public static boolean isSecureFilenameCharacter (final char c)
  {
    return c >= 0x20 && c < 0x80;
  }

  /**
   * Replace all non-ASCII characters from the filename (e.g. German Umlauts)
   * with underscores. Before replacing non-ASCII characters the filename is
   * made valid using {@link #getAsSecureValidFilename(String)}.
   *
   * @param sFilename
   *        Input file name. May not be <code>null</code>.
   * @return <code>null</code> if the input filename was <code>null</code>. The
   *         file name containing only ASCII characters. The returned value is
   *         never an empty String.
   * @see #getAsSecureValidASCIIFilename(String, char)
   */
  @Nullable
  public static String getAsSecureValidASCIIFilename (@Nullable final String sFilename)
  {
    return getAsSecureValidASCIIFilename (sFilename, ILLEGAL_FILENAME_CHAR_REPLACEMENT);
  }

  /**
   * Replace all non-ASCII characters from the filename (e.g. German Umlauts)
   * with a replacement char. Before replacing non-ASCII characters the filename
   * is made valid using {@link #getAsSecureValidFilename(String)}.
   *
   * @param sFilename
   *        Input file name. May not be <code>null</code>.
   * @param cReplacementChar
   *        The replacement character to be used for insecure filenames.
   * @return <code>null</code> if the input filename was <code>null</code>. The
   *         file name containing only ASCII characters. The returned value is
   *         never an empty String.
   * @see #getAsSecureValidASCIIFilename(String)
   * @see #getAsSecureValidFilename(String)
   * @see #isSecureFilenameCharacter(char)
   */
  @Nullable
  public static String getAsSecureValidASCIIFilename (@Nullable final String sFilename, final char cReplacementChar)
  {
    // Make it valid according to the general rules
    final String sValid = getAsSecureValidFilename (sFilename);
    if (sValid == null)
      return null;

    // Start replacing all non-ASCII characters with '_'
    final StringBuilder ret = new StringBuilder (sValid.length ());
    for (final char c : sValid.toCharArray ())
      if (isSecureFilenameCharacter (c))
        ret.append (c);
      else
        ret.append (cReplacementChar);
    return ret.toString ();
  }

  /**
   * Check if the passed character is a path separation character. This method
   * handles both Windows- and Unix-style path separation characters.
   *
   * @param c
   *        The character to check.
   * @return <code>true</code> if the character is a path separation character,
   *         <code>false</code> otherwise.
   */
  public static boolean isPathSeparatorChar (final char c)
  {
    return c == UNIX_SEPARATOR || c == WINDOWS_SEPARATOR;
  }

  /**
   * Check if the passed character sequence starts with a path separation
   * character.
   *
   * @param s
   *        The character sequence to check. May be <code>null</code> or empty.
   * @return <code>true</code> if the character sequences starts with a Windows-
   *         or Unix-style path character.
   * @see #isPathSeparatorChar(char)
   */
  public static boolean startsWithPathSeparatorChar (@Nullable final CharSequence s)
  {
    return isPathSeparatorChar (JCStringHelper.getFirstChar (s));
  }

  /**
   * Check if the passed character sequence ends with a path separation
   * character.
   *
   * @param s
   *        The character sequence to check. May be <code>null</code> or empty.
   * @return <code>true</code> if the character sequences ends with a Windows-
   *         or Unix-style path character.
   * @see #isPathSeparatorChar(char)
   */
  public static boolean endsWithPathSeparatorChar (@Nullable final CharSequence s)
  {
    return isPathSeparatorChar (JCStringHelper.getLastChar (s));
  }

  /**
   * Check if the passed String contains at least one path separator char
   * (either Windows or Unix style).
   *
   * @param s
   *        The string to check. May be <code>null</code>.
   * @return <code>true</code> if the passed string is not <code>null</code> and
   *         contains at least one separator.
   */
  public static boolean containsPathSeparatorChar (@Nullable final String s)
  {
    // This is a tick faster than iterating the s.toCharArray() chars
    return s != null && (s.indexOf (UNIX_SEPARATOR) >= 0 || s.indexOf (WINDOWS_SEPARATOR) >= 0);
  }

  /**
   * Check if the passed file is a system directory. A system directory is
   * either {@value #PATH_CURRENT} or {@value #PATH_PARENT}.
   *
   * @param aFile
   *        The file to be checked. May be <code>null</code>.
   * @return <code>true</code> if the passed file name (not the path) matches
   *         any of the special directory names, <code>false</code> of the
   *         passed file is <code>null</code> or does not denote a special
   *         directory.
   * @see #isSystemInternalDirectory(CharSequence)
   */
  public static boolean isSystemInternalDirectory (@Nullable final File aFile)
  {
    return aFile != null && isSystemInternalDirectory (aFile.getName ());
  }

  /**
   * Check if the passed file is a system directory. A system directory is
   * either {@value #PATH_CURRENT} or {@value #PATH_PARENT}.
   *
   * @param aFile
   *        The file to be checked. May be <code>null</code>.
   * @return <code>true</code> if the passed file name (not the path) matches
   *         any of the special directory names, <code>false</code> of the
   *         passed file is <code>null</code> or does not denote a special
   *         directory.
   * @see #isSystemInternalDirectory(CharSequence)
   */
  public static boolean isSystemInternalDirectory (@Nullable final Path aFile)
  {
    if (aFile == null)
      return false;
    final Path aPureFile = aFile.getFileName ();
    return aPureFile != null && isSystemInternalDirectory (aPureFile.toString ());
  }

  /**
   * Check if the passed string is a system directory. A system directory is
   * either {@value #PATH_CURRENT} or {@value #PATH_PARENT}.
   *
   * @param s
   *        The value to be checked. May be <code>null</code>.
   * @return <code>true</code> if the passed string matches any of the special
   *         directory names, <code>false</code> of the passed string is
   *         <code>null</code> or does not denote a special directory.
   * @see #isSystemInternalDirectory(File)
   */
  public static boolean isSystemInternalDirectory (@Nullable final CharSequence s)
  {
    return s != null && (s.equals (PATH_CURRENT) || s.equals (PATH_PARENT));
  }

  /**
   * Check if the passed file is an UNC path. UNC paths are identified by
   * starting with "//" or "\\".
   *
   * @param aFile
   *        The file to be checked. May not be <code>null</code>.
   * @return <code>true</code> if the file points to an UNC path,
   *         <code>false</code> if not.
   * @see #isUNCPath(String)
   */
  public static boolean isUNCPath (@Nonnull final File aFile)
  {
    final String sPath = aFile.getAbsolutePath ();
    return isUNCPath (sPath);
  }

  /**
   * Check if the passed file is an UNC path. UNC paths are identified by
   * starting with "//" or "\\".
   *
   * @param sFilename
   *        The absolute filename to be checked. May not be <code>null</code>.
   * @return <code>true</code> if the file points to an UNC path,
   *         <code>false</code> if not.
   * @see #isUNCPath(File)
   */
  public static boolean isUNCPath (@Nonnull final String sFilename)
  {
    return sFilename.startsWith (WINDOWS_UNC_PREFIX) || sFilename.startsWith (UNIX_UNC_PREFIX);
  }

  /**
   * Check if the passed file is a Windows local UNC path. This type is
   * identified by starting with "\\?\" or "\\.\".
   *
   * @param aFile
   *        The file to be checked. May not be <code>null</code>.
   * @return <code>true</code> if the file points to an UNC path,
   *         <code>false</code> if not.
   * @see #isWindowsLocalUNCPath(String)
   */
  public static boolean isWindowsLocalUNCPath (@Nonnull final File aFile)
  {
    final String sPath = aFile.getAbsolutePath ();
    return isWindowsLocalUNCPath (sPath);
  }

  /**
   * Check if the passed file is a Windows local UNC path. This type is
   * identified by starting with "\\?\" or "\\.\".
   *
   * @param sFilename
   *        The absolute filename to be checked. May not be <code>null</code>.
   * @return <code>true</code> if the file points to a Windows local UNC path,
   *         <code>false</code> if not.
   * @see #isWindowsLocalUNCPath(File)
   */
  public static boolean isWindowsLocalUNCPath (@Nonnull final String sFilename)
  {
    return sFilename.startsWith (WINDOWS_UNC_PREFIX_LOCAL1) || sFilename.startsWith (WINDOWS_UNC_PREFIX_LOCAL2);
  }

  /**
   * Ensure that the passed path starts with a directory separator character. If
   * the passed path starts with either {@value #WINDOWS_SEPARATOR} or
   * {@value #UNIX_SEPARATOR} no changes are performed.
   *
   * @param sPath
   *        The path to be checked.
   * @return The path that is ensured to start with the directory separator of
   *         the current operating system.
   * @see #startsWithPathSeparatorChar(CharSequence)
   */
  @Nullable
  @CheckReturnValue
  public static String ensurePathStartingWithSeparator (@Nullable final String sPath)
  {
    if (sPath == null)
      return null;
    return startsWithPathSeparatorChar (sPath) ? sPath : File.separator + sPath;
  }

  /**
   * Ensure that the passed path does NOT end with a directory separator
   * character. Any number of trailing {@value #WINDOWS_SEPARATOR} or
   * {@value #UNIX_SEPARATOR} are removed.
   *
   * @param sPath
   *        The path to be checked.
   * @return The path that is ensured to NOT end with the directory separator.
   * @see #endsWithPathSeparatorChar(CharSequence)
   */
  @Nullable
  @CheckReturnValue
  public static String ensurePathEndingWithoutSeparator (@Nullable final String sPath)
  {
    if (sPath == null)
      return null;

    String sRet = sPath;
    while (endsWithPathSeparatorChar (sRet))
      sRet = sRet.substring (0, sRet.length () - 1);
    return sRet;
  }

  /**
   * Ensure that the passed path ends with a directory separator character. If
   * the passed path ends with either {@value #WINDOWS_SEPARATOR} or
   * {@value #UNIX_SEPARATOR} no changes are performed.
   *
   * @param sPath
   *        The path to be checked.
   * @return The path that is ensured to end with the directory separator of the
   *         current operating system.
   * @see #endsWithPathSeparatorChar(CharSequence)
   */
  @Nullable
  @CheckReturnValue
  public static String ensurePathEndingWithSeparator (@Nullable final String sPath)
  {
    if (sPath == null)
      return null;
    return endsWithPathSeparatorChar (sPath) ? sPath : sPath + File.separator;
  }

  /**
   * Check if the passed filename is a Unix hidden filename.
   *
   * @param aFile
   *        The file to check. May be <code>null</code>.
   * @return <code>true</code> if the file is not <code>null</code> and the name
   *         starts with a dot.
   * @see #isHiddenFilename(String)
   */
  public static boolean isHiddenFilename (@Nullable final File aFile)
  {
    return aFile != null && isHiddenFilename (aFile.getName ());
  }

  /**
   * Check if the passed filename is a Unix hidden filename.
   *
   * @param sFilename
   *        The filename to check. May be <code>null</code>.
   * @return <code>true</code> if the filename is neither <code>null</code> nor
   *         empty and starts with a dot.
   * @see #isHiddenFilename(File)
   */
  public static boolean isHiddenFilename (@Nullable final String sFilename)
  {
    return JCStringHelper.hasText (sFilename) && sFilename.charAt (0) == HIDDEN_FILE_PREFIX;
  }
}
