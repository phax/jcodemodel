/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
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

/*
 * @(#)SingleByteEncoder.java	1.14 03/01/23
 */

package com.helger.jcodemodel.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

import sun.nio.cs.Surrogate;

abstract class SingleByteEncoder extends CharsetEncoder
{

  private final short index1[];
  private final String index2;
  private final int mask1;
  private final int mask2;
  private final int shift;

  private final Surrogate.Parser sgp = new Surrogate.Parser ();

  protected SingleByteEncoder (final Charset cs,
                               final short [] index1,
                               final String index2,
                               final int mask1,
                               final int mask2,
                               final int shift)
  {
    super (cs, 1.0f, 1.0f);
    this.index1 = index1;
    this.index2 = index2;
    this.mask1 = mask1;
    this.mask2 = mask2;
    this.shift = shift;
  }

  @Override
  public boolean canEncode (final char c)
  {
    char testEncode;
    testEncode = index2.charAt (index1[(c & mask1) >> shift] + (c & mask2));
    if (testEncode == '\u0000')
      return false;
    else
      return true;
  }

  private CoderResult encodeArrayLoop (final CharBuffer src, final ByteBuffer dst)
  {
    final char [] sa = src.array ();
    int sp = src.arrayOffset () + src.position ();
    final int sl = src.arrayOffset () + src.limit ();
    sp = (sp <= sl ? sp : sl);
    final byte [] da = dst.array ();
    int dp = dst.arrayOffset () + dst.position ();
    final int dl = dst.arrayOffset () + dst.limit ();
    dp = (dp <= dl ? dp : dl);

    try
    {
      while (sp < sl)
      {
        final char c = sa[sp];
        if (Surrogate.is (c))
        {
          if (sgp.parse (c, sa, sp, sl) < 0)
            return sgp.error ();
          return sgp.unmappableResult ();
        }
        if (c >= '\uFFFE')
          return CoderResult.unmappableForLength (1);
        if (dl - dp < 1)
          return CoderResult.OVERFLOW;

        final char e = index2.charAt (index1[(c & mask1) >> shift] + (c & mask2));

        // If output byte is zero because input char is zero
        // then character is mappable, o.w. fail
        if (e == '\u0000' && c != '\u0000')
          return CoderResult.unmappableForLength (1);

        sp++;
        da[dp++] = (byte) e;
      }
      return CoderResult.UNDERFLOW;
    }
    finally
    {
      src.position (sp - src.arrayOffset ());
      dst.position (dp - dst.arrayOffset ());
    }
  }

  private CoderResult encodeBufferLoop (final CharBuffer src, final ByteBuffer dst)
  {
    int mark = src.position ();
    try
    {
      while (src.hasRemaining ())
      {
        final char c = src.get ();
        if (Surrogate.is (c))
        {
          if (sgp.parse (c, src) < 0)
            return sgp.error ();
          return sgp.unmappableResult ();
        }
        if (c >= '\uFFFE')
          return CoderResult.unmappableForLength (1);
        if (!dst.hasRemaining ())
          return CoderResult.OVERFLOW;

        final char e = index2.charAt (index1[(c & mask1) >> shift] + (c & mask2));

        // If output byte is zero because input char is zero
        // then character is mappable, o.w. fail
        if (e == '\u0000' && c != '\u0000')
          return CoderResult.unmappableForLength (1);

        mark++;
        dst.put ((byte) e);
      }
      return CoderResult.UNDERFLOW;
    }
    finally
    {
      src.position (mark);
    }
  }

  @Override
  protected CoderResult encodeLoop (final CharBuffer src, final ByteBuffer dst)
  {
    if (true && src.hasArray () && dst.hasArray ())
      return encodeArrayLoop (src, dst);
    else
      return encodeBufferLoop (src, dst);
  }

  public byte encode (final char inputChar)
  {
    return (byte) index2.charAt (index1[(inputChar & mask1) >> shift] + (inputChar & mask2));
  }
}
