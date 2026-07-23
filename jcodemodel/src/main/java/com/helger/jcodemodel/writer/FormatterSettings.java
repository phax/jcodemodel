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
package com.helger.jcodemodel.writer;

import java.util.function.Consumer;

import com.helger.jcodemodel.writer.settings.Indent;
import com.helger.jcodemodel.writer.settings.Wrap;
import com.helger.jcodemodel.writer.settings.Wrap.ListWrapping.EListWrapStrategy;
import com.helger.jcodemodel.writer.settings.Wrap.WordWrapping.EWordWrapStrategy;

public class FormatterSettings {

  //
  // help configs
  //

  public static final Consumer<FormatterSettings> CONF_PHELGER = settings -> {
    settings.indent
        .useSpaces (2)
        .tabSize (2);
    settings.wrap.method.args
        .condition(EListWrapStrategy.REQUIRED);
    settings.wrap.method.bracket
        .condition(EWordWrapStrategy.ALWAYS);
  };

  public static FormatterSettings phelger() {
    return new FormatterSettings().configure(CONF_PHELGER);
  }

  public static final Consumer<FormatterSettings> CONF_GLELOUET = settings -> {
    settings.indent
        .useTabs(1)
        .tabSize(2);
    settings.wrap.catchClause.types
        .condition(EListWrapStrategy.BINARY)
        .indent(2);
    settings.wrap.forLoop.init
        .condition(EListWrapStrategy.BINARY)
        .indent(2);
    settings.wrap.method.args
        .condition(EListWrapStrategy.BINARY)
        .indent(2);
    settings.wrap.method.bracket
        .condition(EWordWrapStrategy.NEVER);
    settings.wrap.method.params
        .condition(EListWrapStrategy.BINARY)
        .indent(2);
    settings.wrap.variables.block
        .condition(EListWrapStrategy.BINARY)
        .indent(2);
    settings.wrap.variables.field
        .condition(EListWrapStrategy.BINARY)
        .indent(2);
  };

  public static FormatterSettings glelouet() {
    return new FormatterSettings().configure(CONF_PHELGER);
  }

  //
  //
  //

  public final Indent indent = new Indent();

  public final Wrap wrap = new Wrap();

  public FormatterSettings configure(Consumer<FormatterSettings> conf) {
    conf.accept(this);
    return this;
  }

}
