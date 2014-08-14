package com.helger.jcodemodel.tests;

import com.helger.jcodemodel.*;
import com.helger.jcodemodel.tests.util.CodeModelTestsUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStreamWriter;

import static org.junit.Assert.assertEquals;

public class JTypeVarTest
{
    @Test
    public void main () throws JClassAlreadyExistsException, IOException
    {
        final JCodeModel cm = new JCodeModel ();
        final JDefinedClass cls = cm._class ("Test");
        final JMethod m = cls.method (JMod.PUBLIC, cm.VOID, "foo");
        final JTypeVar tv = m.generify("T");
        tv.bound(cm.parseType("java.lang.Comparable<T>").boxify());
        tv.bound(cm.parseType("java.lang.Serializable").boxify());

        assertEquals("T extends java.lang.Comparable<T> & java.lang.Serializable", CodeModelTestsUtils.toString(tv));
    }
}
