package com.helger.jcodemodel.expressions;

import com.helger.jcodemodel.exceptions.JCodeModelException;

public class ExampleUsage {

  public static void main(String[] args) throws JCodeModelException {
    // JCodeModel jcm = new JCodeModel();
    // JDefinedClass jdc = jcm._class("Test");
    // JMethod testMethod = jdc.method(JMod.PUBLIC, jcm.VOID, "test");
    //
    // // TODO this should be a ListExpr.param call insted.
    // JFieldVar fd = jdc.field(JMod.PRIVATE,
    // jcm.ref(List.class).narrow(jcm.ref(String.class)), "myList");
    // ListExpr<String> lex = new ListExpr<>(fd);
    //
    // testMethod.body()._if(lex.isEmpty())._then()
    // .add((IJExpressionStatement) lex.add(StringExpression.of("test")).raw());

  }

}
