package com.helger.jcodemodel.tests.sealed;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;

@TestJCM
public class SealedTestGen {

  public void basicExample(JPackage root) throws JCodeModelException {
    @NonNull
    JDefinedClass itf = root._interface("SealedInterface");
    itf.mods().setSealed(true);
    JDefinedClass implFinal = root._class(JMod.PUBLIC | JMod.FINAL, "ImplFinal")._implements(itf);
    JDefinedClass implSealed = root._class(JMod.PUBLIC | JMod.SEALED, "ImplSealed")._implements(itf);
    JDefinedClass implNonsealed = root._class(JMod.PUBLIC | JMod.NONSEALED, "ImplNonsealed")._implements(itf);
    itf.permits(implFinal, implSealed, implNonsealed);
    JDefinedClass implSealedChildNonSealed =
        root._class(JMod.PUBLIC | JMod.NONSEALED, "ImplSealedChildNonsealed")._extends(implSealed);
    JDefinedClass implSealedChildFinal =
        root._class(JMod.PUBLIC | JMod.FINAL, "ImplSealedChildFinal")._extends(implSealed);
    implSealed.permits(implSealedChildNonSealed, implSealedChildFinal);
  }

}
