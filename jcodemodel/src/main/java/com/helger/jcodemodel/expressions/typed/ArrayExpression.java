/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2026 Philip Helger + contributors
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
package com.helger.jcodemodel.expressions.typed;

import org.jspecify.annotations.NonNull;

import com.helger.base.enforce.ValueEnforcer;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JArray;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;

/**
 * An expression of an array type, for primitive and reference components alike.
 * <p>
 * The runtime type parameter is the array type itself, so the Java array rules follow from the
 * generics: reference arrays are covariant (<code>String []</code> is accepted by the parameter
 * mirror <code>ITypedExpression&lt;? extends Object []&gt;</code>), primitive arrays are not
 * (<code>int []</code> is neither a <code>long []</code> nor an <code>Object []</code>), and every
 * array is an {@link Object}, a {@link Cloneable} and a {@link java.io.Serializable}.
 *
 * @param <A>
 *        The Java array type, e.g. <code>int []</code> or <code>String []</code>.
 * @param <M>
 *        The typed mirror of a component.
 * @param <P>
 *        The parameter mirror of a component, i.e. what can be stored as an element.
 */
public class ArrayExpression <A, M extends ITypedExpression <?>, P extends ITypedExpression <?>> extends AbstractTypedExpression <A> implements
                             IReferenceExpression <A>
{
  private final ArrayType <A, M, P> m_aType;

  protected ArrayExpression (@NonNull final ArrayType <A, M, P> aType, @NonNull final IJExpression aRaw)
  {
    super (aRaw);
    ValueEnforcer.notNull (aType, "Type");
    m_aType = aType;
  }

  @NonNull
  public static <A, M extends ITypedExpression <?>, P extends ITypedExpression <?>> ArrayExpression <A, M, P> of (@NonNull final ArrayType <A, M, P> aType,
                                                                              @NonNull final IJExpression aRaw)
  {
    return new ArrayExpression <> (aType, aRaw);
  }

  /**
   * Add a parameter of an array type to a method.
   *
   * @param <A>
   *        The Java array type.
   * @param <M>
   *        The typed mirror of a component.
   * @param <P>
   *        The parameter mirror of a component.
   * @param aMethod
   *        The method to add the parameter to. May not be <code>null</code>.
   * @param aType
   *        The array type. May not be <code>null</code>.
   * @param sName
   *        The parameter name. May neither be <code>null</code> nor empty.
   * @return The typed parameter reference. Never <code>null</code>.
   */
  @NonNull
  public static <A, M extends ITypedExpression <?>, P extends ITypedExpression <?>> ArrayExpression <A, M, P> param (@NonNull final JMethod aMethod,
                                                                                 @NonNull final ArrayType <A, M, P> aType,
                                                                                 @NonNull final String sName)
  {
    return of (aType, aMethod.param (aType.arrayType (aMethod.owner ()), sName));
  }

  /**
   * @param <A>
   *        The Java array type.
   * @param <M>
   *        The typed mirror of a component.
   * @param <P>
   *        The parameter mirror of a component.
   * @param aCM
   *        The code model. May not be <code>null</code>.
   * @param aType
   *        The array type. May not be <code>null</code>.
   * @param aSize
   *        The array size. May not be <code>null</code>.
   * @return <code>new T [size]</code>
   */
  @NonNull
  public static <A, M extends ITypedExpression <?>, P extends ITypedExpression <?>> ArrayExpression <A, M, P> newArray (@NonNull final JCodeModel aCM,
                                                                                    @NonNull final ArrayType <A, M, P> aType,
                                                                                    @NonNull final IIntConvertible <?> aSize)
  {
    return of (aType, JExpr.newArray (aType.componentType ().type (aCM), aSize.raw ()));
  }

  /**
   * @param <A>
   *        The Java array type.
   * @param <M>
   *        The typed mirror of a component.
   * @param <P>
   *        The parameter mirror of a component.
   * @param aCM
   *        The code model. May not be <code>null</code>.
   * @param aType
   *        The array type. May not be <code>null</code>.
   * @param aValues
   *        The elements. May not be <code>null</code>.
   * @return <code>new T [] { values }</code>
   */
  @NonNull
  @SafeVarargs
  public static <A, M extends ITypedExpression <?>, P extends ITypedExpression <?>> ArrayExpression <A, M, P> newArrayOf (@NonNull final JCodeModel aCM,
                                                                                                                      @NonNull final ArrayType <A, M, P> aType,
                                                                                                                      @NonNull final P... aValues)
  {
    ValueEnforcer.notNull (aValues, "Values");
    final JArray aArray = JExpr.newArray (aType.componentType ().type (aCM));
    for (final P aValue : aValues)
      aArray.add (aValue.raw ());
    return of (aType, aArray);
  }

  /**
   * @param aIndex
   *        The index. May not be <code>null</code>.
   * @return <code>this[index]</code>
   */
  @NonNull
  public M component (@NonNull final IIntConvertible <?> aIndex)
  {
    return m_aType.componentType ().wrap (raw ().component (aIndex.raw ()));
  }

  /**
   * @param nIndex
   *        The index.
   * @return <code>this[index]</code>
   */
  @NonNull
  public M component (final int nIndex)
  {
    return m_aType.componentType ().wrap (raw ().component (nIndex));
  }

  /**
   * @param aIndex
   *        The index. May not be <code>null</code>.
   * @return <code>this[index]</code> as assignment target. Storing into a covariant array can
   *         still throw an {@link ArrayStoreException} at runtime, exactly like in Java.
   */
  @NonNull
  public TypedVariable <M, P> componentVar (@NonNull final IIntConvertible <?> aIndex)
  {
    return TypedVariable.of (m_aType.componentType (), raw ().component (aIndex.raw ()));
  }

  /**
   * @return <code>this.length</code>
   */
  @NonNull
  public IntExpression length ()
  {
    return IntExpression.of (raw ().ref ("length"));
  }
}
