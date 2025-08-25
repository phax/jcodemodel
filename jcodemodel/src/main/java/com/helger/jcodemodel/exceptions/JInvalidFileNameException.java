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
package com.helger.jcodemodel.exceptions;

import jakarta.annotation.Nullable;

/**
 * <p>
 * Exception thrown when trying to create a new resource (folder, file or class file) with a name
 * that is not accepted by target platform.
 * </p>
 * <p>
 * full name, if not null, contains the full name of the directory that was invalid.
 * </p>
 * <p>
 * part name, if not null, contains the part of the directory that was invalid.
 * </p>
 * <p>
 * If both are null, it means that the platform had a rejection based on something else, eg a limit
 * on the number of different files the platform can accept //TODO should it go in another exception
 * ?
 * </p>
 * <p>
 * Typically, if the platform does not accept resources with a name starting with a "cr" , trying to
 * create the file "crazy/cropped" would fail, with fulName being "crazy/cropped" or null depending
 * on the method that threw that exception, and partName being "crazy" or "cropped" depending on the
 * method that threw that exception.
 * </p>
 *
 * @author glelouet
 */
public class JInvalidFileNameException extends JCodeModelException
{

  /** full name of the file that failed, or null */
  private final String m_sFullName;
  /** partial name of the file that failed, or null. */
  private final String m_sPartName;

  /**
   * create an exception, from an invalid relative part and or the invalid global file name
   *
   * @param fullName
   *        full name
   * @param part
   *        part name
   */
  public JInvalidFileNameException (@Nullable final String fullName, @Nullable final String part)
  {
    super ("Resource name '" +
           fullName +
           "' contains the the invalid part '" +
           part +
           "' according to the current file system conventions");
    m_sFullName = fullName;
    m_sPartName = part;
  }

  /**
   * create an exception, from an invalid relative part in a file name
   *
   * @param part
   *        part name
   */
  public JInvalidFileNameException (@Nullable final String part)
  {
    super ("invalid file name : " + part);
    m_sFullName = null;
    m_sPartName = part;
  }

  /**
   * @return the full name
   */
  @Nullable
  public String getFullName ()
  {
    return m_sFullName;
  }

  /**
   * @return the part name
   */
  @Nullable
  public String getPartName ()
  {
    return m_sPartName;
  }
}
