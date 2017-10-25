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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.Map;

import org.junit.Test;

import com.helger.jcodemodel.util.CodeModelTestsHelper;

/**
 * @author Kohsuke Kawaguchi
 * @author Philip Helger
 */
public final class JCodeModelTest
{
  @Test
  public void testParseArray () throws Exception
  {
    final JCodeModel cm = new JCodeModel ();
    assertNotNull (cm.parseType ("java.util.ArrayList<java.lang.String[]>[]"));
    assertNotNull (cm.parseType ("java.util.ArrayList<java.util.ArrayList<java.util.ArrayList<java.lang.String[]>[]>[]>[]"));
    assertNotNull (cm.parseType ("java.util.Comparator<? super java.lang.CharSequence[]>[]"));
  }

  @Test
  public void testIssue28 () throws Exception
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass aEnumClass = cm._package ("com.helger.issue28")._enum ("DummyEnum");
    cm._package ("com.helger.issue28.other")
      ._class ("Class")
      .constructor (JMod.PUBLIC)
      .body ()
      .add (JExpr.enumConstantRef (aEnumClass, "CONSTANT").invoke ("toString"));
    CodeModelTestsHelper.parseCodeModel (cm);
  }

  @Test
  public void testRefClass ()
  {
    final JCodeModel cm = new JCodeModel ();
    assertSame (cm.INT, cm._ref (int.class));
  }

  @Test
  public void testCODEMODEL24 () throws JClassAlreadyExistsException
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass jClass = cm._class ("dummy", EClassType.INTERFACE);
    assertEquals ("dummy", jClass.name ());
    assertEquals (EClassType.INTERFACE, jClass.getClassType ());
  }

  @Test
  public void testEmptyNarrowed () throws JClassAlreadyExistsException
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass jClass = cm._class ("EmptyNarrowed", EClassType.INTERFACE);
    final AbstractJClass hashMap = cm.ref (java.util.HashMap.class).narrowEmpty ();
    jClass.field (JMod.PRIVATE, cm.ref (Map.class).narrow (String.class), "strMap", JExpr._new (hashMap));
    CodeModelTestsHelper.parseCodeModel (cm);
  }
}
