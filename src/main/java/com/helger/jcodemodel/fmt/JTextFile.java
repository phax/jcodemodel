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
package com.helger.jcodemodel.fmt;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;

import com.helger.jcodemodel.ChangeInV4;
import com.helger.jcodemodel.util.JCValueEnforcer;

/**
 * Simple text file.
 *
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class JTextFile extends AbstractJResourceFile
{
  private String m_sContents;
  private final Charset m_aEncoding;

  public JTextFile (@Nonnull final String sName, @Nonnull final Charset aEncoding)
  {
    super (sName);
    m_aEncoding = JCValueEnforcer.notNull (aEncoding, "Encoding");
  }

  /**
   * @return The encoding as provided in the constructor. Never
   *         <code>null</code>.
   */
  @Nonnull
  public final Charset encoding ()
  {
    return m_aEncoding;
  }

  /**
   * @return The content of the text file. <code>null</code> by default.
   */
  @Nullable
  public String contents ()
  {
    return m_sContents;
  }

  /**
   * Set the contents to be written. Overwrites a previous content.
   *
   * @param sContents
   *        The contents to be used. May be <code>null</code>.
   */
  @ChangeInV4 ("return this")
  public void setContents (@Nullable final String sContents)
  {
    m_sContents = sContents;
  }

  @Override
  public void build (@Nonnull @WillNotClose final OutputStream aOS) throws IOException
  {
    if (m_sContents != null)
      try (final Writer w = new OutputStreamWriter (aOS, m_aEncoding)
      {
        @Override
        public void close ()
        {
          // Don't close underlying OS
        }
      })
      {
        w.write (m_sContents);
        w.flush ();
      }
  }

  @Nonnull
  public static JTextFile createFully (@Nonnull final String sName,
                                       @Nonnull final Charset aEncoding,
                                       @Nullable final String sContents)
  {
    final JTextFile ret = new JTextFile (sName, aEncoding);
    ret.setContents (sContents);
    return ret;
  }
}
