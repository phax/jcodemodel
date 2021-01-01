/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2021 Philip Helger + contributors
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

import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.BitSet;

import javax.annotation.Nonnull;

import com.helger.jcodemodel.util.UnicodeEscapeWriter;

/**
 * Special {@link UnicodeEscapeWriter} with less characters to encode.
 * 
 * @author Philip Helger
 */
public class JavaUnicodeEscapeWriter extends UnicodeEscapeWriter
{
  private static final BitSet ESCAPE = new BitSet (128);

  static
  {
    for (int i = 0; i < 0x20; i++)
      if (i != '\t' && i != '\r' && i != '\n')
        ESCAPE.set (i, true);
  }

  // can't change this signature to Encoder because
  // we can't have Encoder in method signature
  private final CharsetEncoder m_aEncoder;

  public JavaUnicodeEscapeWriter (@Nonnull final OutputStreamWriter bw)
  {
    super (bw);
    m_aEncoder = Charset.forName (bw.getEncoding ()).newEncoder ();
  }

  @Override
  protected boolean requireEscaping (final int ch)
  {
    // control characters
    if (ESCAPE.get (ch))
      return true;

    // check ASCII chars, for better performance
    if (ch < 0x80)
      return false;

    return !m_aEncoder.canEncode ((char) ch);
  }
}
