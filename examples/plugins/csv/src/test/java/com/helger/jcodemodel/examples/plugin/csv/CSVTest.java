package com.helger.jcodemodel.examples.plugin.csv;


import java.time.Instant;

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
  }

  @Test
  public void testImmutable() {
    Animal animal = new Dog(Instant.now(), null, "canus");
    animal.setName("wolf");

    new WeirdReference("a", Instant.now()).setVisible(false);
  }

}
