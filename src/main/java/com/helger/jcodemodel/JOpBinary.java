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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.helger.jcodemodel.optimize.ExpressionAccessor;
import com.helger.jcodemodel.optimize.ExpressionCallback;

public class JOpBinary extends AbstractJExpressionImpl
{

  private static AbstractJType moreGeneral (final AbstractJType left, final AbstractJType right)
  {
    final boolean leftIsDouble = left.name ().equals ("double");
    if (leftIsDouble || right.name ().equals ("double"))
      return leftIsDouble ? left : right;
    final boolean leftIsFloat = left.name ().equals ("float");
    if (leftIsFloat || right.name ().equals ("float"))
      return leftIsFloat ? left : right;
    final boolean leftIsLong = left.name ().equals ("long");
    if (leftIsLong || right.name ().equals ("long"))
      return leftIsLong ? left : right;
    return left.owner ().INT;
  }

  private static final Map <String, String> OP_NAMES = new HashMap <String, String> ()
  {
    {
      put ("instanceof", "Instanceof");
      put ("&", "BinaryAnd");
      put ("|", "BinaryOr");
      put ("+", "Plus");
      put ("-", "Minus");
      // TODO
    }
  };
  private IJExpression _left;
  private final String _op;
  private IJGenerable _right;

  protected JOpBinary (@Nonnull final IJExpression left, @Nonnull final String op, @Nonnull final IJGenerable right)
  {
    this._left = left;
    this._op = op;
    this._right = right;
  }

  @Nonnull
  public IJExpression left ()
  {
    return _left;
  }

  @Nonnull
  public String op ()
  {
    return _op;
  }

  @Nonnull
  public IJGenerable right ()
  {
    return _right;
  }

  public void generate (@Nonnull final JFormatter f)
  {
    f.print ('(').generable (_left).print (_op).generable (_right).print (')');
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
    final JOpBinary rhs = (JOpBinary) o;
    return isEqual (_left, rhs._left) && isEqual (_op, rhs._op) && isEqual (_right, rhs._right);
  }

  @Override
  public int hashCode ()
  {
    return getHashCode (this, _left, _op, _right);
  }

  @Override
  AbstractJType derivedType ()
  {
    final AbstractJType leftExpressionType = _left.expressionType ();
    if (_op.equals ("instanceof"))
      return leftExpressionType.owner ().BOOLEAN;

    final AbstractJType rightExpressionType = ((IJExpression) _right).expressionType ();
    if (_op.equals ("+"))
    {
      final boolean leftIsString = leftExpressionType.fullName ().equals ("java.lang.String");
      final boolean rightIsString = rightExpressionType.fullName ().equals ("java.lang.String");
      if (leftIsString || rightIsString)
      {
        return leftIsString ? leftExpressionType : rightExpressionType;
      }
    }
    if ("+-*/%^".indexOf (_op.charAt (0)) >= 0)
    {
      return moreGeneral (leftExpressionType, rightExpressionType);
    }
    if (_op.equals ("|") || _op.equals ("&"))
    {
      if (leftExpressionType.fullName ().equals ("boolean") && rightExpressionType.fullName ().equals ("boolean"))
        return leftExpressionType;
      return moreGeneral (leftExpressionType, rightExpressionType);
    }
    if (_op.startsWith (">>") || _op.equals ("<<"))
    {
      return leftExpressionType;
    }
    return leftExpressionType.owner ().BOOLEAN;
  }

  @Override
  String derivedName ()
  {
    return _left.expressionName () +
           OP_NAMES.get (_op) +
           (_right instanceof IJExpression ? ((IJExpression) _right).expressionName ()
                                          : ((AbstractJType) _right).fullName ());
  }

  @Override
  public boolean forAllSubExpressions (final ExpressionCallback callback)
  {
    if (!visitWithSubExpressions (callback, new ExpressionAccessor ()
    {
      public void set (final IJExpression newExpression)
      {
        _left = newExpression;
      }

      public IJExpression get ()
      {
        return _left;
      }
    }))
      return false;
    if (_right instanceof IJExpression)
    {
      return visitWithSubExpressions (callback, new ExpressionAccessor ()
      {
        public void set (final IJExpression newExpression)
        {
          _right = newExpression;
        }

        public IJExpression get ()
        {
          return (IJExpression) _right;
        }
      });
    }
    return true;
  }
}
