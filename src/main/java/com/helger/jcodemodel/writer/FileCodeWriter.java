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

import com.helger.jcodemodel.AbstractCodeWriter;
import com.helger.jcodemodel.JPackage;

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
    this (aTargetDir, DEFAULT_MARK_READ_ONLY, DEFAULT_CHARSET, getDefaultNewLine ());
  }

  public FileCodeWriter (@Nonnull final File aTargetDir, @Nullable final Charset encoding) throws IOException
  {
    this (aTargetDir, DEFAULT_MARK_READ_ONLY, encoding, getDefaultNewLine ());
  }

  public FileCodeWriter (@Nonnull final File aTargetDir, final boolean bMarkReadOnly) throws IOException
  {
    this (aTargetDir, bMarkReadOnly, DEFAULT_CHARSET, getDefaultNewLine ());
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
    this (aTargetDir, bMarkReadOnly, aEncoding, getDefaultNewLine ());
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
  public OutputStream openBinary (@Nonnull final JPackage pkg, @Nonnull final String fileName) throws IOException
  {
    return new FileOutputStream (getFile (pkg, fileName));
  }

  @Nonnull
  protected File getFile (@Nonnull final JPackage pkg, @Nonnull final String fileName) throws IOException
  {
    File dir;
    if (pkg.isUnnamed ())
      dir = m_aTargetDir;
    else
      dir = new File (m_aTargetDir, _toDirName (pkg));

    if (!dir.exists ())
      dir.mkdirs ();

    final File fn = new File (dir, fileName);
    if (fn.exists ())
    {
      if (!fn.delete ())
        throw new IOException (fn + ": Can't delete previous version");
    }

    if (m_bMarkReadOnly)
      m_aReadOnlyFiles.add (fn);
    return fn;
  }

  @Override
  public void close () throws IOException
  {
    // mark files as read-only if necessary
    for (final File f : m_aReadOnlyFiles)
      f.setReadOnly ();
  }

  /** Converts a package name to the directory name. */
  @Nonnull
  private static String _toDirName (@Nonnull final JPackage aPkg)
  {
    return aPkg.name ().replace ('.', File.separatorChar);
  }
}
