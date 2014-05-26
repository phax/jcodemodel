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
    final JDefinedClass daInner1 = aClass._class ("Inner");
    final JDefinedClass daInnerInner = daInner1._class ("InnerInner");
    final JDefinedClass daInner2 = aClass._class ("DaTestClassInner");
    final JDefinedClass daInner2Inner = daInner2._class ("Inner2");

    assertEquals ("Inner", daInner1.name ());
    assertEquals ("org.test.DaTestClass.Inner", daInner1.fullName ());
    assertEquals ("org.test.DaTestClass$Inner", daInner1.binaryName ());

    assertEquals ("InnerInner", daInnerInner.name ());
    assertEquals ("org.test.DaTestClass.Inner.InnerInner", daInnerInner.fullName ());
    assertEquals ("org.test.DaTestClass$Inner$InnerInner", daInnerInner.binaryName ());

    aClass.method (JMod.PUBLIC, daInner1, "getInner");
    aClass.method (JMod.PUBLIC, daInnerInner, "getInnerInner");
    aClass.method (JMod.PUBLIC, daInner2, "getInner2");
    aClass.method (JMod.PUBLIC, daInner2Inner, "getInner2Inner");

    final JDefinedClass otherClass = codeModel._class ("org.test.OtherClass");
    otherClass.method (JMod.PUBLIC, daInner1, "getInner");
    otherClass.method (JMod.PUBLIC, daInnerInner, "getInnerInner");
    otherClass.method (JMod.PUBLIC, daInner2Inner, "getInner2Inner");
    otherClass.method (JMod.PUBLIC, aClass, "getOuter");

    codeModel.build (new SingleStreamCodeWriter (System.out));
  }
}
