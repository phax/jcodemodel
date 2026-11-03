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

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;

/**
 * Mirror of a subset of {@link String}.
 */
public class StringExpression extends AbstractTypedExpression <String> implements IReferenceExpression <String>
{
  protected StringExpression (@NonNull final IJExpression aRaw)
  {
    super (aRaw);
  }

  @NonNull
  public static StringExpression of (@NonNull final IJExpression aRaw)
  {
    return new StringExpression (aRaw);
  }

  @NonNull
  public static StringExpression lit (@NonNull final String sValue)
  {
    return of (JExpr.lit (sValue));
  }

  /**
   * Add a parameter of type {@link String} to a method.
   *
   * @param aMethod
   *        The method to add the parameter to. May not be <code>null</code>.
   * @param sName
   *        The parameter name. May neither be <code>null</code> nor empty.
   * @return The typed parameter reference. Never <code>null</code>.
   */
  @NonNull
  public static StringExpression param (@NonNull final JMethod aMethod, @NonNull final String sName)
  {
    return of (aMethod.param (String.class, sName));
  }

  /**
   * @return <code>this.length()</code>
   */
  @NonNull
  public IntExpression length ()
  {
    return IntExpression.of (raw ().invoke ("length"));
  }

  /**
   * @return <code>this.isEmpty()</code>
   */
  @NonNull
  public BooleanExpression isEmpty ()
  {
    return BooleanExpression.of (raw ().invoke ("isEmpty"));
  }

  /**
   * @param aIndex
   *        The index. May not be <code>null</code>.
   * @return <code>this.charAt(index)</code>
   */
  @NonNull
  public CharExpression charAt (@NonNull final IIntConvertible <?> aIndex)
  {
    return CharExpression.of (raw ().invoke ("charAt").arg (aIndex.raw ()));
  }

  /**
   * @param aStr
   *        The String to search. May not be <code>null</code>.
   * @return <code>this.indexOf(str)</code>
   */
  @NonNull
  public IntExpression indexOf (@NonNull final ITypedExpression <? extends String> aStr)
  {
    return IntExpression.of (raw ().invoke ("indexOf").arg (aStr.raw ()));
  }

  /**
   * @param aCh
   *        The code point to search. May not be <code>null</code>.
   * @return <code>this.indexOf(ch)</code>
   */
  @NonNull
  public IntExpression indexOf (@NonNull final IIntConvertible <?> aCh)
  {
    return IntExpression.of (raw ().invoke ("indexOf").arg (aCh.raw ()));
  }

  /**
   * @param aSeq
   *        The sequence to search. May not be <code>null</code>.
   * @return <code>this.contains(seq)</code>
   */
  @NonNull
  public BooleanExpression contains (@NonNull final ITypedExpression <? extends CharSequence> aSeq)
  {
    return BooleanExpression.of (raw ().invoke ("contains").arg (aSeq.raw ()));
  }

  /**
   * @param aBeginIndex
   *        The begin index, inclusive. May not be <code>null</code>.
   * @return <code>this.substring(beginIndex)</code>
   */
  @NonNull
  public StringExpression substring (@NonNull final IIntConvertible <?> aBeginIndex)
  {
    return of (raw ().invoke ("substring").arg (aBeginIndex.raw ()));
  }

  /**
   * @param aBeginIndex
   *        The begin index, inclusive. May not be <code>null</code>.
   * @param aEndIndex
   *        The end index, exclusive. May not be <code>null</code>.
   * @return <code>this.substring(beginIndex, endIndex)</code>
   */
  @NonNull
  public StringExpression substring (@NonNull final IIntConvertible <?> aBeginIndex,
                                     @NonNull final IIntConvertible <?> aEndIndex)
  {
    return of (raw ().invoke ("substring").arg (aBeginIndex.raw ()).arg (aEndIndex.raw ()));
  }

  /**
   * @param aStr
   *        The String to append. May not be <code>null</code>.
   * @return <code>this.concat(str)</code>
   */
  @NonNull
  public StringExpression concat (@NonNull final ITypedExpression <? extends String> aStr)
  {
    return of (raw ().invoke ("concat").arg (aStr.raw ()));
  }

  /**
   * String concatenation with any non-void right operand.
   *
   * @param aRhs
   *        The right operand. May not be <code>null</code>.
   * @return <code>this + rhs</code>
   */
  @NonNull
  public StringExpression plus (@NonNull final ITypedExpression <?> aRhs)
  {
    return of (raw ().plus (aRhs.raw ()));
  }

  /**
   * @param sRhs
   *        The String literal to append. May not be <code>null</code>.
   * @return <code>this + "rhs"</code>
   */
  @NonNull
  public StringExpression plus (@NonNull final String sRhs)
  {
    return of (raw ().plus (sRhs));
  }
}
