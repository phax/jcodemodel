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
package com.helger.jcodemodel;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

import javax.annotation.Nonnull;

/**
 * A special version of {@link java.io.PrintWriter} that has a customizable new
 * line string.
 *
 * @author Philip Helger
 */
public final class SourcePrintWriter extends FilterWriter
{
  private final String m_sNewLine;

  public SourcePrintWriter (@Nonnull final Writer aWrappedWriter, @Nonnull final String sNewLine)
  {
    super (aWrappedWriter);
    m_sNewLine = sNewLine;
  }

  private void _handleException (@Nonnull final IOException ex, @Nonnull final String sSource)
  {
    System.err.println ("Error on Writer: " + sSource);
    ex.printStackTrace ();
  }

  private void _write (final char c)
  {
    try
    {
      super.write (c);
    }
    catch (final IOException ex)
    {
      _handleException (ex, "write char");
    }
  }

  private void _write (@Nonnull final String sStr)
  {
    try
    {
      super.write (sStr, 0, sStr.length ());
    }
    catch (final IOException ex)
    {
      _handleException (ex, "write String");
    }
  }

  public void print (final char c)
  {
    _write (c);
  }

  public void print (@Nonnull final String sStr)
  {
    _write (sStr);
  }

  public void println ()
  {
    _write (m_sNewLine);
  }

  public void println (final String sStr)
  {
    _write (sStr);
    _write (m_sNewLine);
  }

  @Override
  public void close ()
  {
    try
    {
      super.close ();
    }
    catch (final IOException ex)
    {
      _handleException (ex, "close");
    }
  }
}
