/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2025 Philip Helger + contributors
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
package com.helger.jcodemodel.util;

import java.io.Serializable;
import java.util.Locale;

import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.hashcode.IHashCodeGenerator;

import jakarta.annotation.Nonnull;

/**
 * Utility class to represent case sensitive or case insensitive keys for file
 * and directory names.
 *
 * @author Philip Helger
 * @since 3.4.0
 */
public final class FSName implements Comparable <FSName>, Serializable
{
  private final String m_sName;
  private final String m_sKey;
  // status vars
  private int m_nHashCode = IHashCodeGenerator.ILLEGAL_HASHCODE;

  private FSName (@Nonnull final String sName, @Nonnull final String sKey)
  {
    m_sName = sName;
    m_sKey = sKey;
  }

  @Nonnull
  public String getName ()
  {
    return m_sName;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final FSName rhs = (FSName) o;
    return m_sKey.equals (rhs.m_sKey);
  }

  @Override
  public int hashCode ()
  {
    int ret = m_nHashCode;
    if (ret == IHashCodeGenerator.ILLEGAL_HASHCODE)
      ret = m_nHashCode = new HashCodeGenerator (this).append (m_sKey).getHashCode ();
    return ret;
  }

  @Override
  public int compareTo (@Nonnull final FSName o)
  {
    return m_sKey.compareTo (o.m_sKey);
  }

  @Nonnull
  public static FSName createCaseSensitive (@Nonnull final String sName)
  {
    ValueEnforcer.notNull (sName, "Name");
    return new FSName (sName, sName);
  }

  @Nonnull
  public static FSName createCaseInsensitive (@Nonnull final String sName)
  {
    ValueEnforcer.notNull (sName, "Name");
    // Unify key to upper case
    return new FSName (sName, sName.toUpperCase (Locale.ROOT));
  }
}
