
package com.helger.jcodemodel.plugin.generators.csv;

import java.io.InputStream;
import java.util.Map;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.plugin.maven.CodeModelBuilder;

public class CSVGenerator implements CodeModelBuilder {

  private String fldSep = ",";

  private String linSep = "\n";

  @Override
  public void configure(Map<String, String> params) {
    fldSep = params.getOrDefault("field_sep", fldSep);
    linSep = params.getOrDefault("line_sep", linSep);
  }

  @Override
  public void build(JCodeModel model, InputStream source) throws JCodeModelException {
    // TODO
  }

}
