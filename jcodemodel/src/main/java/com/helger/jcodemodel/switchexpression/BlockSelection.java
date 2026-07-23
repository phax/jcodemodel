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
package com.helger.jcodemodel.switchexpression;

import java.util.Iterator;
import java.util.List;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.IJStatement;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JThrow;
import com.helger.jcodemodel.JYield;

///
/// represents a list of blocks to which add statements, with syntaxic sugar for yield. Made specifically for switch (and yield)
public interface BlockSelection<Self extends BlockSelection<?>> extends Iterable<JBlock> {

  List<JBlock> blocks();

  /// cast this to the template Self type
  @SuppressWarnings("unchecked")
  default Self selfThis() {
    return (Self) this;
  }

  @Override
  default Iterator<JBlock> iterator() {
    return blocks().iterator();
  }

  default Self add(IJStatement stt) {
    for (JBlock jb : this) {
      jb.add(stt);
    }
    return selfThis();
  }

  default Self _throws(JCodeModel owner, Class<? extends Throwable> clazz, IJExpression... params) {
    return _throws(owner.ref(clazz), params);
  }

  /// create a new throw
  default Self _throws(AbstractJType t, IJExpression... params) {
    JInvocation _new = JExpr._new(t);
    if (params != null) {
      for (IJExpression ije : params) {
        _new.arg(ije);
      }
    }
    add(new JThrow(_new));
    return selfThis();
  }

  default Self yield(IJExpression exp) {
    add(new JYield(exp));
    return selfThis();
  }

}
