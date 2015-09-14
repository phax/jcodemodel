/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2015 Philip Helger
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
/**
 * Output all source files into a single stream.
 *
 * This is primarily for test purposes.
 *
 * @author
 * 	Aleksei Valikov (valikov@gmx.net)
 */
package com.helger.jcodemodel.writer;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.jcodemodel.AbstractCodeWriter;
import com.helger.jcodemodel.JPackage;

public class OutputStreamCodeWriter extends AbstractCodeWriter
{
  private final PrintStream m_aPS;

  /**
   * @param os
   *        This stream will be closed at the end of the code generation.
   */
  public OutputStreamCodeWriter (@Nonnull final OutputStream os, @Nullable final Charset encoding)
  {
    super (encoding);
    try
    {
      m_aPS = encoding == null ? new PrintStream (os, false) : new PrintStream (os, false, encoding.name ());
    }
    catch (final UnsupportedEncodingException ueex)
    {
      throw new IllegalArgumentException (ueex);
    }
  }

  @Override
  public OutputStream openBinary (@Nonnull final JPackage pkg, @Nonnull final String fileName) throws IOException
  {
    return new FilterOutputStream (m_aPS)
    {
      @Override
      public void close ()
      {
        // don't let this stream close
      }
    };
  }

  @Override
  public void close () throws IOException
  {
    m_aPS.close ();
  }
}
