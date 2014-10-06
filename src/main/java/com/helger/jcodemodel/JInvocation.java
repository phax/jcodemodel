/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2014 Philip Helger
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

import com.helger.jcodemodel.optimize.ExpressionAccessor;
import com.helger.jcodemodel.optimize.ExpressionCallback;
import com.helger.jcodemodel.optimize.ExpressionContainer;
import com.helger.jcodemodel.util.HashCodeGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.helger.jcodemodel.util.EqualsUtils.isEqual;
import static com.helger.jcodemodel.util.StringUtils.upper;

/**
 * {@link JMethod} invocation
 */
public class JInvocation extends AbstractJExpressionImpl implements IJStatement, IJOwnedMaybe
{
  private final JCodeModel _owner;

  /**
   * Object expression upon which this method will be invoked, or null if this
   * is a constructor invocation
   */
  private IJGenerable _object;

  /**
   * Name of the method to be invoked. Either this field is set, or
   * {@link #_method}, or {@link #_type} (in which case it's a constructor
   * invocation.) This allows {@link JMethod#name(String) the name of the method
   * to be changed later}.
   */
  private final String _methodName;

  private final JMethod _method;

  private final boolean _isConstructor;

  /**
   * List of argument expressions for this method invocation
   */
  private final List <IJExpression> _args = new ArrayList <IJExpression> ();

  /**
   * If isConstructor==true, this field keeps the type to be created.
   */
  private final AbstractJType _type;

  /**
   * Lazily created list of {@link JTypeVar}s.
   */
  private List <JTypeVar> _typeVariables;

  /**
   * Invokes a method on an object.
   * 
   * @param object
   *        JExpression for the object upon which the named method will be
   *        invoked, or null if none
   * @param name
   *        Name of method to invoke
   */
  protected JInvocation (@Nullable final IJExpression object, @Nonnull final String name)
  {
    // Not possible to determine an owner :(
    this (null, object, name);
  }

  protected JInvocation (@Nullable final IJExpression object, @Nonnull final JMethod method)
  {
    this (method.owner (), object, method);
  }

  /**
   * Invokes a static method on a class.
   */
  protected JInvocation (@Nonnull final AbstractJClass type, @Nonnull final String name)
  {
    this (type.owner (), type, name);
  }

  protected JInvocation (@Nonnull final AbstractJClass type, @Nonnull final JMethod method)
  {
    this (type.owner (), type, method);
  }

  private JInvocation (@Nullable final JCodeModel owner, @Nullable final IJGenerable object, @Nonnull final String name)
  {
    if (name.indexOf ('.') >= 0)
      throw new IllegalArgumentException ("method name contains '.': " + name);
    _owner = owner;
    _object = object;
    _methodName = name;
    _method = null;
    _isConstructor = false;
    _type = null;
  }

  private JInvocation (@Nonnull final JCodeModel owner,
                       @Nullable final IJGenerable object,
                       @Nonnull final JMethod method)
  {
    _owner = owner;
    _object = object;
    _methodName = null;
    _method = method;
    _isConstructor = false;
    _type = null;
  }

  /**
   * Invokes a constructor of an object (i.e., creates a new object.)
   * 
   * @param c
   *        Type of the object to be created. If this type is an array type,
   *        added arguments are treated as array initializer. Thus you can
   *        create an expression like <code>new int[]{1,2,3,4,5}</code>.
   */
  protected JInvocation (@Nonnull final AbstractJType c)
  {
    _owner = c.owner ();
    _object = null;
    _methodName = null;
    _method = null;
    _isConstructor = true;
    _type = c;
  }

  @Nullable
  public JCodeModel owner ()
  {
    return _owner;
  }

  public boolean isConstructor ()
  {
    return _isConstructor;
  }

  /**
   * Add an expression to this invocation's argument list
   * 
   * @param arg
   *        Argument to add to argument list
   */
  @Nonnull
  public JInvocation arg (@Nonnull final IJExpression arg)
  {
    if (arg == null)
      throw new IllegalArgumentException ("argument may not be null");
    _args.add (arg);
    return this;
  }

