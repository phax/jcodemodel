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

import static com.helger.jcodemodel.util.EqualsUtils.isEqual;
import static com.helger.jcodemodel.util.HashCodeGenerator.getHashCode;

import javax.annotation.Nonnull;

import com.helger.jcodemodel.optimize.ExpressionAccessor;
import com.helger.jcodemodel.optimize.ExpressionCallback;

/**
 * Assignment statements, which are also expressions.
 */
public class JAssignment extends AbstractJExpressionImpl implements IJExpressionStatement
{
  private final IJAssignmentTarget _lhs;
  private IJExpression _rhs;
  private final String _op;

  /**
   * Constructor for "=" operator
   *
   * @param lhs
   *        left
   * @param rhs
   *        right
   */
  protected JAssignment (@Nonnull final IJAssignmentTarget lhs, @Nonnull final IJExpression rhs)
  {
    this (lhs, rhs, "");
  }

  /**
   * Constructor for <code>op + "="</code> operator
   *
   * @param lhs
   *        left
   * @param rhs
   *        right
   * @param op
   *        additional operator
   */
  protected JAssignment (@Nonnull final IJAssignmentTarget lhs,
                         @Nonnull final IJExpression rhs,
                         @Nonnull final String op)
  {
    _lhs = lhs;
    _rhs = rhs;
    _op = op;
  }

  @Nonnull
  public IJAssignmentTarget lhs ()
  {
    return _lhs;
  }

  @Nonnull
  public IJExpression rhs ()
  {
    return _rhs;
  }

  /**
   * @return The additional operator (without the "=")
   */
  @Nonnull
  public String op ()
  {
    return _op;
  }

  public void generate (@Nonnull final JFormatter f)
  {
    f.generable (_lhs).print (_op + '=').generable (_rhs);
  }

  public void state (@Nonnull final JFormatter f)
  {
    f.generable (this).print (';').newline ();
  }

  @Override
  public boolean equals (Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof IJExpression))
      return false;
    o = ((IJExpression) o).unwrapped ();
    if (o == null || getClass () != o.getClass ())
      return false;
    final JAssignment rhs = (JAssignment) o;
    return isEqual (_lhs, rhs._lhs) && isEqual (_rhs, rhs._rhs) && isEqual (_op, rhs._op);
  }

  @Override
  public int hashCode ()
  {
    return getHashCode (this, _lhs, _rhs, _op);
  }

  @Override
  AbstractJType derivedType ()
  {
    final AbstractJType type = _lhs.expressionType ();
    if (type != null)
      return type;
    return _rhs.expressionType ();
  }

  @Override
  String derivedName ()
  {
    return _lhs.expressionName () + "AssignedTo" + _rhs.expressionName ();
  }

  @Override
  public boolean forAllSubExpressions (final ExpressionCallback callback)
  {
    if (!_lhs.forAllSubExpressions (callback))
      return false;
    if (!callback.visitAssignmentTarget (_lhs))
      return false;
    return visitWithSubExpressions (callback, new ExpressionAccessor ()
    {
      public void set (final IJExpression newExpression)
      {
        _rhs = newExpression;
      }

      public IJExpression get ()
      {
        return _rhs;
      }
    });
  }
}
