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
/// 2. have 0 arguments
/// 3. produce a JCodeModel
///
/// They can be instance or static methods
///
/// @see GenerateTestFiles#runGeneration
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TestJCM {

}
