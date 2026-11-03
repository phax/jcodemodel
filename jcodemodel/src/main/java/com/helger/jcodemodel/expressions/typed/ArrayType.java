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

import java.util.function.Function;

import org.jspecify.annotations.NonNull;

import com.helger.base.enforce.ValueEnforcer;
import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JCodeModel;

/**
 * Describes an array type for {@link ArrayExpression}. Instances can only be created via the
 * constants, {@link #of(Class, Function)} and {@link #array()}, which keeps the three type
 * parameters consistent with each other.
 *
 * @param <A>
 *        The Java array type, e.g. <code>int []</code> or <code>String []</code>.
 * @param <M>
 *        The typed mirror of a component.
 * @param <P>
 *        The parameter mirror of a component, i.e. what can be stored as an element.
 */
public final class ArrayType <A, M extends ITypedExpression <?>, P extends ITypedExpression <?>>
{
  /** <code>boolean []</code> */
  public static final ArrayType <boolean [], BooleanExpression, IBooleanConvertible <?>> BOOLEAN = new ArrayType <> (aCM -> aCM.BOOLEAN, BooleanExpression::of);

  /** <code>byte []</code> */
  public static final ArrayType <byte [], ByteExpression, IByteConvertible <?>> BYTE = new ArrayType <> (aCM -> aCM.BYTE, ByteExpression::of);

  /** <code>short []</code> */
  public static final ArrayType <short [], ShortExpression, IShortConvertible <?>> SHORT = new ArrayType <> (aCM -> aCM.SHORT, ShortExpression::of);

  /** <code>char []</code> */
  public static final ArrayType <char [], CharExpression, ICharConvertible <?>> CHAR = new ArrayType <> (aCM -> aCM.CHAR, CharExpression::of);

  /** <code>int []</code> */
  public static final ArrayType <int [], IntExpression, IIntConvertible <?>> INT = new ArrayType <> (aCM -> aCM.INT, IntExpression::of);

  /** <code>long []</code> */
  public static final ArrayType <long [], LongExpression, ILongConvertible <?>> LONG = new ArrayType <> (aCM -> aCM.LONG, LongExpression::of);

  /** <code>float []</code> */
  public static final ArrayType <float [], FloatExpression, IFloatConvertible <?>> FLOAT = new ArrayType <> (aCM -> aCM.FLOAT, FloatExpression::of);

  /** <code>double []</code> */
  public static final ArrayType <double [], DoubleExpression, IDoubleConvertible <?>> DOUBLE = new ArrayType <> (aCM -> aCM.DOUBLE, DoubleExpression::of);

  private final Function <JCodeModel, AbstractJType> m_aComponentTypeFct;
  private final Function <IJExpression, M> m_aComponentFactory;

  private ArrayType (@NonNull final Function <JCodeModel, AbstractJType> aComponentTypeFct,
                     @NonNull final Function <IJExpression, M> aComponentFactory)
  {
    m_aComponentTypeFct = aComponentTypeFct;
    m_aComponentFactory = aComponentFactory;
  }

  /**
   * @param aCM
   *        The code model to resolve the type in. May not be <code>null</code>.
   * @return The component type. Never <code>null</code>.
   */
  @NonNull
  public AbstractJType componentType (@NonNull final JCodeModel aCM)
  {
    return m_aComponentTypeFct.apply (aCM);
  }

  /**
   * @param aCM
   *        The code model to resolve the type in. May not be <code>null</code>.
   * @return The array type itself. Never <code>null</code>.
   */
  @NonNull
  public AbstractJType arrayType (@NonNull final JCodeModel aCM)
  {
    return componentType (aCM).array ();
  }

  /**
   * @param aRaw
   *        An untyped expression of the component type. May not be <code>null</code>.
   * @return The typed component. Never <code>null</code>.
   */
  @NonNull
  public M wrapComponent (@NonNull final IJExpression aRaw)
  {
    return m_aComponentFactory.apply (aRaw);
  }

  /**
   * @return The type of an array of this array type, e.g. <code>int [] []</code> for
   *         <code>int []</code>. Never <code>null</code>.
   */
  @NonNull
  public ArrayType <A [], ArrayExpression <A, M>, ITypedExpression <? extends A>> array ()
  {
    return new ArrayType <> (this::arrayType, aRaw -> ArrayExpression.of (this, aRaw));
  }

  /**
   * Array type of a reference component type.
   *
   * @param <E>
   *        The component type.
   * @param <M>
   *        The typed mirror of a component.
   * @param aClass
   *        The component class. May not be <code>null</code>.
   * @param aComponentFactory
   *        Creates the typed mirror of a component, e.g. <code>StringExpression::of</code>. May
   *        not be <code>null</code>.
   * @return The array type. Never <code>null</code>.
   */
  @NonNull
  public static <E, M extends ITypedExpression <E>> ArrayType <E [], M, ITypedExpression <? extends E>> of (@NonNull final Class <E> aClass,
                                                                                                         @NonNull final Function <IJExpression, M> aComponentFactory)
  {
    ValueEnforcer.notNull (aClass, "Class");
    ValueEnforcer.notNull (aComponentFactory, "ComponentFactory");
    return new ArrayType <> (aCM -> aCM.ref (aClass), aComponentFactory);
  }
}
