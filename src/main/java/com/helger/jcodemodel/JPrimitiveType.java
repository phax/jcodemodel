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

import javax.annotation.Nonnull;

import com.helger.jcodemodel.util.JCValueEnforcer;

/**
 * Java built-in primitive types. Instances of this class can be obtained as
 * constants of {@link JCodeModel}, such as {@link JCodeModel#BOOLEAN}.
 */
public class JPrimitiveType extends AbstractJType
{
  private final JCodeModel m_aOwner;
  private final String m_sTypeName;
  /**
   * Corresponding wrapper class. For example, this would be "java.lang.Short"
   * for short.
   */
  private final AbstractJClass m_aWrapperClass;
  private JArrayClass m_aArrayClass;
  private boolean m_bUseValueOf;

  protected JPrimitiveType (@Nonnull final JCodeModel aOwner,
                            @Nonnull final String sTypeName,
                            @Nonnull final Class <?> aWrapper,
                            final boolean bUseValueOf)
  {
    JCValueEnforcer.notNull (aOwner, "Owner");
    JCValueEnforcer.notNull (sTypeName, "TypeName");
    JCValueEnforcer.notNull (aWrapper, "Wrapper");
    m_aOwner = aOwner;
    m_sTypeName = sTypeName;
    m_aWrapperClass = aOwner.ref (aWrapper);
    m_bUseValueOf = bUseValueOf;
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
  public final boolean isPrimitive ()
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

  /*
   * Obtains the wrapper class for this primitive type. For example, this method
   * returns a reference to java.lang.Integer if this object represents int.
   */
  @Override
  @Nonnull
  public final AbstractJClass boxify ()
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
  public final AbstractJType unboxify ()
  {
    return this;
  }

  /**
   * Wraps an expression of this type to the corresponding wrapper class. For
   * example, if this class represents "float", this method will return the
   * expression <code>new Float(x)</code> or <code>Float.valueOf(x)</code> for
   * the parameter <code>x</code>.<br>
   * For void type it throws an {@link IllegalStateException} because this would
   * lead to corrupt code!
   *
   * @param aExpr
   *        Expression to be wrapped
   * @return The created expression. Never <code>null</code>
   * @see #useValueOf()
   */
  @Nonnull
  public IJExpression wrap (@Nonnull final IJExpression aExpr)
  {
    if ("void".equals (m_sTypeName))
      throw new IllegalStateException ("Cannot wrap a 'void' expression!");

    if (m_bUseValueOf)
      return boxify ().staticInvoke ("valueOf").arg (aExpr);

    return JExpr._new (boxify ()).arg (aExpr);
  }

  /**
   * Do the opposite of the wrap method. So for a <code>Float</code> object
   * <code>x</code> it creates <code>x.floatValue()</code><br>
   * For void type it throws an {@link IllegalStateException} because this would
   * lead to corrupt code!
   *
   * @param aExpr
   *        Expression to be unwrapped
   * @return The created primitive value expression. Never <code>null</code>
   */
  @Nonnull
  public IJExpression unwrap (@Nonnull final IJExpression aExpr)
  {
    if ("void".equals (m_sTypeName))
      throw new IllegalStateException ("Cannot unwrap a 'void' expression!");

    // it just so happens that the unwrap method is always
    // things like "intValue" or "booleanValue".
    return aExpr.invoke (m_sTypeName + "Value");
  }

  /**
   * @return <code>true</code> if <code>valueOf</code> should be used in
   *         {@link #wrap(IJExpression)}, <code>false</code> if
   *         <code>new X(y)</code> should be used there. Note:
   *         <code>valueOf</code> is faster in execution since it uses
   *         potentially built in caches of the objects.
   */
  public boolean useValueOf ()
  {
    return m_bUseValueOf;
  }

  /**
   * Determine of <code>valueOf</code> should be used or not.
   *
   * @param bUseValueOf
   *        New value. <code>true</code> to enable usage of <code>valueOf</code>
   */
  public void useValueOf (final boolean bUseValueOf)
  {
    m_bUseValueOf = bUseValueOf;
  }

  public void generate (@Nonnull final JFormatter f)
  {
    f.print (m_sTypeName);
  }
}
