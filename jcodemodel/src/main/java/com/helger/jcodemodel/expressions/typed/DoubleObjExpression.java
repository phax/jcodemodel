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
import com.helger.jcodemodel.JMethod;

/**
 * Mirror of {@link Double}. The name differs from the primitive mirror {@link DoubleExpression} by the
 * <code>Obj</code> infix. It implements the matching convertibility interface, so it can be passed wherever a
 * <code>double</code> is expected (unboxing), without an explicit unboxing call.
 */
public class DoubleObjExpression extends AbstractTypedExpression <Double> implements
                               IReferenceExpression <Double>,
                               IDoubleConvertible <Double>
{
  protected DoubleObjExpression (@NonNull final IJExpression aRaw)
  {
    super (aRaw);
  }

  @NonNull
  public static DoubleObjExpression of (@NonNull final IJExpression aRaw)
  {
    return new DoubleObjExpression (aRaw);
  }

  /**
   * Add a parameter of type {@link Double} to a method.
   *
   * @param aMethod
   *        The method to add the parameter to. May not be <code>null</code>.
   * @param sName
   *        The parameter name. May neither be <code>null</code> nor empty.
   * @return The typed parameter reference. Never <code>null</code>.
   */
  @NonNull
  public static DoubleObjExpression param (@NonNull final JMethod aMethod, @NonNull final String sName)
  {
    return of (aMethod.param (Double.class, sName));
  }

  /**
   * @return <code>this.doubleValue()</code>
   */
  @NonNull
  public DoubleExpression doubleValue ()
  {
    return DoubleExpression.of (raw ().invoke ("doubleValue"));
  }

  @NonNull
  public DoubleExpression minus ()
  {
    return DoubleExpression.of (raw ().minus ());
  }
}
