package com.helger.jcodemodel.plugin.generators.helloworld;

import java.io.InputStream;
import java.util.Map;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.plugin.maven.CodeModelBuilder;
import com.helger.jcodemodel.plugin.maven.generators.JCMGen;

@JCMGen
public class HelloWorldGenerator implements CodeModelBuilder {

  protected String rootPackage = "com.helger.tests.helloworld";
  protected String className = "Hello";
  protected String value = "world";

  @Override
  public void configure(Map<String, String> params) {
    className = params.getOrDefault("name", className);
    value = params.getOrDefault("value", value);
  }

  @Override
  public void build(JCodeModel model, InputStream source) throws JCodeModelException {
    JDefinedClass cl = model._class(expandClassName(className));
    cl.field(JMod.PUBLIC, model._ref(String.class), "value", JExpr.lit(value));
  }

  @Override
  public void setRootPackage(String rootPackage) {
    this.rootPackage = rootPackage;
  }

  @Override
  public String getRootPackage() {
    return rootPackage;
  }

}
