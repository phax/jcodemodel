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

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.jcodemodel.AbstractCodeWriter;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.SourcePrintWriter;

/**
 * Writes all the source files under the specified file folder and inserts a
 * file prolog comment in each java source file.
 *
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class PrologCodeWriter extends FilterCodeWriter
{
  /** prolog comment */
  private final String m_sProlog;

  /**
   * @param aCore
   *        This CodeWriter will be used to actually create a storage for files.
   *        PrologCodeWriter simply decorates this underlying CodeWriter by
   *        adding prolog comments.
   * @param prolog
   *        Strings that will be added as comments. This string may contain
   *        newlines to produce multi-line comments. '//' will be inserted at
   *        the beginning of each line to make it a valid Java comment, so the
   *        caller can just pass strings like "abc\ndef"
   */
  public PrologCodeWriter (@Nonnull final AbstractCodeWriter aCore, @Nullable final String prolog)
  {
    super (aCore);
    m_sProlog = prolog;
  }

  @Override
  public SourcePrintWriter openSource (@Nonnull final JPackage pkg, @Nonnull final String fileName) throws IOException
  {
    final SourcePrintWriter w = super.openSource (pkg, fileName);

    // write prolog if this is a java source file
    if (m_sProlog != null)
    {
      w.println ("//");
      String s = m_sProlog;
      int idx;
      while ((idx = s.indexOf ('\n')) != -1)
      {
        w.println ("// " + s.substring (0, idx));
        s = s.substring (idx + 1);
      }
      w.println ("//");
      w.println ();
    }

    return w;
  }
}
