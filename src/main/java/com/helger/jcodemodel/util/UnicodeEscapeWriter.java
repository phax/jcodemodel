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
package com.helger.jcodemodel.util;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.BitSet;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * {@link Writer} that escapes non US-ASCII characters into Java Unicode escape
 * \\uXXXX. This process is necessary if the method names or field names contain
 * non US-ASCII characters.
 *
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class UnicodeEscapeWriter extends FilterWriter
{
  private static final BitSet ESCAPE = new BitSet (128);

  static
  {
    for (int i = 0; i < 0x20; i++)
      if (i != '\t' && i != '\r' && i != '\n')
        ESCAPE.set (i, true);
  }

  public UnicodeEscapeWriter (@Nonnull final Writer aNext)
  {
    super (aNext);
  }

  /**
   * Check if a character needs escaping or not. Can be overridden in
   * subclasses.
   *
   * @param ch
   *        Character to check
   * @return <code>true</code> if the character needs to be escaped.
   */
  protected boolean requireEscaping (final int ch)
  {
    // control characters
    return ch >= 128 || ESCAPE.get (ch);
  }

  @Override
  public final void write (final int ch) throws IOException
  {
    if (requireEscaping (ch))
    {
      // need to escape
      out.write ("\\u");
      final String s = Integer.toHexString (ch);
      // Leading spaces
      for (int i = s.length (); i < 4; i++)
        out.write ('0');
      out.write (s);
    }
    else
      out.write (ch);
  }

  @Override
  public final void write (@Nonnull final char [] aBuf,
                           @Nonnegative final int nOfs,
                           @Nonnegative final int nLen) throws IOException
  {
    final int nMax = nOfs + nLen;
    for (int i = nOfs; i < nMax; i++)
      write (aBuf[i]);
  }

  @Override
  public final void write (@Nonnull final char [] aBuf) throws IOException
  {
    write (aBuf, 0, aBuf.length);
  }

  @Override
  public final void write (@Nonnull final String sStr,
                           @Nonnegative final int nOfs,
                           @Nonnegative final int nLen) throws IOException
  {
    write (sStr.toCharArray (), nOfs, nLen);
  }

  @Override
  public final void write (@Nonnull final String sStr) throws IOException
  {
    write (sStr.toCharArray (), 0, sStr.length ());
  }
}
