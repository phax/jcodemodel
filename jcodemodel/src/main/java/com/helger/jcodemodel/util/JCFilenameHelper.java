/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2026 Philip Helger + contributors
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

import org.jspecify.annotations.Nullable;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.string.StringHelper;
import com.helger.io.file.FilenameHelper;

/**
 * All kind of file name handling stuff. This class gives you platform independent file name
 * handling.
 *
 * @author Philip Helger
 */
@Immutable
public final class JCFilenameHelper
{
  /**
   * Illegal characters in Windows file names.<br>
   * see http://en.wikipedia.org/wiki/Filename
   */
  private static final char [] WINDOWS_ILLEGAL_CHARACTERS = { 0, '<', '>', '?', '*', ':', '|', '"' };
  /** Modern Linux accept all chars. Test e.g. with <code>touch "*"</code> */
  private static final char [] UNIX_ILLEGAL_CHARACTERS = { 0 };

  /**
   * see http://www.w3.org/TR/widgets/#zip-relative <br>
   * see http://forum.java.sun.com/thread.jspa?threadID=544334&tstart=165<br>
   * see http://en.wikipedia.org/wiki/Filename
   */
  private static final String [] WINDOWS_ILLEGAL_PREFIXES = { "CLOCK$",
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

  private static final char [] WINDOWS_ILLEGAL_SUFFIXES = { '.', ' ', '\t' };

  private JCFilenameHelper ()
  {}

  /**
   * Check if the passed file name is valid. It checks for illegal characters within a filename.
   * This method fits only for filenames on one level.
   *
   * @param sFilename
   *        The filename to check. May be <code>null</code>.
   * @return <code>false</code> if the passed filename is <code>null</code> or empty or invalid.
   *         <code>true</code> if the filename is not empty and valid.
   */
  public static boolean isValidLinuxFilename (@Nullable final String sFilename)
  {
    // empty not allowed
    if (StringHelper.isEmpty (sFilename))
      return false;

    // path separator chars are not allowed in filenames!
    if (FilenameHelper.containsPathSeparatorChar (sFilename))
      return false;

    // Check for reserved directories
    if (FilenameHelper.PATH_CURRENT.equals (sFilename) || FilenameHelper.PATH_PARENT.equals (sFilename))
      return false;

    // Check if file name contains any of the illegal characters
    for (final char cIllegal : UNIX_ILLEGAL_CHARACTERS)
      if (sFilename.indexOf (cIllegal) != -1)
        return false;

    return true;
  }

  /**
   * Check if the passed file name is valid. It checks for illegal prefixes that affects
   * compatibility to Windows, illegal characters within a filename and forbidden suffixes. This
   * method fits only for filenames on one level.
   *
   * @param sFilename
   *        The filename to check. May be <code>null</code>.
   * @return <code>false</code> if the passed filename is <code>null</code> or empty or invalid.
   *         <code>true</code> if the filename is not empty and valid.
   */
  public static boolean isValidWindowsFilename (@Nullable final String sFilename)
  {
    // empty not allowed
    if (StringHelper.isEmpty (sFilename))
      return false;

    // path separator chars are not allowed in filenames!
    if (FilenameHelper.containsPathSeparatorChar (sFilename))
      return false;

    // Check for reserved directories
    if (FilenameHelper.PATH_CURRENT.equals (sFilename) || FilenameHelper.PATH_PARENT.equals (sFilename))
      return false;

    // check for illegal last characters
    if (StringHelper.endsWithAny (sFilename, WINDOWS_ILLEGAL_SUFFIXES))
      return false;

    // Check if file name contains any of the illegal characters
    for (final char cIllegal : WINDOWS_ILLEGAL_CHARACTERS)
      if (sFilename.indexOf (cIllegal) != -1)
        return false;

    // check prefixes directly
    for (final String sIllegalPrefix : WINDOWS_ILLEGAL_PREFIXES)
      if (sFilename.equalsIgnoreCase (sIllegalPrefix))
        return false;

    // check if filename is prefixed with it
    // Note: we can use the default locale, since all fixed names are pure ANSI
    // names
    final String sUCFilename = sFilename.toUpperCase (Locale.ROOT);
    for (final String sIllegalPrefix : WINDOWS_ILLEGAL_PREFIXES)
      if (sUCFilename.startsWith (sIllegalPrefix + "."))
        return false;

    return true;
  }
}
