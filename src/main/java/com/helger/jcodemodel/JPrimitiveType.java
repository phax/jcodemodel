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

import javax.annotation.Nonnull;

/**
 * Java built-in primitive types. Instances of this class can be obtained as
 * constants of {@link JCodeModel}, such as {@link JCodeModel#BOOLEAN}.
 */
public class JPrimitiveType extends AbstractJType
{
  private static final JCodeModel CODE_MODEL = new JCodeModel ();
  public static final JPrimitiveType VOID = CODE_MODEL.VOID;
  public static final JPrimitiveType BOOLEAN = CODE_MODEL.BOOLEAN;
  public static final JPrimitiveType BYTE = CODE_MODEL.BYTE;
  public static final JPrimitiveType SHORT = CODE_MODEL.SHORT;
  public static final JPrimitiveType CHAR = CODE_MODEL.CHAR;
  public static final JPrimitiveType INT = CODE_MODEL.INT;
  public static final JPrimitiveType FLOAT = CODE_MODEL.FLOAT;
  public static final JPrimitiveType LONG = CODE_MODEL.LONG;
  public static final JPrimitiveType DOUBLE = CODE_MODEL.DOUBLE;

  private final JCodeModel m_aOwner;
  private final String m_sTypeName;
  /**
   * Corresponding wrapper class. For example, this would be "java.lang.Short"
   * for short.
   */
  private final AbstractJClass m_aWrapperClass;
  private JArrayClass m_aArrayClass;

  protected JPrimitiveType (@Nonnull final JCodeModel aOwner,
                            @Nonnull final String sTypeName,
                            @Nonnull final Class <?> aWrapper)
  {
    m_aOwner = aOwner;
    m_sTypeName = sTypeName;
    m_aWrapperClass = aOwner.ref (aWrapper);
  }

  @Nonnull
  public JCodeModel owner ()
  {
    return m_aOwner;
  }

  @Override
  @Nonnull
  public String fullName ()
  {
    return m_sTypeName;
  }

  @Override
  @Nonnull
  public String name ()
  {
    return fullName ();
  }

  @Override
  public boolean isPrimitive ()
  {
    return true;
  }

  @Override
  @Nonnull
  public JArrayClass array ()
  {
    if (m_aArrayClass == null)
      m_aArrayClass = new JArrayClass (m_aOwner, this);
    return m_aArrayClass;
  }

  /**
   * Obtains the wrapper class for this primitive type. For example, this method
   * returns a reference to java.lang.Integer if this object represents int.
   */
  @Override
  @Nonnull
  public AbstractJClass boxify ()
  {
    return m_aWrapperClass;
  }

  /**
   * @deprecated calling this method from {@link JPrimitiveType} would be
   *             meaningless, since it's always guaranteed to return
   *             <tt>this</tt>.
   */
  @Deprecated
  @Override
  @Nonnull
  public AbstractJType unboxify ()
  {
    return this;
  }

  /**
   * Wraps an expression of this type to the corresponding wrapper class. For
   * example, if this class represents "float", this method will return the
   * expression <code>new Float(x)</code> for the parameter x.<br>
   * TODO: it's not clear how this method works for VOID.
   */
  @Nonnull
  public IJExpression wrap (@Nonnull final IJExpression exp)
  {
    return JExpr._new (boxify ()).arg (exp);
  }

  /**
   * Do the opposite of the wrap method. REVISIT: it's not clear how this method
   * works for VOID.
   */
  @Nonnull
  public IJExpression unwrap (@Nonnull final IJExpression aExpr)
  {
    // it just so happens that the unwrap method is always
    // things like "intValue" or "booleanValue".
    return aExpr.invoke (m_sTypeName + "Value");
  }

  public void generate (@Nonnull final JFormatter f)
  {
    f.print (m_sTypeName);
  }
}
