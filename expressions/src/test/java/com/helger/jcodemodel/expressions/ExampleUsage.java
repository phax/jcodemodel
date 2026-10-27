package com.helger.jcodemodel.expressions;

import java.util.List;

import com.helger.jcodemodel.IJExpressionStatement;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.expressions.java.util.ListExpr;
import com.helger.jcodemodel.expressions.typed.java.lang.StringExpression;

public class ExampleUsage {

  public static void main(String[] args) throws JCodeModelException {
    JCodeModel jcm = new JCodeModel();
    JDefinedClass jdc = jcm._class("Test");
    JMethod testMethod = jdc.method(JMod.PUBLIC, jcm.VOID, "test");
    JFieldVar fd = jdc.field(JMod.PRIVATE, jcm.ref(List.class).narrow(jcm.ref(String.class)), "myList");
    ListExpr<String> lex = new ListExpr<>(fd);
    StringExpression.of("test");
    testMethod.body()._if(lex.isEmpty())._then()
        .add((IJExpressionStatement) lex.add(StringExpression.of("test")).raw());

  }

}
