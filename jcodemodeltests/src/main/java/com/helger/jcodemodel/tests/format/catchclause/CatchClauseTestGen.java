package com.helger.jcodemodel.tests.format.catchclause;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;

import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.JTryBlock;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.writer.FormatterSettings;
import com.helger.jcodemodel.writer.settings.Wrap.ListWrapping.EListWrapStrategy;

@TestJCM
public class CatchClauseTestGen {

  public static void addCatchMethod(JDefinedClass jdc) {
    {
      JMethod m = jdc.method(JMod.PUBLIC_STATIC_FINAL, jdc.owner().VOID, "multiplecatch");
      JTryBlock tr = m.body()._try();
      // we just iterate over a lot of various children of RuntimeException in the
      // base java packages
      tr._catch(jdc.owner().ref(ClassCastException.class));
      tr._catch(jdc.owner().ref(ArithmeticException.class), "exception1");
      tr._catch(jdc.owner().ref(ArrayIndexOutOfBoundsException.class)).param()
          .addType(jdc.owner().ref(ArrayStoreException.class))
          .addType(jdc.owner().ref(BufferOverflowException.class))
          .addType(jdc.owner().ref(BufferUnderflowException.class));
    }
  }

  protected static void addClassMethod(JPackage root, String className) throws JCodeModelException {
    addCatchMethod(root._class(className));
  }

  public void testWithDisabled(final JPackage root, FormatterSettings settings) throws JCodeModelException {
    settings.wrap.disable();
    addClassMethod(root, "DisabledWrap");
  }

  public void testWithDefaultOptions(final JPackage root) throws JCodeModelException {
    addClassMethod(root, "DefaultWrapOptions");
  }

  public void testWithWrapTypesBinary(final JPackage root, FormatterSettings settings) throws JCodeModelException {
    settings.wrap.catchClause.types.condition(EListWrapStrategy.BINARY);
    addClassMethod(root, "WrapTypesBinary");
  }

  public void testWithWrapTypesRequired(final JPackage root, FormatterSettings settings) throws JCodeModelException {
    settings.wrap.catchClause.types.condition(EListWrapStrategy.REQUIRED);
    addClassMethod(root, "WrapTypesRequired");
  }

}
