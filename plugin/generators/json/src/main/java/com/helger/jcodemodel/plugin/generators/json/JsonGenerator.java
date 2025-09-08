package com.helger.jcodemodel.plugin.generators.json;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helger.jcodemodel.plugin.generators.json.parser.JsonField;
import com.helger.jcodemodel.plugin.generators.json.parser.JsonPackage;
import com.helger.jcodemodel.plugin.maven.generators.FlatStructureGenerator;
import com.helger.jcodemodel.plugin.maven.generators.JCMGen;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FieldOptions;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord.ClassCreation;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord.PackageCreation;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord.SimpleField;

@JCMGen
public class JsonGenerator extends FlatStructureGenerator {

  @Override
  protected Stream<FlatStructRecord> loadSource(InputStream source) {
    try {
      return visitPackage(load(source), null);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected JsonPackage load(InputStream source) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readerFor(JsonPackage.class).readValue(source);
  }

  protected Stream<FlatStructRecord> visitPackage(JsonPackage pck, String path) {
    System.err.println("visit package " + path);
    Stream<FlatStructRecord> ret = Stream.empty();
    if (pck.isClassInfo()) {
      if (pck.clazz != null || pck.parentClassName != null) {
        FieldOptions options = new FieldOptions();
        if (pck.clazz != null) {
          for (String optStr : pck.clazz) {
            applyToFieldOptions(optStr, options);
          }
        }
        ret = Stream.concat(ret, Stream.of(new ClassCreation(path, pck.parentClassName, options)));
      }
      if (pck.fields != null) {
        for (Entry<String, JsonField> e : pck.fields.entrySet()) {
          ret = Stream.concat(ret, visitField(e.getValue(), path, e.getKey()));
        }
      }
    } else {
      if (pck.isPackageInfo()) {
        FieldOptions options = new FieldOptions();
        if (pck.pck != null) {
          for (String optStr : pck.pck) {
            applyToFieldOptions(optStr, options);
          }
        }
        ret = Stream.concat(ret, Stream.of(new PackageCreation(path, options)));
      }
      for (Entry<String, JsonPackage> e : pck.subPackages().entrySet()) {
        String subPath = (path == null ? "" : path + ".") + e.getKey();
        ret = Stream.concat(ret, visitPackage(e.getValue(), subPath));
      }

    }
    return ret;
  }

  protected Stream<FlatStructRecord> visitField(JsonField field, String path, String fieldName) {
    FieldOptions options = new FieldOptions();
    if (field.options != null) {
      for (String optStr : field.options) {
        applyToFieldOptions(optStr, options);
      }
    }
    ArrayDepth ad = ArrayDepth.parse(field.type);
    return Stream.of(new SimpleField(path, fieldName, ad.type(), ad.arrayDepth(), options));
  }

}
