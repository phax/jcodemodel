/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2020 Philip Helger + contributors
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * {@link JPackage} tests.
 */
public final class JPackageTest
{
  @Test
  public void testGetParent () throws Exception
  {
    // Create JCodeModel
    final JCodeModel aCM = JCodeModel.createUnified();

    // Reflect into class
    final AbstractJClass wClass = aCM.ref (JExpr.class);

    // Walk up to the root package
    JPackage wCurrentPackage = wClass._package ();
    while (wCurrentPackage.parent () != null) {
      wCurrentPackage = wCurrentPackage.parent ();
    }

    assertNotNull (wCurrentPackage);
    assertNull (wCurrentPackage.parent ());
  }

  @Test
  public void testInvalidNamesAnyCase ()
  {
    final JCodeModel aCM = JCodeModel.createUnified();

    assertFalse (JPackage.isForcePackageNameLowercase ());

    // May not contain empty parts
    try
    {
      aCM._package (".");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    // May not contain empty parts
    try
    {
      aCM._package ("abc.");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    // May not contain empty parts
    try
    {
      aCM._package ("abc.def.");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    // May not contain empty parts
    try
    {
      aCM._package (".abc");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    // May not contain empty parts
    try
    {
      aCM._package (".abc.def");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    // May not contain empty parts
    try
    {
      aCM._package ("abc..def");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    // May not start with a number
    try
    {
      aCM._package ("123");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    // May not be a keyword
    try
    {
      aCM._package ("class");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    // May not be a keyword
    try
    {
      aCM._package ("org.example.enum");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    // May not be a keyword
    try
    {
      aCM._package ("org.class.simple");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    // May not contain special chars
    try
    {
      aCM._package ("org.pub$.anything");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    // May not contain special chars
    try
    {
      aCM._package ("org.pub+.anything");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    // May not contain special chars
    try
    {
      aCM._package ("org.pub.any/thing");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    // Allow for uppercase stuff
    aCM._package ("Org.PUB.blaFooBar.baZ");
  }

  @Test
  public void testInvalidNamesLowerCase ()
  {
    final JCodeModel aCM = JCodeModel.createUnified();

    assertFalse (JPackage.isForcePackageNameLowercase ());
    try
    {
      // Enforce lowercase
      JPackage.setForcePackageNameLowercase (true);

      // May not contain an upper case char
      try
      {
        aCM._package ("Abc");
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {}

      // May not contain an upper case char
      try
      {
        aCM._package ("org.EXAMPLE.simple");
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {}

      // May not contain an upper case char
      try
      {
        aCM._package ("org.exmaple.UpperCase");
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {}
    }
    finally
    {
      JPackage.setForcePackageNameLowercase (false);
    }
  }
}
