/*
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.jcodemodel.util.CodeModelTestsHelper;

/**
 * Test class for class {@link JDirectClass}.
 *
 * @author Philip Helger
 */
public final class JDirectClassTest
{
  @Test
  public void testBasic () throws Exception
  {
    final JCodeModel cm = new JCodeModel ();

    final JDirectClass rClassId = cm.directClass ("id.aa.R")._class ("id");
    assertEquals ("id", rClassId.name ());
    assertEquals ("id.aa", rClassId._package ().name ());
    assertEquals ("id.aa.R.id", rClassId.fullName ());
    final JDirectClass rClassMenu = cm.directClass ("id.aa.R")._class ("menu");
    assertEquals ("menu", rClassMenu.name ());
    assertEquals ("id.aa", rClassMenu._package ().name ());
    assertEquals ("id.aa.R.menu", rClassMenu.fullName ());

    final JFieldRef myItem = rClassId.staticRef ("myItem");
    final JFieldRef myMenu = rClassMenu.staticRef ("myMenu");

    final JPackage aPkg2 = cm._package ("id.aa");
    final JDefinedClass aClassAct = aPkg2._class ("HelloAndroidActivity_");
    final JMethod aMethodCreate = aClassAct.method (JMod.PUBLIC, cm.BOOLEAN, "onCreateOptionsMenu");
    aMethodCreate.body ().add (JExpr.ref ("menuInflater").invoke ("inflate").arg (myMenu));
    final JMethod aMethodSelected = aClassAct.method (JMod.PUBLIC, cm.BOOLEAN, "onOptionsItemSelected");
    aMethodSelected.body ()._if (JExpr.ref ("itemId_").eq (myItem));

    CodeModelTestsHelper.parseCodeModel (cm);
  }

  @Test
  public void testGenerics () throws Exception
  {
    final JCodeModel cm = new JCodeModel ();

    final AbstractJClass aNarrowedClass = (AbstractJClass) cm.parseType ("com.test.GenericFragmentArguments<S,P>");
    assertTrue (aNarrowedClass instanceof JNarrowedClass);
    assertTrue (aNarrowedClass.erasure () instanceof JDirectClass);
    assertEquals ("com.test", aNarrowedClass._package ().name ());
    assertEquals ("GenericFragmentArguments<S,P>", aNarrowedClass.name ());
    assertEquals ("GenericFragmentArguments", aNarrowedClass.erasure ().name ());
    assertEquals ("com.test.GenericFragmentArguments<S,P>", aNarrowedClass.fullName ());
    assertEquals ("com.test.GenericFragmentArguments", aNarrowedClass.erasure ().fullName ());

    cm._class ("UsingClass").method (JMod.PUBLIC, cm.VOID, "test").body ().add (JExpr._new (aNarrowedClass));

    CodeModelTestsHelper.parseCodeModel (cm);
  }
}
