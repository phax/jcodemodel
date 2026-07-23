/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2026 Philip Helger + contributors
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
package com.helger.jcodemodel.switchexpression;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.IJFormatter;
import com.helger.jcodemodel.IJStatement;
import com.helger.jcodemodel.JLambdaParam;
import com.helger.jcodemodel.JSwitchExpression;
import com.helger.jcodemodel.JThrow;

/// switch case using a pattern as the selection. require java feature >= 21
/// ```
/// 	case char c when c>='&' && c<='z'-> {countChars++; yield 1+c-'a';}
/// ```
@SuppressWarnings ("serial")
public class JCasePattern extends JCaseArrow <JCasePattern>
{

  private final AbstractJType type;

  private final String varName;

  public JCasePattern (JSwitchExpression parent, AbstractJType type, String varName)
  {
    super (parent);
    this.type = type;
    this.varName = varName;
  }

  private final List <IJExpression> guards = new ArrayList <> ();

  public JCasePattern when (Function <JLambdaParam, IJExpression> maker)
  {
    guards.add (maker.apply (param ()));
    return this;
  }

  private JLambdaParam param = null;

  public JLambdaParam param ()
  {
    if (param == null)
    {
      param = new JLambdaParam (type, varName);
    }
    return param;
  }

  public JCasePattern addOn (Function <JLambdaParam, IJStatement> maker)
  {
    return add (maker.apply (param ()));
  }

  public JCasePattern _throwsOn (Function <JLambdaParam, IJExpression> maker)
  {
    return add (new JThrow (maker.apply (param ())));
  }

  public JCasePattern yieldOn (Function <JLambdaParam, IJExpression> maker)
  {
    return super.yield (maker.apply (param ()));
  }

  @Override
  public void state (@NonNull IJFormatter f)
  {
    f.indent ();
    f.print ("case ").declaration (param);
    boolean first = true;
    for (IJExpression ije : guards)
    {
      if (first)
      {
        f.print (" when ");
      }
      else
      {
        f.print (" && ");
      }
      f.generable (ije);
      first = false;
    }
    stateBody (f);
    f.outdent ();
  }

}
