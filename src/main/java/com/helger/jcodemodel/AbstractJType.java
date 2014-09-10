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

import java.util.Iterator;

import javax.annotation.Nonnull;

/**
 * A representation of a type in codeModel. A type is always either primitive (
 * {@link JPrimitiveType}) or a reference type ({@link AbstractJClass}).
 */
public abstract class AbstractJType implements IJGenerable, IJOwned, Comparable <AbstractJType>
{
  /**
   * Obtains a reference to the primitive type object from a type name.
   */
  @Nonnull
  public static JPrimitiveType parse (@Nonnull final JCodeModel codeModel, @Nonnull final String typeName)
  {
    if (typeName.equals ("void"))
      return codeModel.VOID;
    if (typeName.equals ("boolean"))
      return codeModel.BOOLEAN;
    if (typeName.equals ("byte"))
      return codeModel.BYTE;
    if (typeName.equals ("short"))
      return codeModel.SHORT;
    if (typeName.equals ("char"))
      return codeModel.CHAR;
    if (typeName.equals ("int"))
      return codeModel.INT;
    if (typeName.equals ("float"))
      return codeModel.FLOAT;
    if (typeName.equals ("long"))
      return codeModel.LONG;
    if (typeName.equals ("double"))
      return codeModel.DOUBLE;
    throw new IllegalArgumentException ("Not a primitive type: " + typeName);
  }

  /**
   * Gets the full name of the type. See
   * http://java.sun.com/docs/books/jls/second_edition/html/names.doc.html#25430
   * for the details.
   *
   * @return Strings like "int", "java.lang.String", "java.io.File[]". Never
   *         null.
   */
  public abstract String fullName ();

  /**
   * Gets the binary name of the type. See
   * http://java.sun.com/docs/books/jls/third_edition/html/binaryComp.html#44909
   *
   * @return Name like "Foo$Bar", "int", "java.lang.String", "java.io.File[]".
   *         Never null.
   */
  @Nonnull
  public String binaryName ()
  {
    return fullName ();
  }

  /**
   * Gets the name of this type.
   *
   * @return Names like "int", "void", "BigInteger".
   */
  @Nonnull
  public abstract String name ();

  /**
   * Create an array type of this type. This method is undefined for primitive
   * void type, which doesn't have any corresponding array representation.
   *
   * @return A {@link JArrayClass} representing the array type whose element
   *         type is this type
   */
  public abstract JArrayClass array ();

  /** Tell whether or not this is an array type. */
  public boolean isArray ()
  {
    return false;
  }

  /**
   * Tell whether or not this is a built-in primitive type, such as int or void.
   */
  public boolean isPrimitive ()
  {
    return false;
  }

  /**
   * If this class is a primitive type, return the boxed class. Otherwise return
   * <tt>this</tt>.
   * <p>
   * For example, for "int", this method returns "java.lang.Integer".
   */
  public abstract AbstractJClass boxify ();

  /**
   * If this class is a wrapper type for a primitive, return the primitive type.
   * Otherwise return <tt>this</tt>.
   * <p>
   * For example, for "java.lang.Integer", this method returns "int".
   */
  public abstract AbstractJType unboxify ();

  /**
   * Returns the erasure of this type.
   */
  public AbstractJType erasure ()
  {
    return this;
  }

  /**
   * Returns true if this is a referenced type.
   */
  public final boolean isReference ()
  {
    return !isPrimitive ();
  }

  /**
   * If this is an array, returns the component type of the array. (T of T[])
   */
  public AbstractJType elementType ()
  {
    throw new IllegalArgumentException ("Not an array type");
  }

  @Override
  public String toString ()
  {
    return this.getClass ().getName () + '(' + fullName () + ')';
  }

  /**
   * Compare two JTypes by FQCN, giving sorting precedence to types that belong
   * to packages java and javax over all others. This method is used to sort
   * generated import statments in a conventional way for readability.
   */
  public int compareTo (@Nonnull final AbstractJType o)
  {
    final String lhs = fullName ();
    final String rhs = o.fullName ();
    final boolean p = lhs.startsWith ("java");
    final boolean q = rhs.startsWith ("java");

    if (p && !q)
      return -1;
    if (!p && q)
      return 1;
    return lhs.compareTo (rhs);
  }

