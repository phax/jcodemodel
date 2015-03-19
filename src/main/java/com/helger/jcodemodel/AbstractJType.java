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

import java.util.Iterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A representation of a type in codeModel. A type is always either primitive (
 * {@link JPrimitiveType}) or a reference type ({@link AbstractJClass}).<br>
 * Note: up to version 2.7.6 this class implemented
 * <code>Comparable &lt;AbstractJType&gt;</code>. Since this was specific to
 * import handling on emitting code, it was removed with 2.7.7!
 */
public abstract class AbstractJType implements IJGenerable, IJOwned
{
  /**
   * Obtains a reference to the primitive type object from a type name.
   */
  @Nonnull
  public static JPrimitiveType parse (@Nonnull final JCodeModel codeModel, @Nonnull final String sTypeName)
  {
    if (sTypeName.equals ("void"))
      return codeModel.VOID;
    if (sTypeName.equals ("boolean"))
      return codeModel.BOOLEAN;
    if (sTypeName.equals ("byte"))
      return codeModel.BYTE;
    if (sTypeName.equals ("short"))
      return codeModel.SHORT;
    if (sTypeName.equals ("char"))
      return codeModel.CHAR;
    if (sTypeName.equals ("int"))
      return codeModel.INT;
    if (sTypeName.equals ("float"))
      return codeModel.FLOAT;
    if (sTypeName.equals ("long"))
      return codeModel.LONG;
    if (sTypeName.equals ("double"))
      return codeModel.DOUBLE;
    throw new IllegalArgumentException ("Not a primitive type: " + sTypeName);
  }

  /**
   * Gets the full name of the type. See
   * http://java.sun.com/docs/books/jls/second_edition/html/names.doc.html#25430
   * for the details.
   *
   * @return Strings like "int", "java.lang.String", "java.io.File[]". May be
   *         <code>null</code> for unnamed classes.
   */
  @Nullable
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
   * Tells whether or not this is an error-type.
   * <p>
   * Error types are not actual Java types and shouldn't be used in actually
   * generated code.
   *
   * @see JErrorClass
   */
  public boolean isError ()
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
   * @return the erasure of this type. This is only relevant for narrowed
   *         classes like <code>List&lt;Integer&gt;</code> in which case this
   *         method returns the reference to <code>List</code> without any
   *         generic type parameters.
   */
  @Nonnull
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
   * Checks the relationship between two types.
   * <p>
   * This method performes superset of actions that are performed by
   * {@link Class#isAssignableFrom(Class)} For example,
   * baseClass.isAssignableFrom(derivedClass) is always true.
   * <p>
   * There are two differences of this method and
   * {@link Class#isAssignableFrom(Class)}
   * <ol>
   * <li>This method works with primitive types
   * <li>This method processes generic arguments and supports wildcards
   * </ol>
   * <p>
   * Examples:
   * <ol>
   * <li>[[List]].isAssignableFrom ([[List&lt;T&gt;]])</li>
   * <li>[[List&lt;T&gt;]].isAssignableFrom ([[List]])</li>
   * <li>[[List&lt;? extends Object&gt;]].isAssignableFrom
   * ([[List&lt;Integer&gt;]])</li>
   * <li>[[List&lt;? super Serializable&gt;]].isAssignableFrom
   * ([[List&lt;String&gt;]])</li>
   * <li>[[List&lt;? super Serializable&gt;]].isAssignableFrom
   * ([[List&lt;String&gt;]])</li>
   * <li>[[List&lt;? extends Object&gt;]].isAssignableFrom ([[List&lt;? extends
   * Integer&gt;]])</li>
   * <li>[[List&lt;? extends List&lt;? extends Object&gt;&gt;]].isAssignableFrom
   * ([[List&lt;List&lt;Integer&gt;&gt;]])</li>
   * </ol>
   */
  public boolean isAssignableFrom (final AbstractJType that)
  {
    return isAssignableFrom (that, true);
  }

  protected boolean isAssignableFrom (final AbstractJType that, final boolean allowsRawTypeUnchekedConversion)
  {
    if (isError () || that.isError ())
      return false;
    if (this.equals (that))
      return true;

    if (this.isReference () && that.isReference ())
    {
      final AbstractJClass thisClass = (AbstractJClass) this;
      final AbstractJClass thatClass = (AbstractJClass) that;

      // Bottom: Anything anything = null
      if (thatClass instanceof JNullType)
        return true;

      // Top: Object object = (Anything)anything
      if (thisClass == thisClass._package ().owner ().ref (Object.class))
        return true;

      // Array covariance: i. e. Object[] array1 = (Integer[])array2
      if (this.isArray () && that.isArray ())
        return this.elementType ().isAssignableFrom (that.elementType (), false);

      if (thisClass.erasure ().equals (thatClass.erasure ()))
      {
        // Raw classes: i. e. List list1 = (List<T>)list2;
        if (!thisClass.isParameterized ())
          return true;

        // Raw classes unchecked conversion: i. e. List<T> list1 = (List)list2
        if (!thatClass.isParameterized ())
          return allowsRawTypeUnchekedConversion;

        for (int i = 0; i < thisClass.getTypeParameters ().size (); i++)
        {
          final AbstractJClass thisParameter = thisClass.getTypeParameters ().get (i);
          final AbstractJClass thatParameter = thatClass.getTypeParameters ().get (i);

          if (thisParameter instanceof JTypeWildcard)
          {
            final JTypeWildcard thisWildcard = (JTypeWildcard) thisParameter;

            if (thatParameter instanceof JTypeWildcard)
            {
              final JTypeWildcard thatWildcard = (JTypeWildcard) thatParameter;
              if (thisWildcard.boundMode () != thatWildcard.boundMode ())
                return false;
              if (thisWildcard.boundMode () == JTypeWildcard.EBoundMode.EXTENDS)
                return thisWildcard.bound ().isAssignableFrom (thatWildcard.bound (), false);
              if (thisWildcard.boundMode () == JTypeWildcard.EBoundMode.SUPER)
                return thatWildcard.bound ().isAssignableFrom (thisWildcard.bound (), false);
              throw new IllegalStateException ("Unsupported wildcard bound mode: " + thisWildcard.boundMode ());
            }

            if (thisWildcard.boundMode () == JTypeWildcard.EBoundMode.EXTENDS)
              return thisWildcard.bound ().isAssignableFrom (thatParameter, false);
            if (thisWildcard.boundMode () == JTypeWildcard.EBoundMode.SUPER)
              return thatParameter.isAssignableFrom (thisWildcard.bound (), false);
            throw new IllegalStateException ("Unsupported wildcard bound mode: " + thisWildcard.boundMode ());
          }

          if (!thisParameter.equals (thatParameter))
            return false;
        }
        return true;
      }

      final AbstractJClass thatClassBase = thatClass._extends ();
      if (thatClassBase != null && this.isAssignableFrom (thatClassBase))
        return true;

      final Iterator <AbstractJClass> i = thatClass._implements ();
      while (i.hasNext ())
      {
        final AbstractJClass thatClassInterface = i.next ();
        if (this.isAssignableFrom (thatClassInterface))
          return true;
      }
      // false so far
    }

    return false;
  }
}
