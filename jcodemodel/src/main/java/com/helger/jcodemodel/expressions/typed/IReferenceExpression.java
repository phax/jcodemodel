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
 * An expression of a reference type, offering the members of {@link Object}.
 *
 * @param <T>
 *        The runtime type of the expression.
 */
public interface IReferenceExpression <T> extends ITypedExpression <T>
{
  @NonNull
  default BooleanExpression eqNull ()
  {
    return BooleanExpression.of (raw ().eqNull ());
  }

  @NonNull
  default BooleanExpression neNull ()
  {
    return BooleanExpression.of (raw ().neNull ());
  }

  /**
   * Reference comparison. Java accepts <code>a == b</code> if either type can be cast to the
   * other; requiring a sub type of <code>T</code> is stricter, but never accepts code that does
   * not compile.
   *
   * @param aRhs
   *        The right operand. May not be <code>null</code>.
   * @return <code>this == rhs</code>
   */
  @NonNull
  default BooleanExpression eqRef (@NonNull final IReferenceExpression <? extends T> aRhs)
  {
    return BooleanExpression.of (raw ().eq (aRhs.raw ()));
  }

  /**
   * @param aRhs
   *        The right operand. May not be <code>null</code>.
   * @return <code>this != rhs</code>
   * @see #eqRef(IReferenceExpression)
   */
  @NonNull
  default BooleanExpression neRef (@NonNull final IReferenceExpression <? extends T> aRhs)
  {
    return BooleanExpression.of (raw ().ne (aRhs.raw ()));
  }

  /**
   * @param aRhs
   *        The argument. May not be <code>null</code>.
   * @return <code>this.equals(rhs)</code>
   */
  @NonNull
  default BooleanExpression callEquals (@NonNull final ITypedExpression <?> aRhs)
  {
    return BooleanExpression.of (raw ().invoke ("equals").arg (aRhs.raw ()));
  }

  /**
   * @return <code>this.hashCode()</code>
   */
  @NonNull
  default IntExpression callHashCode ()
  {
    return IntExpression.of (raw ().invoke ("hashCode"));
  }

  /**
   * @return <code>this.toString()</code>
   */
  @NonNull
  default StringExpression callToString ()
  {
    return StringExpression.of (raw ().invoke ("toString"));
  }
}
