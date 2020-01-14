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
package com.helger.jcodemodel.writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.jcodemodel.util.JCStringHelper;

/**
 * Writes all the source files under the specified file folder.
 *
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class FileCodeWriter extends AbstractCodeWriter
{
  public static final boolean DEFAULT_MARK_READ_ONLY = false;
  public static final Charset DEFAULT_CHARSET = null;

  /** The target directory to put source code. */
  private final File m_aTargetDir;

  /** specify whether or not to mark the generated files read-only */
  private final boolean m_bMarkReadOnly;

  /** Files that shall be marked as read only. */
  private final Set <File> m_aReadOnlyFiles = new HashSet <> ();

  public FileCodeWriter (@Nonnull final File aTargetDir) throws IOException
  {
    this (aTargetDir, DEFAULT_MARK_READ_ONLY, DEFAULT_CHARSET, JCMWriter.getDefaultNewLine ());
  }

  public FileCodeWriter (@Nonnull final File aTargetDir, @Nullable final Charset aEncoding) throws IOException
  {
    this (aTargetDir, DEFAULT_MARK_READ_ONLY, aEncoding, JCMWriter.getDefaultNewLine ());
  }

  public FileCodeWriter (@Nonnull final File aTargetDir, final boolean bMarkReadOnly) throws IOException
  {
    this (aTargetDir, bMarkReadOnly, DEFAULT_CHARSET, JCMWriter.getDefaultNewLine ());
  }

  public FileCodeWriter (@Nonnull final File aTargetDir,
                         @Nullable final Charset aEncoding,
                         @Nonnull final String sNewLine) throws IOException
  {
    this (aTargetDir, DEFAULT_MARK_READ_ONLY, aEncoding, sNewLine);
  }

  public FileCodeWriter (@Nonnull final File aTargetDir,
                         final boolean bMarkReadOnly,
                         @Nullable final Charset aEncoding) throws IOException
  {
    this (aTargetDir, bMarkReadOnly, aEncoding, JCMWriter.getDefaultNewLine ());
  }

  public FileCodeWriter (@Nonnull final File aTargetDir,
                         final boolean bMarkReadOnly,
                         @Nullable final Charset aEncoding,
                         @Nonnull final String sNewLine) throws IOException
  {
    super (aEncoding, sNewLine);
    m_aTargetDir = aTargetDir;
    m_bMarkReadOnly = bMarkReadOnly;
    if (!aTargetDir.exists () || !aTargetDir.isDirectory ())
      throw new IOException (aTargetDir + ": non-existent directory");
  }

  @Override
  @Nonnull
  public OutputStream openBinary (@Nonnull final String sDirName, @Nonnull final String sFilename) throws IOException
  {
    return new FileOutputStream (getFile (sDirName, sFilename));
  }

  @Nonnull
  protected File getFile (@Nonnull final String sDirName, @Nonnull final String sFilename) throws IOException
  {
    final File aDir;
    if (JCStringHelper.hasNoText (sDirName))
      aDir = m_aTargetDir;
    else
      aDir = new File (m_aTargetDir, sDirName);

    if (!aDir.exists ())
      if (!aDir.mkdirs ())
        throw new IOException (aDir + ": failed to create directory");

    final File aFile = new File (aDir, sFilename);
    if (aFile.exists ())
    {
      if (!aFile.delete ())
        throw new IOException (aFile + ": Can't delete previous version");
    }

    if (m_bMarkReadOnly)
      m_aReadOnlyFiles.add (aFile);
    return aFile;
  }

  @Override
  public void close () throws IOException
  {
    // mark files as read-only if necessary
    for (final File f : m_aReadOnlyFiles)
      if (!f.setReadOnly ())
        throw new IOException (f + ": Can't make file read-only");
  }
}
