package com.helger.jcodemodel.tests.expressions.typed;

import com.helger.jcodemodel.IJAssignmentTarget;
import com.helger.jcodemodel.JBlock;
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
import com.helger.jcodemodel.expressions.typed.primitives.BoolExpression;
import com.helger.jcodemodel.expressions.typed.primitives.CharExpression;
import com.helger.jcodemodel.expressions.typed.primitives.CharExpression.CharArrExp;
import com.helger.jcodemodel.expressions.typed.primitives.IntExpression;

@TestJCM
public class TypedExpressionTestGen
{

  // create a palyndrom test on a string using the TypedExpressions
  public void palyndromTypedExpr (JPackage jp) throws JCodeModelException
  {
    JDefinedClass cl = jp._class ("PalyndromTypedExpression");
    JMethod meth = cl.method (JMod.PUBLIC_STATIC_FINAL, jp.owner ().BOOLEAN, "test");
    StringExpression se = StringExpression.param (meth, "str");
    meth.body ()._if (se.isNull ().or (se.length ().le (TETools.of (1))))._then ()._return (JExpr.TRUE);

    JForLoop for_ = meth.body ()._for ();
    IntExpression ie = IntExpression.of (for_.init (jp.owner ().INT, "i", JExpr.lit (0)));
    for_.test (ie.le (se.length ().div (TETools.of (2))));
    for_.update (ie.postIncr ());

    for_.body ()
        ._if (se.charAt (ie).ne (se.charAt (se.length ().sub (ie).sub (IntExpression.of (1)))))
        ._then ()
        ._return (JExpr.FALSE);
    meth.body ()._return (JExpr.TRUE);
  }

  // generates the same code but using the IJExpressions only
  public void palyndromIJExpr (JPackage jp) throws JCodeModelException
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

  // generate a class with replace(String source, char oldChar, char newChar)
  public void stringReplaceTypedExpression (JPackage jp) throws JCodeModelException
  {
    JDefinedClass cl = jp._class ("StringReplaceTypedExpression");
    JMethod meth = cl.method (JMod.PUBLIC_STATIC_FINAL, jp.owner ().ref (String.class), "replace");
    StringExpression source = StringExpression.param (meth, "source");
    CharExpression oldChar = CharExpression.param (meth, "oldChar");
    CharExpression newchar = CharExpression.param (meth, "newchar");
    JBlock body = meth.body ();
    body._if (source.isNull ().or (source.isEmpty ()))._then ()._return (source);
    CharArrExp arr = new CharArrExp (body.decl (jp.owner ().ref (char [].class), "arr", source.toCharArray ().raw ()));
    BoolExpression changed = new BoolExpression (body.decl (jp.owner ().BOOLEAN, "changed", JExpr.lit (false)));
    JForLoop for_ = body._for ();
    IntExpression i = IntExpression.of (for_.init (jp.owner ().INT, "i", JExpr.lit (0)));
    for_.test (i.lt (arr.length ()));
    for_.update (i.postIncr ());
    JBlock forBody = for_.body ();
    JBlock onChange = forBody._if (arr.at (i).eq (oldChar))._then ();
    onChange.add (JExpr.assign ((IJAssignmentTarget) arr.at (i).raw (), newchar));
    onChange.add (JExpr.assign ((IJAssignmentTarget) changed.raw (), JExpr.lit (true)));
    body._return (changed.ternary (StringExpression.new_ (jp.owner (), arr), source));
  }

}