  public boolean isUnifiableWith (final AbstractJType that)
  {
    if (this == that)
      return true;

    if (this instanceof JTypeWildcard && that instanceof JTypeWildcard)
    {
      final JTypeWildcard thisWildcard = (JTypeWildcard) this;
      final JTypeWildcard thatWildcard = (JTypeWildcard) that;
      if (thisWildcard.boundMode () != thatWildcard.boundMode ())
        return false;
      if (thisWildcard.boundMode () == JTypeWildcard.EBoundMode.EXTENDS)
        return thisWildcard.bound ().isSubtypeOf (thatWildcard.bound ());
      return thisWildcard.bound ().isSupertypeOf (thatWildcard.bound ());
    }
    else
      if (this instanceof JTypeWildcard)
      {
        final JTypeWildcard thisWildcard = (JTypeWildcard) this;
        if (thisWildcard.boundMode () == JTypeWildcard.EBoundMode.EXTENDS)
        {
          final AbstractJClass thisWildcardBase = thisWildcard.bound ();
          return that.isSubtypeOf (thisWildcardBase);
        }
        final AbstractJClass thisWildcardSuper = thisWildcard.bound ();
        return that.isSupertypeOf (thisWildcardSuper);
      }
    if (that instanceof JTypeWildcard)
    {
      final JTypeWildcard thatWildcard = (JTypeWildcard) that;
      if (thatWildcard.boundMode () == JTypeWildcard.EBoundMode.EXTENDS)
      {
        final AbstractJClass thatWildcardBase = thatWildcard.bound ();
        return this.isSubtypeOf (thatWildcardBase);
      }
      final AbstractJClass thatWildcardSuper = thatWildcard.bound ();
      return this.isSupertypeOf (thatWildcardSuper);
    }

    if (this.isArray () && that.isArray ())
      return this.elementType ().isUnifiableWith (that.elementType ());

    if (this.isReference () && that.isReference ())
    {
      final AbstractJClass thisClass = (AbstractJClass) this;
      final AbstractJClass thatClass = (AbstractJClass) that;

      if (thisClass.erasure () == thatClass.erasure () && thisClass.isParameterized () && thatClass.isParameterized ())
      {
        for (int i = 0; i < thisClass.getTypeParameters ().size (); i++)
        {
          final AbstractJClass parameter1 = thisClass.getTypeParameters ().get (i);
          final AbstractJClass parameter2 = thatClass.getTypeParameters ().get (i);
          if (!parameter1.isUnifiableWith (parameter2))
            return false;
        }
        return true;
      }
    }

    return false;
  }

  /***
   * Full fledged supertype relation using rules for generics and wildcards
   */
  public boolean isSupertypeOf (final AbstractJType that)
  {
    return that.isSubtypeOf (this);
  }

  /***
   * Full fledged subtype relation using rules for generics and wildcards
   */
  public boolean isSubtypeOf (final AbstractJType that)
  {
    if (this.isUnifiableWith (that))
      return true;

    if (this.isReference () && that.isReference ())
    {
      final AbstractJClass thisClass = (AbstractJClass) this;
      final AbstractJClass thatClass = (AbstractJClass) that;

      // Bottom
      if (thisClass instanceof JNullType)
        return true;

      // Top
      if (thatClass == thatClass._package ().owner ().ref (Object.class))
        return true;

      // Raw classes: i. e. List<T> <: List and List <: List<T>
      if (thisClass.erasure () == thatClass.erasure () &&
          (!thatClass.isParameterized () || !thisClass.isParameterized ()))
        return true;

      // Array covariance: i. e. Integer[] <: Object[]
      if (this.isArray () && that.isArray ())
        return this.elementType ().isSubtypeOf (that.elementType ());

      final AbstractJClass thisClassBase = thisClass._extends ();
      if (thisClassBase != null && thisClassBase.isSubtypeOf (thatClass))
        return true;

      final Iterator <AbstractJClass> i = thisClass._implements ();
      while (i.hasNext ())
      {
        final AbstractJClass thisClassInterface = i.next ();
        if (thisClassInterface.isSubtypeOf (thatClass))
          return true;
      }
      // false so far
    }

    return false;
  }
}
