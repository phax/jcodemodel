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
package com.helger.jcodemodel.supplementary.issues;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JEnumConstant;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JSwitch;
import com.helger.jcodemodel.JVar;
import com.helger.jcodemodel.util.CodeModelTestsHelper;

/**
 * Test for https://github.com/phax/jcodemodel/issues/41
 *
 * @author Philip Helger
 */
public final class Issue41FuncTest
{
  @Test
  public void testSwitchInnerEnum () throws Exception
  {
    final JCodeModel cm = new JCodeModel ();

    final JDefinedClass c2 = cm._package ("issue41")._class ("Issue41Test");

    final JDefinedClass jEnumClass = c2._enum ("MyEnum");
    final JEnumConstant ca = jEnumClass.enumConstant ("A");
    final JEnumConstant cb = jEnumClass.enumConstant ("B");
    jEnumClass.enumConstant ("C");

    final JMethod m = c2.method (0, cm.VOID, "dummy");
    final JVar p = m.param (jEnumClass, "enumParam");
    final JSwitch s = m.body ()._switch (p);
    s._case (ca).body ()._break ();
    s._case (cb).body ()._break ();
    s._default ().body ()._break ();

    CodeModelTestsHelper.parseCodeModel (cm);
  }

  @Test
  public void testSwitchInt () throws Exception
  {
    final JCodeModel cm = new JCodeModel ();

    final JDefinedClass c2 = cm._package ("issue41")._class ("Issue41Test2");

    final JDefinedClass jEnumClass = c2._enum ("MyEnum");
    final JEnumConstant ca = jEnumClass.enumConstant ("A");
    final JEnumConstant cb = jEnumClass.enumConstant ("B");
    jEnumClass.enumConstant ("C");

    final JMethod m = c2.method (0, jEnumClass, "dummy");
    final JVar p = m.param (cm.INT, "val");
    final JSwitch s = m.body ()._switch (p);
    s._case (JExpr.lit (1)).body ()._return (ca);
    s._case (JExpr.lit (2)).body ()._return (cb);
    s._default ().body ()._return (JExpr._null ());

    CodeModelTestsHelper.parseCodeModel (cm);
  }

  @Test
  public void testSwitchInt2 () throws Exception
  {
    final Map <Integer, String> aMap = new HashMap <> ();
    aMap.put (Integer.valueOf (1), "One");
    aMap.put (Integer.valueOf (2), "Two");
    aMap.put (Integer.valueOf (3), "Three");

    final JCodeModel cm = new JCodeModel ();

    final JDefinedClass c2 = cm._package ("issue41")._class ("Issue41Test3");
    final JDefinedClass jEnumClass = c2._enum ("MyEnum");

    final JMethod m = c2.method (0, jEnumClass, "dummy");
    final JVar p = m.param (cm.INT, "val");
    final JSwitch s = m.body ()._switch (p);
    for (final Map.Entry <Integer, String> aEntry : aMap.entrySet ())
    {
      final JEnumConstant ec = jEnumClass.enumConstant (aEntry.getValue ());
      s._case (JExpr.lit (aEntry.getKey ().intValue ())).body ()._return (ec);
    }

    CodeModelTestsHelper.parseCodeModel (cm);
  }
}
