/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2017 Philip Helger + contributors
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.jcodemodel.util.JCHashCodeGenerator;
import com.helger.jcodemodel.util.JCValueEnforcer;

/**
 * {@link JMethod} invocation
 */
public class JInvocation implements IJExpressionStatement, IJOwnedMaybe
{
  private final JCodeModel m_aOwner;

  /**
   * Object expression upon which this method will be invoked, or null if this
   * is a constructor invocation
   */
  private final IJGenerable m_aObject;

  /**
   * Name of the method to be invoked. Either this field is set, or
   * {@link #m_aMethod}, or {@link #m_aConstructorType} (in which case it's a
   * constructor invocation.) This allows {@link JMethod#name(String) the name
   * of the method to be changed later}.
   */
  private final String m_sMethodName;

  private final JMethod m_aMethod;

  private final boolean m_bIsConstructor;

  /**
   * List of argument expressions for this method invocation
   */
  private final List <IJExpression> m_aArgs = new ArrayList <> ();

  /**
   * If isConstructor==true, this field keeps the type to be created.
   */
  private final AbstractJType m_aConstructorType;

  /**
   * Lazily created list of {@link JTypeVar}s.
   */
  private List <JTypeVar> m_aTypeVariables;

  /**
   * Invokes a method on an object.
   *
   * @param aObject
   *        JExpression for the object upon which the named method will be
   *        invoked, or null if none
   * @param sName
   *        Name of method to invoke
   */
  protected JInvocation (@Nullable final IJExpression aObject, @Nonnull final String sName)
  {
    // Not possible to determine an owner :(
    this (null, aObject, sName);
  }

  protected JInvocation (@Nullable final IJExpression aObject, @Nonnull final JMethod aMethod)
  {
    this (aMethod.owner (), aObject, aMethod);
  }

  /**
   * Invokes a static method on a class.
   *
   * @param aType
   *        Parent type
   * @param sMethodName
   *        Method name to be invoked
   */
  protected JInvocation (@Nonnull final AbstractJClass aType, @Nonnull final String sMethodName)
  {
    this (aType.owner (), aType, sMethodName);
  }

  /**
   * Invokes a static method on a class.
   *
   * @param aType
   *        Parent type
   * @param aMethod
   *        Method to be invoked
   */
  protected JInvocation (@Nonnull final AbstractJClass aType, @Nonnull final JMethod aMethod)
  {
    this (aType.owner (), aType, aMethod);
  }

  private JInvocation (@Nullable final JCodeModel aOwner,
                       @Nullable final IJGenerable aObject,
                       @Nonnull final String sName)
  {
    JCValueEnforcer.notNull (sName, "Name");
    JCValueEnforcer.isFalse (sName.indexOf ('.') >= 0, () -> "method name contains '.': " + sName);
    m_aOwner = aOwner;
    m_aObject = aObject;
    m_sMethodName = sName;
    m_aMethod = null;
    m_bIsConstructor = false;
    m_aConstructorType = null;
  }

  private JInvocation (@Nonnull final JCodeModel aOwner,
                       @Nullable final IJGenerable aObject,
                       @Nonnull final JMethod aMethod)
  {
    JCValueEnforcer.notNull (aOwner, "Owner");
    JCValueEnforcer.notNull (aMethod, "Method");
    m_aOwner = aOwner;
    m_aObject = aObject;
    m_sMethodName = null;
    m_aMethod = aMethod;
    m_bIsConstructor = false;
    m_aConstructorType = null;
  }

  /**
   * Invokes a constructor of an object (i.e., creates a new object.)
   *
   * @param aConstructorType
   *        Type of the object to be created. If this type is an array type,
   *        added arguments are treated as array initializer. Thus you can
   *        create an expression like <code>new int[]{1,2,3,4,5}</code>.
   */
  protected JInvocation (@Nonnull final AbstractJType aConstructorType)
  {
    JCValueEnforcer.notNull (aConstructorType, "ConstructorType");
    m_aOwner = aConstructorType.owner ();
    m_aObject = null;
    m_sMethodName = null;
    m_aMethod = null;
    m_bIsConstructor = true;
    m_aConstructorType = aConstructorType;
  }

