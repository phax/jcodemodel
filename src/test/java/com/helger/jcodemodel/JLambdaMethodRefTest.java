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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.jcodemodel.util.CodeModelTestsHelper;

/**
 * Test class for class {@link JLambdaMethodRef}.
 *
 * @author Philip Helger
 */
public final class JLambdaMethodRefTest
{
  @Test
  public void testStaticMethodRef_Name ()
  {
    final JCodeModel cm = new JCodeModel ();

    final JLambdaMethodRef aLambda = cm.ref (Object.class).methodRef ("toString");
    assertEquals ("java.lang.Object::toString", CodeModelTestsHelper.toString (aLambda));
    assertTrue (aLambda.isStaticRef ());
    assertNull (aLambda.method ());
    assertSame (cm.ref (Object.class), aLambda.type ());
    assertNull (aLambda.var ());
    assertEquals ("toString", aLambda.methodName ());
  }

  @Test
  public void testStaticMethodRef_JMethod () throws JClassAlreadyExistsException
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass cl = cm._class ("com.helger.test.LambdaTest");
    final JMethod m = cl.method (JMod.PUBLIC | JMod.STATIC, cm.ref (String.class), "myToString");
    final JVar p = m.param (Object.class, "obj");
    m.body ()._return (cm.ref (String.class).staticInvoke ("valueOf").arg (p));

    final JLambdaMethodRef aLambda = new JLambdaMethodRef (m);
    assertEquals ("com.helger.test.LambdaTest::myToString", CodeModelTestsHelper.toString (aLambda));
    assertTrue (aLambda.isStaticRef ());
    assertSame (m, aLambda.method ());
    assertSame (cl, aLambda.type ());
    assertNull (aLambda.var ());
    assertEquals ("myToString", aLambda.methodName ());

    // Modify original method
    m.name ("newName");
    assertEquals ("com.helger.test.LambdaTest::newName", CodeModelTestsHelper.toString (aLambda));
    assertTrue (aLambda.isStaticRef ());
    assertSame (m, aLambda.method ());
    assertSame (cl, aLambda.type ());
    assertNull (aLambda.var ());
    assertEquals ("newName", aLambda.methodName ());
  }

  @Test
  public void testStaticMethodRef_New () throws JClassAlreadyExistsException
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass cl = cm._class ("com.helger.test.LambdaTest");

    final JLambdaMethodRef aLambda = new JLambdaMethodRef (cl);
    assertEquals ("com.helger.test.LambdaTest::new", CodeModelTestsHelper.toString (aLambda));
    assertTrue (aLambda.isStaticRef ());
    assertNull (aLambda.method ());
    assertSame (cl, aLambda.type ());
    assertNull (aLambda.var ());
    assertEquals ("new", aLambda.methodName ());
  }

  @Test
  public void testInstanceMethodRef_Name ()
  {
    final JCodeModel cm = new JCodeModel ();
    final JBlock aBlock = new JBlock ();
    final JVar aVar = aBlock.decl (cm._ref (Object.class), "aObj");

    final JLambdaMethodRef aLambda = new JLambdaMethodRef (aVar, "toString");
    assertEquals ("aObj::toString", CodeModelTestsHelper.toString (aLambda));
    assertFalse (aLambda.isStaticRef ());
    assertNull (aLambda.method ());
    assertSame (cm._ref (Object.class), aLambda.type ());
    assertSame (aVar, aLambda.var ());
    assertEquals ("toString", aLambda.methodName ());
  }

  @Test
  public void testInstanceMethodRef_JMethod () throws JClassAlreadyExistsException
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass cl = cm._class ("com.helger.test.LambdaTest");
    final JMethod m = cl.method (JMod.PUBLIC, cm.ref (String.class), "myToString");
    final JVar p = m.param (Object.class, "obj");
    m.body ()._return (cm.ref (String.class).staticInvoke ("valueOf").arg (p));

    final JBlock aBlock = new JBlock ();
    final JVar aVar = aBlock.decl (cl, "aObj");

    final JLambdaMethodRef aLambda = new JLambdaMethodRef (aVar, m);
    assertEquals ("aObj::myToString", CodeModelTestsHelper.toString (aLambda));
    assertFalse (aLambda.isStaticRef ());
    assertSame (m, aLambda.method ());
    assertSame (cl, aLambda.type ());
    assertSame (aVar, aLambda.var ());
    assertEquals ("myToString", aLambda.methodName ());
  }

  @Test
  public void testInvocationMethodRef_Name () throws JClassAlreadyExistsException
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass cl = cm._class ("com.helger.test.LambdaInvocationMethodRefName");

    final JLambdaMethodRef lambdaMethod = new JLambdaMethodRef (cm.ref ("test.Person"), "getFirstName");
    final JInvocation jInvocation = JExpr._this ().invoke ("andThen").arg (lambdaMethod);
    final JLambdaMethodRef aLambda = new JLambdaMethodRef (jInvocation, "apply");
    assertEquals ("this.andThen(test.Person::getFirstName)::apply", CodeModelTestsHelper.toString (aLambda));

    final JMethod con = cl.constructor (JMod.PUBLIC);
    con.body ().decl (cm.ref (Object.class), "any", aLambda);
    CodeModelTestsHelper.parseCodeModel (cm);
  }

  @Test
  public void testInvocationMethodRef_JMethod () throws JClassAlreadyExistsException
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass cl = cm._class ("com.helger.test.LambdaInvocationMethodRefJMethod");

    final JMethod m = cl.method (JMod.PUBLIC, cm.ref (String.class), "myToString");
    final JVar p = m.param (Object.class, "obj");
    m.body ()._return (cm.ref (String.class).staticInvoke ("valueOf").arg (p));

    final JLambdaMethodRef lambdaMethod = new JLambdaMethodRef (cm.ref ("test.Person"), "getFirstName");
    final JInvocation jInvocation = JExpr._this ().invoke ("andThen").arg (lambdaMethod);
    final JLambdaMethodRef aLambda = new JLambdaMethodRef (jInvocation, m);
    assertEquals ("this.andThen(test.Person::getFirstName)::myToString", CodeModelTestsHelper.toString (aLambda));

    final JMethod con = cl.constructor (JMod.PUBLIC);
    con.body ().decl (cm.ref (Object.class), "any", aLambda);
    CodeModelTestsHelper.parseCodeModel (cm);
  }
}
