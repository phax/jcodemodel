package com.helger.jcodemodel.plugin.generators.json.parser;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JsonField {

  @JsonProperty("class")
  public String type;

  public List<String> options = new ArrayList<>();

}
