/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2026 Philip Helger + contributors
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License"). You
 * may not use this file except in compliance with the License. You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt. See the License for the specific
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
 * Version 2] license." If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above. However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package com.helger.jcodemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.enforce.ValueEnforcer;
import com.helger.jcodemodel.vars.JBlockVar;

/**
 * For statement
 */
public class JForLoop implements IJStatement {

  // either a init var, or expressions
  private JBlockVar initVar = null;
  private final List<IJExpression> m_aInitExprs = new ArrayList<>();
  private IJExpression m_aTestExpr;
  private final List<IJExpression> m_aUpdateExprs = new ArrayList<>();
  private JBlock m_aBody;

  public JForLoop() {
  }

  public JBlockVar getInitVar() {
    return initVar;
  }

  /// thow an exception if can't create a new var
  protected void checkInitVar() {
    if (initVar != null) {
      throw new RuntimeException("a for loop can only have one type variable, this already has one");
    }
    if (!m_aInitExprs.isEmpty()) {
      throw new RuntimeException(
          "a for loop must have either variable declaration or expressions, this already has expressions");
    }
  }

  /// @return true if we can init using expressions
  protected void checkInitExpr() {
    if (initVar != null) {
      throw new RuntimeException(
          "a for loop must have either variable declaration or expressions, this already has variable");
    }
  }

  @NonNull
  public JBlockVar init(final int nMods,
      @NonNull final AbstractJType aType,
      @NonNull final String sVarName,
      @Nullable final IJExpression aInitExpr) {
    checkInitVar();
    final JBlockVar aVar = new JBlockVar(JMods.forVar(nMods), aType, sVarName, aInitExpr);
    initVar = aVar;
    return aVar;
  }

  @NonNull
  public JBlockVar
      init(@NonNull final AbstractJType aType, @NonNull final String sVarName, @Nullable final IJExpression aInitExpr) {
    return init(JMod.NONE, aType, sVarName, aInitExpr);
  }

  public JForLoop init(@NonNull final JVar aVar, @NonNull final IJExpression aRhs) {
    return init(JExpr.assign(aVar, aRhs));
  }

  public JForLoop init(IJExpression ije) {
    checkInitExpr();
    m_aInitExprs.add(ije);
    return this;
  }

  /**
   * @return List of {@link IJExpression} or {@link JVar}
   */
  @NonNull
  public List<IJExpression> initsMutable() {
    return m_aInitExprs;
  }

  /**
   * @return List of {@link IJExpression} or {@link JVar}
   */
  @NonNull
  public List<IJExpression> inits() {
    return Collections.unmodifiableList(initsMutable());
  }

  public void test(@Nullable final IJExpression aTestExpr) {
    m_aTestExpr = aTestExpr;
  }

  @Nullable
  public IJExpression test() {
    return m_aTestExpr;
  }

  public void update(@NonNull final IJExpression aUpdate) {
    ValueEnforcer.notNull(aUpdate, "Update");
    m_aUpdateExprs.add(aUpdate);
  }

  @NonNull
  public List<IJExpression> updatesMutable() {
    return m_aUpdateExprs;
  }

  @NonNull
  public List<IJExpression> updates() {
    return Collections.unmodifiableList(updatesMutable());
  }

  @NonNull
  public JBlock body() {
    if (m_aBody == null) {
      m_aBody = new JBlock();
    }
    return m_aBody;
  }

  @Override
  public void state(@NonNull final IJFormatter f) {
    f.print("for (");
    stateInit(f);
    f.print(';');
    if (m_aTestExpr != null) {
      f.generable(m_aTestExpr);
    }
    f.print(';');
    if (m_aUpdateExprs != null) {
      f.generable(m_aUpdateExprs);
    }
    f.print(')');
    if (m_aBody != null) {
      f.generable(m_aBody).newline();
    } else {
      f.print(';').newline();
    }
  }

  protected void stateInit(@NonNull final IJFormatter f) {
    if (initVar != null) {
      // init a variable
      if (f.options().wrap.disabled) {
        boolean bFirst = true;
        for (final JVar o : initVar.streamVars().toList()) {
          if (!bFirst) {
            f.print(',');
          }
          f.var(o);
          bFirst = false;
        }
      } else {
        f.vars(initVar.streamVars().toList(), f.options().wrap.forLoop.init);
      }
    } else
    // init a list of expressions
    if (f.options().wrap.disabled) {
      boolean bFirst = true;
      for (final IJExpression o : m_aInitExprs) {
        if (!bFirst) {
          f.print(',');
        }
        f.generable(o);
        bFirst = false;
      }
    } else {
      f.generable(m_aInitExprs, ",", f.options().wrap.forLoop.init);
    }

  }
}
