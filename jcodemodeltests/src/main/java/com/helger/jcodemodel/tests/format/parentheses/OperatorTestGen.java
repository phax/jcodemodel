package com.helger.jcodemodel.tests.format.parentheses;

import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JForLoop;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JOp;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.JVar;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.vars.JBlockVar;
import com.helger.jcodemodel.writer.FormatterSettings;
import com.helger.jcodemodel.writer.settings.Parentheses.EParenthesesStrategy;

@TestJCM
public class OperatorTestGen {

  protected static void addMethods(JDefinedClass clazz) {
    {
      JMethod meth =
          clazz.method(JMod.PUBLIC | JMod.STATIC, clazz.owner().INT, "multIfSameOddityElseAdd");
      meth.javadoc().add("test precedence of ternary operator and mathematical operations");
      JVar a = meth.param(clazz.owner().INT, "a");
      JVar b = meth.param(clazz.owner().INT, "b");
      meth.body()._return(
          JExpr.cond(
              JOp.mod(a, JExpr.lit(2)).eq(JOp.mod(b, JExpr.lit(2))),
              JOp.mul(a, b),
              JOp.plus(a, b)));
    }
    {
      JMethod meth =
          clazz.method(JMod.PUBLIC | JMod.STATIC, clazz.owner().CHAR, "representBools");
      meth.javadoc().add("test precedence of multiple ternary op");
      JVar a = meth.param(clazz.owner().BOOLEAN, "a");
      JVar b = meth.param(clazz.owner().BOOLEAN, "b");
      meth.body()._return(
          JExpr.cond(
              a,
              JExpr.cond(b, JExpr.lit('3'), JExpr.lit('2')),
              JExpr.cond(b, JExpr.lit('1'), JExpr.lit('0'))));
    }
    {
      JMethod meth =
          clazz.method(JMod.PUBLIC | JMod.STATIC, clazz.owner().ref(String.class), "concat");
      meth.javadoc().add("test precedence of multiple ternary operations with other operations");
      JVar a = meth.param(clazz.owner().ref(String.class), "a");
      JVar b = meth.param(clazz.owner().ref(String.class), "b");
      meth.body()._return(
          JExpr.cond(
              a.eqNull(),
              b,
              JExpr.cond(b.eqNull(), a, a.plus(b))));
    }
    {
      JMethod meth =
          clazz.method(JMod.PUBLIC | JMod.STATIC, clazz.owner().INT, "bitwiseImply");
      meth.javadoc().add("test precedence of unary operations");
      JVar a = meth.param(clazz.owner().INT, "a");
      JVar b = meth.param(clazz.owner().INT, "b");
      meth.body()._return(
          a.complement().bor(b));
    }
    {
      JMethod meth =
          clazz.method(JMod.PUBLIC | JMod.STATIC, clazz.owner().INT, "arrIdxCoalesce");
      meth.javadoc().add("test precedence of array component and ternary operator");
      JVar a = meth.param(clazz.owner().INT.array(), "a");
      JVar b = meth.param(clazz.owner().INT.array(), "b");
      JVar i = meth.param(clazz.owner().INT, "i");
      meth.body()._return(JExpr.cond(
          a.eqNull().cor(a.ref("length").lte(i)),
          b,
          a)
          .component(i));
    }
    {
      JMethod meth =
          clazz.method (JMod.PUBLIC | JMod.STATIC, clazz.owner ().BOOLEAN, "isSortedAsc");
      meth.javadoc().add("test precedence of array component and comparison");
      JVar arr = meth.param(clazz.owner().INT.array(), "arr");
      // if(arr==null || arr.length==0) return true;
      meth.body ()._if (arr.eqNull ().cor (arr.ref ("length").lte (JExpr.lit (1))))._then ()._return (JExpr.TRUE);
      // for(int i = arr.length-2; i>= 0 ; i--)
      JForLoop _for = meth.body ()._for ();
      JBlockVar i = _for.init (clazz.owner ().INT, "i", arr.ref ("length").minus (2));
      _for.test (i.gte (0));
      _for.update (i.decr ());
      // if(arr[i]>arr[i+1) return false;
      _for.body ()._if (arr.component (i).gt (arr.component (i.plus (1))))._then ()._return (JExpr.FALSE);
      meth.body()._return(JExpr.TRUE);
    }
  }

  protected static void addClassMethod(JPackage root, String className) throws JCodeModelException {
    addMethods(root._class(className));
  }

  public void testWithParenthesesAlways(final JPackage root, FormatterSettings settings) throws JCodeModelException {
    settings.parentheses.global = EParenthesesStrategy.ALWAYS;
    addClassMethod(root, "OperatorParenthesesAlways");
  }

  public void testWithParenthesesNoToken(final JPackage root, FormatterSettings settings) throws JCodeModelException {
    settings.parentheses.global = EParenthesesStrategy.NOTOKEN;
    addClassMethod(root, "OperatorParenthesesNoToken");
  }

  public void testWithParenthesesRequired(final JPackage root, FormatterSettings settings) throws JCodeModelException {
    settings.parentheses.global = EParenthesesStrategy.REQUIRED;
    addClassMethod(root, "OperatorParenthesesRequired");
  }

}
