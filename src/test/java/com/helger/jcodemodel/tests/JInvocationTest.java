package com.helger.jcodemodel.tests;

import java.io.IOException;
import java.util.ArrayList;
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
  public void main () throws JClassAlreadyExistsException, IOException
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass cls = cm._class ("TestInvocation");
    final JTypeVar tc = cls.generify ("IMPL");

    final JMethod mc = cls.constructor (JMod.PUBLIC);
    mc.param (JMod.FINAL, tc, "ctor");

    final JMethod m1 = cls.method (JMod.PUBLIC, cm.VOID, "foo");
    final JTypeVar tv = m1.generify ("T");
    m1.param (JMod.FINAL, tv, "foo");
    m1.body ()._return ();

    final JMethod m2 = cls.method (JMod.PUBLIC, cm.VOID, "foo2");
    final JTypeVar tv1 = m2.generify ("T");
    final JTypeVar tv2 = m2.generify ("U");
    final JTypeVar tv3 = m2.generify ("V");
    m2.param (JMod.FINAL, tv1, "t");
    m2.param (JMod.FINAL, tv2, "u");
    m2.param (JMod.FINAL, tv3, "v");
    m2.body ()._return ();

    final JMethod minvoke = cls.method (JMod.PUBLIC, cm.VOID, "bar");
    minvoke.body ()._new (cls).generify (Integer.class).arg (cm.INT.wrap (JExpr.lit (17)));
    minvoke.body ().invokeThis (m1).generify (String.class).arg ("jippie");
    minvoke.body ()
           .invokeThis (m2)
           .generify (String.class)
           .generify (cls)
           .generify (cm.ref (List.class).narrow (Long.class))
           .arg ("jippie")
           .arg (JExpr._this ())
           .arg (JExpr._new (cm.ref (ArrayList.class).narrow (Long.class)));

    cm.build (new SingleStreamCodeWriter (System.out));
  }
}
