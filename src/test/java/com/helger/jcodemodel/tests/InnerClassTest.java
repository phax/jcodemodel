package com.helger.jcodemodel.tests;

import org.junit.Test;

import com.helger.jcodemodel.JClassAlreadyExistsException;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.tests.util.CodeModelTestsUtils;

public class InnerClassTest {

	@Test
	public void innerClassesAreImported() throws JClassAlreadyExistsException {
		JCodeModel codeModel = new JCodeModel();
		JDefinedClass aClass = codeModel._class("org.test.DaTestClass");
//		JDefinedClass daInner = aClass._class("Inner");

//		Assert.assertEquals("org.test.DaTestClass.Inner", daInner.fullName());
//		Assert.assertEquals("org.test.DaTestClass$Inner", daInner.binaryName());
//		Assert.assertEquals("Inner", daInner.name());

//		aClass.method(JMod.PUBLIC, daInner, "getInner");
		final JDefinedClass otherClass = codeModel
				._class("org.test.OtherClass");
//		otherClass.method(JMod.PUBLIC, daInner, "getInner");
		otherClass.method(JMod.PUBLIC, aClass, "getOuter");
		System.out.println(CodeModelTestsUtils.declare(otherClass));

	}
}
