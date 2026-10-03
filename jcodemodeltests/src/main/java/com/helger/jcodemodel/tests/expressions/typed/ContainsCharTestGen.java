package com.helger.jcodemodel.tests.expressions.typed;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.expressions.typed.InstanceOfExpression;
import com.helger.jcodemodel.expressions.typed.java.lang.ObjectExpression;
import com.helger.jcodemodel.expressions.typed.java.lang.StringExpression;
import com.helger.jcodemodel.expressions.typed.primitives.CharExpression;
import com.helger.jcodemodel.expressions.typed.primitives.IntExpression;

/// generate a class with contains(Object containing, char contained) .
/// Checks the instanceof pattern 
@TestJCM
public class ContainsCharTestGen
{

  public void ContainsTypedExpression (JPackage jp, JCodeModel jcm) throws JCodeModelException
  {
    JDefinedClass cl = jp._class ("ContainsTypedExpression");
    JMethod meth = cl.method (JMod.PUBLIC_STATIC_FINAL, jp.owner ().BOOLEAN, "contains");
    ObjectExpression <Object> containing = ObjectExpression.addParam (meth, Object.class, "containing");
    CharExpression contained = CharExpression.param (meth, "contained");

    // if( containing instanceof char c) return c == contained;
    // is not valid java , as objects can't be cast to primitives.
    //
    // InstanceOfExpression <Character, CharExpression> ioc = containing.instanceOf (jcm,
    // char.class,
    // "c",
    // CharExpression.class);
    // meth.body ()._if (ioc)._then ()._return (ioc.getTypedVar ().eq (contained));

    // if( containing instanceof String s && !s.isEmpty()) return s.indexOf(contained)>-1;
    InstanceOfExpression <String, StringExpression> ios = containing.instanceOf (jcm,
                                                                                 String.class,
                                                                                 "s",
                                                                                 StringExpression.class);
    meth.body ()
        ._if (ios.and (s -> s.isEmpty ().not ()))
        ._then ()
        ._return (ios.getTypedVar ().indexOf (contained).gt (IntExpression.of (-1)));

    meth.body ()._return (JExpr.FALSE);
  }

}
