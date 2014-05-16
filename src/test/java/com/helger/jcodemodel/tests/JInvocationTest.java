package com.helger.jcodemodel.tests;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

import com.helger.jcodemodel.JClassAlreadyExistsException;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JTypeVar;
import com.helger.jcodemodel.writer.SingleStreamCodeWriter;

public class JInvocationTest
{
  @Test
  public void testWithGenerics () throws JClassAlreadyExistsException, IOException
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass cls = cm._class ("TestInvocation");
    final JTypeVar tc = cls.generify ("IMPL");

    final JMethod mc = cls.constructor (JMod.PUBLIC);
    mc.param (JMod.FINAL, tc, "ctor");

    final JMethod m1 = cls.method (JMod.PUBLIC, cm.VOID, "foo1");
    final JTypeVar tv1 = m1.generify ("T");
    m1.param (JMod.FINAL, tv1, "foo");
    m1.body ()._return ();

    final JMethod m1a = cls.method (JMod.PUBLIC, cm.VOID, "foo1a");
    final JTypeVar tv1a = m1a.generify ("T", BigInteger.class);
    m1a.param (JMod.FINAL, tv1a, "foo");
    m1a.body ()._return ();

    final JMethod m1b = cls.method (JMod.PUBLIC, cm.VOID, "foo1b");
    m1b.param (JMod.FINAL, cm.ref (Comparator.class).narrow (cm.ref (CharSequence.class).wildcardSuper ()), "foo");
    m1b.body ()._return ();

    final JMethod m2 = cls.method (JMod.PUBLIC, cm.VOID, "foo2");
    final JTypeVar tv21 = m2.generify ("T");
    final JTypeVar tv22 = m2.generify ("U");
    final JTypeVar tv23 = m2.generify ("V");
    m2.param (JMod.FINAL, tv21, "t");
    m2.param (JMod.FINAL, tv22, "u");
    m2.param (JMod.FINAL, tv23, "v");
    m2.body ()._return ();

    final JMethod minvoke = cls.method (JMod.PUBLIC, cm.VOID, "bar");
    minvoke.body ()._new (cls).narrow (Integer.class).arg (cm.INT.wrap (JExpr.lit (17)));
    minvoke.body ().invokeThis (m1).narrow (String.class).arg ("jippie");
    minvoke.body ().invoke (m1).arg ("jippie");
    minvoke.body ()
           .invokeThis (m2)
           .narrow (String.class)
           .narrow (cls)
           .narrow (cm.ref (List.class).narrow (Long.class))
           .arg ("jippie")
           .arg (JExpr._this ())
           .arg (JExpr._new (cm.ref (ArrayList.class).narrow (Long.class)));
    minvoke.body ()
           .invoke (m2)
           .arg ("jippie")
           .arg (JExpr._this ())
           .arg (JExpr._new (cm.ref (ArrayList.class).narrow (Long.class)));

    cm.build (new SingleStreamCodeWriter (System.out));
  }
}
