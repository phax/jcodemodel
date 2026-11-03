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

import com.helger.jcodemodel.JExpr;

/**
 * An expression that can be passed to a parameter of type <code>double</code>: every primitive
 * numeric type and every boxed numeric type (by unboxing followed by widening).
 * <p>
 * The arithmetic operators declared here return <code>double</code>. Sub interfaces declare
 * overloads with a narrower parameter and result type, so that overload resolution performs the
 * binary numeric promotion of JLS 5.6.
 *
 * @param <T>
 *        The runtime type of the expression.
 */
public interface IDoubleConvertible <T> extends ITypedExpression <T>
{
  @NonNull
  default DoubleExpression plus (@NonNull final IDoubleConvertible <?> aRhs)
  {
    return DoubleExpression.of (raw ().plus (aRhs.raw ()));
  }

  @NonNull
  default DoubleExpression plus (final double dRhs)
  {
    return DoubleExpression.of (raw ().plus (dRhs));
  }

  @NonNull
  default DoubleExpression minus (@NonNull final IDoubleConvertible <?> aRhs)
  {
    return DoubleExpression.of (raw ().minus (aRhs.raw ()));
  }

  @NonNull
  default DoubleExpression minus (final double dRhs)
  {
    return DoubleExpression.of (raw ().minus (dRhs));
  }

  @NonNull
  default DoubleExpression mul (@NonNull final IDoubleConvertible <?> aRhs)
  {
    return DoubleExpression.of (raw ().mul (aRhs.raw ()));
  }

  @NonNull
  default DoubleExpression mul (final double dRhs)
  {
    return DoubleExpression.of (raw ().mul (dRhs));
  }

  @NonNull
  default DoubleExpression div (@NonNull final IDoubleConvertible <?> aRhs)
  {
    return DoubleExpression.of (raw ().div (aRhs.raw ()));
  }

  @NonNull
  default DoubleExpression div (final double dRhs)
  {
    return DoubleExpression.of (raw ().div (dRhs));
  }

  @NonNull
  default DoubleExpression mod (@NonNull final IDoubleConvertible <?> aRhs)
  {
    return DoubleExpression.of (raw ().mod (aRhs.raw ()));
  }

  @NonNull
  default DoubleExpression mod (final double dRhs)
  {
    return DoubleExpression.of (raw ().mod (JExpr.lit (dRhs)));
  }

  @NonNull
  default BooleanExpression lt (@NonNull final IDoubleConvertible <?> aRhs)
  {
    return BooleanExpression.of (raw ().lt (aRhs.raw ()));
  }

  @NonNull
  default BooleanExpression lte (@NonNull final IDoubleConvertible <?> aRhs)
  {
    return BooleanExpression.of (raw ().lte (aRhs.raw ()));
  }

  @NonNull
  default BooleanExpression gt (@NonNull final IDoubleConvertible <?> aRhs)
  {
    return BooleanExpression.of (raw ().gt (aRhs.raw ()));
  }

  @NonNull
  default BooleanExpression gte (@NonNull final IDoubleConvertible <?> aRhs)
  {
    return BooleanExpression.of (raw ().gte (aRhs.raw ()));
  }
}
