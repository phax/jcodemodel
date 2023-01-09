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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.Nonnull;
import javax.annotation.WillNotClose;

import com.helger.jcodemodel.util.JCSecureLoader;

/**
 * Allows an application to copy a resource file to the output.
 *
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class JStaticFile extends AbstractJResourceFile
{
  private final ClassLoader m_aClassLoader;
  private final String m_sResourceName;
  private final boolean m_bIsResource;

  public JStaticFile (@Nonnull final String sResourceName)
  {
    this (sResourceName, !sResourceName.endsWith (".java"));
  }

  public JStaticFile (@Nonnull final String sResourceName, final boolean bIsResource)
  {
    this (JCSecureLoader.getClassClassLoader (JStaticFile.class), sResourceName, bIsResource);
  }

  /**
   * @param aClassLoader
   *        Class loader to use. May not be <code>null</code>.
   * @param sResourceName
   *        Path to use for loading via resource. May not be <code>null</code>.
   * @param bIsResource
   *        <code>false</code> if this is a Java source file. <code>true</code>
   *        if this is other resource files.
   */
  public JStaticFile (@Nonnull final ClassLoader aClassLoader,
                      @Nonnull final String sResourceName,
                      final boolean bIsResource)
  {
    super (sResourceName.substring (sResourceName.lastIndexOf ('/') + 1));
    m_aClassLoader = aClassLoader;
    m_sResourceName = sResourceName;
    m_bIsResource = bIsResource;
  }

  @Override
  public final boolean isResource ()
  {
    return m_bIsResource;
  }

  @Override
  public void build (@Nonnull @WillNotClose final OutputStream aOS) throws IOException
  {
    try (final DataInputStream dis = new DataInputStream (m_aClassLoader.getResourceAsStream (m_sResourceName)))
    {
      final byte [] buf = new byte [256];
      int sz;
      while ((sz = dis.read (buf)) > 0)
        aOS.write (buf, 0, sz);
    }
  }
}