  /**
   * Adds a literal argument. Short for {@code arg(JExpr.lit(v))}
   */
  @Nonnull
  public JInvocation arg (@Nonnull final boolean v)
  {
    return arg (JExpr.lit (v));
  }

  /**
   * Adds a literal argument. Short for {@code arg(JExpr.lit(v))}
   */
  @Nonnull
  public JInvocation arg (@Nonnull final char v)
  {
    return arg (JExpr.lit (v));
  }

  /**
   * Adds a literal argument. Short for {@code arg(JExpr.lit(v))}
   */
  @Nonnull
  public JInvocation arg (@Nonnull final double v)
  {
    return arg (JExpr.lit (v));
  }

  /**
   * Adds a literal argument. Short for {@code arg(JExpr.lit(v))}
   */
  @Nonnull
  public JInvocation arg (@Nonnull final float v)
  {
    return arg (JExpr.lit (v));
  }

  /**
   * Adds a literal argument. Short for {@code arg(JExpr.lit(v))}
   */
  @Nonnull
  public JInvocation arg (@Nonnull final int v)
  {
    return arg (JExpr.lit (v));
  }

  /**
   * Adds a literal argument. Short for {@code arg(JExpr.lit(v))}
   */
  @Nonnull
  public JInvocation arg (@Nonnull final long v)
  {
    return arg (JExpr.lit (v));
  }

  /**
   * Adds a literal argument. Short for {@code arg(JExpr.lit(v))}
   */
  @Nonnull
  public JInvocation arg (@Nonnull final String v)
  {
    return arg (JExpr.lit (v));
  }

  /**
   * Returns all arguments of the invocation.
   * 
   * @return If there's no arguments, an empty array will be returned.
   */
  @Nonnull
  public IJExpression [] listArgs ()
  {
    return _args.toArray (new IJExpression [_args.size ()]);
  }

  /**
   * Returns all arguments of the invocation.
   * 
   * @return If there's no arguments, an empty list will be returned.
   */
  @Nonnull
  public List <IJExpression> args ()
  {
    return new ArrayList <IJExpression> (_args);
  }

  @Nonnull
  private JCodeModel _narrowOwner ()
  {
    final JCodeModel owner = owner ();
    if (owner == null)
      throw new IllegalStateException ("No owner is present, so this invocation cannot be generified!");
    return owner;
  }

  @Nonnull
  public JInvocation narrow (@Nonnull final String name)
  {
    final JTypeVar v = new JTypeVar (_narrowOwner (), name);
    if (_typeVariables == null)
      _typeVariables = new ArrayList <JTypeVar> (3);
    _typeVariables.add (v);
    return this;
  }

  @Nonnull
  public JInvocation narrow (@Nonnull final Class <?> bound)
  {
    return narrow (_narrowOwner ().ref (bound));
  }

  @Nonnull
  public JInvocation narrow (@Nonnull final AbstractJClass bound)
  {
    final JTypeVar v = new JTypeVarClass (bound);
    if (_typeVariables == null)
      _typeVariables = new ArrayList <JTypeVar> (3);
    _typeVariables.add (v);
    return this;
  }

  @Nonnull
  public List <JTypeVar> typeParamList ()
  {
    if (_typeVariables == null)
      return Collections.<JTypeVar> emptyList ();
    return new ArrayList <JTypeVar> (_typeVariables);
  }

  private void _addTypeVars (@Nonnull final JFormatter f)
  {
    if (_typeVariables != null && !_typeVariables.isEmpty ())
    {
      f.print ('<');
      int nIndex = 0;
      for (final JTypeVar aTypeVar : _typeVariables)
      {
        if (nIndex++ > 0)
          f.print (',');
        // Use type here to get the import (if needed)
        f.type (aTypeVar);
      }
      f.print (JFormatter.CLOSE_TYPE_ARGS);
    }
  }

  private String methodName ()
  {
    return _methodName != null ? _methodName : _method.name ();
  }

