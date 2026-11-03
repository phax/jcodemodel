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
 * An expression that can be passed to a parameter of type <code>boolean</code>, and therefore be
 * used as a condition.
 * <p>
 * The <code>cond</code> overloads create the conditional operator <code>this ? a : b</code> with
 * the result type of JLS 15.25. Numeric operands are handled by overload resolution like binary
 * numeric promotion, including the rules that keep <code>byte</code>, <code>short</code> and
 * <code>char</code>. Reference operands must have the same mirror type: with different types Java
 * uses the least upper bound, or even a numeric conditional for two boxed numeric types.
 * <p>
 * Not modelled: an operand of type <code>byte</code>, <code>short</code> or <code>char</code>
 * combined with an <code>int</code> constant that fits keeps the narrower type in Java, while the
 * typed result is <code>int</code>.
 *
 * @param <T>
 *        The runtime type of the expression.
 */
public interface IBooleanConvertible <T> extends ITypedExpression <T>
{
  @NonNull
  default BooleanExpression not ()
  {
    return BooleanExpression.of (raw ().not ());
  }

  @NonNull
  default BooleanExpression cand (@NonNull final IBooleanConvertible <?> aRhs)
  {
    return BooleanExpression.of (raw ().cand (aRhs.raw ()));
  }

  @NonNull
  default BooleanExpression cor (@NonNull final IBooleanConvertible <?> aRhs)
  {
    return BooleanExpression.of (raw ().cor (aRhs.raw ()));
  }

  @NonNull
  default BooleanExpression band (@NonNull final IBooleanConvertible <?> aRhs)
  {
    return BooleanExpression.of (raw ().band (aRhs.raw ()));
  }

  @NonNull
  default BooleanExpression bor (@NonNull final IBooleanConvertible <?> aRhs)
  {
    return BooleanExpression.of (raw ().bor (aRhs.raw ()));
  }

  @NonNull
  default BooleanExpression xor (@NonNull final IBooleanConvertible <?> aRhs)
  {
    return BooleanExpression.of (raw ().xor (aRhs.raw ()));
  }

  @NonNull
  default DoubleExpression cond (@NonNull final IDoubleConvertible <?> aThen, @NonNull final IDoubleConvertible <?> aElse)
  {
    return DoubleExpression.of (JExpr.cond (raw (), aThen.raw (), aElse.raw ()));
  }

  @NonNull
  default FloatExpression cond (@NonNull final IFloatConvertible <?> aThen, @NonNull final IFloatConvertible <?> aElse)
  {
    return FloatExpression.of (JExpr.cond (raw (), aThen.raw (), aElse.raw ()));
  }

  @NonNull
  default LongExpression cond (@NonNull final ILongConvertible <?> aThen, @NonNull final ILongConvertible <?> aElse)
  {
    return LongExpression.of (JExpr.cond (raw (), aThen.raw (), aElse.raw ()));
  }

  @NonNull
  default IntExpression cond (@NonNull final IIntConvertible <?> aThen, @NonNull final IIntConvertible <?> aElse)
  {
    return IntExpression.of (JExpr.cond (raw (), aThen.raw (), aElse.raw ()));
  }

  @NonNull
  default ShortExpression cond (@NonNull final IShortConvertible <?> aThen, @NonNull final IShortConvertible <?> aElse)
  {
    return ShortExpression.of (JExpr.cond (raw (), aThen.raw (), aElse.raw ()));
  }

  @NonNull
  default ByteExpression cond (@NonNull final IByteConvertible <?> aThen, @NonNull final IByteConvertible <?> aElse)
  {
    return ByteExpression.of (JExpr.cond (raw (), aThen.raw (), aElse.raw ()));
  }

  @NonNull
  default CharExpression cond (@NonNull final ICharConvertible <?> aThen, @NonNull final ICharConvertible <?> aElse)
  {
    return CharExpression.of (JExpr.cond (raw (), aThen.raw (), aElse.raw ()));
  }

  @NonNull
  default BooleanExpression cond (@NonNull final IBooleanConvertible <?> aThen, @NonNull final IBooleanConvertible <?> aElse)
  {
    return BooleanExpression.of (JExpr.cond (raw (), aThen.raw (), aElse.raw ()));
  }

  @NonNull
  default LongObjExpression cond (@NonNull final LongObjExpression aThen, @NonNull final LongObjExpression aElse)
  {
    return LongObjExpression.of (JExpr.cond (raw (), aThen.raw (), aElse.raw ()));
  }

  @NonNull
  default ShortObjExpression cond (@NonNull final ShortObjExpression aThen, @NonNull final ShortObjExpression aElse)
  {
    return ShortObjExpression.of (JExpr.cond (raw (), aThen.raw (), aElse.raw ()));
  }

  @NonNull
  default ByteObjExpression cond (@NonNull final ByteObjExpression aThen, @NonNull final ByteObjExpression aElse)
  {
    return ByteObjExpression.of (JExpr.cond (raw (), aThen.raw (), aElse.raw ()));
  }

  @NonNull
  default FloatObjExpression cond (@NonNull final FloatObjExpression aThen, @NonNull final FloatObjExpression aElse)
  {
    return FloatObjExpression.of (JExpr.cond (raw (), aThen.raw (), aElse.raw ()));
  }

  @NonNull
  default DoubleObjExpression cond (@NonNull final DoubleObjExpression aThen, @NonNull final DoubleObjExpression aElse)
  {
    return DoubleObjExpression.of (JExpr.cond (raw (), aThen.raw (), aElse.raw ()));
  }

  @NonNull
  default BooleanObjExpression cond (@NonNull final BooleanObjExpression aThen, @NonNull final BooleanObjExpression aElse)
  {
    return BooleanObjExpression.of (JExpr.cond (raw (), aThen.raw (), aElse.raw ()));
  }

  @NonNull
  default IntegerExpression cond (@NonNull final IntegerExpression aThen, @NonNull final IntegerExpression aElse)
  {
    return IntegerExpression.of (JExpr.cond (raw (), aThen.raw (), aElse.raw ()));
  }

  @NonNull
  default CharacterExpression cond (@NonNull final CharacterExpression aThen, @NonNull final CharacterExpression aElse)
  {
    return CharacterExpression.of (JExpr.cond (raw (), aThen.raw (), aElse.raw ()));
  }

  @NonNull
  default StringExpression cond (@NonNull final StringExpression aThen, @NonNull final StringExpression aElse)
  {
    return StringExpression.of (JExpr.cond (raw (), aThen.raw (), aElse.raw ()));
  }

  /**
   * The conditional operator for two operands of the same reference mirror type. The type is passed
   * as {@link TypedType}, which is invariant in <code>M</code>: a factory function would let javac
   * infer <code>M</code> as the intersection of two different mirror types.
   *
   * @param <M>
   *        The typed mirror of both operands and the result.
   * @param aType
   *        The type of both operands and the result. May not be <code>null</code>.
   * @param aThen
   *        The value if true. May not be <code>null</code>.
   * @param aElse
   *        The value if false. May not be <code>null</code>.
   * @return <code>this ? then : else</code>
   */
  @NonNull
  default <M extends IReferenceExpression <?>> M cond (@NonNull final TypedType <M, ?> aType,
                                                     @NonNull final M aThen,
                                                     @NonNull final M aElse)
  {
    return aType.wrap (JExpr.cond (raw (), aThen.raw (), aElse.raw ()));
  }
}
