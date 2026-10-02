package com.helger.jcodemodel.tests.expressions.typed;

import com.helger.jcodemodel.IJAssignmentTarget;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JForLoop;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.expressions.typed.java.lang.StringExpression;
import com.helger.jcodemodel.expressions.typed.primitives.BoolExpression;
import com.helger.jcodemodel.expressions.typed.primitives.CharExpression;
import com.helger.jcodemodel.expressions.typed.primitives.CharExpression.CharArrExp;
import com.helger.jcodemodel.expressions.typed.primitives.IntExpression;

/// generate a class with replace(String source, char oldChar, char newChar) .
/// Checks that calling various String, array functions does not crash ^^

@TestJCM
public class StringReplaceTestGen
{

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
