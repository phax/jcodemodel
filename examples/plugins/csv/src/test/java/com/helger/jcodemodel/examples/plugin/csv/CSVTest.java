/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2025 Philip Helger + contributors
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
package com.helger.jcodemodel.examples.plugin.csv;


import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.examples.plugin.csv.basic.EmptyClass;
import com.helger.jcodemodel.examples.plugin.csv.basic.SimpleFields;
import com.helger.jcodemodel.examples.plugin.csv.deeparray.Example2;
import com.helger.jcodemodel.examples.plugin.csv.getset.Example3;
import com.helger.jcodemodel.examples.plugin.csv.immutable.Animal;
import com.helger.jcodemodel.examples.plugin.csv.immutable.Dog;
import com.helger.jcodemodel.examples.plugin.csv.immutable.WeirdReference;
import com.helger.jcodemodel.examples.plugin.csv.inherit.City;
import com.helger.jcodemodel.examples.plugin.csv.inherit.Dated;
import com.helger.jcodemodel.examples.plugin.csv.lastupdated.LastUpdated;
import com.helger.jcodemodel.examples.plugin.csv.redirect.ABC;
import com.helger.jcodemodel.examples.plugin.csv.redirect.Redirected;
import com.helger.jcodemodel.examples.plugin.csv.resolve.Child;
import com.helger.jcodemodel.examples.plugin.csv.resolve.Imported;
import com.helger.jcodemodel.examples.plugin.csv.resolve.Parent;

public class CSVTest {

  @Test
  public void testBasic() {
    new EmptyClass();

    SimpleFields test1 = new SimpleFields();
    test1.i = 3;
    test1.c = 'c';
    test1.s = "s";
    test1.sarr = new String[] { "array" };
  }

  @Test
  public void testDeepArray() {
    Example2 test = new Example2();
    test.darr = new double[][] { { 0.1, 0.2 } };
    test.iarr = new int[] { 9, 7, 5, 3 };
  }

  @Test
  public void testGetSet() {
    Example3 test = new Example3();
    test.setI(45);
    Assert.assertEquals(45, test.getI());
    test.getSarr();
  }

  @Test
  public void testResolve() {
    Parent parent = new Parent();
    Child child = new Child();
    parent.setChildren(new Child[] { child });
    child.setParent(parent);
    parent.getChildren();
    child.getParent();

    Imported imported = new Imported();
    imported.setModel(new JCodeModel());
    imported.setModelArr(new JCodeModel[] {});
  }

  @Test
  public void testLastUpdated() {
    LastUpdated test = new LastUpdated();
    Assert.assertNull(test.getLastUpdated());
    test.setI(5);
    Assert.assertNotNull(test.getLastUpdated());
  }

  @Test
  public void testRedirect() {
    ABC abc = new ABC();
    Redirected redirected = new Redirected();
    redirected.setAbc(abc);
    abc.setA(49);
    Assert.assertEquals(49, redirected.getA());
    abc.setA(404);
    Assert.assertEquals(404, redirected.getA());
  }

  @Test
  public void testInherit() {
    City test = new City();
    test.setX(25);

    Serializable c = new Dated(Instant.now());
    c.toString();
  }

  @Test
  public void testImmutable() {
    Animal animal = new Dog(Instant.now(), 0, null, new ArrayList<>(), "canus");
    animal.setName("wolf");

    new WeirdReference("a", Instant.now()).setVisible(false);
  }

}
