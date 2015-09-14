/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2015 Philip Helger
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
package com.helger.jcodemodel;

import static com.helger.jcodemodel.util.JCEqualsHelper.isEqual;
import static com.helger.jcodemodel.util.JCHashCodeGenerator.getHashCode;

import javax.annotation.Nonnull;

public class JOpTernary extends AbstractJExpressionImpl
{
  private final IJExpression _e1;
  private final String _op1;
  private final IJExpression _e2;
  private final String _op2;
  private final IJExpression _e3;

  protected JOpTernary (@Nonnull final IJExpression e1,
                        @Nonnull final String op1,
                        @Nonnull final IJExpression e2,
                        @Nonnull final String op2,
                        @Nonnull final IJExpression e3)
  {
    this._e1 = e1;
    this._op1 = op1;
    this._e2 = e2;
    this._op2 = op2;
    this._e3 = e3;
  }

  @Nonnull
  public IJExpression expr1 ()
  {
    return _e1;
  }

  @Nonnull
  public String op1 ()
  {
    return _op1;
  }

  @Nonnull
  public IJGenerable expr2 ()
  {
    return _e2;
  }

  @Nonnull
  public String op2 ()
  {
    return _op2;
  }

  @Nonnull
  public IJGenerable expr3 ()
  {
    return _e3;
  }

  public void generate (@Nonnull final JFormatter f)
  {
    f.print ('(').generable (_e1).print (_op1).generable (_e2).print (_op2).generable (_e3).print (')');
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || getClass () != o.getClass ())
      return false;
    final JOpTernary rhs = (JOpTernary) o;
    return isEqual (_e1, rhs._e1) &&
           isEqual (_op1, rhs._op1) &&
           isEqual (_e2, rhs._e2) &&
           isEqual (_op2, rhs._op2) &&
           isEqual (_e3, rhs._e3);
  }

  @Override
  public int hashCode ()
  {
    return getHashCode (this, _e1, _op1, _e2, _op2, _e3);
  }
}
