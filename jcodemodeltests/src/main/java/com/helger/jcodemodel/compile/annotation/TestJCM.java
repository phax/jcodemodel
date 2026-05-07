package com.helger.jcodemodel.compile.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

///
/// This annotation when applied to a class notifies that this class should be used to generate test classes, before running the tests.
///
/// The methods that are selected must
/// 1. be public
/// 2. produce a JCodeModel or require a JCM or a package.
///
/// When requested, the package provided is the method's class' package
///
/// They can be instance or static methods
///
/// @see GenerateTestFiles#runGeneration
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TestJCM {

}
