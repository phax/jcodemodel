/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * JMethod invocation
 */
public class JInvocation extends AbstractJExpressionImpl implements IJStatement, IJOwnedMaybe
{
  private final JCodeModel _owner;

  /**
   * Object expression upon which this method will be invoked, or null if this
   * is a constructor invocation
   */
  private final IJGenerable _object;

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
  private final List <IJExpression> args = new ArrayList <IJExpression> ();

  /**
   * If isConstructor==true, this field keeps the type to be created.
   */
  private final AbstractJType _type;

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
    args.add (arg);
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
    return args.toArray (new IJExpression [args.size ()]);
  }

  /**
   * Returns all arguments of the invocation.
   * 
   * @return If there's no arguments, an empty list will be returned.
   */
  @Nonnull
  public List <IJExpression> args ()
  {
    return new ArrayList <IJExpression> (args);
  }

  public void generate (@Nonnull final JFormatter f)
  {
    if (_isConstructor)
    {
      if (_type.isArray ())
      {
        // [RESULT] new T[]{arg1,arg2,arg3,...};
        f.print ("new").generable (_type).print ('{');
      }
      else
      {
        // [RESULT] new T(
        f.print ("new").generable (_type).print ('(');
      }
    }
    else
    {
      // method name
      String name = _methodName;
      if (name == null)
        name = _method.name ();

      if (_object != null)
        f.generable (_object).print ('.').print (name).print ('(');
      else
        f.id (name).print ('(');
    }

    // Method arguments
    f.generable (args);

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
}
