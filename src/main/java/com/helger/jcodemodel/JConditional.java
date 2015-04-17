/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2015 Philip Helger
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

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import javax.annotation.Nonnull;

import com.helger.jcodemodel.optimize.BranchingStatement;
import com.helger.jcodemodel.optimize.BranchingStatementVisitor;

/**
 * If statement, with optional else clause
 */
public class JConditional implements IJStatement, BranchingStatement
{
  /**
   * Expression to test to determine branching
   */
  private final IJExpression m_aTestExpr;

  /**
   * Block of statements for "then" clause. Must always be present
   */
  private final JBlock m_aThenBlock = new JBlock ();

  /**
   * Block of statements for optional "else" clause
   */
  private JBlock m_aElseBlock;

  /**
   * Constructor
   *
   * @param aTestExpr
   *        JExpression which will determine branching
   */
  protected JConditional (@Nonnull final IJExpression aTestExpr)
  {
    if (aTestExpr == null)
      throw new NullPointerException ("Test expression");
    m_aTestExpr = aTestExpr;
  }

  @Nonnull
  public IJExpression test ()
  {
    return m_aTestExpr;
  }

  /**
   * Return the block to be executed by the "then" branch
   *
   * @return Then block. Never <code>null</code>.
   */
  @Nonnull
  public JBlock _then ()
  {
    return m_aThenBlock;
  }

  /**
   * Create a block to be executed by "else" branch
   *
   * @return Newly generated else block
   */
  @Nonnull
  public JBlock _else ()
  {
    if (m_aElseBlock == null)
      m_aElseBlock = new JBlock ();
    return m_aElseBlock;
  }

  /**
   * Creates <tt>... else if(...) ...</tt> code.
   * 
   * @param aTestExpr
   *        The test expression for the new if
   */
  @Nonnull
  public JConditional _elseif (@Nonnull final IJExpression aTestExpr)
  {
    return _else ()._if (aTestExpr);
  }

  public void state (@Nonnull final JFormatter f)
  {
    if (m_aTestExpr == JExpr.TRUE)
    {
      m_aThenBlock.generateBody (f);
      return;
    }
    if (m_aTestExpr == JExpr.FALSE)
    {
      m_aElseBlock.generateBody (f);
      return;
    }

    if (JOp.hasTopOp (m_aTestExpr))
    {
      f.print ("if ").generable (m_aTestExpr);
    }
    else
    {
      f.print ("if (").generable (m_aTestExpr).print (')');
    }
    f.generable (m_aThenBlock);
    if (m_aElseBlock != null)
      f.print ("else").generable (m_aElseBlock);
    f.newline ();
  }

  public void apply (final BranchingStatementVisitor visitor)
  {
    visitor.visit (m_aTestExpr);
    visitor.visit (m_aElseBlock != null ? asList (m_aThenBlock, m_aElseBlock) : singletonList (m_aThenBlock));
  }
}
