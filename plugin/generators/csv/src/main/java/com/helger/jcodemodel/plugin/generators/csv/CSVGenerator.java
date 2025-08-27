
package com.helger.jcodemodel.plugin.generators.csv;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Stream;

import com.helger.jcodemodel.plugin.maven.generators.FlatStructureGenerator;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FieldConstruct;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FieldOptions;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FieldVisibility;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord.ClassCreation;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord.SimpleField;

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
    FieldOptions options = new FieldOptions();
    if (spl.length >= 4) {
      for(int i= 3; i<spl.length;i++) {
        String optStr = spl[i];
        if (optStr == null || optStr.isBlank()) {
          continue;
        }
        if (i == 3) {
          FieldVisibility fv = FieldVisibility.of(optStr);
          if (fv == null) {
            throw new UnsupportedOperationException("can't deduce visibility from "+optStr);
          } else {
            fv.apply(options);
          }
        } else {
          FieldConstruct fa = FieldConstruct.of(optStr);
          if (fa == null) {
            throw new UnsupportedOperationException("can't deduce option from "+optStr);
          } else {
            fa.apply(options);
          }
        }
      }
    }
    return new SimpleField(className, fieldName, fieldClassName, arrayDepth, options);
  }

}
