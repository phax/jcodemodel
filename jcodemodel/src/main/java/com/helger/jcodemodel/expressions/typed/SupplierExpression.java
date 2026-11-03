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

import java.util.function.Supplier;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JLambda;

/**
 * Mirror of {@link Supplier}.
 *
 * @param <R>
 *        The result type.
 */
public class SupplierExpression <R> extends AbstractTypedExpression <Supplier <R>> implements
                                IReferenceExpression <Supplier <R>>
{
  protected SupplierExpression (@NonNull final IJExpression aRaw)
  {
    super (aRaw);
  }

  @NonNull
  public static <R> SupplierExpression <R> of (@NonNull final IJExpression aRaw)
  {
    return new SupplierExpression <> (aRaw);
  }

  /**
   * @param <R>
   *        The result type.
   * @param <MR>
   *        The typed mirror of the result.
   * @param aResultType
   *        The result type. May not be <code>null</code>.
   * @return The type <code>Supplier&lt;R&gt;</code>, e.g. to declare a parameter. Never
   *         <code>null</code>.
   */
  @NonNull
  public static <R, MR extends IReferenceExpression <R>> TypedType <SupplierExpression <R>, ITypedExpression <? extends Supplier <R>>> type (@NonNull final TypedType <MR, ?> aResultType)
  {
    return new TypedType <> (aCM -> aCM.ref (Supplier.class).narrow (aResultType.type (aCM).boxify ()),
                             SupplierExpression::of);
  }

  /**
   * Create the lambda <code>() -&gt; value</code>. The result type is taken from the value.
   *
   * @param <R>
   *        The result type.
   * @param aValue
   *        The supplied value. May not be <code>null</code>.
   * @return The lambda. Never <code>null</code>.
   */
  @NonNull
  public static <R> SupplierExpression <R> lambda (@NonNull final ITypedExpression <R> aValue)
  {
    final JLambda aLambda = new JLambda ();
    aLambda.body ().lambdaExpr (aValue.raw ());
    return of (aLambda);
  }

  /**
   * @return <code>this.get()</code>, without a dedicated mirror. Never <code>null</code>.
   */
  @NonNull
  public ObjectExpression <R> get ()
  {
    return ObjectExpression.of (raw ().invoke ("get"));
  }

  /**
   * @param <MR>
   *        The typed mirror of the result.
   * @param aResultType
   *        The result type, providing the mirror. May not be <code>null</code>.
   * @return <code>this.get()</code>. Never <code>null</code>.
   */
  @NonNull
  public <MR extends IReferenceExpression <R>> MR get (@NonNull final TypedType <MR, ?> aResultType)
  {
    return aResultType.wrap (raw ().invoke ("get"));
  }
}
