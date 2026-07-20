package com.helger.jcodemodel.tests.format.variables.field;

import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.writer.FormatterSettings;
import com.helger.jcodemodel.writer.settings.Wrap.ListWrapping.EListWrapStrategy;

@TestJCM
public class FieldVarFormatTestGen {

  protected static void addField(JDefinedClass clazz) {
    clazz.field(JMod.PUBLIC_STATIC_FINAL, clazz.owner().CHAR.array(), "a", JExpr._null());
    JFieldVar b =
        clazz.field(JMod.PUBLIC_STATIC_FINAL, clazz.owner().CHAR.array(), "b", JExpr.arrayInit(JExpr.lit('b')));
    b.andVar("c", 1, JExpr._null());
    for (int i = 2; i < 10; i++) {
      b.andVar("variable_" + i, JExpr._null());
    }
  }

  protected static void addClassMethod(JPackage root, String className) throws JCodeModelException {
    addField(root._class(className));
  }

  // don't test with disabled : this feature did not exist before, there is
  // nothing to use

  public void testWithDefaultOptions(final JPackage root) throws JCodeModelException {
    addClassMethod(root, "DefaultWrapOptions");
  }

  public void testWithWrapFieldBinary(final JPackage root, FormatterSettings settings) throws JCodeModelException {
    settings.wrap.variables.field.condition(EListWrapStrategy.BINARY);
    addClassMethod(root, "WrapFieldBinary");
  }

  public void testWithWrapFieldRequired(final JPackage root, FormatterSettings settings) throws JCodeModelException {
    settings.wrap.variables.field.condition(EListWrapStrategy.REQUIRED);
    addClassMethod(root, "WrapFieldRequired");
  }

}
