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
import javax.annotation.Nullable;

import com.helger.jcodemodel.optimize.ExpressionAccessor;
import com.helger.jcodemodel.optimize.ExpressionCallback;
import com.helger.jcodemodel.util.StringUtils;

/**
 * Field Reference
 */
public class JFieldRef extends AbstractJExpressionAssignmentTargetImpl implements IJOwnedMaybe
{
  private final JCodeModel _owner;

  /**
   * Object expression upon which this field will be accessed, or null for the
   * implicit 'this'.
   */
  private IJGenerable _object;

  /**
   * Name of the field to be accessed. Either this or {@link #_var} is set.
   */
  private final String _name;

  /**
   * Variable to be accessed.
   */
  private final JVar _var;

  /**
   * Indicates if an explicit this should be generated
   */
  private boolean _explicitThis;

  /**
   * Field reference constructor given an object expression and field name.
   * <code>object.name</code> or just <code>name</code> if object is
   * <code>null</code>.
   *
   * @param object
   *        JExpression for the object upon which the named field will be
   *        accessed. May be <code>null</code>.
   * @param name
   *        Name of field to access. May not be <code>null</code>.
   */
  protected JFieldRef (@Nullable final IJExpression object, @Nonnull final String name)
  {
    this (null, object, name, (JVar) null, false);
  }

  protected JFieldRef (@Nullable final IJExpression object, @Nonnull final JVar var)
  {
    this (null, object, (String) null, var, false);
  }

  /**
   * Static field reference.
   */
  protected JFieldRef (final AbstractJType type, @Nonnull final String name)
  {
    this (type.owner (), type, name, (JVar) null, false);
  }

  protected JFieldRef (final AbstractJType type, @Nonnull final JVar var)
  {
    this (type.owner (), type, (String) null, var, false);
  }

  protected JFieldRef (@Nullable final IJGenerable object, @Nonnull final String name, final boolean explicitThis)
  {
    this (null, object, name, (JVar) null, explicitThis);
  }

  protected JFieldRef (@Nullable final IJGenerable object, @Nonnull final JVar var, final boolean explicitThis)
  {
    this (null, object, (String) null, var, explicitThis);
  }

  private JFieldRef (@Nullable final JCodeModel owner,
                     @Nullable final IJGenerable object,
                     @Nullable final String name,
                     @Nullable final JVar var,
                     final boolean explicitThis)
  {
    if (name != null && name.indexOf ('.') >= 0)
      throw new IllegalArgumentException ("Field name contains '.': " + name);
    if (name == null && var == null)
      throw new IllegalArgumentException ("name or var must be present");
    _owner = owner;
    _object = object;
    _name = name;
    _var = var;
    _explicitThis = explicitThis;
  }

  @Nullable
  public JCodeModel owner ()
  {
    return _owner;
  }

  @Nullable
  public IJGenerable object ()
  {
    return _object;
  }

  @Nonnull
  public String name ()
  {
    String name = _name;
    if (name == null)
      name = _var.name ();
    return name;
  }

  @Nullable
  public JVar var ()
  {
    return _var;
  }

  public boolean explicitThis ()
  {
    return _explicitThis;
  }

  public JFieldRef explicitThis (final boolean explicitThis)
  {
    this._explicitThis = explicitThis;
    return this;
  }

  public void generate (@Nonnull final JFormatter f)
  {
    final String name = name ();

    if (_object != null)
      f.generable (_object).print ('.').print (name);
    else
      if (_explicitThis)
        f.print ("this.").print (name);
      else
        f.id (name);
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
    final JFieldRef rhs = (JFieldRef) o;
    return isEqual (_object, rhs._object) && isEqual (name (), rhs.name ());
  }

  @Override
  public int hashCode ()
  {
    return getHashCode (this, _object, name ());
  }

  @Override
  AbstractJType derivedType ()
  {
    if (_var != null)
    {
      return _var.type ();
    }
    return null;
  }

  @Override
  String derivedName ()
  {
    if (_object instanceof IJExpression)
    {
      return ((IJExpression) _object).expressionName () + StringUtils.upper (name ());
    }
    return name ();
  }

  @Override
  public boolean forAllSubExpressions (final ExpressionCallback callback)
  {
    if (_object instanceof IJExpression)
    {
      return visitWithSubExpressions (callback, new ExpressionAccessor ()
      {
        public void set (final IJExpression newExpression)
        {
          _object = newExpression;
        }

        public IJExpression get ()
        {
          return (IJExpression) _object;
        }
      });
    }
    return true;
  }
}
