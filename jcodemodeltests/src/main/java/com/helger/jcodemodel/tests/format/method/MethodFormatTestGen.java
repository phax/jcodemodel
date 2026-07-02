package com.helger.jcodemodel.tests.format.method;

import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.writer.FormatterOptions;
import com.helger.jcodemodel.writer.options.Wrap.EWrapListStrategy;

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

  public void testWithDefaultOptions(final JPackage root) throws JCodeModelException {
    addClassMethod(root, "DefaultOptions");
  }

  public void testWithWrapDisabled(final JPackage root, FormatterOptions options) throws JCodeModelException {
    options.wrap.disable();
    addClassMethod(root, "WrapDisabled");
  }

  public void testWithBinary2Indent(final JPackage root, FormatterOptions options) throws JCodeModelException {
    options.wrap.method.declaration
        .condition(EWrapListStrategy.BINARY)
        .indent(2);
    addClassMethod(root, "WrapBinaryI2");
  }

  public void testWithBinary2IndentWidth50(final JPackage root, FormatterOptions options) throws JCodeModelException {
    options.wrap.method.declaration
        .condition(EWrapListStrategy.BINARY)
        .indent(2);
    options.wrap.lineWidth(50);
    addClassMethod(root, "WrapBinaryI2W50");
  }

}
