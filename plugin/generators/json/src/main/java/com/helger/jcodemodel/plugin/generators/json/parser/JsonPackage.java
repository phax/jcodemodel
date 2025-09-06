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
    return clazz != null || fields != null && !fields.isEmpty();
  }

  /**
   * @return true if this has package-specific information that need to be handled
   */
  public boolean isPackageInfo() {
    return pck != null;
  }

}
