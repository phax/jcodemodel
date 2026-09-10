package com.helger.jcodemodel.tests.format.parentheses;

import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JDoLoop;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JForLoop;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JOp;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.JSwitch;
import com.helger.jcodemodel.JVar;
import com.helger.jcodemodel.JWhileLoop;
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
    {
      JMethod meth = clazz.method(JMod.PUBLIC | JMod.STATIC, clazz.owner().INT, "subChain");
      meth.javadoc().add("a left associative operator must keep its right operand grouped");
      JVar a = meth.param(clazz.owner().INT, "a");
      JVar b = meth.param(clazz.owner().INT, "b");
      JVar c = meth.param(clazz.owner().INT, "c");
      meth.body()._return(JOp.minus(a, JOp.minus(b, c)));
    }
    {
      JMethod meth = clazz.method(JMod.PUBLIC | JMod.STATIC, clazz.owner().INT, "divChain");
      meth.javadoc().add("same as subChain, for the multiplicative level");
      JVar a = meth.param(clazz.owner().INT, "a");
      JVar b = meth.param(clazz.owner().INT, "b");
      JVar c = meth.param(clazz.owner().INT, "c");
      meth.body()._return(JOp.div(a, JOp.div(b, c)));
    }
    {
      JMethod meth = clazz.method(JMod.PUBLIC | JMod.STATIC, clazz.owner().INT, "shiftChain");
      meth.javadoc().add("same as subChain, for the shift level");
      JVar a = meth.param(clazz.owner().INT, "a");
      JVar b = meth.param(clazz.owner().INT, "b");
      JVar c = meth.param(clazz.owner().INT, "c");
      meth.body()._return(JOp.shr(a, JOp.shr(b, c)));
    }
    {
      JMethod meth = clazz.method(JMod.PUBLIC | JMod.STATIC, clazz.owner().INT, "sumLength");
      meth.javadoc().add("an operator used as the target of a method call must be grouped");
      JVar a = meth.param(clazz.owner().ref(String.class), "a");
      JVar b = meth.param(clazz.owner().ref(String.class), "b");
      meth.body()._return(JOp.plus(a, b).invoke("length"));
    }
    {
      JMethod meth = clazz.method(JMod.PUBLIC | JMod.STATIC, clazz.owner().INT, "castLength");
      meth.javadoc().add("a cast used as the target of a method call must be grouped");
      JVar o = meth.param(clazz.owner().ref(Object.class), "o");
      meth.body()._return(JExpr.cast(clazz.owner().ref(String.class), o).invoke("length"));
    }
    {
      JMethod meth = clazz.method(JMod.PUBLIC | JMod.STATIC, clazz.owner().INT, "condLength");
      meth.javadoc().add("a ternary used as the target of a method call must be grouped");
      JVar t = meth.param(clazz.owner().BOOLEAN, "t");
      JVar a = meth.param(clazz.owner().ref(String.class), "a");
      JVar b = meth.param(clazz.owner().ref(String.class), "b");
      meth.body()._return(JExpr.cond(t, a, b).invoke("length"));
    }
    {
      JMethod meth = clazz.method(JMod.PUBLIC | JMod.STATIC, clazz.owner().INT, "assignLength");
      meth.javadoc().add("an assignment used as the target of a method call must be grouped");
      JVar a = meth.param(clazz.owner().ref(String.class), "a");
      JBlockVar b = meth.body().decl(clazz.owner().ref(String.class), "b", JExpr._null());
      meth.body()._return(JExpr.assign(b, a).invoke("length"));
    }
    {
      JMethod meth = clazz.method(JMod.PUBLIC | JMod.STATIC, clazz.owner().INT, "castArrayComponent");
      meth.javadoc().add("a cast used as the target of an array access must be grouped");
      JVar o = meth.param(clazz.owner().ref(Object.class), "o");
      JVar i = meth.param(clazz.owner().INT, "i");
      meth.body()._return(JExpr.cast(clazz.owner().INT.array(), o).component(i));
    }
    {
      JMethod meth = clazz.method(JMod.PUBLIC | JMod.STATIC, clazz.owner().INT, "castArrayLength");
      meth.javadoc().add("a cast used as the target of a field access must be grouped");
      JVar o = meth.param(clazz.owner().ref(Object.class), "o");
      meth.body()._return(JExpr.cast(clazz.owner().INT.array(), o).ref("length"));
    }
    {
      JMethod meth = clazz.method(JMod.PUBLIC | JMod.STATIC, clazz.owner().INT, "negNeg");
      meth.javadoc().add("stacked unary operators must never be glued into a single token");
      JVar a = meth.param(clazz.owner().INT, "a");
      meth.body()._return(JOp.minus(JOp.minus(a)));
    }
    {
      JMethod meth = clazz.method(JMod.PUBLIC | JMod.STATIC, clazz.owner().INT, "minusNegLiteral");
      meth.javadoc().add("a negative literal must not be glued to the operator before it");
      JVar a = meth.param(clazz.owner().INT, "a");
      meth.body()._return(JOp.minus(a, JExpr.lit(-1)));
    }
    {
      JMethod meth =
          clazz.method(JMod.PUBLIC | JMod.STATIC, clazz.owner().ref(Integer.class), "castNeg");
      meth.javadoc().add("a reference type cast of a unary minus must be grouped, "
          + "because \"(Integer) -a\" is parsed as a subtraction");
      JVar a = meth.param(clazz.owner().INT, "a");
      meth.body()._return(JExpr.cast(clazz.owner().ref(Integer.class), JOp.minus(a)));
    }
    {
      JMethod meth = clazz.method(JMod.PUBLIC | JMod.STATIC, clazz.owner().INT, "countDownWhile");
      meth.javadoc().add("the test of a while loop is always parenthesized");
      JVar n = meth.param(clazz.owner().INT, "n");
      JBlockVar i = meth.body().decl(clazz.owner().INT, "i", JExpr.lit(0));
      JWhileLoop _while = meth.body()._while(JOp.gt(n, JExpr.lit(0)));
      _while.body().assign(n, JOp.minus(n, JExpr.lit(1)));
      _while.body().assign(i, JOp.plus(i, JExpr.lit(1)));
      meth.body()._return(i);
    }
    {
      JMethod meth = clazz.method(JMod.PUBLIC | JMod.STATIC, clazz.owner().INT, "doubleUntilDo");
      meth.javadoc().add("the test of a do loop is always parenthesized");
      JVar n = meth.param(clazz.owner().INT, "n");
      JDoLoop _do = meth.body()._do(JOp.lt(n, JExpr.lit(100)));
      _do.body().assign(n, JOp.mul(n, JExpr.lit(2)));
      meth.body()._return(n);
    }
    {
      JMethod meth = clazz.method(JMod.PUBLIC | JMod.STATIC, clazz.owner().INT, "switchOnSum");
      meth.javadoc().add("the test of a switch statement is always parenthesized");
      JVar a = meth.param(clazz.owner().INT, "a");
      JVar b = meth.param(clazz.owner().INT, "b");
      JSwitch _switch = meth.body()._switch(JOp.plus(a, b));
      _switch._case(JExpr.lit(0)).body()._return(JExpr.lit(10));
      _switch._case(JExpr.lit(1)).body()._return(JExpr.lit(11));
      _switch._default().body()._return(JExpr.lit(-1));
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
