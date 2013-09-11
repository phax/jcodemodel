/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
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

import javax.annotation.Nonnull;

/**
 * Field Reference
 */

public class JFieldRef extends AbstractJExpressionImpl implements JAssignmentTarget
{
  /**
   * Object expression upon which this field will be accessed, or null for the
   * implicit 'this'.
   */
  private final JGenerable object;

  /**
   * Name of the field to be accessed. Either this or {@link #var} is set.
   */
  private String name;

  /**
   * Variable to be accessed.
   */
  private JVar var;

  /**
   * Indicates if an explicit this should be generated
   */
  private final boolean explicitThis;

  /**
   * Field reference constructor given an object expression and field name
   * 
   * @param object
   *        JExpression for the object upon which the named field will be
   *        accessed,
   * @param name
   *        Name of field to access
   */
  protected JFieldRef (final JExpression object, final String name)
  {
    this (object, name, false);
  }

  protected JFieldRef (final JExpression object, final JVar v)
  {
    this (object, v, false);
  }

  /**
   * Static field reference.
   */
  protected JFieldRef (final AbstractJType type, @Nonnull final String name)
  {
    this (type, name, false);
  }

  protected JFieldRef (final AbstractJType type, final JVar v)
  {
    this (type, v, false);
  }

  protected JFieldRef (final JGenerable object, @Nonnull final String name, final boolean explicitThis)
  {
    this.explicitThis = explicitThis;
    this.object = object;
    if (name.indexOf ('.') >= 0)
      throw new IllegalArgumentException ("Field name contains '.': " + name);
    this.name = name;
  }

  protected JFieldRef (final JGenerable object, final JVar var, final boolean explicitThis)
  {
    this.explicitThis = explicitThis;
    this.object = object;
    this.var = var;
  }

  public String name ()
  {
    String name = this.name;
    if (name == null)
      name = var.name ();
    return name;
  }

  public JVar var ()
  {
    return var;
  }

  public boolean explicitThis ()
  {
    return explicitThis;
  }

  public void generate (@Nonnull final JFormatter f)
  {
    final String name = name ();

    if (object != null)
    {
      f.generable (object).print ('.').print (name);
    }
    else
    {
      if (explicitThis)
      {
        f.print ("this.").print (name);
      }
      else
      {
        f.id (name);
      }
    }
  }

  @Nonnull
  public JExpression assign (@Nonnull final JExpression rhs)
  {
    return JExpr.assign (this, rhs);
  }

  @Nonnull
  public JExpression assignPlus (@Nonnull final JExpression rhs)
  {
    return JExpr.assignPlus (this, rhs);
  }
}
