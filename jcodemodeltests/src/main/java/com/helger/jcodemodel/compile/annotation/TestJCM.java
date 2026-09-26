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
package com.helger.jcodemodel.compile.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.writer.FormatterSettings;

/**
 * This annotation when applied to a class notifies that this class should be used to generate test
 * classes, before running the tests. During the generation phase, the class is inspected and its
 * methods are checked to produce a {@link JCodeModel} which is then exported.
 * <p>
 * The methods that are selected for generation must verify :
 * <ol>
 * <li>Be public.</li>
 * <li>Not annotated with {@link Ignore}</li>
 * <li>Return a JCodeModel ; and/or have an argument as a JCM and/or a {@link JPackage}. The JCM
 * exported is the one produced, if any, then the one passed as argument or the one or the JPackage
 * argument.</li>
 * <li>Not have any argument that can't be resolved for injection (see below)</li>
 * <li>Can be instance or static method (no constraint).</li>
 * </ol>
 * <p>
 * When present as an argument, the package provided is the method's class' package. So in a class
 * "my.own.Class", annotated with @TestJCM , a method "call(JPackage jp)" will receive the jp with
 * path "my.own"
 * </p>
 * <p>
 * When run, the arguments of the function are resolved to actual objects and injected in the method
 * call :
 * <ul>
 * <li>A {@link JCodeModel} arg is created at most once per function. It is not shared because the
 * modification of this JCM in one function could impact the execution of another function in a
 * non-deterministic way</li>
 * <li>A {@link JPackage} arg is resolved to a package with the method's class' package, in the JCM
 * created for that method. This means if both JCM and JPackage arguments are present, the
 * JPackage's owner is the JCM.</li>
 * <li>A {@link FormatterSettings} arg is resolved to a new (default) one. Once it's created, it
 * will be applied when exporting the JCM.</li>
 * <li>Any other argument type invalidates the generation of code for that method, until we add new
 * types to inject. To avoid a method being called later, you can annotate the utility method with
 * {@link Ignore}. Or simply not make it public, as this will invalidate the selection of the
 * method</li>
 * </ul>
 * <p>
 * 
 * @see GenerateTestFiles#runGeneration
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TestJCM {

  /**
   * mark a method as ignored by the generator.
   */
  @Retention (RetentionPolicy.RUNTIME)
  @Target (ElementType.METHOD)
  public @interface Ignore
  {

  }

}
