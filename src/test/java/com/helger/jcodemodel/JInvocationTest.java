/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2015 Philip Helger
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package com.helger.jcodemodel;

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
