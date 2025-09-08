package com.helger.jcodemodel.plugin.generators.yaml;


import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.helger.jcodemodel.plugin.generators.json.JsonGenerator;
import com.helger.jcodemodel.plugin.generators.json.parser.JsonPackage;
import com.helger.jcodemodel.plugin.maven.generators.JCMGen;

@JCMGen
public class YamlGenerator extends JsonGenerator {

  @Override
  protected JsonPackage load(InputStream source) throws IOException {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    return mapper.readerFor(JsonPackage.class).readValue(source);
  }

}
