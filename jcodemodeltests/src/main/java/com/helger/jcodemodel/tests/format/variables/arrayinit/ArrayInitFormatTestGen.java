package com.helger.jcodemodel.tests.format.variables.arrayinit;

import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.writer.FormatterSettings;
import com.helger.jcodemodel.writer.settings.Wrap.ListWrapping.EListWrapStrategy;

@TestJCM
public class ArrayInitFormatTestGen {

  protected static void addField(JDefinedClass clazz) {

    clazz.field(JMod.PUBLIC_STATIC_FINAL, clazz.owner().CHAR.array(), "empty", JExpr.arrayInit());
    clazz.field(JMod.PUBLIC_STATIC_FINAL, clazz.owner().INT.array(), "many",
        JExpr.arrayInit(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20));
    clazz.field(JMod.PUBLIC_STATIC_FINAL, clazz.owner().INT.array(), "few",
        JExpr.arrayInit(0, 1, 2, 3, 4));
  }

  protected static void addClassArrays(JPackage root, String className) throws JCodeModelException {
    addField(root._class(className));
  }

  // don't test with disabled : this feature did not exist before, there is
  // nothing to use

  public void testWithDefaultOptions(final JPackage root) throws JCodeModelException {
    addClassArrays(root, "DefaultArrayInitWrapOptions");
  }

  public void testWithWrapFieldBinary(final JPackage root, FormatterSettings settings) throws JCodeModelException {
    settings.wrap.variables.array.condition(EListWrapStrategy.BINARY);
    addClassArrays(root, "WrapArrayInitBinary");
  }

  public void testWithWrapFieldRequired(final JPackage root, FormatterSettings settings) throws JCodeModelException {
    settings.wrap.variables.array.condition(EListWrapStrategy.REQUIRED);
    addClassArrays(root, "WrapArrayInitRequired");
  }

  public void testWithWrapFieldNever(final JPackage root, FormatterSettings settings) throws JCodeModelException {
    settings.wrap.variables.array.condition(EListWrapStrategy.NEVER);
    addClassArrays(root, "WrapArrayInitNever");
  }

  public void testWithWrapFieldAlways(final JPackage root, FormatterSettings settings) throws JCodeModelException {
    settings.wrap.variables.array.condition(EListWrapStrategy.ALWAYS);
    addClassArrays(root, "WrapArrayInitAlways");
  }

}
