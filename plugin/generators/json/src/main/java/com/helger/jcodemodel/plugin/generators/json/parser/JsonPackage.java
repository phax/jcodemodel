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
package com.helger.jcodemodel.plugin.generators.json.parser;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * can be a package, a class if no package
 */
public class JsonPackage {

  @JsonProperty("var")
  public LinkedHashMap<String, JsonField> fields = new LinkedHashMap<>();

  @JsonProperty("package")
  public List<String> pck = null;

  @JsonProperty("class")
  public List<String> clazz = null;

  @JsonProperty("extends")
  public String parentClassName = null;

  private LinkedHashMap<String, JsonPackage> subPackages = new LinkedHashMap<>();

  @JsonAnyGetter
  public Map<String, JsonPackage> subPackages() {
    return subPackages;
  }

  @JsonAnySetter
  public void setPackage(String name, JsonPackage pck) {
    subPackages.put(name, pck);
  }

  /**
   * @return true if this requires to build a class
   */
  public boolean isClassInfo() {
    return clazz != null || fields != null && !fields.isEmpty() || parentClassName != null;
  }

  /**
   * @return true if this has package-specific information that need to be handled
   */
  public boolean isPackageInfo() {
    return pck != null;
  }

}
