package com.helger.jcodemodel.tests.format.method;

import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.writer.FormatterOptions;
import com.helger.jcodemodel.writer.options.Wrap.WrapList.EWrapListStrategy;
import com.helger.jcodemodel.writer.options.Wrap.WrapWord.EWrapWordStrategy;

@TestJCM
public class MethodFormatTestGen {

  protected static void addMethods(JDefinedClass clazz) {
    JMethod meth =
        clazz.method(JMod.PUBLIC | JMod.STATIC, clazz.owner().VOID, "artificiallyVeryLongMethodNameWith2Args");
    meth.param(clazz.owner().INT, "i1");
    meth.param(clazz.owner().INT, "i2");

    meth = clazz.method(JMod.PUBLIC | JMod.STATIC, clazz.owner().VOID, "artificiallyVeryLongMethodNameWith5Args");
    meth.param(clazz.owner().INT, "i1");
    meth.param(clazz.owner().INT, "i2");
    meth.param(clazz.owner().INT, "i3");
    meth.param(clazz.owner().INT, "i4");
    meth.param(clazz.owner().INT, "i5");

    meth = clazz.method(JMod.PUBLIC | JMod.STATIC, clazz.owner().VOID, "artificiallyVeryLongMethodNameWithVarArgs");
    meth.varParam(clazz.owner().INT, "ints");

    meth =
        clazz.method(JMod.PUBLIC | JMod.STATIC, clazz.owner().VOID,
            "artificiallyVeryLongMethodNameWith2ArgsAndVarArgs");
    meth.param(clazz.owner().INT, "i1");
    meth.param(clazz.owner().INT, "i2");
    meth.varParam(clazz.owner().INT, "ints");

    meth =
        clazz.method(JMod.PUBLIC | JMod.STATIC, clazz.owner().VOID,
            "artificiallyVeryLongMethodNameWith5ArgsAndVarArgs");
    meth.param(clazz.owner().INT, "i1");
    meth.param(clazz.owner().INT, "i2");
    meth.param(clazz.owner().INT, "i3");
    meth.param(clazz.owner().INT, "i4");
    meth.param(clazz.owner().INT, "i5");
    meth.varParam(clazz.owner().INT, "ints");
  }

  protected static void addClassMethod(JPackage root, String className) throws JCodeModelException {
    addMethods(root._class(className));
  }

  public void testWithWrapDisabled(final JPackage root, FormatterOptions options) throws JCodeModelException {
    options.wrap.disable();
    addClassMethod(root, "WrapDisabled");
  }

  public void testWithDefaultOptions(final JPackage root) throws JCodeModelException {
    addClassMethod(root, "DefaultOptions");
  }

  public void testWithWrapParamsAlways(final JPackage root, FormatterOptions options) throws JCodeModelException {
    options.wrap.method.params.condition(EWrapListStrategy.ALWAYS);
    addClassMethod(root, "WrapParamsAlways");
  }

  public void testWithWrapParamsNever(final JPackage root, FormatterOptions options) throws JCodeModelException {
    options.wrap.method.params.condition(EWrapListStrategy.NEVER);
    addClassMethod(root, "WrapParamsNever");
  }

  public void testWithWrapParamsRequird(final JPackage root, FormatterOptions options) throws JCodeModelException {
    options.wrap.method.params.condition(EWrapListStrategy.REQUIRED);
    addClassMethod(root, "WrapParamsRequired");
  }

  public void testWithParamsBinaryIndent2(final JPackage root, FormatterOptions options) throws JCodeModelException {
    options.wrap.method.params
        .condition(EWrapListStrategy.BINARY)
        .indent(2);
    addClassMethod(root, "WrapParamsBinaryI2");
  }

  public void testWithParamsBinaryIndent2Width50(final JPackage root, FormatterOptions options)
      throws JCodeModelException {
    options.wrap.method.params
        .condition(EWrapListStrategy.BINARY)
        .indent(2);
    options.wrap.lineWidth(50);
    addClassMethod(root, "WrapParamsBinaryI2W50");
  }

  public void testWithBracketWrapAlways(final JPackage root, FormatterOptions options) throws JCodeModelException {
    options.wrap.method.bracket
        .condition(EWrapWordStrategy.ALWAYS)
        .indent(0);
    addClassMethod(root, "WrapBracketAlways");
  }

  public void testWithBracketWrapNever(final JPackage root, FormatterOptions options) throws JCodeModelException {
    options.wrap.method.bracket
        .condition(EWrapWordStrategy.NEVER)
        .indent(0);
    addClassMethod(root, "WrapBracketNever");
  }

  public void testWithBracketWrapRequired(final JPackage root, FormatterOptions options) throws JCodeModelException {
    options.wrap.method.bracket
        .condition(EWrapWordStrategy.REQUIRED)
        .indent(0);
    addClassMethod(root, "WrapBracketRequired");
  }

}
