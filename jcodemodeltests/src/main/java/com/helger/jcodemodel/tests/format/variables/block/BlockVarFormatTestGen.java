package com.helger.jcodemodel.tests.format.variables.block;

import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.vars.JBlockVar;
import com.helger.jcodemodel.writer.FormatterSettings;
import com.helger.jcodemodel.writer.settings.Wrap.ListWrapping.EListWrapStrategy;

@TestJCM
public class BlockVarFormatTestGen {

  protected static void addMethods(JDefinedClass clazz) {
    JMethod meth =
        clazz.method(JMod.PUBLIC | JMod.STATIC, clazz.owner().VOID, "test");
    meth.body().decl(clazz.owner().CHAR, "a");
    JBlockVar b = meth.body().decl(clazz.owner().CHAR.array(), "b", JExpr._null());
    b.andVar("c", 1, JExpr.arrayInit(JExpr.arrayInit()));
    for (int i = 2; i < 10; i++) {
      char charidx = (char) ('b' + i);
      b.andVar("variable_" + charidx, JExpr.arrayInit(JExpr.lit(charidx)));
    }
  }

  protected static void addClassMethod(JPackage root, String className) throws JCodeModelException {
    addMethods(root._class(className));
  }

  // don't test with disabled : this feature did not exist before, there is
  // nothing to use

  public void testWithDefaultOptions(final JPackage root) throws JCodeModelException {
    addClassMethod(root, "DefaultWrapOptions");
  }

  public void testWithWrapBlockBinary(final JPackage root, FormatterSettings settings) throws JCodeModelException {
    settings.wrap.variables.block.condition(EListWrapStrategy.BINARY);
    addClassMethod(root, "WrapBlockBinary");
  }

  public void testWithWrapBlockRequired(final JPackage root, FormatterSettings settings) throws JCodeModelException {
    settings.wrap.variables.block.condition(EListWrapStrategy.REQUIRED);
    addClassMethod(root, "WrapBlockRequired");
  }

}
