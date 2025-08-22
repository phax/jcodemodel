package com.helger.jcodemodel.plugin.maven;

import java.util.Map;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.exceptions.JCodeModelException;

public interface CodeModelBuilder {

  void configure(Map<String, String> params);

  void build(JCodeModel model) throws JCodeModelException;

}
