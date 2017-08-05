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

import java.util.Collection;

import javax.annotation.Nonnull;

/**
 * The common aspect of a package and a class.
 *
 * @author Philip Helger
 * @param <CLASSTYPE>
 *        Implementation type
 */
public interface IJClassContainer <CLASSTYPE extends IJClassContainer <CLASSTYPE>> extends IJOwned
{
  /**
   * @return <code>true</code> if the container is a class, <code>false</code>
   *         if it is a package
   * @see #isPackage()
   */
  boolean isClass ();

  /**
   * @return <code>true</code> if the container is a package, <code>false</code>
   *         if it is a class.
   * @see #isClass()
   */
  boolean isPackage ();

  /**
   * @return Parent {@link IJClassContainer}. If this is a package, this method
   *         returns a parent package, or <code>null</code> if this package is
   *         the root package. If this is an outer-most class, this method
   *         returns a {@link JPackage} to which it belongs. If this is an inner
   *         class, this method returns the outer class.
   */
  IJClassContainer <?> parentContainer ();

  /**
   * @return The nearest package parent. If <tt>this.isPackage()</tt>, then
   *         return <tt>this</tt>.
   */
  JPackage getPackage ();

  /**
   * Creates a new class/enum/interface/annotation. This is the most generic
   * method.
   *
   * @param nMods
   *        Modifiers for this ...
   * @param sName
   *        Name of ... to be added to this package.
   * @param eClassType
   *        The type of class to create. May not be <code>null</code>.
   * @return The created ...
   * @throws JClassAlreadyExistsException
   *         If another class/interface/... with the same name already exists
   */
  @Nonnull
  CLASSTYPE _class (int nMods,
                    @Nonnull String sName,
                    @Nonnull EClassType eClassType) throws JClassAlreadyExistsException;

  /**
   * Add a new public class to this class/package.
   *
   * @param sName
   *        Name of class to be added to this package
   * @return Newly generated class
   * @throws JClassAlreadyExistsException
   *         If another class/interface/... with the same name already exists
   */
  @Nonnull
  default CLASSTYPE _class (@Nonnull final String sName) throws JClassAlreadyExistsException
  {
    return _class (JMod.PUBLIC, sName);
  }

  /**
   * Add a new class to this package/class.
   *
   * @param nMods
   *        Modifiers for this class declaration
   * @param sName
   *        Name of class to be added to this package
   * @return Newly generated class
   * @throws JClassAlreadyExistsException
   *         If another class/interface/... with the same name already exists
   */
  @Nonnull
  default CLASSTYPE _class (final int nMods, @Nonnull final String sName) throws JClassAlreadyExistsException
  {
    return _class (nMods, sName, EClassType.CLASS);
  }

  /**
   * Adds a public interface to this package.
   *
   * @param sName
   *        Name of interface to be added to this package
   * @return Newly generated interface
   * @throws JClassAlreadyExistsException
   *         If another class/interface/... with the same name already exists
   */
  @Nonnull
  default CLASSTYPE _interface (@Nonnull final String sName) throws JClassAlreadyExistsException
  {
    return _interface (JMod.PUBLIC, sName);
  }

  /**
   * Add an interface to this class/package.
   *
   * @param nMods
   *        Modifiers for this interface declaration
   * @param sName
   *        Name of interface to be added to this package
   * @return Newly generated interface
   * @throws JClassAlreadyExistsException
   *         If another class/interface/... with the same name already exists
   */
  @Nonnull
  default CLASSTYPE _interface (final int nMods, @Nonnull final String sName) throws JClassAlreadyExistsException
  {
    return _class (JMod.PUBLIC, sName, EClassType.INTERFACE);
  }

  /**
   * Add an annotationType Declaration to this package
   *
   * @param sName
   *        Name of the annotation Type declaration to be added to this package
   * @return newly created Annotation Type Declaration
   * @throws JClassAlreadyExistsException
   *         If another class/interface/... with the same name already exists
   */
  @Nonnull
  default CLASSTYPE _annotationTypeDeclaration (@Nonnull final String sName) throws JClassAlreadyExistsException
  {
    return _annotationTypeDeclaration (JMod.PUBLIC, sName);
  }

  /**
   * Add an annotationType Declaration to this package
   *
   * @param nMods
   *        Modifiers for this annotation Type declaration
   * @param sName
   *        Name of the annotation Type declaration to be added to this package
   * @return newly created Annotation Type Declaration
   * @throws JClassAlreadyExistsException
   *         If another class/interface/... with the same name already exists
   */
  @Nonnull
  default CLASSTYPE _annotationTypeDeclaration (final int nMods,
                                                @Nonnull final String sName) throws JClassAlreadyExistsException
  {
    return _class (nMods, sName, EClassType.ANNOTATION_TYPE_DECL);
  }

  /**
   * Add a public enum to this package
   *
   * @param sName
   *        Name of the enum to be added to this package
   * @return newly created enum
   * @throws JClassAlreadyExistsException
   *         If another class/interface/... with the same name already exists
   */
  @Nonnull
  default CLASSTYPE _enum (@Nonnull final String sName) throws JClassAlreadyExistsException
  {
    return _enum (JMod.PUBLIC, sName);
  }

  /**
   * Add a enum to this package
   *
   * @param nMods
   *        Modifiers for this enum declaration
   * @param sName
   *        Name of the enum to be added to this package
   * @return newly created Enum
   * @throws JClassAlreadyExistsException
   *         If another class/interface/... with the same name already exists
   */
  @Nonnull
  default CLASSTYPE _enum (final int nMods, @Nonnull final String sName) throws JClassAlreadyExistsException
  {
    return _class (nMods, sName, EClassType.ENUM);
  }

  /**
   * @return A collection with all nested classes defined in this class. Never
   *         <code>null</code>.
   */
  @Nonnull
  Collection <CLASSTYPE> classes ();
}
