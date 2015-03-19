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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.jcodemodel.JTypeWildcard.EBoundMode;

/**
 * Represents a Java reference type, such as a class, an interface, an enum, an
 * array type, a parameterized type.
 * <p>
 * To be exact, this object represents an "use" of a reference type, not
 * necessarily a declaration of it, which is modeled as {@link JDefinedClass}.
 */
public abstract class AbstractJClass extends AbstractJType
{
  /**
   * Sometimes useful reusable empty array.
   */
  public static final JTypeVar [] EMPTY_ARRAY = new JTypeVar [0];

  private final JCodeModel m_aOwner;
  private JArrayClass m_aArrayClass;

  protected AbstractJClass (@Nonnull final JCodeModel aOwner)
  {
    if (aOwner == null)
      throw new NullPointerException ("owner");
    m_aOwner = aOwner;
  }

  /**
   * Gets the name of this class.
   *
   * @return name of this class, without any qualification. For example, this
   *         method returns "String" for <code>java.lang.String</code>.
   */
  @Override
  public abstract String name ();

  /**
   * Gets the package to which this class belongs. TODO: shall we move move this
   * down?
   *
   * @return The {@link JPackage} this class belongs to. Is usually not
   *         <code>null</code> except for the {@link JTypeVar} and the
   *         {@link JTypeWildcard} implementation.
   */
  public abstract JPackage _package ();

  /**
   * Returns the class in which this class is nested, or <tt>null</tt> if this
   * is a top-level class.
   */
  @Nullable
  public AbstractJClass outer ()
  {
    return null;
  }

  @Nonnull
  public final JCodeModel owner ()
  {
    return m_aOwner;
  }

  /**
   * Gets the super class of this class.
   *
   * @return Returns the {@link AbstractJClass} representing the superclass of
   *         the entity (class or interface) represented by this
   *         {@link AbstractJClass}. Even if no super class is given explicitly
   *         or this {@link AbstractJClass} is not a class, this method still
   *         returns {@link AbstractJClass} for {@link Object}. If this
   *         {@link AbstractJClass} represents {@link Object}, return null.
   */
  @Nullable
  public abstract AbstractJClass _extends ();

  /**
   * Iterates all super interfaces directly implemented by this class/interface.
   *
   * @return A non-null valid iterator that iterates all {@link AbstractJClass}
   *         objects that represents those interfaces implemented by this
   *         object.
   */
  @Nonnull
  public abstract Iterator <AbstractJClass> _implements ();

  /**
   * Checks if this object represents an interface.
   */
  public abstract boolean isInterface ();

  /**
   * Checks if this class is an abstract class.
   */
  public abstract boolean isAbstract ();

  /**
   * If this class represents one of the wrapper classes defined in the
   * java.lang package, return the corresponding primitive type. Otherwise null.
   */
  @Nullable
  public JPrimitiveType getPrimitiveType ()
  {
    return null;
  }

  /**
   * @deprecated calling this method from {@link AbstractJClass} would be
   *             meaningless, since it's always guaranteed to return
   *             <tt>this</tt>.
   */
  @Deprecated
  @Override
  public AbstractJClass boxify ()
  {
    return this;
  }

  @Override
  @Nonnull
  public AbstractJType unboxify ()
  {
    final JPrimitiveType pt = getPrimitiveType ();
    return pt == null ? (AbstractJType) this : pt;
  }

  @Override
  @Nonnull
  public AbstractJClass erasure ()
  {
    return this;
  }

  /**
   * Gets the parameterization of the given base type.
   * <p>
   * For example, given the following
   *
   * <pre>
   * <code>
   * interface Foo&lt;T&gt; extends List&lt;List&lt;T&gt;&gt; {}
   * interface Bar extends Foo&lt;String&gt; {}
   * </code>
   * </pre>
   *
   * This method works like this:
   *
   * <pre>
   * <code>
   * getBaseClass( Bar, List ) = List&lt;List&lt;String&gt;
   * getBaseClass( Bar, Foo  ) = Foo&lt;String&gt;
   * getBaseClass( Foo&lt;? extends Number&gt;, Collection ) = Collection&lt;List&lt;? extends Number&gt;&gt;
   * getBaseClass( ArrayList&lt;? extends BigInteger&gt;, List ) = List&lt;? extends BigInteger&gt;
   * </code>
   * </pre>
   *
   * @param baseType
   *        The class whose parameterization we are interested in.
   * @return The use of {@code baseType} in {@code this} type. or null if the
   *         type is not assignable to the base type.
   */
  @Nullable
  public final AbstractJClass getBaseClass (@Nonnull final AbstractJClass baseType)
  {
    if (erasure ().equals (baseType))
      return this;

    final AbstractJClass b = _extends ();
    if (b != null)
    {
      final AbstractJClass bc = b.getBaseClass (baseType);
      if (bc != null)
        return bc;
    }

    final Iterator <AbstractJClass> itfs = _implements ();
    while (itfs.hasNext ())
    {
      final AbstractJClass bc = itfs.next ().getBaseClass (baseType);
      if (bc != null)
        return bc;
    }

    return null;
  }

  @Nullable
  public final AbstractJClass getBaseClass (@Nonnull final Class <?> baseType)
  {
    return getBaseClass (owner ().ref (baseType));
  }

