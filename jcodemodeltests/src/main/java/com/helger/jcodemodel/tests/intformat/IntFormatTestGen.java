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
        JExpr.lit(32).hex().separateEvery(separateEvery).separatorSize(sepSize));
    clazz.field(JMod.PUBLIC_STATIC_FINAL, clazz.owner().INT, "i32o",
        JExpr.lit(32).octal().separateEvery(separateEvery).separatorSize(sepSize));

    clazz.field(JMod.PUBLIC_STATIC_FINAL, clazz.owner().INT, "i1Mb",
        JExpr.lit(1024 * 1024).binary().separateEvery(separateEvery).separatorSize(sepSize));
    clazz.field(JMod.PUBLIC_STATIC_FINAL, clazz.owner().INT, "i1Md",
        JExpr.lit(1024 * 1024).decimal().separateEvery(separateEvery).separatorSize(sepSize));
    clazz.field(JMod.PUBLIC_STATIC_FINAL, clazz.owner().INT, "i1Mh",
        JExpr.lit(1024 * 1024).hex().separateEvery(separateEvery).separatorSize(sepSize));
    clazz.field(JMod.PUBLIC_STATIC_FINAL, clazz.owner().INT, "i1Mo",
        JExpr.lit(1024 * 1024).octal().separateEvery(separateEvery).separatorSize(sepSize));

    clazz.field(JMod.PUBLIC_STATIC_FINAL, clazz.owner().INT, "iNeg1kb",
        JExpr.lit(-1024).binary().separateEvery(separateEvery).separatorSize(sepSize));
    clazz.field(JMod.PUBLIC_STATIC_FINAL, clazz.owner().INT, "iNeg1kd",
        JExpr.lit(-1024).decimal().separateEvery(separateEvery).separatorSize(sepSize));
    clazz.field(JMod.PUBLIC_STATIC_FINAL, clazz.owner().INT, "iNeg1kh",
        JExpr.lit(-1024).hex().separateEvery(separateEvery).separatorSize(sepSize));
    clazz.field(JMod.PUBLIC_STATIC_FINAL, clazz.owner().INT, "iNeg1ko",
        JExpr.lit(-1024).octal().separateEvery(separateEvery).separatorSize(sepSize));
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
