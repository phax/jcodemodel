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
package com.helger.jcodemodel.writer.settings;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.jcodemodel.writer.JCMWriter;

///
///  recommended way to set indentation is to call #useSpaces or  #useTabs ; but can directly set the string.
///
public class Indent
{
  // Variable name is used externally
  public String string = JCMWriter.DEFAULT_INDENT_STRING;

  /// how many spaces do we consider a tab to take
  // Variable name is used externally
  public int tabSize = 4;

  public Indent ()
  {}

  @NonNull
  public Indent useSpaces (@Nonnegative final int nb)
  {
    string = " ".repeat (nb);
    return this;
  }

  /// defaults to 4 spaces
  @NonNull
  public Indent useSpaces ()
  {
    return useSpaces (4);
  }

  @NonNull
  public Indent useTabs (@Nonnegative final int nb)
  {
    string = "\t".repeat (nb);
    return this;
  }

  /// defaults to 1 tab
  @NonNull
  public Indent useTabs ()
  {
    return useTabs (1);
  }

  @NonNull
  public String string ()
  {
    return string;
  }

  @NonNull
  public Indent withString (@Nullable final String str)
  {
    if (str == null || str.isEmpty ())
    {
      this.string = "";
    }
    else
    {
      this.string = str;
    }
    return this;
  }

  @NonNull
  public Indent tabSize (@Nonnegative final int size)
  {
    tabSize = size;
    return this;
  }

  public int tabSize ()
  {
    return tabSize;
  }
}
