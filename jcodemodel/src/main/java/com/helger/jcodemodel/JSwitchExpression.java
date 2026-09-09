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
package com.helger.jcodemodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.switchexpression.JCaseArrow;
import com.helger.jcodemodel.switchexpression.JCaseSpecialSelector;
import com.helger.jcodemodel.switchexpression.JCaseStatic;

///
/// A switch whose result can be used as an expression or a statement.
/// basically a copy of [JSwitch]
/// It has two specific blocks : null and default. When writing those, they should be tested for equality.
public class JSwitchExpression implements IJExpressionStatement
{

  /**
   * Test part of switch statement.
   */
  private final IJExpression m_aTestExpr;

  /**
   * vector of JCases.
   */
  private final List <JCaseArrow <?>> m_aCases = new ArrayList <> ();

  /**
   * a single default block
   */
  private JBlock m_aDefaultBlock;

  /**
   * a single null block
   */
  private JBlock m_aNullBlock;

  /**
   * Construct a switch statement
   *
   * @param aTestExpr
   *        expression
   */
  public JSwitchExpression (@NonNull final IJExpression aTestExpr)
  {
    m_aTestExpr = aTestExpr;
  }

  @NonNull
  public IJExpression test ()
  {
    return m_aTestExpr;
  }

  @NonNull
  public Iterator <JCaseArrow <?>> cases ()
  {
    return m_aCases.iterator ();
  }

  @NonNull
  public JCaseStatic _case (@NonNull final JEnumConstant constantRef)
  {
    return _case (JExpr.enumConstantRef (constantRef.type (), constantRef.name ()));
  }

  @NonNull
  public JCaseStatic _case (@NonNull final IJExpression aLabel)
  {
    final JCaseStatic c = new JCaseStatic (this, aLabel);
    m_aCases.add (c);
    return c;
  }

  // requires j25
  // @NonNull
  // public JCasePattern _case(AbstractJType type, String varName) {
  // JCasePattern c = new JCasePattern(this, type, varName);
  // m_aCases.add(c);
  // return c;
  // }

  // requires j25
  // @NonNull
  // public JCasePattern _case(JCodeModel jcm, Class<?> cl, String varName) {
  // return _case(jcm.ref(cl), varName);
  // }

  // requires j21
  // public JCaseSpecialSelector _null() {
  // return new JCaseSpecialSelector(this, true);
  // }

  public JCaseSpecialSelector _default ()
  {
    return new JCaseSpecialSelector (this, false);
  }

  @NonNull
  public JBlock defaultBlock ()
  {
    if (m_aDefaultBlock == null)
    {
      m_aDefaultBlock = new JLambdaBlock ();
    }
    return m_aDefaultBlock;
  }

  @NonNull
  public JBlock nullBlock ()
  {
    if (m_aNullBlock == null)
    {
      m_aNullBlock = new JLambdaBlock ();
    }
    return m_aNullBlock;
  }

  @Override
  public void generate (@NonNull final IJFormatter f)
  {
    generate25 (f);
  }

  protected void generate25 (@NonNull final IJFormatter f)
  {
    f.print ("switch (").generable (m_aTestExpr).print (')').print (" {").newline ();
    for (final JCaseArrow <?> c : m_aCases)
    {
      f.statement (c);
    }
    f.indent ();
    if (m_aNullBlock != null &&
      m_aDefaultBlock != null &&
      m_aNullBlock.getContents ().equals (m_aDefaultBlock.getContents ()))
    {
      f.print ("case null, default -> ").statement (m_aNullBlock);
    }
    else
    {
      if (m_aNullBlock != null)
      {
        f.print ("case null -> ").statement (m_aNullBlock);
      }
      if (m_aDefaultBlock != null)
      {
        f.print ("default -> ").statement (m_aDefaultBlock);
      }
    }
    f.outdent ();
    f.print ('}').newline ();
  }

  protected void generatePre25 (@SuppressWarnings ("unused") @NonNull final IJFormatter f)
  {
    // TODO in that case we generate only the static ones ; the pattern cases must
    // be added on top of the default.
    throw new UnsupportedOperationException ("not implemented yet.");
  }

  @Override
  public void state (@NonNull final IJFormatter f)
  {
    f.generable (this).print (';').newline ();
  }

}
