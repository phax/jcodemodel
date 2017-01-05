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

import org.junit.Test;

import com.helger.jcodemodel.util.CodeModelTestsHelper;

/**
 * @author Kohsuke Kawaguchi
 */
public final class JavadocFuncTest
{
  @Test
  public void testOnPackage () throws Exception
  {
    final JCodeModel cm = new JCodeModel ();
    final JPackage pkg = cm._package ("foo");
    final JDocComment aComment = pkg.javadoc ();
    aComment.add ("Package description");
    aComment.addTag ("since").add ("1.0");
    aComment.addAuthor ().add ("JCodeModel unit test");
    aComment.addDeprecated ().add ("Just for testing");

    CodeModelTestsHelper.parseCodeModel (cm);
  }

  @Test
  public void testOnClass () throws Exception
  {
    final JCodeModel cm = new JCodeModel ();
    final JPackage pkg = cm._package ("foo");

    final JDefinedClass cls = pkg._class (JMod.PUBLIC | JMod.FINAL, "Dummy");
    cls.javadoc ().add ("Class comment");
    cls.javadoc ().addAuthor ().add ("JavadocFuncTest");

    final JMethod method = cls.method (JMod.PUBLIC | JMod.STATIC, String.class, "getPlusX");
    final JVar aParam = method.param (String.class, "any");
    method.body ()._return (aParam.plus ("X"));
    method.javadoc ().add ("Description");
    method.javadoc ().addParam (aParam).add ("Input value");
    method.javadoc ().addReturn ().add ("Input value plus \"X\".");
    method.javadoc ().addThrows (NullPointerException.class).add ("If input is null");
    method.javadoc ().addTag ("since").add ("JCodeModel 2.8.5");

    CodeModelTestsHelper.parseCodeModel (cm);
  }
}
