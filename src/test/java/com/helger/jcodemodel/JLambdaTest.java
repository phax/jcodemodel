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

import org.junit.Test;

import com.helger.jcodemodel.util.CodeModelTestsHelper;

/**
 * Test class for class {@link JLambda}.
 *
 * @author Philip Helger
 */
public final class JLambdaTest
{
  private static final String CRLF = AbstractCodeWriter.getDefaultNewLine ();

  @Test
  public void testExpressionBasic ()
  {
    final JLambda aLambda = new JLambda ();
    final JLambdaParam aParam = aLambda.addParam ("x");
    aLambda.body ().lambdaExpr (aParam.mul (2));
    assertEquals ("x -> (x* 2)", CodeModelTestsHelper.toString (aLambda));
  }

  @Test
  public void testExpressionNoParam ()
  {
    final JLambda aLambda = new JLambda ();
    aLambda.body ().lambdaExpr (JExpr.lit (2));
    assertEquals ("() ->  2", CodeModelTestsHelper.toString (aLambda));
  }

  @Test
  public void testExpressionBasicType ()
  {
    final JCodeModel cm = new JCodeModel ();

    final JLambda aLambda = new JLambda ();
    final JLambdaParam aParam = aLambda.addParam (cm.INT, "x");
    aLambda.body ().lambdaExpr (aParam.mul (2));
    assertEquals ("(int x) -> (x* 2)", CodeModelTestsHelper.toString (aLambda));
  }

  @Test
  public void testExpressionBasic2 ()
  {
    final JLambda aLambda = new JLambda ();
    final JLambdaParam aParam1 = aLambda.addParam ("x");
    final JLambdaParam aParam2 = aLambda.addParam ("y");
    aLambda.body ().lambdaExpr (aParam1.plus (aParam2));
    assertEquals ("(x, y) -> (x + y)", CodeModelTestsHelper.toString (aLambda));
  }

  @Test
  public void testExpressionBasicType2 ()
  {
    final JCodeModel cm = new JCodeModel ();

    final JLambda aLambda = new JLambda ();
    final JLambdaParam aParam1 = aLambda.addParam (cm.INT, "x");
    final JLambdaParam aParam2 = aLambda.addParam (cm.BYTE, "y");
    aLambda.body ().lambdaExpr (aParam1.plus (aParam2));
    assertEquals ("(int x, byte y) -> (x + y)", CodeModelTestsHelper.toString (aLambda));
  }

  @Test
  public void testStatementBasicType ()
  {
    final JCodeModel cm = new JCodeModel ();

    final JLambda aLambda = new JLambda ();
    final JLambdaParam aParam = aLambda.addParam (cm.INT, "x");
    aLambda.body ()._return (aParam.plus (1));
    assertEquals ("(int x) -> {" + CRLF + "    return (x + 1);" + CRLF + "}" + CRLF,
                  CodeModelTestsHelper.toString (aLambda));
  }
}
