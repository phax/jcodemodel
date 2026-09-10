/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2026 Philip Helger + contributors
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

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.JOp.EPrecedence;

/**
 * Common interface for code components that can generate uses of themselves.
 */
public interface IJGenerable extends IJObject
{
  void generate (@NonNull IJFormatter f);

  /**
   * The binding strength of this element, used to decide whether it must be surrounded by
   * parentheses when it is used as the operand of an operator. An operand binding looser than the
   * operator applied to it changes the meaning of that operand: <code>a+b*c</code> binds as tight
   * as <code>+</code>, so applying <code>++</code> to it requires parentheses, because
   * <code>a+b*c++</code> is not <code>(a+b*c)++</code>.<br>
   * Most elements are not operators at all, so the default is the tightest binding
   * {@link EPrecedence#TOKEN} - e.g. a method invocation <code>myFunction ()</code>.
   *
   * @return The binding strength of this element. Never <code>null</code>.
   */
  @NonNull
  default EPrecedence operatorPrecedence ()
  {
    return EPrecedence.TOKEN;
  }
}