  @Nullable
  public JCodeModel owner ()
  {
    return m_aOwner;
  }

  public boolean isConstructor ()
  {
    return m_bIsConstructor;
  }

  /**
   * Add an expression to this invocation's argument list
   *
   * @param aArg
   *        Argument to add to argument list
   * @return this for chaining
   */
  @Nonnull
  public JInvocation arg (@Nonnull final IJExpression aArg)
  {
    JCValueEnforcer.notNull (aArg, "Argument");
    m_aArgs.add (aArg);
    return this;
  }

  /**
   * Adds a literal argument. Short for {@code arg(JExpr.lit(v))}
   *
   * @param v
   *        Value to be added to the argument list
   * @return this for chaining
   */
  @Nonnull
  public JInvocation arg (@Nonnull final boolean v)
  {
    return arg (JExpr.lit (v));
  }

  /**
   * Adds a literal argument. Short for {@code arg(JExpr.lit(v))}
   *
   * @param v
   *        Value to be added to the argument list
   * @return this for chaining
   */
  @Nonnull
  public JInvocation arg (@Nonnull final char v)
  {
    return arg (JExpr.lit (v));
  }

  /**
   * Adds a literal argument. Short for {@code arg(JExpr.lit(v))}
   *
   * @param v
   *        Value to be added to the argument list
   * @return this for chaining
   */
  @Nonnull
  public JInvocation arg (@Nonnull final double v)
  {
    return arg (JExpr.lit (v));
  }

  /**
   * Adds a literal argument. Short for {@code arg(JExpr.lit(v))}
   *
   * @param v
   *        Value to be added to the argument list
   * @return this for chaining
   */
  @Nonnull
  public JInvocation arg (@Nonnull final float v)
  {
    return arg (JExpr.lit (v));
  }

  /**
   * Adds a literal argument. Short for {@code arg(JExpr.lit(v))}
   *
   * @param v
   *        Value to be added to the argument list
   * @return this for chaining
   */
  @Nonnull
  public JInvocation arg (@Nonnull final int v)
  {
    return arg (JExpr.lit (v));
  }

  /**
   * Adds a literal argument. Short for {@code arg(JExpr.lit(v))}
   *
   * @param v
   *        Value to be added to the argument list
   * @return this for chaining
   */
  @Nonnull
  public JInvocation arg (@Nonnull final long v)
  {
    return arg (JExpr.lit (v));
  }

  /**
   * Adds a literal argument. Short for {@code arg(JExpr.lit(v))}
   *
   * @param v
   *        Value to be added to the argument list
   * @return this for chaining
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
   * @deprecated Use {@link #args()} instead
   */
  @Nonnull
  @Deprecated
  public IJExpression [] listArgs ()
  {
    return m_aArgs.toArray (new IJExpression [m_aArgs.size ()]);
  }

