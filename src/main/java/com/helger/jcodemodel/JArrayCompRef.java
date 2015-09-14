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
import static com.helger.jcodemodel.util.JCStringUtils.upper;

import javax.annotation.Nonnull;

import com.helger.jcodemodel.optimize.ExpressionAccessor;
import com.helger.jcodemodel.optimize.ExpressionCallback;

/**
 * array component reference.
 */
public class JArrayCompRef extends AbstractJExpressionAssignmentTargetImpl
{
  /**
   * JArray expression upon which this component will be accessed.
   */
  private IJExpression _array;

  /**
   * Integer expression representing index of the component
   */
  private IJExpression _index;

  /**
   * JArray component reference constructor given an array expression and index.
   *
   * @param array
   *        JExpression for the array upon which the component will be accessed,
   * @param index
   *        JExpression for index of component to access
   */
  protected JArrayCompRef (@Nonnull final IJExpression array, @Nonnull final IJExpression index)
  {
    if (array == null || index == null)
      throw new NullPointerException ();
    this._array = array;
    this._index = index;
  }

  @Nonnull
  public IJExpression array ()
  {
    return _array;
  }

  @Nonnull
  public IJExpression index ()
  {
    return _index;
  }

  public void generate (@Nonnull final JFormatter f)
  {
    f.generable (_array).print ('[').generable (_index).print (']');
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || getClass () != o.getClass ())
      return false;
    final JArrayCompRef rhs = (JArrayCompRef) o;
    return isEqual (_array, rhs._array) && isEqual (_index, rhs._index);
  }

  @Override
  public int hashCode ()
  {
    return getHashCode (this, _array, _index);
  }

  @Override
  AbstractJType derivedType ()
  {
    return _array.expressionType ().elementType ();
  }

  @Override
  String derivedName ()
  {
    return _array.expressionName () + "ElementAt" + upper (_index.expressionName ());
  }

  @Override
  public boolean forAllSubExpressions (final ExpressionCallback callback)
  {
    if (!visitWithSubExpressions (callback, new ExpressionAccessor ()
    {
      public void set (final IJExpression newExpression)
      {
        _array = newExpression;
      }

      public IJExpression get ()
      {
        return _array;
      }
    }))
      return false;
    return visitWithSubExpressions (callback, new ExpressionAccessor ()
    {
      public void set (final IJExpression newExpression)
      {
        _index = newExpression;
      }

      public IJExpression get ()
      {
        return _index;
      }
    });
  }
}
