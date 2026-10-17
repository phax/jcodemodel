package com.helger.jcodemodel.expressions.typed;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JVar;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.expressions.typed.java.lang.StringExpression;
import com.helger.jcodemodel.util.CodeModelTestsHelper;

public class LambdaExpressionTest
{

  @Test
  public void test () throws JCodeModelException
  {
    JCodeModel jcm = new JCodeModel ();

    Assert.assertEquals ("java.util.Collection::isEmpty",
                         CodeModelTestsHelper.generate (LambdaExpression.predicate (jcm, Collection <?>::isEmpty)
                                                                        .raw ()));

    // we want a variable, so we need to create a class for a block
    JDefinedClass cl = jcm._class ("test");
    JBlock block = cl.init ();
    // String s = "test";
    JVar s = block.decl (jcm.ref (String.class), "test").init (JExpr.lit ("test"));
    StringExpression se = StringExpression.of (s);
    Assert.assertEquals ("test::contains",
                         CodeModelTestsHelper.generate (LambdaExpression.predicate (se::contains).raw ()));

    // can't work since we use a non-typedreference object as base
    // predicate (jcm, new HashSet <> ()::contains);
  }

}
