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
package com.helger.jcodemodel.vars;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJFormatter;

///
///  A variable that is declared in a catch block
///
///  - only allowed mod is final
///  - type is required, even though the actual type should be exception
///  - No init expression
///
/// its type must be an abstractJClass since generics are not allowed
///
/// Its internal type is the initial one, then set to null when new types are added, since it needs be deduced at compile.
///
/// @see https://docs.oracle.com/javase/specs/jls/se25/html/jls-14.html#jls-14.20-510
///
public class JCatchFormalParameter extends JArgVar {

  /// list of types for the variable
  private List<AbstractJType> m_lTypes = new ArrayList<>();

  public JCatchFormalParameter(boolean final_, @NonNull AbstractJClass aType, @NonNull String sName) {
    super(final_, aType, sName);
    m_lTypes.add(aType);
  }

  public JCatchFormalParameter addType(AbstractJClass type) {
    if (type != null) {
      type(type);
      m_lTypes.add(type);
    }
    return this;
  }

  @Override
  public AbstractJClass type() {
    return (AbstractJClass) super.type();
  }

  @Override
  protected void bindType(@NonNull IJFormatter f) {
    f.generable(m_lTypes, " | ", f.settings().wrap.catchClause.types);
  }

  @Override
  public String separator() {
    throw new UnsupportedOperationException("can't declare two vars in a catch block");
  }

}
