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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Nonnull;

import com.helger.jcodemodel.AbstractCodeWriter;
import com.helger.jcodemodel.JPackage;

/**
 * Writes all the files into a zip file.
 *
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class ZipCodeWriter extends AbstractCodeWriter
{
  private final ZipOutputStream m_aZOS;
  private final OutputStream m_aFOS;

  /**
   * @param target
   *        Zip file will be written to this stream.
   */
  public ZipCodeWriter (@Nonnull final OutputStream target)
  {
    this (target, getDefaultNewLine ());
  }

  /**
   * @param target
   *        Zip file will be written to this stream.
   * @param sNewLine
   *        The new line string to be used for source files
   */
  public ZipCodeWriter (@Nonnull final OutputStream target, @Nonnull final String sNewLine)
  {
    super (null, sNewLine);
    m_aZOS = new ZipOutputStream (target);
    // nullify the close method.
    m_aFOS = new FilterOutputStream (m_aZOS)
    {
      @Override
      public void close ()
      {
        // Do not close
      }
    };
  }

  @Override
  public OutputStream openBinary (@Nonnull final JPackage pkg, @Nonnull final String fileName) throws IOException
  {
    String name = fileName;
    if (!pkg.isUnnamed ())
      name = _toDirName (pkg) + name;

    m_aZOS.putNextEntry (new ZipEntry (name));
    return m_aFOS;
  }

  /** Converts a package name to the directory name. */
  private static String _toDirName (@Nonnull final JPackage pkg)
  {
    return pkg.name ().replace ('.', '/') + '/';
  }

  @Override
  public void close () throws IOException
  {
    m_aZOS.close ();
  }
}
