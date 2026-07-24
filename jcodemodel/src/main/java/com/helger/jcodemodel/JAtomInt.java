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
package com.helger.jcodemodel;

import static com.helger.jcodemodel.util.JCHashCodeGenerator.getHashCode;

import java.util.function.IntFunction;

import org.jspecify.annotations.NonNull;

import com.helger.base.equals.EqualsHelper;

/**
 * A special atom for int values
 */
public class JAtomInt implements IJExpression
{

  /// @see https://docs.oracle.com/javase/specs/jls/se17/html/jls-3.html#jls-3.10.1
  public static enum Representation
  {
    BINARY ("0b", Integer::toBinaryString),
    DECIMAL ("", Integer::toString)
    {
      @Override
      protected String pad (@NonNull String body, int qtty)
      {
        return body;
      }
    },
    HEX ("0x", Integer::toHexString),
    OCTAL ("0", Integer::toOctalString);

    @NonNull
    final IntFunction <String> representer;

    @NonNull
    final String prefix;

    Representation (String prefix, IntFunction <String> representer)
    {
      this.prefix = prefix;
      this.representer = representer;
    }

    public String represent (int i, int sepEvery, int sepSize, int padding)
    {
      boolean neg = i < 0;
      i = neg ? -i : i;
      StringBuilder sb = new StringBuilder ();
      if (neg)
        sb.append ('-');
      sb.append (prefix);
      addSep (pad (representer.apply (i), padding), sepEvery, sepSize, sb);
      return sb.toString ();
    }

    /// @param source unsigned non-prefixed representation , eg a5 for -0xa5 .
    static void addSep (@NonNull String source, int sepEvery, int sepSize, StringBuilder sb)
    {
      if (sepEvery < 1 || sepEvery >= source.length () || sepSize < 1)
      {
        sb.append (source);
        return;
      }
      String sep = "_".repeat (sepSize);
      for (int start = 0, end = source.length () % sepEvery; end <= source.length (); start = end, end += sepEvery)
      {
        if (start != 0)
          sb.append (sep);
        sb.append (source.substring (start, end));
      }
    }

    protected String pad (@NonNull String body, int qtty)
    {
      if (qtty <= body.length ())
        return body;
      return "0".repeat (qtty - body.length ()) + body;
    }
  }

  private final int m_nValue;

  @NonNull
  private Representation representation = Representation.DECIMAL;

  protected JAtomInt (final int nWhat)
  {
    m_nValue = nWhat;
  }

  public JAtomInt representation (Representation representation)
  {
    if (representation != null)
      this.representation = representation;
    return this;
  }

  public JAtomInt binary ()
  {
    return representation (Representation.BINARY);
  }

  public JAtomInt decimal ()
  {
    return representation (Representation.DECIMAL);
  }

  public JAtomInt hex ()
  {
    return representation (Representation.HEX);
  }

  public JAtomInt octal ()
  {
    return representation (Representation.OCTAL);
  }

  public int what ()
  {
    return m_nValue;
  }

  /// how many underscores per separation
  private int separatorSize = 1;

  public int separatorSize ()
  {
    return separatorSize;
  }

  public JAtomInt separatorSize (int size)
  {
    this.separatorSize = size;
    return this;
  }

  /// how many character before underscore separation
  private int separateEvery = 0;

  public int separateEvery ()
  {
    return separateEvery;
  }

  public JAtomInt separateEvery (int every)
  {
    this.separateEvery = every;
    return this;
  }

  /// how many character minimum must the body have. ignored for decimal representation.
  ///
  /// for example, the binary representation for 0 with padding 4 is 0b0000 .
  private int padding = 0;

  public int padding ()
  {
    return padding;
  }

  public JAtomInt padding (int padding)
  {
    this.padding = padding;
    return this;
  }

  public void generate (@NonNull final IJFormatter f)
  {
    f.print (representation.represent (m_nValue, separateEvery, separatorSize, padding));
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || getClass () != o.getClass ())
      return false;
    final JAtomInt rhs = (JAtomInt) o;
    return EqualsHelper.equals (m_nValue, rhs.m_nValue);
  }

  @Override
  public int hashCode ()
  {
    return getHashCode (this, Integer.valueOf (m_nValue));
  }
}
