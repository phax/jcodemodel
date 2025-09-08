package com.helger.jcodemodel.plugin.generators.yaml;


import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.helger.jcodemodel.plugin.generators.json.JsonGenerator;
import com.helger.jcodemodel.plugin.generators.json.parser.JsonPackage;
import com.helger.jcodemodel.plugin.maven.generators.JCMGen;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord;

@JCMGen
public class YamlGenerator extends JsonGenerator {

  @Override
  protected Stream<FlatStructRecord> loadSource(InputStream source) {
    Stream<FlatStructRecord> ret = Stream.empty();
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    try {
      JsonPackage ms = mapper.readerFor(JsonPackage.class).readValue(source);
      ret = Stream.concat(ret, visitPackage(ms, null));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return ret;
  }

}
