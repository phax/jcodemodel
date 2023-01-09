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
package com.helger.jcodemodel;

import javax.annotation.concurrent.Immutable;

/**
 * Modifier constants.
 */
@Immutable
public final class JMod
{
  public static final int NONE = 0x000;
  public static final int PUBLIC = 0x001;
  public static final int PROTECTED = 0x002;
  public static final int PRIVATE = 0x004;
  public static final int FINAL = 0x008;
  public static final int STATIC = 0x010;
  public static final int ABSTRACT = 0x020;
  public static final int NATIVE = 0x040;
  public static final int SYNCHRONIZED = 0x080;
  public static final int TRANSIENT = 0x100;
  public static final int VOLATILE = 0x200;
  /** Java8 default method indicator */
  public static final int DEFAULT = 0x400;
  public static final int STRICTFP = 0x800;

  public static final int PRIVATE_FINAL = PRIVATE | FINAL;
  public static final int PUBLIC_STATIC_FINAL = PUBLIC | STATIC | FINAL;

  private JMod ()
  {}
}
