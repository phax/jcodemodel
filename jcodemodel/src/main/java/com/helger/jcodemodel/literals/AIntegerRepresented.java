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
package com.helger.jcodemodel.literals;

import org.jspecify.annotations.NonNull;

/// Something that has an IntegerRepresentation to update, in practice only JAtomInt and JAtomLong
/// 
/// Its abstract because it's just a tooling class to extend.
/// 
/// @param T must be declaring class, eg `class A extends AIntegerRepresented<A>`
public abstract class AIntegerRepresented <T extends AIntegerRepresented <T>>
{

  @NonNull
  protected IntegerRepresentation m_aRepresentation = IntegerRepresentation.DEFAULT;

  @SuppressWarnings ("unchecked")
  protected T self ()
  {
    return (T) this;
  }

  @NonNull
  public IntegerRepresentation representation ()
  {
    return m_aRepresentation;
  }

  /// change the internal representation to the provided one
  ///
  /// @return this
  /// @param representation if null, nothing changes.
  public @NonNull T representation (IntegerRepresentation representation)
  {
    if (representation != null)
      this.m_aRepresentation = representation;
    return self ();
  }

  /// change the internal representation to show positive sign
  ///
  /// @return this
  public @NonNull T positiveSign (boolean positiveSign)
  {
    return representation (representation ().positiveSign (positiveSign));
  }

  /// change the internal representation to use binary base
  ///
  /// @return this
  public @NonNull T binary ()
  {
    return representation (representation ().base (EIntegerBase.BINARY));
  }

  /// change the internal representation to use decimal base
  ///
  /// @return this
  public @NonNull T decimal ()
  {
    return representation (representation ().base (EIntegerBase.DECIMAL));
  }

  /// change the internal representation to use hexadecimal base
  ///
  /// @return this
  public @NonNull T hexadecimal ()
  {
    return representation (representation ().base (EIntegerBase.HEXADECIMAL));
  }

  /// change the internal representation to use octal base
  ///
  /// @return this
  public @NonNull T octal ()
  {
    return representation (representation ().base (EIntegerBase.OCTAL));
  }

  /// change the internal representation to use a fixed separator size (the number of character
  /// BETWEEN
  /// each separated group), used only when **NO** separator format is provided
  ///
  /// @return this
  public @NonNull T separatorSize (int size)
  {
    return representation (representation ().separatorSize (size));
  }

  /// change the internal representation to use a fixed separator distance (the maximum number of
  /// character IN
  /// a separated group), used only when **NO** separator format is provided
  ///
  /// @return this
  public @NonNull T separateEvery (int every)
  {
    return representation (representation ().separateEvery (every));
  }

  /// change the internal representation to use a padding value. The padding is not used for decimal
  /// base, since leading "0" makes an octal.
  ///
  /// @return this
  public @NonNull T padding (int padding)
  {
    return representation (representation ().padding (padding));
  }

}
