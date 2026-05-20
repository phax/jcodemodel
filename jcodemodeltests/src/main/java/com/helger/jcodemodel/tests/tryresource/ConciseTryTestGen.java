package com.helger.jcodemodel.tests.tryresource;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.JVar;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;

@TestJCM
public class ConciseTryTestGen {

  public interface NoErrorCloseable extends AutoCloseable {

    /// doesn't throw an ioexception
    @Override
    void close();
  }

  public void basicTry(JPackage root, JCodeModel jcm) throws JCodeModelException {
    JDefinedClass cl = root._class("BasicTry");
    JMethod m = cl.method(JMod.PUBLIC, jcm.VOID, "close");
    JVar p = m.param(NoErrorCloseable.class, "p");
    m.body()._try().withResource(p);
  }

}
