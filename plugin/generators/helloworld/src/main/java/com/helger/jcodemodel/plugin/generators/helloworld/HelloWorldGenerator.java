package com.helger.jcodemodel.plugin.generators.helloworld;

import java.util.Map;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.plugin.maven.CodeModelBuilder;

public class HelloWorldGenerator implements CodeModelBuilder {

  private String className = null;
  private String value = null;

  @Override
  public void configure(Map<String, String> params) {
    className = params.get("name");
    value = params.get("value");
  }

  @Override
  public void build(JCodeModel model) throws JCodeModelException {
    JDefinedClass cl = model._class(className == null ? "Hello" : className);
    cl.field(JMod.PUBLIC, model._ref(String.class), "value").assign(JExpr.lit(value == null ? "world" : value));
  }

}
