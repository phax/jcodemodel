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
 * Describes a Java type for variables, array components and other places that are read and
 * written. Instances can only be created via the constants and the factory methods, which keeps
 * both type parameters consistent with each other.
 *
 * @param <M>
 *        The typed mirror of a value of this type, used when reading.
 * @param <P>
 *        The parameter mirror of this type, i.e. what can be assigned to it.
 */
public final class TypedType <M extends ITypedExpression <?>, P extends ITypedExpression <?>>
{
  /** <code>boolean</code> */
  public static final TypedType <BooleanExpression, IBooleanConvertible <?>> BOOLEAN = new TypedType <> (aCM -> aCM.BOOLEAN, BooleanExpression::of);

  /** <code>byte</code> */
  public static final TypedType <ByteExpression, IByteConvertible <?>> BYTE = new TypedType <> (aCM -> aCM.BYTE, ByteExpression::of);

  /** <code>short</code> */
  public static final TypedType <ShortExpression, IShortConvertible <?>> SHORT = new TypedType <> (aCM -> aCM.SHORT, ShortExpression::of);

  /** <code>char</code> */
  public static final TypedType <CharExpression, ICharConvertible <?>> CHAR = new TypedType <> (aCM -> aCM.CHAR, CharExpression::of);

  /** <code>int</code> */
  public static final TypedType <IntExpression, IIntConvertible <?>> INT = new TypedType <> (aCM -> aCM.INT, IntExpression::of);

  /** <code>long</code> */
  public static final TypedType <LongExpression, ILongConvertible <?>> LONG = new TypedType <> (aCM -> aCM.LONG, LongExpression::of);

  /** <code>float</code> */
  public static final TypedType <FloatExpression, IFloatConvertible <?>> FLOAT = new TypedType <> (aCM -> aCM.FLOAT, FloatExpression::of);

  /** <code>double</code> */
  public static final TypedType <DoubleExpression, IDoubleConvertible <?>> DOUBLE = new TypedType <> (aCM -> aCM.DOUBLE, DoubleExpression::of);

  /** {@link Integer} */
  public static final TypedType <IntegerExpression, ITypedExpression <? extends Integer>> INTEGER = of (Integer.class,
                                                                                                    IntegerExpression::of);

  /** {@link Character} */
  public static final TypedType <CharacterExpression, ITypedExpression <? extends Character>> CHARACTER = of (Character.class,
                                                                                                          CharacterExpression::of);

  /** {@link String} */
  public static final TypedType <StringExpression, ITypedExpression <? extends String>> STRING = of (String.class,
                                                                                                 StringExpression::of);

  private final Function <JCodeModel, AbstractJType> m_aTypeFct;
  private final Function <IJExpression, M> m_aFactory;

  TypedType (@NonNull final Function <JCodeModel, AbstractJType> aTypeFct,
             @NonNull final Function <IJExpression, M> aFactory)
  {
    m_aTypeFct = aTypeFct;
    m_aFactory = aFactory;
  }

  /**
   * @param aCM
   *        The code model to resolve the type in. May not be <code>null</code>.
   * @return The Java type. Never <code>null</code>.
   */
  @NonNull
  public AbstractJType type (@NonNull final JCodeModel aCM)
  {
    return m_aTypeFct.apply (aCM);
  }

  /**
   * @param aRaw
   *        An untyped expression of this type. May not be <code>null</code>.
   * @return The typed mirror. Never <code>null</code>.
   */
  @NonNull
  public M wrap (@NonNull final IJExpression aRaw)
  {
    return m_aFactory.apply (aRaw);
  }

  /**
   * A reference type with a dedicated mirror.
   *
   * @param <E>
   *        The Java type.
   * @param <M>
   *        The typed mirror.
   * @param aClass
   *        The class. May not be <code>null</code>.
   * @param aFactory
   *        Creates the typed mirror, e.g. <code>StringExpression::of</code>. May not be
   *        <code>null</code>.
   * @return The type. Never <code>null</code>.
   */
  @NonNull
  public static <E, M extends ITypedExpression <E>> TypedType <M, ITypedExpression <? extends E>> of (@NonNull final Class <E> aClass,
                                                                                                    @NonNull final Function <IJExpression, M> aFactory)
  {
    ValueEnforcer.notNull (aClass, "Class");
    ValueEnforcer.notNull (aFactory, "Factory");
    return new TypedType <> (aCM -> aCM.ref (aClass), aFactory);
  }

  /**
   * A reference type without a dedicated mirror.
   *
   * @param <E>
   *        The Java type.
   * @param aClass
   *        The class. May not be <code>null</code>.
   * @return The type, using {@link ObjectExpression} as mirror. Never <code>null</code>.
   */
  @NonNull
  public static <E> TypedType <ObjectExpression <E>, ITypedExpression <? extends E>> object (@NonNull final Class <E> aClass)
  {
    return of (aClass, ObjectExpression::of);
  }
}
