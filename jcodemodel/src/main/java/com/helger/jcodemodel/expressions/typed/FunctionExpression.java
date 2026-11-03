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

import java.util.function.Function;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JLambda;

/**
 * Mirror of {@link Function}.
 *
 * @param <A>
 *        The argument type.
 * @param <R>
 *        The result type.
 */
public class FunctionExpression <A, R> extends AbstractTypedExpression <Function <A, R>> implements
                                IReferenceExpression <Function <A, R>>
{
  protected FunctionExpression (@NonNull final IJExpression aRaw)
  {
    super (aRaw);
  }

  @NonNull
  public static <A, R> FunctionExpression <A, R> of (@NonNull final IJExpression aRaw)
  {
    return new FunctionExpression <> (aRaw);
  }

  /**
   * @param <A>
   *        The argument type.
   * @param <MA>
   *        The typed mirror of the argument.
   * @param <R>
   *        The result type.
   * @param <MR>
   *        The typed mirror of the result.
   * @param aArgType
   *        The argument type. May not be <code>null</code>.
   * @param aResultType
   *        The result type. May not be <code>null</code>.
   * @return The type <code>Function&lt;A, R&gt;</code>, e.g. to declare a parameter. Never
   *         <code>null</code>.
   */
  @NonNull
  public static <A, MA extends IReferenceExpression <A>, R, MR extends IReferenceExpression <R>> TypedType <FunctionExpression <A, R>, ITypedExpression <? extends Function <A, R>>> type (@NonNull final TypedType <MA, ?> aArgType,
                                                                                                                                                                                       @NonNull final TypedType <MR, ?> aResultType)
  {
    return new TypedType <> (aCM -> aCM.ref (Function.class)
                                       .narrow (aArgType.type (aCM).boxify (), aResultType.type (aCM).boxify ()),
                             FunctionExpression::of);
  }

  /**
   * Create the lambda <code>name -&gt; body</code>.
   * <p>
   * The parameter type must have a reference mirror (e.g. {@link TypedType#INTEGER}, not
   * {@link TypedType#INT}), because type arguments are always reference types. The result type is taken from the body, so a body of type <code>int</code>
   * creates a <code>Function&lt;A, Integer&gt;</code>, exactly like Java infers it.
   *
   * @param <A>
   *        The argument type.
   * @param <MA>
   *        The typed mirror of the argument.
   * @param <R>
   *        The result type.
   * @param aArgType
   *        The argument type. May not be <code>null</code>.
   * @param sName
   *        The parameter name. May neither be <code>null</code> nor empty.
   * @param aBody
   *        Creates the body from the typed parameter. May not be <code>null</code>.
   * @return The lambda. Never <code>null</code>.
   */
  @NonNull
  public static <A, MA extends IReferenceExpression <A>, R> FunctionExpression <A, R> lambda (@NonNull final TypedType <MA, ?> aArgType,
                                                                                              @NonNull final String sName,
                                                                                              @NonNull final Function <MA, ? extends ITypedExpression <R>> aBody)
  {
    final JLambda aLambda = new JLambda ();
    final MA aParam = aArgType.wrap (aLambda.addParam (sName));
    aLambda.body ().lambdaExpr (aBody.apply (aParam).raw ());
    return of (aLambda);
  }

  /**
   * @param aArg
   *        The argument. May not be <code>null</code>.
   * @return <code>this.apply(arg)</code>, without a dedicated mirror. Never <code>null</code>.
   */
  @NonNull
  public ObjectExpression <R> apply (@NonNull final ITypedExpression <? extends A> aArg)
  {
    return ObjectExpression.of (raw ().invoke ("apply").arg (aArg.raw ()));
  }

  /**
   * @param <MR>
   *        The typed mirror of the result.
   * @param aArg
   *        The argument. May not be <code>null</code>.
   * @param aResultType
   *        The result type, providing the mirror. May not be <code>null</code>.
   * @return <code>this.apply(arg)</code>. Never <code>null</code>.
   */
  @NonNull
  public <MR extends IReferenceExpression <R>> MR apply (@NonNull final ITypedExpression <? extends A> aArg,
                                                         @NonNull final TypedType <MR, ?> aResultType)
  {
    return aResultType.wrap (raw ().invoke ("apply").arg (aArg.raw ()));
  }
}
