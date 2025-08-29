package com.helger.jcodemodel.plugin.generators.helloworld;

import java.util.Map;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.plugin.maven.CodeModelBuilder;

public class HelloWorldGenerator implements CodeModelBuilder {

  protected String className = "com.helger.tests.helloworld.Hello";
  protected String value = "world";

  @Override
  public void configure(Map<String, String> params) {
    className = params.getOrDefault("name", className);
    value = params.getOrDefault("value", value);
  }

  @Override
  public void build(JCodeModel model) throws JCodeModelException {
    JDefinedClass cl = model._class(className);
    cl.field(JMod.PUBLIC, model._ref(String.class), "value", JExpr.lit(value));
  }

}
