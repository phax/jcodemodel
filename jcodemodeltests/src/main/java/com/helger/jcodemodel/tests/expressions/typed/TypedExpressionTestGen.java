package com.helger.jcodemodel.tests.expressions.typed;

import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JForLoop;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.JVar;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.expressions.typed.TETools;
import com.helger.jcodemodel.expressions.typed.java.lang.StringExpression;
import com.helger.jcodemodel.expressions.typed.primitives.IntExpression;

@TestJCM
public class TypedExpressionTestGen
{

  // create a palyndrom test on a string using the TypedExpressions
  public void palyndromTestTExpr (JPackage jp) throws JCodeModelException
  {
    JDefinedClass cl = jp._class ("PalyndromTypedExpression");
    JMethod meth = cl.method (JMod.PUBLIC_STATIC_FINAL, jp.owner ().BOOLEAN, "test");
    StringExpression se = StringExpression.param (meth, "str");
    meth.body ()._if (se.isNull ().or (se.length ().le (TETools.of (1))).raw ())._then ()._return (JExpr.TRUE);

    JForLoop for_ = meth.body ()._for ();
    IntExpression ie = IntExpression.of (for_.init (jp.owner ().INT, "i", JExpr.lit (0)));
    for_.test (ie.le (se.length ().div (TETools.of (2))).raw ());
    for_.update (ie.incrPost ().raw ());

    for_.body ()
        ._if (se.charAt (ie).ne (se.charAt (se.length ().sub (ie).sub (IntExpression.of (1)))).raw ())
        ._then ()
        ._return (JExpr.FALSE);
    meth.body ()._return (JExpr.TRUE);
  }

  // generates the same code but using the IJExpressions only
  public void palyndromTestTIJExpr (JPackage jp) throws JCodeModelException
  {
    JDefinedClass cl = jp._class ("PalyndromIJExpression");
    JMethod meth = cl.method (JMod.PUBLIC_STATIC_FINAL, jp.owner ().BOOLEAN, "test");
    JVar s = meth.param (jp.owner ().ref (String.class), "str");
    meth.body ()._if (s.eqNull ().cor (s.invoke ("length").lte (JExpr.lit (1))))._then ()._return (JExpr.TRUE);

    JForLoop for_ = meth.body ()._for ();
    JVar i = for_.init (jp.owner ().INT, "i", JExpr.lit (0));
    for_.test (i.lte (s.invoke ("length").div (JExpr.lit (2))));
    for_.update (i.incr ());

    for_.body ()
        ._if (s.invoke ("charAt")
               .arg (i)
               .ne (s.invoke ("charAt").arg (s.invoke ("length").minus (i).minus (JExpr.lit (1)))))
        ._then ()
        ._return (JExpr.FALSE);
    meth.body ()._return (JExpr.TRUE);
  }


}
