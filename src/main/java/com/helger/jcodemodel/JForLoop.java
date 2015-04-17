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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.jcodemodel.optimize.ExpressionAccessor;
import com.helger.jcodemodel.optimize.ExpressionCallback;
import com.helger.jcodemodel.optimize.ExpressionContainer;
import com.helger.jcodemodel.optimize.Loop;

/**
 * For statement
 */
public class JForLoop implements IJStatement, Loop
{
  private final List <Object> m_aInitExprs = new ArrayList <Object> ();
  private IJExpression m_aTestExpr;
  private final List <IJExpression> m_aUpdateExprs = new ArrayList <IJExpression> ();
  private JBlock _body;

  protected JForLoop ()
  {}

  @Nonnull
  public JVar init (final int mods,
                    @Nonnull final AbstractJType aType,
                    @Nonnull final String sVarName,
                    @Nullable final IJExpression aInitExpr)
  {
    final JVar aVar = new JVar (JMods.forVar (mods), aType, sVarName, aInitExpr);
    m_aInitExprs.add (aVar);
    return aVar;
  }

  @Nonnull
  public JVar init (@Nonnull final AbstractJType aType,
                    @Nonnull final String sVarName,
                    @Nullable final IJExpression aInitExpr)
  {
    return init (JMod.NONE, aType, sVarName, aInitExpr);
  }

  public void init (@Nonnull final JVar aVar, @Nonnull final IJExpression aRhs)
  {
    final JAssignment aAssignment = JExpr.assign (aVar, aRhs);
    m_aInitExprs.add (aAssignment);
  }

  /**
   * @return List of {@link IJExpression} or {@link JVar}
   */
  @Nonnull
  public List <Object> inits ()
  {
    return Collections.unmodifiableList (m_aInitExprs);
  }

  public void test (@Nullable final IJExpression aTestExpr)
  {
    m_aTestExpr = aTestExpr;
  }

  @Nullable
  public IJExpression test ()
  {
    return m_aTestExpr;
  }

  public void update (@Nonnull final IJExpression aUpdate)
  {
    if (aUpdate == null)
      throw new NullPointerException ("Update expression");

    m_aUpdateExprs.add (aUpdate);
  }

  @Nonnull
  public List <IJExpression> updates ()
  {
    return Collections.unmodifiableList (m_aUpdateExprs);
  }

  @Nonnull
  public ExpressionContainer statementsExecutedOnce ()
  {
    return new ExpressionContainer ()
    {
      public boolean forAllSubExpressions (final ExpressionCallback callback)
      {
        for (final Object aInit : m_aInitExprs)
        {
          if (aInit instanceof IJExpression && !((IJExpression) aInit).forAllSubExpressions (callback))
            return false;
        }
        return true;
      }
    };
  }

  public ExpressionContainer statementsExecutedOnEachIteration ()
  {
    return new ExpressionContainer ()
    {
      public boolean forAllSubExpressions (final ExpressionCallback callback)
      {
        for (final IJExpression update : m_aUpdateExprs)
        {
          if (!update.forAllSubExpressions (callback))
            return false;
        }

        return AbstractJExpressionImpl.visitWithSubExpressions (callback, new ExpressionAccessor ()
        {
          public void set (final IJExpression newExpression)
          {
            m_aTestExpr = newExpression;
          }

          public IJExpression get ()
          {
            return m_aTestExpr;
          }
        });
      }
    };
  }

  @Nonnull
  public JBlock body ()
  {
    if (_body == null)
      _body = new JBlock ();
    return _body;
  }

  public void state (@Nonnull final JFormatter f)
  {
    f.print ("for (");
    boolean first = true;
    for (final Object o : m_aInitExprs)
    {
      if (!first)
        f.print (',');
      if (o instanceof JVar)
        f.var ((JVar) o);
      else
        f.generable ((IJExpression) o);
      first = false;
    }
    f.print (';').generable (m_aTestExpr).print (';').generable (m_aUpdateExprs).print (')');
    if (_body != null)
      f.generable (_body).newline ();
    else
      f.print (';').newline ();
  }
}
