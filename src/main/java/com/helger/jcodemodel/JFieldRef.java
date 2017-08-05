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

import static com.helger.jcodemodel.util.JCEqualsHelper.isEqual;
import static com.helger.jcodemodel.util.JCHashCodeGenerator.getHashCode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.jcodemodel.util.JCValueEnforcer;

/**
 * Field Reference
 */
public class JFieldRef implements IJAssignmentTarget, IJOwnedMaybe
{
  private final JCodeModel m_aOwner;

  /**
   * Object expression upon which this field will be accessed, or null for the
   * implicit 'this'.
   */
  private final IJGenerable m_aObject;

  /**
   * Name of the field to be accessed. Either this or {@link #m_aVar} is set.
   */
  private final String m_sName;

  /**
   * Variable to be accessed.
   */
  private final JVar m_aVar;

  /**
   * Indicates if an explicit this should be generated
   */
  private boolean m_bExplicitThis;

  /**
   * Field reference constructor given an object expression and field name.
   * <code>object.name</code> or just <code>name</code> if object is
   * <code>null</code>.
   *
   * @param aObject
   *        JExpression for the object upon which the named field will be
   *        accessed. May be <code>null</code>.
   * @param sName
   *        Name of field to access. May not be <code>null</code>.
   */
  protected JFieldRef (@Nullable final IJExpression aObject, @Nonnull final String sName)
  {
    this (null, aObject, sName, (JVar) null, false);
  }

  protected JFieldRef (@Nullable final IJExpression aObject, @Nonnull final JVar aVar)
  {
    this (null, aObject, (String) null, aVar, false);
  }

  /**
   * Static field reference.
   *
   * @param aType
   *        Type to use
   * @param sName
   *        Field name
   */
  protected JFieldRef (@Nonnull final AbstractJType aType, @Nonnull final String sName)
  {
    this (aType.owner (), aType, sName, (JVar) null, false);
  }

  /**
   * Static field reference.
   *
   * @param aType
   *        Type to use
   * @param aVar
   *        Referenced variable
   */
  protected JFieldRef (@Nonnull final AbstractJType aType, @Nonnull final JVar aVar)
  {
    this (aType.owner (), aType, (String) null, aVar, false);
  }

  protected JFieldRef (@Nullable final IJGenerable aObject, @Nonnull final String sName, final boolean bExplicitThis)
  {
    this (null, aObject, sName, (JVar) null, bExplicitThis);
  }

  protected JFieldRef (@Nullable final IJGenerable aObject, @Nonnull final JVar aVar, final boolean bExplicitThis)
  {
    this (null, aObject, (String) null, aVar, bExplicitThis);
  }

  private JFieldRef (@Nullable final JCodeModel aOwner,
                     @Nullable final IJGenerable aObject,
                     @Nullable final String sName,
                     @Nullable final JVar aVar,
                     final boolean bExplicitThis)
  {
    JCValueEnforcer.isTrue (sName == null || sName.indexOf ('.') < 0, () -> "Field name contains '.': " + sName);
    JCValueEnforcer.isFalse (sName == null && aVar == null, "name or var must be present");
    m_aOwner = aOwner;
    m_aObject = aObject;
    m_sName = sName;
    m_aVar = aVar;
    m_bExplicitThis = bExplicitThis;
  }

  @Nullable
  public JCodeModel owner ()
  {
    return m_aOwner;
  }

  @Nullable
  public IJGenerable object ()
  {
    return m_aObject;
  }

  @Nonnull
  public String name ()
  {
    String sName = m_sName;
    if (sName == null)
      sName = m_aVar.name ();
    return sName;
  }

  @Nullable
  public JVar var ()
  {
    return m_aVar;
  }

  public boolean explicitThis ()
  {
    return m_bExplicitThis;
  }

  @Nonnull
  public JFieldRef explicitThis (final boolean bExplicitThis)
  {
    m_bExplicitThis = bExplicitThis;
    return this;
  }

  public void generate (@Nonnull final JFormatter f)
  {
    final String name = name ();

    if (m_aObject != null)
    {
      if (m_aObject instanceof AbstractJType)
        f.type ((AbstractJType) m_aObject);
      else
        f.generable (m_aObject);
      f.print ('.').print (name);
    }
    else
      if (m_bExplicitThis)
        f.print ("this.").print (name);
      else
        f.id (name);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || getClass () != o.getClass ())
      return false;
    final JFieldRef rhs = (JFieldRef) o;
    return isEqual (m_aObject, rhs.m_aObject) &&
           isEqual (name (), rhs.name ()) &&
           isEqual (m_bExplicitThis, rhs.m_bExplicitThis);
  }

  @Override
  public int hashCode ()
  {
    return getHashCode (this, m_aObject, name (), Boolean.valueOf (m_bExplicitThis));
  }
}