  /**
   * Returns all arguments of the invocation.
   *
   * @return If there's no arguments, an empty list will be returned.
   */
  @Nonnull
  public List <IJExpression> args ()
  {
    return new ArrayList <> (m_aArgs);
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
  public JInvocation narrow (@Nonnull final String sName)
  {
    final JTypeVar v = new JTypeVar (_narrowOwner (), sName);
    if (m_aTypeVariables == null)
      m_aTypeVariables = new ArrayList <> (3);
    m_aTypeVariables.add (v);
    return this;
  }

  @Nonnull
  public JInvocation narrow (@Nonnull final Class <?> aBound)
  {
    return narrow (_narrowOwner ().ref (aBound));
  }

  @Nonnull
  public JInvocation narrow (@Nonnull final AbstractJClass aBound)
  {
    final JTypeVarClass v = new JTypeVarClass (aBound);
    if (m_aTypeVariables == null)
      m_aTypeVariables = new ArrayList <> (3);
    m_aTypeVariables.add (v);
    return this;
  }

  @Nonnull
  public List <JTypeVar> typeParamList ()
  {
    if (m_aTypeVariables == null)
      return Collections.<JTypeVar> emptyList ();
    return new ArrayList <> (m_aTypeVariables);
  }

  private void _addTypeVars (@Nonnull final JFormatter f)
  {
    if (m_aTypeVariables != null && !m_aTypeVariables.isEmpty ())
    {
      f.print ('<');
      int nIndex = 0;
      for (final JTypeVar aTypeVar : m_aTypeVariables)
      {
        if (nIndex++ > 0)
          f.print (',');
        // Use type here to get the import (if needed)
        f.type (aTypeVar);
      }
      f.print (JFormatter.CLOSE_TYPE_ARGS);
    }
  }

  @Nullable
  private String _methodName ()
  {
    return m_aMethod != null ? m_aMethod.name () : m_sMethodName;
  }

  public void generate (@Nonnull final JFormatter f)
  {
    if (m_bIsConstructor)
    {
      if (m_aConstructorType.isArray ())
      {
        // [RESULT] new T[]{arg1,arg2,arg3,...};
        f.print ("new").generable (m_aConstructorType);
        _addTypeVars (f);
        f.print ('{');
      }
      else
      {
        // [RESULT] new T(
        f.print ("new").generable (m_aConstructorType);
        _addTypeVars (f);
        f.print ('(');
      }
    }
    else
    {
      // Not a constructor
      final String name = _methodName ();

      if (m_aObject != null)
      {
        // object.<generics> name (
        f.generable (m_aObject).print ('.');
        _addTypeVars (f);
        f.print (name);
        f.print ('(');
      }
      else
      {
        // name (
        f.id (name).print ('(');
      }
    }

    // Method arguments
    f.generable (m_aArgs);

    // Close arg list
    if (m_bIsConstructor && m_aConstructorType.isArray ())
      f.print ('}');
    else
      f.print (')');

    if (m_aConstructorType instanceof JDefinedClass && ((JDefinedClass) m_aConstructorType).isAnonymous ())
    {
      ((JAnonymousClass) m_aConstructorType).declareBody (f);
    }
  }

  public void state (@Nonnull final JFormatter f)
  {
    f.generable (this).print (';').newline ();
  }

  @Nonnull
  private String _typeFullName ()
  {
    return m_aConstructorType != null ? m_aConstructorType.fullName () : "";
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || getClass () != o.getClass ())
      return false;
    final JInvocation rhs = (JInvocation) o;
    if (!(isEqual (m_aObject, rhs.m_aObject) &&
          isEqual (m_bIsConstructor, rhs.m_bIsConstructor) &&
          (m_bIsConstructor || isEqual (_methodName (), rhs._methodName ())) &&
          isEqual (m_aArgs, rhs.m_aArgs) &&
          isEqual (_typeFullName (), rhs._typeFullName ())))
    {
      return false;
    }
    if (m_aTypeVariables == null)
      return rhs.m_aTypeVariables == null;
    if (rhs.m_aTypeVariables == null)
      return false;
    if (m_aTypeVariables.size () != rhs.m_aTypeVariables.size ())
      return false;
    for (int i = 0; i < m_aTypeVariables.size (); i++)
    {
      if (!isEqual (m_aTypeVariables.get (i).fullName (), rhs.m_aTypeVariables.get (i).fullName ()))
        return false;
    }
    return true;
  }

  @Override
  public int hashCode ()
  {
    JCHashCodeGenerator hashCodeGenerator = new JCHashCodeGenerator (this).append (m_aObject).append (m_bIsConstructor);
    if (!m_bIsConstructor)
      hashCodeGenerator = hashCodeGenerator.append (_methodName ());
    hashCodeGenerator = hashCodeGenerator.append (m_aArgs).append (_typeFullName ());
    if (m_aTypeVariables != null)
    {
      hashCodeGenerator = hashCodeGenerator.append (m_aTypeVariables.size ());
      for (final JTypeVar typeVariable : m_aTypeVariables)
      {
        hashCodeGenerator = hashCodeGenerator.append (typeVariable.fullName ());
      }
    }
    return hashCodeGenerator.getHashCode ();
  }

  /**
   * Create a special <code>super()</code> invocation. It may only be used as
   * the first statement inside a constructor - but this is not enforced by this
   * API!
   * 
   * @return A new non-<code>null</code> {@link JInvocation}
   * @since 3.0.1
   */
  @Nonnull
  public static JInvocation _super ()
  {
    return new JInvocation (null, null, JExpr._super ().what ());
  }
}
