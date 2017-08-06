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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is a single Java 8 lambda expression. It consists of 0-n parameters and
 * a body statement. For example in <code>(a, b) -&gt; a + b</code> "a" and "b"
 * are parameters and "a + b" is the body statement.
 *
 * @author Philip Helger
 * @since 2.7.10
 */
public class JLambda implements IJExpression
{
  private final List <JLambdaParam> m_aParams = new ArrayList <> ();
  private final JLambdaBlock m_aBodyStatement = new JLambdaBlock ();

  /**
   * Create an empty lambda without any parameter.
   */
  public JLambda ()
  {}

  /**
   * Add a parameter without a type name.
   *
   * @param sName
   *        The variable name to use. May not be <code>null</code>.
   * @return The created {@link JLambdaParam} object.
   */
  @Nonnull
  public JLambdaParam addParam (@Nonnull final String sName)
  {
    final JLambdaParam aParam = new JLambdaParam ((AbstractJType) null, sName);
    m_aParams.add (aParam);
    return aParam;
  }

  /**
   * Add a parameter with a type name.
   *
   * @param aType
   *        The Type of the parameter. May be <code>null</code>.
   * @param sName
   *        The variable name to use. May not be <code>null</code>.
   * @return The created {@link JLambdaParam} object.
   */
  @Nonnull
  public JLambdaParam addParam (@Nullable final AbstractJType aType, @Nonnull final String sName)
  {
    final JLambdaParam aParam = new JLambdaParam (aType, sName);
    m_aParams.add (aParam);
    return aParam;
  }

  /**
   * @return An unmodifiable list with all parameters present. Never
   *         <code>null</code>.
   */
  @Nonnull
  public List <JLambdaParam> params ()
  {
    return Collections.unmodifiableList (m_aParams);
  }

  @Nonnegative
  public int paramCount ()
  {
    return m_aParams.size ();
  }

  @Nonnull
  public JLambdaBlock body ()
  {
    return m_aBodyStatement;
  }

  public void generate (@Nonnull final JFormatter f)
  {
    final int nParams = m_aParams.size ();
    if (nParams > 0)
    {
      final JLambdaParam aParam0 = m_aParams.get (0);
      for (int i = 1; i < nParams; ++i)
        if (m_aParams.get (i).hasType () != aParam0.hasType ())
          throw new IllegalStateException ("Lambda expression parameters must all have types or none may have a type!");
    }
    if (m_aBodyStatement.isEmpty ())
      throw new IllegalStateException ("Lambda expression is empty!");

    // Print parameters
    if (nParams == 0)
      f.print ("()");
    else
      if (nParams == 1 && !m_aParams.get (0).hasType ())
      {
        // Braces can be omitted for single parameters without a type
        m_aParams.get (0).declare (f);
      }
      else
      {
        f.print ('(');
        for (int i = 0; i < nParams; ++i)
        {
          if (i > 0)
            f.print (',');
          m_aParams.get (i).declare (f);
        }
        f.print (')');
      }
    f.print (" -> ");

    // Print body
    final boolean bBraces = m_aBodyStatement.size () != 1 ||
                            !(m_aBodyStatement.getContents ().get (0) instanceof IJExpression);
    m_aBodyStatement.bracesRequired (bBraces);
    f.statement (m_aBodyStatement);
  }

  /**
   * Create a new no argument lambda that just returns the provided expression.
   *
   * @param aExpr
   *        Expression to be returned. May not be <code>null</code>.
   * @return Never <code>null</code>.
   * @since 3.0.0
   */
  @Nonnull
  public static JLambda simple (@Nonnull final IJExpression aExpr)
  {
    final JLambda ret = new JLambda ();
    ret.body ().lambdaExpr (aExpr);
    return ret;
  }
}
