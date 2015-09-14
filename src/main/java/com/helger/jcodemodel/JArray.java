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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.jcodemodel.optimize.ExpressionAccessor;
import com.helger.jcodemodel.optimize.ExpressionCallback;
import com.helger.jcodemodel.util.JCStringUtils;

/**
 * array creation and initialization.
 */
public class JArray extends AbstractJExpressionImpl
{
  private final AbstractJType _type;
  private IJExpression _size;
  private List <IJExpression> _exprs;

  protected JArray (@Nonnull final AbstractJType type, @Nullable final IJExpression size)
  {
    _type = type;
    _size = size;
  }

  @Nonnull
  public AbstractJType type ()
  {
    return _type;
  }

  @Nullable
  public IJExpression size ()
  {
    return _size;
  }

  /**
   * Add an element to the array initializer
   *
   * @return this
   */
  @Nonnull
  public JArray add (@Nonnull final IJExpression e)
  {
    if (_exprs == null)
      _exprs = new ArrayList <IJExpression> ();
    _exprs.add (e);
    return this;
  }

  /**
   * Remove all elements from the array initializer
   *
   * @return this
   */
  @Nonnull
  public JArray removeAll ()
  {
    _exprs = null;
    return this;
  }

  @Nonnull
  public List <IJExpression> exprs ()
  {
    if (_exprs == null)
      _exprs = new ArrayList <IJExpression> ();
    return Collections.unmodifiableList (_exprs);
  }

  public boolean hasExprs ()
  {
    return _exprs != null && !_exprs.isEmpty ();
  }

  public void generate (@Nonnull final JFormatter f)
  {
    // generally we produce new T[x], but when T is an array type (T=T'[])
    // then new T'[][x] is wrong. It has to be new T'[x][].
    int arrayCount = 0;
    AbstractJType t = _type;
    final boolean hasExprs = hasExprs ();

    while (t.isArray ())
    {
      t = t.elementType ();
      arrayCount++;
    }

    f.print ("new").generable (t).print ('[');
    if (_size != null)
      f.generable (_size);
    f.print (']');

    for (int i = 0; i < arrayCount; i++)
      f.print ("[]");

    if (_size == null || hasExprs)
      f.print ('{');
    if (hasExprs)
      f.generable (_exprs);
    else
      f.print (' ');
    if (_size == null || hasExprs)
      f.print ('}');
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || getClass () != o.getClass ())
      return false;
    final JArray rhs = (JArray) o;
    return isEqual (_type.fullName (), rhs._type.fullName ()) &&
           isEqual (_size, rhs._size) &&
           isEqual (_exprs, rhs._exprs);
  }

  @Override
  public int hashCode ()
  {
    return getHashCode (this, _type.fullName (), _size, _exprs);
  }

  @Override
  AbstractJType derivedType ()
  {
    return _type.array ();
  }

  @Override
  String derivedName ()
  {
    return JCStringUtils.lower (_type.name ()) + "ArrayOfSize" + _size.expressionName ();
  }

  @Override
  public boolean forAllSubExpressions (final ExpressionCallback callback)
  {
    if (_size != null)
    {
      if (!visitWithSubExpressions (callback, new ExpressionAccessor ()
      {
        public void set (final IJExpression newExpression)
        {
          _size = newExpression;
        }

        public IJExpression get ()
        {
          return _size;
        }
      }))
        return false;
    }
    for (int i = 0; i < (_exprs != null ? _exprs.size () : 0); i++)
    {
      final IJExpression expr = _exprs.get (i);
      final int finalI = i;
      if (!visitWithSubExpressions (callback, new ExpressionAccessor ()
      {
        public void set (final IJExpression newExpression)
        {
          _exprs.set (finalI, newExpression);
        }

        public IJExpression get ()
        {
          return _exprs.get (finalI);
        }
      }))
        return false;
    }
    return true;
  }
}
