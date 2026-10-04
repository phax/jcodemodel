package com.helger.jcodemodel.tests.expressions.typed;

import com.helger.jcodemodel.IJStatement;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.expressions.typed.java.lang.StringExpression;
import com.helger.jcodemodel.expressions.typed.primitives.CharExpression;
import com.helger.jcodemodel.expressions.typed.primitives.IntExpression;

///
/// Check that boxing / unboxing is correctly represented.
/// 
/// Basically, this means an Integer is an int, and a char is an int, but a Character is not an Integer.
/// 
/// checks that the passing of int/Integer is correct.

@TestJCM
public class BoxedCheckTestGen
{
  public void boxedTypeExpression (JPackage jp, JCodeModel jcm) throws JCodeModelException
  {
    JDefinedClass cl = jp._class ("BoxedTypeExpression");
    JMethod meth = cl.method (JMod.PUBLIC_STATIC_FINAL, jcm.VOID, "test");
    JBlock body = meth.body ();
    StringExpression s = StringExpression.of (body.decl (jcm.ref (String.class), "s", JExpr.lit ("test")));

    "test".charAt (0);
    // compiles as we pass an int to an int param
    body.add ((IJStatement) s.charAt (IntExpression.of (0)).raw ());

    "test".charAt ((Integer) 0);
    // compiles as an Integer can be boxed into an int.
    // body.add ((IJStatement) s.charAt (ObjectExpression.<Integer> of (JExpr.lit (0).castTo
    // (jcm.ref (Integer.class))))
    // .raw ());

    "test".charAt ('\0');
    // compiles as a char is an int
    body.add ((IJStatement) s.charAt (CharExpression.of ('\0')).raw ());

    "test".charAt ((Character) '\0');
    // compiles since a Character is unboxed into a char, which is an int.
    // body.add ((IJStatement) s.charAt (ObjectExpression.<Character> of (JExpr.lit (0).castTo
    // (jcm.ref (Integer.class))));

    // does not compile since a Character is not an Integer
    // Integer i = Character.valueOf ('a');
  }

}
