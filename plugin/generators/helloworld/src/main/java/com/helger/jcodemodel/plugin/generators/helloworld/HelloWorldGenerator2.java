package com.helger.jcodemodel.plugin.generators.helloworld;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.exceptions.JCodeModelException;

public class HelloWorldGenerator2 extends HelloWorldGenerator {

  @Override
  public void build(JCodeModel model) throws JCodeModelException {
    JDefinedClass cl = model._class(className);
    cl.field(JMod.PUBLIC, model._ref(String.class), "value2", JExpr.lit(value));
  }

}
