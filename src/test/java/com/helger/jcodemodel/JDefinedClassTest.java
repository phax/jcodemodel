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

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.helger.jcodemodel.util.CodeModelTestsHelper;

/**
 * Test class for class {@link JDefinedClass}.
 *
 * @author Philip Helger
 */
public final class JDefinedClassTest
{
  @Test
  public void generatesInstanceInit () throws Exception
  {
    /**
     * <pre>
     * /-**
     * * Line 1
     * * Line 2
     * * Line 3
     * *-/
     * package myPackage;
     *
     * class MyClass
     * {
     *   private String myField;
     *
     *   {
     *     this.myField = "myValue";
     *   }
     * }
     * </pre>
     */

    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass c = cm._package ("myPackage")._class (0, "MyClass");
    c.headerComment ().add ("Line 1\nLine 2\nLine 3");
    final JFieldVar myField = c.field (JMod.PRIVATE, String.class, "myField");
    c.instanceInit ().assign (JExpr._this ().ref (myField), JExpr.lit ("myValue"));

    final CompilationUnit aCU = CodeModelTestsHelper.parseAndGetSingleClassCodeModel (cm);
    final TypeDeclaration <?> typeDeclaration = aCU.getTypes ().get (0);
    final ClassOrInterfaceDeclaration classDeclaration = (ClassOrInterfaceDeclaration) typeDeclaration;
    final InitializerDeclaration initializerDeclaration = (InitializerDeclaration) classDeclaration.getMembers ()
                                                                                                   .get (1);
    assertNotNull (initializerDeclaration);
  }

  @Test
  public void testCallSuper () throws Exception
  {
    final JCodeModel cm = new JCodeModel ();

    // Base class
    final JDefinedClass c1 = cm._package ("myPackage")._class (0, "BaseClass");
    final JMethod con1 = c1.constructor (JMod.PUBLIC);
    final JVar p1 = con1.param (JMod.FINAL, cm.ref (String.class), "str");
    con1.body ()
        .add (new JFieldRef (cm.ref (System.class), "out").invoke ("println").arg (JExpr.lit ("Got ").plus (p1)));

    // Derived class
    final JDefinedClass c2 = cm._package ("myPackage")._class (0, "DerivedClass");
    c2._extends (c1);
    final JMethod con2 = c2.constructor (JMod.PUBLIC);
    con2.body ().invokeSuper ().arg ("Test");
    CodeModelTestsHelper.parseCodeModel (cm);
  }
}
