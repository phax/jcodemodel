
package com.helger.jcodemodel.plugin.generators.csv;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.helger.jcodemodel.plugin.maven.generators.FlatStructureGenerator;
import com.helger.jcodemodel.plugin.maven.generators.JCMGen;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FieldOptions;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord.ClassCreation;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord.Encapsulated;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord.Encapsulation;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord.PackageCreation;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord.SimpleField;

@JCMGen
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
    String[] spl = line.trim().split(fldSep);
    String className = spl[0].trim();

    // field name for fields. Absent for non-fields

    String fieldName = null;
    if (spl.length > 1) {
      fieldName = spl[1].trim();
    }

    // find the type specified, if any, and array depth


    String fieldClassName = null;
    List<Encapsulation> encs = new ArrayList<>();
    if (spl.length > 2) {
      Encapsulated ec = Encapsulated.parse(spl[2]);
      fieldClassName = ec.baseClassName();
      encs = ec.encapsulations();
    }

    FieldOptions options = new FieldOptions();
    if (spl.length >= 4) {
      for (int i = 3; i < spl.length; i++) {
        applyToFieldOptions(spl[i], options);
      }
    }

    // no field name specified : class or package definition
    if (fieldName == null || fieldName.isBlank()) {
      if (className.contains(" ")) {
        return new PackageCreation(className.replaceAll(".* ", ""), options);
      } else {
        return new ClassCreation(className, fieldClassName, options);
      }
    } else {
      return new SimpleField(className, fieldName, fieldClassName, encs, options);
    }
  }

}
