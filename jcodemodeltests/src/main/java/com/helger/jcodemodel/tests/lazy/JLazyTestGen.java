package com.helger.jcodemodel.tests.lazy;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.patterns.JLazy;

@TestJCM
public class JLazyTestGen {

  // methods for the tests to call.

  public static int count = 0;

  public static int inc() {
    return ++count;
  }

  public void testLazyClass(JPackage root) throws JCodeModelException {
    JDefinedClass lazyClass = root._class("GeneratedLazyClass");

    var syncinstance =
        new JLazy(lazyClass, Integer.class, "getSyncInstance")
        .init(root.owner().ref(JLazyTestGen.class).staticInvoke("inc"));
    var syncstatic =
        new JLazy(lazyClass, Integer.class, "getSyncStatic")
        ._static()
        .init(root.owner().ref(JLazyTestGen.class).staticInvoke("inc"));
    var asyncinstance =
        new JLazy(lazyClass, Integer.class, "getASyncInstance")
        .sync(false)
        .init(root.owner().ref(JLazyTestGen.class).staticInvoke("inc"));
    var asyncstatic =
        new JLazy(lazyClass, Integer.class, "getASyncStatic")
        ._static()
        .sync(false)
        .init(root.owner().ref(JLazyTestGen.class).staticInvoke("inc"));

    IJExpression sum =
        syncinstance.expr()
        .plus(syncstatic.expr())
        .plus(asyncinstance.expr())
        .plus(asyncstatic.expr());
    lazyClass.method(JMod.PUBLIC, root.owner().INT, "sum").body()._return(sum);
  }

}
