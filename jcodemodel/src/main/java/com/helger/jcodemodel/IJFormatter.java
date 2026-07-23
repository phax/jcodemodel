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

import java.io.Closeable;
import java.util.Collection;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.writer.FormatterSettings;
import com.helger.jcodemodel.writer.settings.Wrap.ListWrapping;

/**
 * Base interface for JFormatter.
 *
 * @author Philip Helger
 */
public interface IJFormatter extends Closeable
{
  /**
   * Special character token we use to differentiate '&gt;' as an operator and
   * '&gt;' as the end of the type arguments. The former uses '&gt;' and it
   * requires a preceding whitespace. The latter uses this, and it does not have
   * a preceding whitespace.
   */
  char CLOSE_TYPE_ARGS = '\uFFFF';

  /**
   * @return <code>true</code> if we are in the printing mode, where we actually
   *         produce text. The other (internal) mode is the "collecting mode".
   */
  boolean isPrinting ();

  /**
   * Increment the indentation level.
   *
   * @return this for chaining
   */
  @NonNull
  default IJFormatter indent ()
  {
    return indent (1);
  }

  @NonNull
  IJFormatter indent (int nb);

  /**
   * Decrement the indentation level.
   *
   * @return this for chaining
   */
  @NonNull
  default IJFormatter outdent ()
  {
    return outdent (1);
  }

  @NonNull
  IJFormatter outdent (int nb);

  /**
   * Print a new line into the stream
   *
   * @return this for chaining
   */
  @NonNull
  IJFormatter newline ();

  /**
   * @return the internal String used for new lines.
   */
  String getNewLine ();

  /**
   * @return the size of the line written, so far.
   */
  int currentLineSize ();

  /**
   * Print a char into the stream
   *
   * @param c
   *        the char
   * @return this for chaining
   */
  @NonNull
  IJFormatter print (char c);

  @NonNull
  default IJFormatter printCloseTypeArgs ()
  {
    return print (CLOSE_TYPE_ARGS);
  }

  /**
   * Print a String into the stream. Indentation happens automatically.
   *
   * @param sStr
   *        the String
   * @return this
   */
  @NonNull
  IJFormatter print (@NonNull String sStr);

  default IJFormatter println (@NonNull String sStr)
  {
    return print (sStr).print (getNewLine ());
  }

  /**
   * Print a type name.
   * <p>
   * In the collecting mode we use this information to decide what types to
   * import and what not to.
   *
   * @param aType
   *        Type to be emitted
   * @return this for chaining
   */
  @NonNull
  IJFormatter type (@NonNull AbstractJClass aType);

  @NonNull
  default IJFormatter type (@NonNull final AbstractJType aType)
  {
    if (aType.isReference ())
      return type ((AbstractJClass) aType);
    return generable (aType);
  }

  /**
   * Cause the {@link JVar} to generate source for itself. With annotations,
   * type, name and init expression.
   *
   * @param aVar
   *        the {@link JVar} object
   * @return this for chaining
   */
  @NonNull
  IJFormatter var (@NonNull JVar aVar);

  default IJFormatter vars (@NonNull final Collection <? extends JVar> aList)
  {
    return vars (aList, null);
  }

  IJFormatter vars (@NonNull final Collection <? extends JVar> aList, ListWrapping wrapping);

  /**
   * Print an identifier
   *
   * @param sID
   *        identifier
   * @return this for chaining
   */
  @NonNull
  IJFormatter id (@NonNull String sID);

  /**
   * Cause the {@link IJGenerable} object to generate source for itself
   *
   * @param aObj
   *        the object
   * @return this for chaining
   */
  @NonNull
  IJFormatter generable (@NonNull IJGenerable aObj);

  /**
   * Produces {@link IJGenerable}s separated by ','
   *
   * @param aList
   *        List of {@link IJGenerable} objects that will be separated by a
   *        comma
   * @return this for chaining
   */
  @NonNull
  default IJFormatter generable (@NonNull final Collection <? extends IJGenerable> aList)
  {
    return generable (aList, ",", null);
  }

  @NonNull
  IJFormatter generable (@NonNull final Collection <? extends IJGenerable> aList, String separator, ListWrapping wrapping);

  /**
   * Cause the {@link IJStatement} to generate source for itself
   *
   * @param aObj
   *        the object
   * @return this for chaining
   */
  @NonNull
  IJFormatter statement (@NonNull IJStatement aObj);

  /**
   * Cause the {@link IJDeclaration} to generate source for itself
   *
   * @param aObj
   *        the object
   * @return this for chaining
   */
  @NonNull
  IJFormatter declaration (@NonNull IJDeclaration aObj);

  FormatterSettings settings ();

  public interface IContextCloser extends AutoCloseable
  {
    @Override
    void close ();

    IContextCloser persistOnClose (boolean b);

    default IContextCloser persistOnClose ()
    {
      return persistOnClose (true);
    }

    /// shortcut to set the persist then close. Since the close is idempotent, this
    /// call may have no result.
    default void commit ()
    {
      persistOnClose ().close ();
    }

    /// shortcut to unset the persist then close. Since the close is idempotent,
    /// this call may have no result.
    default void rollback ()
    {
      persistOnClose (false).close ();
    }

    /// @return true after #close has been called at least once.
    boolean isClosed ();

    /// get the actual buffer's value
    String value ();
  }

  /// create a new buffer to write data into.
  IContextCloser addContextLayer ();
}
