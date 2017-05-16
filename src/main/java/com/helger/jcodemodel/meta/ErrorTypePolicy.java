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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.helger.jcodemodel.meta;

import javax.annotation.Nonnull;

import com.helger.jcodemodel.util.JCValueEnforcer;

/**
 * Defines policy to use with error-types.
 * <p>
 * {@code tryBind} parameter provides access to (re-)binding of references to
 * error-types.
 * <p>
 * We may use elements provided by Java compiler during jcodemodel code
 * generation. Existing java source code may already have references to not yet
 * generated classes. In such scenario Java-compiler will give us error-types.
 * When this occures we may try to rebind error-types to classes defined in
 * jcodemodel, but missing in existing Java source code accessible to
 * Java-compiler.
 * <p>
 * When {@code tryBind} parameter is true, we try to rebind error-types to
 * classes defined in jcodemodel. When {@code tryBind} parameter is false,
 * error-types are returned as is.
 *
 * @author Victor Nazarov &lt;asviraspossible@gmail.com&gt;
 */
public class ErrorTypePolicy
{
  public static enum EAction
  {
    THROW_EXCEPTION,
    CREATE_ERROR_TYPE
  }

  private final EAction m_eAction;
  private final boolean m_bTryBind;

  /**
   * @see ErrorTypePolicy
   * @param aAction
   *        action to perform if any error-type is found.
   * @param tryBind
   *        if true try to (re-)bind references to error-types to existing
   *        types.
   */
  public ErrorTypePolicy (@Nonnull final EAction aAction, final boolean tryBind)
  {
    m_eAction = JCValueEnforcer.notNull (aAction, "Action");
    m_bTryBind = tryBind;
  }

  /**
   * Action to perform if any error-type is found.
   */
  @Nonnull
  EAction action ()
  {
    return m_eAction;
  }

  /**
   * Try to rebind error-types to classes defined in jcodemodel.
   * <p>
   * We may use elements provided by Java compiler during jcodemodel code
   * generation. Existing java source code may already have references to not
   * yet generated classes. In such scenario Java-compiler will give us
   * error-types. When this occures we may try to rebind error-types to classes
   * defined in jcodemodel, but missing in existing Java source code accessible
   * to Java-compiler.
   * <p>
   * When {@code tryBind} parameter is true, we try to rebind error-types to
   * classes defined in jcodemodel. When {@code tryBind} parameter is false,
   * error-types are returned as is. Action to perform if any error-type is
   * found.
   */
  boolean tryBind ()
  {
    return m_bTryBind;
  }
}
