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
import java.util.Locale;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.jcodemodel.ChangeInV4;

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

  @ChangeInV4
  static boolean isFileSystemCaseSensitive ()
  {
    try
    {
      // let the system property override, in case the user really
      // wants to override.
      if (System.getProperty ("com.sun.codemodel.FileSystemCaseSensitive") != null)
      {
        System.err.println ("Dear JCodeModel user: the currently defined system property 'com.sun.codemodel.FileSystemCaseSensitive' will not be evaluated in the upcoming v4. Use JCodeMode.setFileSystemConvention instead.");
        return true;
      }

      // Add special override to differentiate if Sun implementation is also in
      // scope
      if (System.getProperty ("com.helger.jcodemodel.FileSystemCaseSensitive") != null)
      {
        System.err.println ("Dear JCodeModel user: the currently defined system property 'com.helger.jcodemodel.FileSystemCaseSensitive' will not be evaluated in the upcoming v4. Use JCodeMode.setFileSystemConvention instead.");
        return true;
      }
    }
    catch (final Exception e)
    {
      // Fall through
    }

    // on Unix, it's case sensitive.
    return File.separatorChar == '/';
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
   * Check if the passed file name is valid. It checks for illegal characters
   * within a filename. This method fits only for filenames on one level.
   *
   * @param sFilename
   *        The filename to check. May be <code>null</code>.
   * @return <code>false</code> if the passed filename is <code>null</code> or
   *         empty or invalid. <code>true</code> if the filename is not empty
   *         and valid.
   */
  public static boolean isValidLinuxFilename (@Nullable final String sFilename)
  {
    // empty not allowed
    if (JCStringHelper.hasNoText (sFilename))
      return false;

    // path separator chars are not allowed in filenames!
    if (containsPathSeparatorChar (sFilename))
      return false;

    // Check for reserved directories
    if (PATH_CURRENT.equals (sFilename) || PATH_PARENT.equals (sFilename))
      return false;

    // Check if file name contains any of the illegal characters
    for (final char cIllegal : ILLEGAL_CHARACTERS_OTHERS)
      if (sFilename.indexOf (cIllegal) != -1)
        return false;

    return true;
  }

  /**
   * Check if the passed file name is valid. It checks for illegal prefixes that
   * affects compatibility to Windows, illegal characters within a filename and
   * forbidden suffixes. This method fits only for filenames on one level.
   *
   * @param sFilename
   *        The filename to check. May be <code>null</code>.
   * @return <code>false</code> if the passed filename is <code>null</code> or
   *         empty or invalid. <code>true</code> if the filename is not empty
   *         and valid.
   */
  public static boolean isValidWindowsFilename (@Nullable final String sFilename)
  {
    // empty not allowed
    if (JCStringHelper.hasNoText (sFilename))
      return false;

    // path separator chars are not allowed in filenames!
    if (containsPathSeparatorChar (sFilename))
      return false;

    // Check for reserved directories
    if (PATH_CURRENT.equals (sFilename) || PATH_PARENT.equals (sFilename))
      return false;

    // check for illegal last characters
    if (JCStringHelper.endsWithAny (sFilename, ILLEGAL_SUFFIXES))
      return false;

    // Check if file name contains any of the illegal characters
    for (final char cIllegal : ILLEGAL_CHARACTERS_WINDOWS)
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
}
