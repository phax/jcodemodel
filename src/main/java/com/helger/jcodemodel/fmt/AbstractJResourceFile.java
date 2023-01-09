/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2020 Philip Helger + contributors
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
package com.helger.jcodemodel.fmt;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.Nonnull;
import javax.annotation.WillNotClose;

import com.helger.jcodemodel.util.JCValueEnforcer;

/**
 * Represents a resource file in the application-specific file format.
 */
public abstract class AbstractJResourceFile
{
  private final String m_sName;

  protected AbstractJResourceFile (@Nonnull final String sName)
  {
    JCValueEnforcer.notNull (sName, "Name");
    m_sName = sName;
  }

  /**
   * @return the name of this property file
   */
  @Nonnull
  public final String name ()
  {
    return m_sName;
  }

  /**
   * @return <code>true</code> if this file should be generated into the
   *         directory that the resource files go into. <code>false</code> if
   *         this file should be generated into the directory where other source
   *         files go.
   */
  public boolean isResource ()
  {
    return true;
  }

  /**
   * called by JCMWriter to produce the file image.
   *
   * @param aOS
   *        OutputStream to write to. May not be <code>null</code> and will not
   *        be closed afterwards.
   * @throws IOException
   *         If writing on the stream throws an error
   */
  public abstract void build (@Nonnull @WillNotClose OutputStream aOS) throws IOException;
}
