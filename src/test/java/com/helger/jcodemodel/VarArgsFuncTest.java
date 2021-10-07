/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2021 Philip Helger + contributors
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

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.helger.jcodemodel.util.CodeModelTestsHelper;

/**
 * Simple program to test the generation of the varargs feature in jdk 1.5
 *
 * @author Bhakti Mehta Bhakti.Mehta@sun.com
 */
/*
 * ====================================================== This is how the output
 * from this program looks like Still need to learn how to work on instantiation
 * and args ========================================================= public
 * class Test { public void foo(java.lang.String param1, java.lang.Integer
 * param2, java.lang.String param5, java.lang.Object... param3) { for (int count
 * = 0; (count<(param3.length)); count ++) {
 * java.lang.System.out.println((param3[count])); } } public static void
 * main(java.lang.String[] args) { } }
 * ==========================================================
 */

public final class VarArgsFuncTest
{
  @Test
  public void testBasic () throws Exception
  {
    final JCodeModel cm = JCodeModel.createUnified ();
    final JDefinedClass cls = cm._package ("org.example")._class ("TestVarArgs");
    final JMethod m = cls.method (JMod.PUBLIC | JMod.STATIC, cm.VOID, "foo");
    m.param (String.class, "param1");
    m.param (Integer.class, "param2");
    final JVar jParam3 = m.varParam (Object.class, "param3");

    // checking for param after varParam it behaves ok
    m.param (String.class, "param4");

    final JForLoop forloop = m.body ()._for ();
    final JVar jcount = forloop.init (cm.INT, "count", JExpr.lit (0));
    forloop.test (jcount.lt (jParam3.ref ("length")));
    forloop.update (jcount.incr ());

    final JVar typearray = m.varParam ();
    assertNotNull (typearray);

    // JInvocation invocation =
    forloop.body ().add (cm.ref (System.class).staticRef ("out").invoke ("println").arg (jParam3.component (jcount)));

    final JMethod main = cls.method (JMod.PUBLIC | JMod.STATIC, cm.VOID, "main");
    main.param (String [].class, "args");
    main.body ()
        .directStatement ("new Test().foo(new String(\"Param1\"),new Integer(5),null,new String(\"Param3\"),new String(\"Param4\"));");

    CodeModelTestsHelper.parseCodeModel (cm);
    if (false)
      CodeModelTestsHelper.compileCodeModel (cm);
  }

  @Test
  public void testExample () throws Exception
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass cls = cm._package ("org.example")._class ("TestVarArgs");
    final JMethod m = cls.method (JMod.PUBLIC, cm.VOID, "foo");
    m.param (String.class, "param1");
    m.param (Integer.class, "param2");
    // That is the VarArg param
    final JVar jParam3 = m.varParam (String.class, "param3");

    final JForLoop forloop = m.body ()._for ();
    final JVar jcount = forloop.init (cm.INT, "count", JExpr.lit (0));
    forloop.test (jcount.lt (jParam3.ref ("length")));
    forloop.update (jcount.incr ());

    forloop.body ().add (cm.ref (System.class).staticRef ("out").invoke ("println").arg (jParam3.component (jcount)));

    final JMethod main = cls.method (JMod.PUBLIC | JMod.STATIC, cm.VOID, "main");
    main.param (String [].class, "args");
    main.body ()
        .add (cls._new ()
                 .invoke (m)
                 .arg ("Param1")
                 .arg (cm.ref (Integer.class).staticInvoke ("valueOf").arg (5))
                 .arg ("Param 3a")
                 .arg ("Param 3b"));

    CodeModelTestsHelper.parseCodeModel (cm);
    CodeModelTestsHelper.compileCodeModel (cm);
  }
}
