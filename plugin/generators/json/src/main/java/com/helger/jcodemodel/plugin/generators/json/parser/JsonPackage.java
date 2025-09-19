/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
