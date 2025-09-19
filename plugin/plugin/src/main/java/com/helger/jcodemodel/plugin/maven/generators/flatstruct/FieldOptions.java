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
package com.helger.jcodemodel.plugin.maven.generators.flatstruct;

/**
 * list of options to apply to fields. Those can be hold by classes or packages,
 * represented by their {@link #parent}.
 */
public class FieldOptions {

  private FieldOptions parent = null;

  public FieldOptions setParent(FieldOptions parent) {
    this.parent = parent;
    return this;
  }

  // is field final

  private Boolean _final = null;

  public FieldOptions setFinal(Boolean _final) {
    this._final = _final;
    return this;
  }

  public static final boolean DEFAULT_FINAL = false;

  public boolean isFinal() {
    if (_final != null) {
      return _final;
    }
    if (parent != null) {
      return parent.isFinal();
    }
    return DEFAULT_FINAL;
  }


  // create getter

  private Boolean getter = null;

  public FieldOptions setGetter(Boolean getter) {
    this.getter = getter;
    return this;
  }

  public static final boolean DEFAULT_GETTER = false;

  public boolean isGetter() {
    if (getter != null) {
      return getter;
    }
    if (parent != null) {
      return parent.isGetter();
    }
    return DEFAULT_GETTER;
  }

  // create Instant lastUpdated

  private Boolean lastUpdated = null;

  public FieldOptions setLastUpdated(Boolean lastUpdated) {
    this.lastUpdated = lastUpdated;
    return this;
  }

  public static final boolean DEFAULT_LAST_UPDATED = false;

  public boolean isLastUpdated() {
    if (lastUpdated != null) {
      return lastUpdated;
    }
    if (parent != null) {
      return parent.isLastUpdated();
    }
    return DEFAULT_LAST_UPDATED;
  }

  // is field a list

  private Boolean list = null;

  public FieldOptions setList(Boolean list) {
    this.list = list;
    return this;
  }

  public static final boolean DEFAULT_LIST = false;

  public boolean isList() {
    if (list != null) {
      return list;
    }
    if (parent != null) {
      return parent.isList();
    }
    return DEFAULT_LIST;
  }

  // redirect field methods on the owner class

  private Boolean redirect = null;

  public FieldOptions setRedirect(Boolean redirect) {
    this.redirect = redirect;
    return this;
  }

  public static final boolean DEFAULT_REDIRECT = false;

  public boolean isRedirect() {
    if (redirect != null) {
      return redirect;
    }
    if (parent != null) {
      return parent.isRedirect();
    }
    return DEFAULT_REDIRECT;
  }

  // create setter

  private Boolean setter = null;

  public FieldOptions setSetter(Boolean setter) {
    this.setter = setter;
    return this;
  }

  public static final boolean DEFAULT_SETTER = false;

  public boolean isSetter() {
    if (setter != null) {
      return setter;
    }
    if (parent != null) {
      return parent.isSetter();
    }
    return DEFAULT_SETTER;
  }

  // visibility of the field

  private FieldVisibility visibility = null;

  public FieldOptions setVisibility(FieldVisibility visibility) {
    this.visibility = visibility;
    return this;
  }

  public static final FieldVisibility DEFAULT_VISIBILITY = FieldVisibility.PUBLIC;

  public FieldVisibility getVisibility() {
    if (visibility != null) {
      return visibility;
    }
    if (parent != null) {
      return parent.getVisibility();
    }
    return DEFAULT_VISIBILITY;
  }

}