  public void generate (@Nonnull final JFormatter f)
  {
    if (_isConstructor)
    {
      if (_type.isArray ())
      {
        // [RESULT] new T[]{arg1,arg2,arg3,...};
        f.print ("new").generable (_type);
        _addTypeVars (f);
        f.print ('{');
      }
      else
      {
        // [RESULT] new T(
        f.print ("new").generable (_type);
        _addTypeVars (f);
        f.print ('(');
      }
    }
    else
    {
      String name = methodName ();

      if (_object != null)
      {
        // object.<generics> name (
        f.generable (_object).print ('.');
        _addTypeVars (f);
        f.print (name).print ('(');
      }
      else
      {
        // name (
        f.id (name).print ('(');
      }
    }

    // Method arguments
    f.generable (_args);

    // Close arg list
    if (_isConstructor && _type.isArray ())
      f.print ('}');
    else
      f.print (')');

    if (_type instanceof JDefinedClass && ((JDefinedClass) _type).isAnonymous ())
    {
      ((JAnonymousClass) _type).declareBody (f);
    }
  }

  public void state (@Nonnull final JFormatter f)
  {
    f.generable (this).print (';').newline ();
  }

  private String typeFullName ()
  {
    return _type != null ? _type.fullName () : "";
  }

  public boolean equals (Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof IJExpression))
      return false;
    o = ((IJExpression) o).unwrapped ();
    if (o == null || getClass () != o.getClass ())
      return false;
    JInvocation rhs = (JInvocation) o;
    if (!(isEqual (_object, rhs._object) &&
        isEqual (methodName (), rhs.methodName ()) &&
        isEqual (_isConstructor, rhs._isConstructor) &&
        isEqual (_args, rhs._args) &&
        isEqual (typeFullName (), rhs.typeFullName ())))
    {
      return false;
    }
    if (_typeVariables == null)
      return rhs._typeVariables == null;
    if (rhs._typeVariables == null)
      return false;
    if (_typeVariables.size () != rhs._typeVariables.size ())
      return false;
    for (int i = 0; i < _typeVariables.size (); i++)
    {
      if (!isEqual (_typeVariables.get (i).fullName (),
          rhs._typeVariables.get (i).fullName ()))
        return false;
    }
    return true;
  }

  public int hashCode ()
  {
    HashCodeGenerator hashCodeGenerator = new HashCodeGenerator (this)
        .append (_object)
        .append (methodName ())
        .append (_isConstructor)
        .append (_args)
        .append (typeFullName ());
    if (_typeVariables != null)
    {
      hashCodeGenerator = hashCodeGenerator.append (_typeVariables.size ());
      for (JTypeVar typeVariable : _typeVariables)
      {
        hashCodeGenerator = hashCodeGenerator.append (typeVariable.fullName ());
      }
    }
    return hashCodeGenerator.getHashCode ();
  }

  @Override
  AbstractJType derivedType ()
  {
    if (_type != null)
      return _type;
    if (_method != null)
      return _method.type ();
    return null;
  }

  @Override
  String derivedName ()
  {
    String name;
    if (_object instanceof IJExpression)
    {
      name = ((IJExpression) _object).expressionName () +
          upper (methodName ());
    } else
    {
      name = methodName ();
    }
    for (IJExpression arg : _args)
    {
      name += upper (arg.expressionName ());
    }
    return name;
  }

  @Override
  public boolean forAllSubExpressions (ExpressionCallback callback)
  {
    if (_object instanceof IJExpression)
    {
      if (!visitWithSubExpressions (callback, new ExpressionAccessor ()
      {
        public void set (IJExpression newExpression)
        {
          _object = newExpression;
        }

        public IJExpression get ()
        {
          return (IJExpression) _object;
        }
      }))
        return false;
    }
    for (int i = 0; i < _args.size (); i++)
    {
      final int finalI = i;
      if (!visitWithSubExpressions (callback, new ExpressionAccessor ()
      {
        public void set (IJExpression newExpression)
        {
          _args.set (finalI, newExpression);
        }

        public IJExpression get ()
        {
          return _args.get (finalI);
        }
      }))
        return false;
    }
    if (_type instanceof ExpressionContainer)
    {
      return ((ExpressionContainer) _type).forAllSubExpressions (callback);
    }
    return true;
  }
}
