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
package com.helger.jcodemodel.expressions.typed;

import org.jspecify.annotations.NonNull;

/**
 * An expression that can be passed to a parameter of type <code>long</code>. This is also the
 * parameter type of the right operand of all shift operators.
 *
 * @param <T>
 *        The runtime type of the expression.
 */
public interface ILongConvertible <T> extends IFloatConvertible <T>
{
  @NonNull
  default LongExpression plus (@NonNull final ILongConvertible <?> aRhs)
  {
    return LongExpression.of (raw ().plus (aRhs.raw ()));
  }

  @NonNull
  default LongExpression plus (final long nRhs)
  {
    return LongExpression.of (raw ().plus (nRhs));
  }

  @NonNull
  default LongExpression minus (@NonNull final ILongConvertible <?> aRhs)
  {
    return LongExpression.of (raw ().minus (aRhs.raw ()));
  }

  @NonNull
  default LongExpression minus (final long nRhs)
  {
    return LongExpression.of (raw ().minus (nRhs));
  }

  @NonNull
  default LongExpression mul (@NonNull final ILongConvertible <?> aRhs)
  {
    return LongExpression.of (raw ().mul (aRhs.raw ()));
  }

  @NonNull
  default LongExpression mul (final long nRhs)
  {
    return LongExpression.of (raw ().mul (nRhs));
  }

  @NonNull
  default LongExpression div (@NonNull final ILongConvertible <?> aRhs)
  {
    return LongExpression.of (raw ().div (aRhs.raw ()));
  }

  @NonNull
  default LongExpression div (final long nRhs)
  {
    return LongExpression.of (raw ().div (nRhs));
  }

  @NonNull
  default LongExpression mod (@NonNull final ILongConvertible <?> aRhs)
  {
    return LongExpression.of (raw ().mod (aRhs.raw ()));
  }

  @NonNull
  default LongExpression mod (final long nRhs)
  {
    return LongExpression.of (raw ().mod (nRhs));
  }

  @NonNull
  default LongExpression band (@NonNull final ILongConvertible <?> aRhs)
  {
    return LongExpression.of (raw ().band (aRhs.raw ()));
  }

  @NonNull
  default LongExpression bor (@NonNull final ILongConvertible <?> aRhs)
  {
    return LongExpression.of (raw ().bor (aRhs.raw ()));
  }

  @NonNull
  default LongExpression xor (@NonNull final ILongConvertible <?> aRhs)
  {
    return LongExpression.of (raw ().xor (aRhs.raw ()));
  }
}
