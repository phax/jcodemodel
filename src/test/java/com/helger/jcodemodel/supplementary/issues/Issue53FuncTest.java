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

import java.util.function.Supplier;

import org.junit.Test;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JLambda;
import com.helger.jcodemodel.JLambdaMethodRef;
import com.helger.jcodemodel.JLambdaParam;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JVar;
import com.helger.jcodemodel.util.CodeModelTestsHelper;

/**
 * Test for https://github.com/phax/jcodemodel/issues/53
 *
 * @author Philip Helger
 */
public final class Issue53FuncTest
{
  @Test
  public void testIssue () throws Exception
  {
    final JCodeModel generator = new JCodeModel ();

    final JDefinedClass aInterface = generator._package ("issue53")._interface ("ITest");
    final JLambdaMethodRef methodLambda = new JLambdaMethodRef (generator.ref (String.class), "toString");

    JMethod method = aInterface.method (JMod.DEFAULT, generator.ref (Supplier.class).narrowAny (), "description");
    {
      final JLambda lambda = JLambda.simple (JExpr._this ()
                                                  .invoke ("getValueProvider")
                                                  .invoke ("andThen")
                                                  .arg (methodLambda));
      method.body ()._return (lambda);
    }

    method = aInterface.method (JMod.DEFAULT, generator.ref (Supplier.class).narrow (String.class), "description2");
    {
      final JLambda lambda = new JLambda ();
      final JLambdaParam aParam = lambda.addParam ("xx");
      lambda.body ().lambdaExpr (aParam.invoke ("getValueProvider").invoke ("andThen").arg (methodLambda));
      method.body ()._return (lambda);
    }

    method = aInterface.method (JMod.DEFAULT, generator.VOID, "description3");
    {
      final JVar l1 = method.body ().decl (generator.ref (Supplier.class).narrowAny (),
                                           "x1",
                                           JLambda.simple (JExpr._this ()
                                                                .invoke ("getValueProvider")
                                                                .invoke ("andThen")
                                                                .arg (methodLambda)));

      final JLambda lambda = new JLambda ();
      final JLambdaParam aParam = lambda.addParam ("xx");
      lambda.body ().lambdaExpr (aParam.invoke ("getValueProvider").invoke ("andThen").arg (methodLambda));
      method.body ().decl (generator.ref (Object.class), "x2", lambda);
      method.body ().add (l1.invoke ("get"));
    }

    CodeModelTestsHelper.parseCodeModel (generator);
  }
}
