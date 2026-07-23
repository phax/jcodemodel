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

import com.helger.jcodemodel.writer.settings.wrap.CatchClause;
import com.helger.jcodemodel.writer.settings.wrap.ForLoop;
import com.helger.jcodemodel.writer.settings.wrap.Method;
import com.helger.jcodemodel.writer.settings.wrap.Variables;

public class Wrap
{
  /// configuration of a generated code's word wrapping : when to wrap it, how
  /// many
  /// indent.
  ///
  /// This is a generic idea for any one-time element, like the "implements" part
  /// of a class, a method's return type, etc.
  public static class WordWrapping
  {
    public enum EWordWrapStrategy
    {
      /// always wrap the element
      ALWAYS,
      /// never wrap
      NEVER,
      /// only wrap if otherwise would go over the line size.
      REQUIRED;
    }

    /// when do we wrap this specific code generation
    public EWordWrapStrategy condition = EWordWrapStrategy.NEVER;

    /// when we wrap, how much do we indent the code
    public int indent = 1;

    @NonNull
    public WordWrapping condition (@Nullable final EWordWrapStrategy value)
    {
      if (value != null)
      {
        condition = value;
      }
      return this;
    }

    @NonNull
    public WordWrapping indent (final int value)
    {
      indent = value;
      return this;
    }

  }

  /// configuration of a generated code's elements wrapping : when to wrap the
  /// elements, how many indent.
  ///
  /// This is a generic idea for list of elements which can be wrapped
  /// individually, like the list of interfaces implemented by a class, its list
  /// of Generics, etc.
  public static class ListWrapping
  {
    public enum EListWrapStrategy
    {
      /// always wrap all the elements
      ALWAYS (false),
      /// never wrap any element. All on the same line
      NEVER (false),
      /// only the minimum number of elements.
      REQUIRED (false),
      /// once an element should be wrapped, all are.
      BINARY (true),
      /// wrap all if more than 3 elements ; first item never wrapped
      PAST3 (false);

      public final boolean twoPasses;

      EListWrapStrategy (final boolean bTwoPasses)
      {
        this.twoPasses = bTwoPasses;
      }
    }

    /// when do we wrap this specific code generation
    public EListWrapStrategy condition = EListWrapStrategy.PAST3;

    /// when we wrap, how much do we indent the code
    public int indent = 1;

    /// when false, we wrap before the separator
    public boolean wrapAfterSep = true;

    @NonNull
    public ListWrapping condition (@Nullable final EListWrapStrategy value)
    {
      if (value != null)
      {
        condition = value;
      }
      return this;
    }

    @NonNull
    public ListWrapping indent (final int value)
    {
      indent = value;
      return this;
    }

    @NonNull
    public ListWrapping wrapAfterSep (final boolean value)
    {
      wrapAfterSep = value;
      return this;
    }

    @NonNull
    public ListWrapping wrapBeforeSep ()
    {
      return wrapAfterSep (false);
    }

    @NonNull
    public ListWrapping wrapAfterSep ()
    {
      return wrapAfterSep (true);
    }
  }

  public static final int DEFAULT_LINE_WIDTH = 80;

  /// wrapping is required if an element would increase the line above
  /// this number of characters
  public int lineWidth = DEFAULT_LINE_WIDTH;

  /// feature flag. When set to true, all wrapping methods should be replaced with
  /// wrapping-oblivious ones.
  public boolean disabled = false;

  public final CatchClause catchClause = new CatchClause ();

  public final ForLoop forLoop = new ForLoop ();

  public final Method method = new Method ();

  public final Variables variables = new Variables ();

  @NonNull
  public Wrap lineWidth (final int value)
  {
    lineWidth = value;
    return this;
  }

  @NonNull
  public Wrap disable (final boolean value)
  {
    disabled = value;
    return this;
  }

  public Wrap disable ()
  {
    return disable (true);
  }

}
