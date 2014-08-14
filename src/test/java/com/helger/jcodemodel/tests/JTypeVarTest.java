package com.helger.jcodemodel.tests;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;

import org.junit.Test;

import com.helger.jcodemodel.JClassAlreadyExistsException;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JTypeVar;
import com.helger.jcodemodel.tests.util.CodeModelTestsUtils;

public final class JTypeVarTest
{
  @Test
  public void main () throws JClassAlreadyExistsException
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass cls = cm._class ("Test");
    final JMethod m = cls.method (JMod.PUBLIC, cm.VOID, "foo");
    final JTypeVar tv = m.generify ("T");
    tv.bound (cm.parseType ("java.lang.Comparable<T>").boxify ());
    tv.bound (cm.ref (Serializable.class));

    assertEquals ("T extends java.lang.Comparable<T> & java.io.Serializable", CodeModelTestsUtils.toString (tv));
    assertEquals ("public<T extends java.lang.Comparable<T> & java.io.Serializable> void foo() {\n" + "}\n",
                  CodeModelTestsUtils.toString (m).replace ("\r", ""));
  }
}
