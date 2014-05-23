package com.helger.jcodemodel.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.writer.SingleStreamCodeWriter;

public class InnerClassTest
{
  @Test
  public void innerClassesAreImported () throws Exception
  {
    final JCodeModel codeModel = new JCodeModel ();
    final JDefinedClass aClass = codeModel._class ("org.test.DaTestClass");
    final JDefinedClass daInner = aClass._class ("Inner");

    assertEquals ("Inner", daInner.name ());
    assertEquals ("org.test.DaTestClass.Inner", daInner.fullName ());
    assertEquals ("org.test.DaTestClass$Inner", daInner.binaryName ());

    aClass.method (JMod.PUBLIC, daInner, "getInner");

    final JDefinedClass otherClass = codeModel._class ("org.test.OtherClass");
    otherClass.method (JMod.PUBLIC, daInner, "getInner");
    otherClass.method (JMod.PUBLIC, aClass, "getOuter");

    codeModel.build (new SingleStreamCodeWriter (System.out));
  }
}
