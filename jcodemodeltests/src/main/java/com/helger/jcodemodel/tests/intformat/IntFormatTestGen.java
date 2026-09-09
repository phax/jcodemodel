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
package com.helger.jcodemodel.tests.intformat;

import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;

@TestJCM
public class IntFormatTestGen {

  static void addFields(JDefinedClass clazz, int separateEvery, int sepSize) {
    clazz.field(JMod.PUBLIC_STATIC_FINAL, clazz.owner().INT, "i32b",
        JExpr.lit(32).binary().separateEvery(separateEvery).separatorSize(sepSize));
    clazz.field(JMod.PUBLIC_STATIC_FINAL, clazz.owner().INT, "i32d",
        JExpr.lit(32).decimal().separateEvery(separateEvery).separatorSize(sepSize));
    clazz.field(JMod.PUBLIC_STATIC_FINAL, clazz.owner().INT, "i32h",
        JExpr.lit(32).hexadecimal().separateEvery(separateEvery).separatorSize(sepSize));
    clazz.field(JMod.PUBLIC_STATIC_FINAL, clazz.owner().INT, "i32o",
        JExpr.lit(32).octal().separateEvery(separateEvery).separatorSize(sepSize));

    clazz.field(JMod.PUBLIC_STATIC_FINAL, clazz.owner().INT, "i1Mb",
        JExpr.lit(1024 * 1024).binary().separateEvery(separateEvery).separatorSize(sepSize));
    clazz.field(JMod.PUBLIC_STATIC_FINAL, clazz.owner().INT, "i1Md",
        JExpr.lit(1024 * 1024).decimal().separateEvery(separateEvery).separatorSize(sepSize));
    clazz.field(JMod.PUBLIC_STATIC_FINAL, clazz.owner().INT, "i1Mh",
        JExpr.lit(1024 * 1024).hexadecimal().separateEvery(separateEvery).separatorSize(sepSize));
    clazz.field(JMod.PUBLIC_STATIC_FINAL, clazz.owner().INT, "i1Mo",
        JExpr.lit(1024 * 1024).octal().separateEvery(separateEvery).separatorSize(sepSize));

    clazz.field(JMod.PUBLIC_STATIC_FINAL, clazz.owner().INT, "iNeg1kb",
        JExpr.lit(-1024).binary().separateEvery(separateEvery).separatorSize(sepSize));
    clazz.field(JMod.PUBLIC_STATIC_FINAL, clazz.owner().INT, "iNeg1kd",
        JExpr.lit(-1024).decimal().separateEvery(separateEvery).separatorSize(sepSize));
    clazz.field(JMod.PUBLIC_STATIC_FINAL, clazz.owner().INT, "iNeg1kh",
        JExpr.lit(-1024).hexadecimal().separateEvery(separateEvery).separatorSize(sepSize));
    clazz.field(JMod.PUBLIC_STATIC_FINAL, clazz.owner().INT, "iNeg1ko",
        JExpr.lit(-1024).octal().separateEvery(separateEvery).separatorSize(sepSize));

    clazz.field(JMod.PUBLIC_STATIC_FINAL, clazz.owner().INT, "i42p5b",
        JExpr.lit(42).binary().separateEvery(separateEvery).separatorSize(sepSize).padding(5));
    clazz.field(JMod.PUBLIC_STATIC_FINAL, clazz.owner().INT, "i42p5d",
        JExpr.lit(42).decimal().separateEvery(separateEvery).separatorSize(sepSize).padding(5));
    clazz.field(JMod.PUBLIC_STATIC_FINAL, clazz.owner().INT, "i42p5h",
        JExpr.lit(42).hexadecimal().separateEvery(separateEvery).separatorSize(sepSize).padding(5));
    clazz.field(JMod.PUBLIC_STATIC_FINAL, clazz.owner().INT, "i42p5o",
        JExpr.lit(42).octal().separateEvery(separateEvery).separatorSize(sepSize).padding(5));
  }

  static void generate(JPackage root, int separateEvery, int sepSize) throws JCodeModelException {
    JDefinedClass jdc = root._class("FormatIntWithSeparatorEvery" + separateEvery + "Size" + sepSize);
    addFields(jdc, separateEvery, sepSize);
  }

  public void generateExamples(JPackage root) throws JCodeModelException {
    generate(root, 0, 1);
    generate(root, 3, 1);
    generate(root, 2, 2);
  }

}
