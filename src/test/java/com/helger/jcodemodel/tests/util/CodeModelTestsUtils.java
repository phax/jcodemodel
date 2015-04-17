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
package com.helger.jcodemodel.tests.util;

import java.io.StringWriter;

import javax.annotation.Nonnull;

import com.helger.jcodemodel.IJDeclaration;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.IJGenerable;
import com.helger.jcodemodel.IJStatement;
import com.helger.jcodemodel.JFormatter;

/**
 * Various utilities for codemodel tests.
 *
 * @author Aleksei Valikov
 */
public final class CodeModelTestsUtils
{
  /** Hidden constructor. */
  private CodeModelTestsUtils ()
  {}

  /**
   * Prints an expression into a string.
   *
   * @param aExpression
   *        expression to print into a string.
   * @return Expression formatted as a string.
   */
  @Nonnull
  public static String toString (@Nonnull final IJExpression aExpression)
  {
    if (aExpression == null)
      throw new IllegalArgumentException ("Generable must not be null.");

    final StringWriter aSW = new StringWriter ();
    final JFormatter formatter = new JFormatter (aSW);
    aExpression.generate (formatter);
    return aSW.toString ();
  }

  /**
   * Prints a declaration into a string.
   *
   * @param aDeclaration
   *        declaration to print into a string.
   * @return Declaration formatted as a string.
   */
  @Nonnull
  public static String toString (@Nonnull final IJDeclaration aDeclaration)
  {
    if (aDeclaration == null)
      throw new IllegalArgumentException ("Declaration must not be null.");

    final StringWriter aSW = new StringWriter ();
    final JFormatter formatter = new JFormatter (aSW);
    aDeclaration.declare (formatter);
    return aSW.toString ();
  }

  /**
   * Prints a statement into a string.
   *
   * @param aStatement
   *        declaration to print into a string.
   * @return Declaration formatted as a string.
   */
  @Nonnull
  public static String toString (@Nonnull final IJStatement aStatement)
  {
    if (aStatement == null)
      throw new IllegalArgumentException ("Statement must not be null.");

    final StringWriter aSW = new StringWriter ();
    final JFormatter formatter = new JFormatter (aSW);
    aStatement.state (formatter);
    return aSW.toString ();
  }

  @Nonnull
  public static String declare (@Nonnull final IJDeclaration declaration)
  {
    if (declaration == null)
      throw new IllegalArgumentException ("Declaration must not be null.");

    final StringWriter aSW = new StringWriter ();
    final JFormatter formatter = new JFormatter (aSW);
    declaration.declare (formatter);
    return aSW.toString ();
  }

  @Nonnull
  public static String generate (@Nonnull final IJGenerable generable)
  {
    if (generable == null)
      throw new IllegalArgumentException ("Generable must not be null.");

    final StringWriter aSW = new StringWriter ();
    final JFormatter formatter = new JFormatter (aSW);
    generable.generate (formatter);
    return aSW.toString ();
  }
}
