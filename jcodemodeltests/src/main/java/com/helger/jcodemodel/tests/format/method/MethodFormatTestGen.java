/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.jcodemodel.tests.format.method;

import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.writer.FormatterSettings;
import com.helger.jcodemodel.writer.settings.Wrap.ListWrapping.EListWrapStrategy;
import com.helger.jcodemodel.writer.settings.Wrap.WordWrapping.EWordWrapStrategy;

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

  //
  // generic wrapping : default, disabled
  //

  public void testWithDisabled(final JPackage root, FormatterSettings settings) throws JCodeModelException {
    settings.wrap.disable();
    addClassMethod(root, "DisabledWrap");
  }

  public void testWithDefaultOptions(final JPackage root) throws JCodeModelException {
    addClassMethod(root, "DefaultWrapOptions");
  }

  //
  // name
  //

  public void testWithWrapNameAlwaysIndent3(final JPackage root, FormatterSettings settings)
      throws JCodeModelException {
    settings.wrap.method.name
        .condition(EWordWrapStrategy.ALWAYS)
        .indent(3);
    addClassMethod(root, "WrapNameAlwaysI3");
  }

  public void testWithWrapNameNever(final JPackage root, FormatterSettings settings) throws JCodeModelException {
    settings.wrap.method.name.condition(EWordWrapStrategy.NEVER);
    addClassMethod(root, "WrapNameNever");
  }

  public void testWithWrapNameRequiredIndent1Width70(final JPackage root, FormatterSettings settings)
      throws JCodeModelException {
    settings.wrap.method.name
        .condition(EWordWrapStrategy.REQUIRED)
        .indent(1);
    settings.wrap.lineWidth = 70;
    addClassMethod(root, "WrapNameRequiredI1W70");
  }

  //
  // params
  //

  public void testWithWrapParamsAlways(final JPackage root, FormatterSettings settings) throws JCodeModelException {
    settings.wrap.method.params.condition(EListWrapStrategy.ALWAYS);
    addClassMethod(root, "WrapParamsAlways");
  }

  public void testWithWrapParamsAlwaysBeforeSep(final JPackage root, FormatterSettings settings)
      throws JCodeModelException {
    settings.wrap.method.params.condition(EListWrapStrategy.ALWAYS)
        .wrapBeforeSep();
    addClassMethod(root, "WrapParamsAlwaysBeforeSep");
  }

  public void testWithWrapParamsNever(final JPackage root, FormatterSettings settings) throws JCodeModelException {
    settings.wrap.method.params.condition(EListWrapStrategy.NEVER);
    addClassMethod(root, "WrapParamsNever");
  }

  public void testWithWrapParamsRequird(final JPackage root, FormatterSettings settings) throws JCodeModelException {
    settings.wrap.method.params.condition(EListWrapStrategy.REQUIRED);
    addClassMethod(root, "WrapParamsRequired");
  }

  public void testWithParamsBinaryIndent2(final JPackage root, FormatterSettings settings) throws JCodeModelException {
    settings.wrap.method.params
        .condition(EListWrapStrategy.BINARY)
        .indent(2);
    addClassMethod(root, "WrapParamsBinaryI2");
  }

  public void testWithParamsBinaryIndent2Width50(final JPackage root, FormatterSettings settings)
      throws JCodeModelException {
    settings.wrap.method.params
        .condition(EListWrapStrategy.BINARY)
        .indent(2);
    settings.wrap.lineWidth(50);
    addClassMethod(root, "WrapParamsBinaryI2W50");
  }

  //
  // bracket
  //

  public void testWithBracketWrapAlways(final JPackage root, FormatterSettings settings) throws JCodeModelException {
    settings.wrap.method.bracket
        .condition(EWordWrapStrategy.ALWAYS)
        .indent(0);
    addClassMethod(root, "WrapBracketAlways");
  }

  public void testWithBracketWrapNever(final JPackage root, FormatterSettings settings) throws JCodeModelException {
    settings.wrap.method.bracket
        .condition(EWordWrapStrategy.NEVER)
        .indent(0);
    addClassMethod(root, "WrapBracketNever");
  }

  public void testWithBracketWrapRequired(final JPackage root, FormatterSettings settings) throws JCodeModelException {
    settings.wrap.method.bracket
        .condition(EWordWrapStrategy.REQUIRED)
        .indent(0);
    addClassMethod(root, "WrapBracketRequired");
  }

}
