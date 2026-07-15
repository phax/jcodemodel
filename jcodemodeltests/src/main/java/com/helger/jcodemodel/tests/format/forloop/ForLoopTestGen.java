package com.helger.jcodemodel.tests.format.forloop;

import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JForLoop;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.vars.JBlockVar;
import com.helger.jcodemodel.writer.FormatterSettings;
import com.helger.jcodemodel.writer.settings.Wrap.ListWrapping.EListWrapStrategy;

@TestJCM
public class ForLoopTestGen {

  public static void addForMethod(JDefinedClass jdc) {

    {
      JMethod m = jdc.method(JMod.PUBLIC_STATIC_FINAL, jdc.owner().VOID, "initExpressions");
      JBlockVar vi = m.body().decl(jdc.owner().INT, "i");
      JForLoop _for = m.body()._for();
      for (int i = 0; i < 10; i++) {
        _for.init(vi, JExpr.lit(i));
      }
    }

    {
      JMethod m = jdc.method(JMod.PUBLIC_STATIC_FINAL, jdc.owner().VOID, "initVariables");
      JForLoop _for = m.body()._for();
      JBlockVar initV = _for.init(jdc.owner().INT, "init0", JExpr.lit(0));
      for (int i = 1; i < 10; i++) {
        initV.andVar("init" + i, i, JExpr._null());
      }
    }
  }

  protected static void addClassMethod(JPackage root, String className) throws JCodeModelException {
    addForMethod(root._class(className));
  }

  public void testWithDisabled(final JPackage root, FormatterSettings settings) throws JCodeModelException {
    settings.wrap.disable();
    addClassMethod(root, "DisabledWrap");
  }

  public void testWithDefaultOptions(final JPackage root) throws JCodeModelException {
    addClassMethod(root, "DefaultWrapOptions");
  }

  public void testWithWrapInitBinary(final JPackage root, FormatterSettings settings) throws JCodeModelException {
    settings.wrap.forLoop.init.condition(EListWrapStrategy.BINARY);
    addClassMethod(root, "WrapInitBinary");
  }

  public void testWithWrapInitRequired(final JPackage root, FormatterSettings settings) throws JCodeModelException {
    settings.wrap.forLoop.init.condition(EListWrapStrategy.REQUIRED);
    addClassMethod(root, "WrapInitRequired");
  }

}