  @Override
  @Nonnull
  public JArrayClass array ()
  {
    if (m_aArrayClass == null)
      m_aArrayClass = new JArrayClass (owner (), this);
    return m_aArrayClass;
  }

  /**
   * "Narrows" a generic class to a concrete class by specifying a type
   * argument.
   * <p>
   * <code>.narrow(X)</code> builds <code>Set&lt;X&gt;</code> from
   * <code>Set</code>.
   */
  @Nonnull
  public JNarrowedClass narrow (@Nonnull final Class <?> clazz)
  {
    return narrow (owner ().ref (clazz));
  }

  @Nonnull
  public AbstractJClass narrow (@Nonnull final Class <?>... clazz)
  {
    final AbstractJClass [] r = new AbstractJClass [clazz.length];
    for (int i = 0; i < clazz.length; i++)
      r[i] = owner ().ref (clazz[i]);
    return narrow (r);
  }

  /**
   * "Narrows" a generic class to a concrete class by specifying a type
   * argument.
   * <p>
   * <code>.narrow(X)</code> builds <code>Set&lt;X&gt;</code> from
   * <code>Set</code>.
   */
  @Nonnull
  public JNarrowedClass narrow (final AbstractJClass clazz)
  {
    return new JNarrowedClass (this, clazz);
  }

  @Nonnull
  public JNarrowedClass narrow (@Nonnull final AbstractJType type)
  {
    return narrow (type.boxify ());
  }

  @Nonnull
  public AbstractJClass narrow (@Nonnull final AbstractJClass... clazz)
  {
    if (clazz.length == 0)
      return this;
    return new JNarrowedClass (this, Arrays.asList (clazz.clone ()));
  }

  @Nonnull
  public AbstractJClass narrow (@Nonnull final List <? extends AbstractJClass> clazz)
  {
    if (clazz.isEmpty ())
      return this;
    return new JNarrowedClass (this, new ArrayList <AbstractJClass> (clazz));
  }

  /**
   * If this class is parameterized, return the type parameter of the given
   * index.
   */
  @Nonnull
  public List <? extends AbstractJClass> getTypeParameters ()
  {
    return Collections.emptyList ();
  }

  /**
   * Iterates all the type parameters of this class/interface.
   * <p>
   * For example, if this {@link AbstractJClass} represents
   * <code>Set&lt;T&gt;</code>, this method returns an array that contains
   * single {@link JTypeVar} for 'T'.
   */
  @Nonnull
  public JTypeVar [] typeParams ()
  {
    return EMPTY_ARRAY;
  }

  /**
   * Returns true if this class is a parameterized class.
   */
  public final boolean isParameterized ()
  {
    return erasure () != this;
  }

  /**
   * Create "? extends T" from T.
   *
   * @return never null
   */
  @Nonnull
  public final JTypeWildcard wildcard ()
  {
    return wildcard (EBoundMode.EXTENDS);
  }

  /**
   * Create "? super T" from T.
   *
   * @return never null
   */
  @Nonnull
  public final JTypeWildcard wildcardSuper ()
  {
    return wildcard (EBoundMode.SUPER);
  }

  /**
   * Create "? extends T" from T or "? super T" from T.
   *
   * @param eMode
   *        "extends" or "super"
   * @return never null
   */
  @Nonnull
  public final JTypeWildcard wildcard (@Nonnull final EBoundMode eMode)
  {
    return new JTypeWildcard (this, eMode);
  }

  /**
   * Substitutes the type variables with their actual arguments.
   * <p>
   * For example, when this class is Map&lt;String,Map&lt;V&gt;&gt;, (where V
   * then doing substituteParams( V, Integer ) returns a {@link AbstractJClass}
   * for <code>Map&lt;String,Map&lt;Integer&gt;&gt;</code>.
   * <p>
   * This method needs to work recursively.
   */
  protected abstract AbstractJClass substituteParams (@Nonnull JTypeVar [] variables,
                                                      @Nonnull List <? extends AbstractJClass> bindings);

  @Override
  public String toString ()
  {
    return getClass ().getName () + '(' + name () + ')';
  }

  @Nonnull
  public final IJExpression dotclass ()
  {
    return JExpr.dotclass (this);
  }

  /**
   * Generates a static method invocation.
   */
  @Nonnull
  public final JInvocation staticInvoke (@Nonnull final JMethod method)
  {
    return new JInvocation (this, method);
  }

  /**
   * Generates a static method invocation.
   */
  @Nonnull
  public final JInvocation staticInvoke (@Nonnull final String method)
  {
    return new JInvocation (this, method);
  }

  /**
   * Static field reference.
   */
  @Nonnull
  public final JFieldRef staticRef (final String field)
  {
    return new JFieldRef (this, field);
  }

  /**
   * Static field reference.
   */
  @Nonnull
  public final JFieldRef staticRef (final JVar field)
  {
    return new JFieldRef (this, field);
  }

  public void generate (@Nonnull final JFormatter f)
  {
    f.type (this);
  }

  /**
   * Prints the class name in javadoc @link format.
   */
  void printLink (@Nonnull final JFormatter f)
  {
    f.print ("{@link ").generable (this).print ('}');
  }
}
