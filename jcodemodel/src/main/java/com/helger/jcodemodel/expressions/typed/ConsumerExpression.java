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

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JLambda;

/**
 * Mirror of {@link Consumer}.
 *
 * @param <A>
 *        The argument type.
 */
public class ConsumerExpression <A> extends AbstractTypedExpression <Consumer <A>> implements
                                IReferenceExpression <Consumer <A>>
{
  protected ConsumerExpression (@NonNull final IJExpression aRaw)
  {
    super (aRaw);
  }

  @NonNull
  public static <A> ConsumerExpression <A> of (@NonNull final IJExpression aRaw)
  {
    return new ConsumerExpression <> (aRaw);
  }

  /**
   * @param <A>
   *        The argument type.
   * @param <MA>
   *        The typed mirror of the argument.
   * @param aArgType
   *        The argument type. May not be <code>null</code>.
   * @return The type <code>Consumer&lt;A&gt;</code>, e.g. to declare a parameter. Never
   *         <code>null</code>.
   */
  @NonNull
  public static <A, MA extends IReferenceExpression <A>> TypedType <ConsumerExpression <A>, ITypedExpression <? extends Consumer <A>>> type (@NonNull final TypedType <MA, ?> aArgType)
  {
    return new TypedType <> (aCM -> aCM.ref (Consumer.class).narrow (aArgType.type (aCM).boxify ()),
                             ConsumerExpression::of);
  }

  /**
   * Create the lambda <code>name -&gt; { body }</code>.
   * <p>
   * The parameter type must have a reference mirror (e.g. {@link TypedType#INTEGER}, not
   * {@link TypedType#INT}), because type arguments are always reference types.
   *
   * @param <A>
   *        The argument type.
   * @param <MA>
   *        The typed mirror of the argument.
   * @param aArgType
   *        The argument type. May not be <code>null</code>.
   * @param sName
   *        The parameter name. May neither be <code>null</code> nor empty.
   * @param aBody
   *        Fills the lambda body block, using the typed parameter. May not be <code>null</code>.
   * @return The lambda. Never <code>null</code>.
   */
  @NonNull
  public static <A, MA extends IReferenceExpression <A>> ConsumerExpression <A> lambda (@NonNull final TypedType <MA, ?> aArgType,
                                                                                        @NonNull final String sName,
                                                                                        @NonNull final BiConsumer <JBlock, MA> aBody)
  {
    final JLambda aLambda = new JLambda ();
    final MA aParam = aArgType.wrap (aLambda.addParam (sName));
    aBody.accept (aLambda.body (), aParam);
    return of (aLambda);
  }

  /**
   * @param aArg
   *        The argument. May not be <code>null</code>.
   * @return <code>this.accept(arg)</code>, which is <code>void</code>. Never <code>null</code>.
   */
  @NonNull
  public VoidExpression accept (@NonNull final ITypedExpression <? extends A> aArg)
  {
    return VoidExpression.of (raw ().invoke ("accept").arg (aArg.raw ()));
  }
}
