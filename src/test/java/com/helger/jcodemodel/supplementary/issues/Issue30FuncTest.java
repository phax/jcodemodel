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

import org.junit.Test;

import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JDirectClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.util.CodeModelTestsHelper;

/**
 * Test for https://github.com/phax/jcodemodel/issues/30
 *
 * @author Philip Helger
 */
public final class Issue30FuncTest
{
  @Test
  public void test () throws Exception
  {
    final JCodeModel cm = new JCodeModel ();

    final JPackage aPkg1 = cm._package ("id.myapp");
    final JDefinedClass aClass_R = aPkg1._class ("R");
    final JDefinedClass aClass_id = aClass_R._class (JMod.PUBLIC | JMod.STATIC, "id");
    final JFieldVar aFieldItem = aClass_id.field (JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
                                                  cm.INT,
                                                  "myItem",
                                                  JExpr.lit (1));
    final JDefinedClass aClass_menu = aClass_R._class (JMod.PUBLIC | JMod.STATIC, "menu");
    final JFieldVar aFieldMenu = aClass_menu.field (JMod.PUBLIC | JMod.STATIC | JMod.FINAL,
                                                    cm.INT,
                                                    "myMenu",
                                                    JExpr.lit (2));

    final JPackage aPkg2 = cm._package ("demo");
    final JDefinedClass aClassAct = aPkg2._class ("HelloAndroidActivity_");
    final JMethod aMethodCreate = aClassAct.method (JMod.PUBLIC, cm.BOOLEAN, "onCreateOptionsMenu");
    aMethodCreate.body ().add (JExpr.ref ("menuInflater").invoke ("inflate").arg (aFieldMenu.fieldRef ()));
    final JMethod aMethodSelected = aClassAct.method (JMod.PUBLIC, cm.BOOLEAN, "onOptionsItemSelected");
    aMethodSelected.body ()._if (JExpr.ref ("itemId_").eq (aFieldItem.fieldRef ()));

    CodeModelTestsHelper.parseCodeModel (cm);
  }

  @Test
  public void testRegression1 () throws Exception
  {
    final JCodeModel cm = new JCodeModel ();

    final JPackage aPkg1 = cm._package ("id.myapp.activity");

    final JDefinedClass testClass = aPkg1._class ("TestClass");

    final JDirectClass androidR = cm.directClass ("android.R");
    final JDirectClass androidRId = androidR._class ("id");
    final JDirectClass myR = cm.directClass ("id.myapp.R");
    final JDirectClass myRId = myR._class ("id");

    final JBlock constructorBody = testClass.constructor (JMod.PUBLIC).body ();
    constructorBody.decl (cm.INT, "myInt", androidRId.staticRef ("someId"));
    constructorBody.decl (cm.INT, "myInt2", myRId.staticRef ("otherId"));

    CodeModelTestsHelper.parseCodeModel (cm);
  }

  @Test
  public void testRegression1VerySpecialCase () throws Exception
  {
    final JCodeModel cm = new JCodeModel ();

    final JPackage aPkg1 = cm._package ("id.myapp.activity");

    // Class is named like imported class
    final JDefinedClass testClass = aPkg1._class ("R");

    final JDirectClass androidR = cm.directClass ("android.R");
    final JDirectClass androidRId = androidR._class ("id");
    final JDirectClass myR = cm.directClass ("id.myapp.R");
    final JDirectClass myRId = myR._class ("id");

    final JBlock constructorBody = testClass.constructor (JMod.PUBLIC).body ();
    constructorBody.decl (cm.INT, "myInt", androidRId.staticRef ("someId"));
    constructorBody.decl (cm.INT, "myInt2", myRId.staticRef ("otherId"));

    CodeModelTestsHelper.parseCodeModel (cm);
  }
}
