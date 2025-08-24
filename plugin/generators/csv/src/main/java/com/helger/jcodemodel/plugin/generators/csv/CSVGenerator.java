
package com.helger.jcodemodel.plugin.generators.csv;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Stream;

import com.helger.jcodemodel.plugin.maven.generators.FlatStructureGenerator;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord.ClassCreation;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord.KnownClassArrayField;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord.KnownClassFlatField;

public class CSVGenerator extends FlatStructureGenerator {

  private String fldSep = ",";

  @Override
  public void configure(Map<String, String> params) {
    fldSep = params.getOrDefault("field_sep", fldSep);
  }

  @Override
  protected Stream<FlatStructRecord> loadSource(InputStream source) {
    return new BufferedReader(new InputStreamReader(source)).lines()
        .map(this::convertLine)
        .filter(r -> r != null);
  }

  protected FlatStructRecord convertLine(String line) {
    if (line == null || line.isBlank()) {
      return null;
    }
    line = line.trim();
    String[] spl = line.split(fldSep);
    String className = spl[0].trim();
    if (spl.length == 1) {
      return new ClassCreation(className);
    }
    if (spl.length == 2) {
      return null;
    }
    String fieldName = spl[1].trim(),
        fieldClassName = spl[2].trim();
    int arrayDepth = 0;
    while (fieldClassName.endsWith("[]")) {
      arrayDepth++;
      fieldClassName = fieldClassName.replaceFirst("\\[\\]", "").trim();

    }
    try {
      Class<?> cl = convertType(fieldClassName);
      return arrayDepth > 0
          ? new KnownClassArrayField(className, fieldName, cl, arrayDepth)
          : new KnownClassFlatField(className, fieldName, cl);
    } catch (ClassNotFoundException e) {
      throw new UnsupportedOperationException(e);
    }
  }

}
