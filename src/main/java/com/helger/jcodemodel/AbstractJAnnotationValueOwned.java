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

import com.helger.jcodemodel.util.JCNameUtilities;
import com.helger.jcodemodel.util.JCValueEnforcer;

/**
 * Things that can be values of an annotation element and have an owning code
 * model (see {@link IJOwned}).
 *
 * @author Philip Helger
 */
public abstract class AbstractJAnnotationValueOwned extends AbstractJAnnotationValue implements IJOwned
{
  protected final class JEnumConstantExpr implements IJExpression
  {
    private final Enum <?> m_aEnumConstant;

    protected JEnumConstantExpr (@Nonnull final Enum <?> aEnumConstant)
    {
      m_aEnumConstant = JCValueEnforcer.notNull (aEnumConstant, "EnumConstant");
    }

    public void generate (@Nonnull final JFormatter f)
    {
      f.type (owner ().ref (m_aEnumConstant.getDeclaringClass ())).print ('.').print (m_aEnumConstant.name ());
    }
  }

  protected final class FullClassNameExpr implements IJExpression
  {
    private final Class <?> m_aClass;

    protected FullClassNameExpr (@Nonnull final Class <?> aClass)
    {
      m_aClass = JCValueEnforcer.notNull (aClass, "Class");
    }

    public void generate (@Nonnull final JFormatter f)
    {
      f.print (JCNameUtilities.getFullName (m_aClass)).print (".class");
    }
  }

  @Nonnull
  public AbstractJAnnotationValue wrap (@Nonnull final Enum <?> aEnumConstant)
  {
    return new JAnnotationStringValue (new JEnumConstantExpr (aEnumConstant), aEnumConstant);
  }

  @Nonnull
  public AbstractJAnnotationValue wrap (@Nonnull final Class <?> aClass)
  {
    return new JAnnotationStringValue (new FullClassNameExpr (aClass), aClass);
  }
}
