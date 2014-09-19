/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.jcodemodel.util;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.w3c.dom.Node;

/**
 * A small hash code creation class based on the article found in the net. See
 * <a href=
 * "http://www.angelikalanger.com/Articles/JavaSpektrum/03.HashCode/03.HashCode.html"
 * >this article</a> for details.<br>
 * After calling {@link #append(Object)} for all objects use
 * {@link #getHashCode()} to retrieve the calculated hash code. Once the hash
 * code was calculated no modifications are allowed.<br>
 * <p>
 * A real world example for a final class derived from {@link Object} or a base
 * class looks like this:
 * </p>
 *
 * <pre>
 * &#064;Override
 * public int hashCode ()
 * {
 *   return new HashCodeGenerator (this).append (member1).append (member2).getHashCode ();
 * }
 * </pre>
 * <p>
 * For a derived class, the typical code looks like this, assuming the base
 * class also uses {@link HashCodeGenerator}:
 * </p>
 *
 * <pre>
 * &#064;Override
 * public int hashCode ()
 * {
 *   return HashCodeGenerator.getDerived (super.hashCode ()).append (member3).append (member4).getHashCode ();
 * }
 * </pre>
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class HashCodeGenerator
{
  /** Represents an illegal hash code that is never to be returned! */
  public static final int ILLEGAL_HASHCODE = 0;

  /** Use a prime number as the start. */
  public static final int INITIAL_HASHCODE = 17;

  /**
   * Once the hash code generation has been queried, no further changes may be
   * done. This flag indicates, whether new items can be added or not.
   */
  private boolean m_bClosed = false;

  /** The current hash code value. */
  private int m_nHC = INITIAL_HASHCODE;

  /**
   * This is a sanity constructor that allows for any object to be passed in the
   * constructor (e.g. <code>this</code>) from which the class is extracted as
   * the initial value of the hash code.
   *
   * @param aSrcObject
   *        The source object from which the class is extracted. May not be
   *        <code>null</code>.
   */
  public HashCodeGenerator (@Nonnull final Object aSrcObject)
  {
    this (aSrcObject instanceof Class <?> ? (Class <?>) aSrcObject : aSrcObject.getClass ());
  }

  /**
   * This constructor requires a class name, because in case a class has no
   * instance variables the hash code may be the same for different instances of
   * different classes.
   *
   * @param aClass
   *        The class this instance is about to create a hash code for. May not
   *        be <code>null</code>.
   */
  public HashCodeGenerator (@Nonnull final Class <?> aClass)
  {
    if (aClass == null)
      throw new NullPointerException ("class");

    // Use the class name
    append (aClass.getName ());

    // Is it an array class? If so add the component class name.
    final Class <?> aComponentType = aClass.getComponentType ();
    if (aComponentType != null)
      append (aComponentType.getName ());
  }

  private HashCodeGenerator (final int nSuperHashCode)
  {
    m_nHC = nSuperHashCode;
  }

  private void _checkClosed ()
  {
    if (m_bClosed)
      throw new IllegalStateException ("Hash code cannot be changed anymore!");
  }

  /**
   * Atomic type hash code generation.
   *
   * @param x
   *        Array to add
   * @return this
   */
  @Nonnull
  public HashCodeGenerator append (final boolean x)
  {
    _checkClosed ();
    m_nHC = HashCodeCalculator.append (m_nHC, x);
    return this;
  }

  /**
   * Atomic type hash code generation.
   *
   * @param x
   *        Array to add
   * @return this
   */
  @Nonnull
  public HashCodeGenerator append (final byte x)
  {
    _checkClosed ();
    m_nHC = HashCodeCalculator.append (m_nHC, x);
    return this;
  }

  /**
   * Atomic type hash code generation.
   *
   * @param x
   *        Array to add
   * @return this
   */
  @Nonnull
  public HashCodeGenerator append (final char x)
  {
    _checkClosed ();
    m_nHC = HashCodeCalculator.append (m_nHC, x);
    return this;
  }

  /**
   * Atomic type hash code generation.
   *
   * @param x
   *        Array to add
   * @return this
   */
  @Nonnull
  public HashCodeGenerator append (final double x)
  {
    _checkClosed ();
    m_nHC = HashCodeCalculator.append (m_nHC, x);
    return this;
  }

  /**
   * Atomic type hash code generation.
   *
   * @param x
   *        Array to add
   * @return this
   */
  @Nonnull
  public HashCodeGenerator append (final float x)
  {
    _checkClosed ();
    m_nHC = HashCodeCalculator.append (m_nHC, x);
    return this;
  }

  /**
   * Atomic type hash code generation.
   *
   * @param x
   *        Array to add
   * @return this
   */
  @Nonnull
  public HashCodeGenerator append (final int x)
  {
    _checkClosed ();
    m_nHC = HashCodeCalculator.append (m_nHC, x);
    return this;
  }

  /**
   * Atomic type hash code generation.
   *
   * @param x
   *        Array to add
   * @return this
   */
  @Nonnull
  public HashCodeGenerator append (final long x)
  {
    _checkClosed ();
    m_nHC = HashCodeCalculator.append (m_nHC, x);
    return this;
  }

  /**
   * Atomic type hash code generation.
   *
   * @param x
   *        Array to add
   * @return this
   */
  @Nonnull
  public HashCodeGenerator append (final short x)
  {
    _checkClosed ();
    m_nHC = HashCodeCalculator.append (m_nHC, x);
    return this;
  }

  /**
   * Object hash code generation.
   *
   * @param x
   *        Array to add
   * @return this
   */
  @Nonnull
  public HashCodeGenerator append (@Nullable final Object x)
  {
    _checkClosed ();
    m_nHC = HashCodeCalculator.append (m_nHC, x);
    return this;
  }

  /**
   * Object hash code generation.
   *
   * @param x
   *        Array to add
   * @return this
   */
  @Nonnull
  public HashCodeGenerator append (@Nullable final Enum <?> x)
  {
    _checkClosed ();
    m_nHC = HashCodeCalculator.append (m_nHC, x);
    return this;
  }

  /**
   * Array hash code generation.
   *
   * @param x
   *        Array to add
   * @return this
   */
  @Nonnull
  public HashCodeGenerator append (@Nullable final boolean [] x)
  {
    _checkClosed ();
    m_nHC = HashCodeCalculator.append (m_nHC, x);
    return this;
  }

  /**
   * Array hash code generation.
   *
   * @param x
   *        Array to add
   * @return this
   */
  @Nonnull
  public HashCodeGenerator append (@Nullable final byte [] x)
  {
    _checkClosed ();
    m_nHC = HashCodeCalculator.append (m_nHC, x);
    return this;
  }

  /**
   * Array hash code generation.
   *
   * @param x
   *        Array to add
   * @return this
   */
  @Nonnull
  public HashCodeGenerator append (@Nullable final char [] x)
  {
    _checkClosed ();
    m_nHC = HashCodeCalculator.append (m_nHC, x);
    return this;
  }

  /**
   * Array hash code generation.
   *
   * @param x
   *        Array to add
   * @return this
   */
  @Nonnull
  public HashCodeGenerator append (@Nullable final double [] x)
  {
    _checkClosed ();
    m_nHC = HashCodeCalculator.append (m_nHC, x);
    return this;
  }

  /**
   * Array hash code generation.
   *
   * @param x
   *        Array to add
   * @return this
   */
  @Nonnull
  public HashCodeGenerator append (@Nullable final float [] x)
  {
    _checkClosed ();
    m_nHC = HashCodeCalculator.append (m_nHC, x);
    return this;
  }

  /**
   * Array hash code generation.
   *
   * @param x
   *        Array to add
   * @return this
   */
  @Nonnull
  public HashCodeGenerator append (@Nullable final int [] x)
  {
    _checkClosed ();
    m_nHC = HashCodeCalculator.append (m_nHC, x);
    return this;
  }

  /**
   * Array hash code generation.
   *
   * @param x
   *        Array to add
   * @return this
   */
  @Nonnull
  public HashCodeGenerator append (@Nullable final long [] x)
  {
    _checkClosed ();
    m_nHC = HashCodeCalculator.append (m_nHC, x);
    return this;
  }

  /**
   * Array hash code generation.
   *
   * @param x
   *        Array to add
   * @return this
   */
  @Nonnull
  public HashCodeGenerator append (@Nullable final short [] x)
  {
    _checkClosed ();
    m_nHC = HashCodeCalculator.append (m_nHC, x);
    return this;
  }

  /**
   * Array hash code generation.
   *
   * @param x
   *        Array to add
   * @return this
   */
  @Nonnull
  public HashCodeGenerator append (@Nullable final Object [] x)
  {
    _checkClosed ();
    m_nHC = HashCodeCalculator.append (m_nHC, x);
    return this;
  }

  /**
   * Array hash code generation.
   *
   * @param x
   *        Array to add
   * @return this
   */
  @Nonnull
  public HashCodeGenerator append (@Nullable final Enum <?> [] x)
  {
    _checkClosed ();
    m_nHC = HashCodeCalculator.append (m_nHC, x);
    return this;
  }

  /**
   * Type specific hash code generation because parameter class has no
   * overloaded equals method.
   *
   * @param x
   *        object to add
   * @return this
   */
  @Nonnull
  public HashCodeGenerator append (@Nullable final StringBuffer x)
  {
    _checkClosed ();
    m_nHC = HashCodeCalculator.append (m_nHC, x);
    return this;
  }

  /**
   * Type specific hash code generation because parameter class has no
   * overloaded equals method.
   *
   * @param x
   *        object to add
   * @return this
   */
  @Nonnull
  public HashCodeGenerator append (@Nullable final StringBuilder x)
  {
    _checkClosed ();
    m_nHC = HashCodeCalculator.append (m_nHC, x);
    return this;
  }

  /**
   * @param x
   *        to be included in the hash code generation.
   * @return this
   */
  @Nonnull
  public HashCodeGenerator append (@Nullable final Iterable <?> x)
  {
    _checkClosed ();
    m_nHC = HashCodeCalculator.append (m_nHC, x);
    return this;
  }

  /**
   * @param x
   *        to be included in the hash code generation.
   * @return this
   */
  @Nonnull
  public HashCodeGenerator append (@Nullable final Map <?, ?> x)
  {
    _checkClosed ();
    m_nHC = HashCodeCalculator.append (m_nHC, x);
    return this;
  }

  /**
   * @param x
   *        to be included in the hash code generation.
   * @return this
   */
  @Nonnull
  public HashCodeGenerator append (@Nullable final Node x)
  {
    _checkClosed ();
    m_nHC = HashCodeCalculator.append (m_nHC, x);
    return this;
  }

  /**
   * Retrieve the final hash code. Once this method has been called, no further
   * calls to append can be done since the hash value is locked!
   *
   * @return The finally completed hash code. The returned value is never
   *         {@link #ILLEGAL_HASHCODE}. If the calculated hash code would be
   *         {@link #ILLEGAL_HASHCODE} it is changed to -1 instead.
   */
  public int getHashCode ()
  {
    m_bClosed = true;

    // This is for the very rare case, that the calculated hash code results in
    // an illegal value.
    if (m_nHC == ILLEGAL_HASHCODE)
      m_nHC = -1;
    return m_nHC;
  }

  /**
   * @return The same as {@link #getHashCode()} but as an {@link Integer}
   *         object. Never <code>null</code>.
   */
  @Nonnull
  public Integer getHashCodeObj ()
  {
    return Integer.valueOf (getHashCode ());
  }

  /**
   * Never compare {@link HashCodeGenerator} objects :)
   */
  @Deprecated
  @Override
  public boolean equals (final Object o)
  {
    return o == this;
  }

  /**
   * Always use {@link #getHashCode()}
   *
   * @return {@link #getHashCode()}
   * @see #getHashCode()
   */
  @Override
  @Deprecated
  public int hashCode ()
  {
    return getHashCode ();
  }

  /**
   * Create a {@link HashCodeGenerator} for derived classes where the base class
   * also uses the {@link HashCodeGenerator}. This avoid calculating the hash
   * code of the class name more than once.
   *
   * @param nSuperHashCode
   *        Always pass in <code>super.hashCode ()</code>
   * @return Never <code>null</code>
   */
  @Nonnull
  public static HashCodeGenerator getDerived (final int nSuperHashCode)
  {
    if (nSuperHashCode == ILLEGAL_HASHCODE)
      throw new IllegalArgumentException ("Passed hash code is invalid!");
    return new HashCodeGenerator (nSuperHashCode);
  }
}
