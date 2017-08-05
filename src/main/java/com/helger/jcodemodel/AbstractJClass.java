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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.jcodemodel.util.JCValueEnforcer;

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
    m_aOwner = JCValueEnforcer.notNull (aOwner, "Owner");
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
   * @return the class in which this class is nested, or <tt>null</tt> if this
   *         is a top-level class.
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
   * @return <code>true</code> if this object represents an interface.
   */
  public abstract boolean isInterface ();

  /**
   * @return <code>true</code> if this class is an abstract class.
   */
  public abstract boolean isAbstract ();

  /**
   * @return If this class represents one of the wrapper classes defined in the
   *         <code>java.lang</code> package, return the corresponding primitive
   *         type. Otherwise <code>null</code>.
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
  public final AbstractJClass boxify ()
  {
    return this;
  }

  @Override
  @Nonnull
  public final AbstractJType unboxify ()
  {
    final JPrimitiveType aPrimitiveType = getPrimitiveType ();
    return aPrimitiveType == null ? this : aPrimitiveType;
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
   * @param aBaseType
   *        The class whose parameterization we are interested in.
   * @return The use of {@code baseType} in {@code this} type. or null if the
   *         type is not assignable to the base type.
   */
  @Nullable
  public final AbstractJClass getBaseClass (@Nonnull final AbstractJClass aBaseType)
  {
    if (erasure ().equals (aBaseType))
      return this;

    final AbstractJClass b = _extends ();
    if (b != null)
    {
      final AbstractJClass bc = b.getBaseClass (aBaseType);
      if (bc != null)
        return bc;
    }

    final Iterator <AbstractJClass> itfs = _implements ();
    while (itfs.hasNext ())
    {
      final AbstractJClass bc = itfs.next ().getBaseClass (aBaseType);
      if (bc != null)
        return bc;
    }

    return null;
  }

  @Nullable
  public final AbstractJClass getBaseClass (@Nonnull final Class <?> aBaseType)
  {
    return getBaseClass (owner ().ref (aBaseType));
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
   * argument.<br>
   * <code>.narrow(X)</code> builds <code>Set&lt;X&gt;</code> from
   * <code>Set</code>.
   *
   * @param aClazz
   *        class to narrow with
   * @return Never <code>null</code>. Narrowed class.
   */
  @Nonnull
  public JNarrowedClass narrow (@Nonnull final Class <?> aClazz)
  {
    return narrow (owner ().ref (aClazz));
  }

  @Nonnull
  public AbstractJClass narrow (@Nonnull final Class <?>... aClazzes)
  {
    final List <AbstractJClass> r = new ArrayList <> (aClazzes.length);
    for (final Class <?> aElement : aClazzes)
      r.add (owner ().ref (aElement));
    return narrow (r);
  }

  /**
   * "Narrows" a generic class to a concrete class by specifying a type
   * argument. <br>
   * <code>.narrow(X)</code> builds <code>Set&lt;X&gt;</code> from
   * <code>Set</code>.
   *
   * @param aClazz
   *        class to narrow with
   * @return Never <code>null</code>. Narrowed class.
   */
  @Nonnull
  public JNarrowedClass narrow (@Nonnull final AbstractJClass aClazz)
  {
    return new JNarrowedClass (this, aClazz);
  }

  @Nonnull
  public JNarrowedClass narrow (@Nonnull final AbstractJType aType)
  {
    return narrow (aType.boxify ());
  }

  @Nonnull
  public AbstractJClass narrow (@Nonnull final AbstractJClass... aClazz)
  {
    return new JNarrowedClass (this, Arrays.asList (aClazz.clone ()));
  }

  @Nonnull
  public AbstractJClass narrow (@Nonnull final List <? extends AbstractJClass> aClazz)
  {
    return new JNarrowedClass (this, new ArrayList <> (aClazz));
  }

  /**
   * @return A narrowed type without any type parameter (as in
   *         <code>HashMap &lt;&gt;</code>)
   */
  @Nonnull
  public AbstractJClass narrowEmpty ()
  {
    return new JNarrowedClass (this, new ArrayList <> ());
  }

  /**
   * @return A narrowed type just with a "?" parameter (as in
   *         <code>HashMap &lt;?&gt;</code>)
   * @since 3.0.0
   */
  @Nonnull
  public AbstractJClass narrowAny ()
  {
    return narrow (owner ().wildcard ());
  }

  /**
   * @return If this class is parameterized, the type parameters of the given
   *         index.
   */
  @Nonnull
  public List <? extends AbstractJClass> getTypeParameters ()
  {
    return Collections.emptyList ();
  }

  /**
   * Iterates all the type parameters of this class/interface. <br>
   * For example, if this {@link AbstractJClass} represents
   * <code>Set&lt;T&gt;</code>, this method returns an array that contains
   * single {@link JTypeVar} for 'T'.
   *
   * @return All type parameters as array.
   */
  @Nonnull
  public JTypeVar [] typeParams ()
  {
    return EMPTY_ARRAY;
  }

  /**
   * @return <code>true</code> if this class is a parameterized class.
   */
  public final boolean isParameterized ()
  {
    return erasure () != this;
  }

  /**
   * Create "? extends T" from T.
   *
   * @return never <code>null</code>
   * @deprecated Use {@link #wildcardExtends()} instead
   */
  @Nonnull
  @Deprecated
  public final JTypeWildcard wildcard ()
  {
    return wildcardExtends ();
  }

  /**
   * Create "? extends T" from T.
   *
   * @return never <code>null</code>
   * @since 3.0.0
   */
  @Nonnull
  public final JTypeWildcard wildcardExtends ()
  {
    return wildcard (EWildcardBoundMode.EXTENDS);
  }

  /**
   * Create "? super T" from T.
   *
   * @return never <code>null</code>
   */
  @Nonnull
  public final JTypeWildcard wildcardSuper ()
  {
    return wildcard (EWildcardBoundMode.SUPER);
  }

  /**
   * Create "? extends T" from T or "? super T" from T.
   *
   * @param eMode
   *        "extends" or "super"
   * @return never <code>null</code>
   */
  @Nonnull
  public final JTypeWildcard wildcard (@Nonnull final EWildcardBoundMode eMode)
  {
    return new JTypeWildcard (this, eMode);
  }

  /**
   * Substitutes the type variables with their actual arguments. <br>
   * For example, when this class is Map&lt;String,Map&lt;V&gt;&gt;, (where V
   * then doing substituteParams( V, Integer ) returns a {@link AbstractJClass}
   * for <code>Map&lt;String,Map&lt;Integer&gt;&gt;</code>. <br>
   * This method needs to work recursively.
   *
   * @param aVariables
   *        Type variables
   * @param aBindings
   *        Bindings
   * @return Never <code>null</code>.
   */
  @Nonnull
  protected abstract AbstractJClass substituteParams (@Nonnull JTypeVar [] aVariables,
                                                      @Nonnull List <? extends AbstractJClass> aBindings);

  /**
   * @return name<code>.class</code>
   */
  @Nonnull
  public final IJExpression dotclass ()
  {
    return JExpr.dotclass (this);
  }

  /**
   * Generates a static method invocation.
   *
   * @param aMethod
   *        Method to be invoked
   * @return Newly created {@link JInvocation}
   */
  @Nonnull
  public final JInvocation staticInvoke (@Nonnull final JMethod aMethod)
  {
    return new JInvocation (this, aMethod);
  }

  /**
   * Generates a static method invocation.
   *
   * @param sMethod
   *        Method to be invoked
   * @return Newly created {@link JInvocation}
   */
  @Nonnull
  public final JInvocation staticInvoke (@Nonnull final String sMethod)
  {
    return new JInvocation (this, sMethod);
  }

  /**
   * Static field reference.
   *
   * @param sField
   *        Field to be referenced
   * @return Newly created {@link JFieldRef}
   */
  @Nonnull
  public final JFieldRef staticRef (@Nonnull final String sField)
  {
    return new JFieldRef (this, sField);
  }

  /**
   * Static field reference.
   *
   * @param aField
   *        Field to be referenced
   * @return Newly created {@link JFieldRef}
   */
  @Nonnull
  public final JFieldRef staticRef (@Nonnull final JVar aField)
  {
    return new JFieldRef (this, aField);
  }

  /**
   * Method reference for JDK8 (as in <code>String::valueOf</code>).
   *
   * @param sMethod
   *        Method to be referenced
   * @return Newly created {@link JLambdaMethodRef}
   */
  @Nonnull
  public final JLambdaMethodRef methodRef (@Nonnull final String sMethod)
  {
    return new JLambdaMethodRef (this, sMethod);
  }

  public void generate (@Nonnull final JFormatter f)
  {
    f.type (this);
  }

  /**
   * Prints the class name in javadoc @link format.
   *
   * @param f
   *        Formatter to be used
   */
  void printLink (@Nonnull final JFormatter f)
  {
    f.print ("{@link ").generable (this).print ('}');
  }

  @Override
  public String toString ()
  {
    return getClass ().getName () + '(' + fullName () + ')';
  }
}
