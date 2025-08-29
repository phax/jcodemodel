package com.helger.jcodemodel.examples.plugin.csv;


import org.junit.Assert;
import org.junit.Test;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.examples.plugin.csv.example4.Child;
import com.helger.jcodemodel.examples.plugin.csv.example4.Imported;
import com.helger.jcodemodel.examples.plugin.csv.example4.Parent;
import com.helger.jcodemodel.examples.plugin.csv.example5.ABC;
import com.helger.jcodemodel.examples.plugin.csv.example5.LastUpdated;
import com.helger.jcodemodel.examples.plugin.csv.example5.Redirected;

public class CSVTest {

  @Test
  public void testExample1() {
    new Example1();

    Example1b test1 = new Example1b();
    test1.i = 3;
    test1.c = 'c';
    test1.s = "s";
    test1.sarr = new String[] { "array" };
  }

  @Test
  public void testExample2() {
    Example2 test = new Example2();
    test.darr = new double[][] { { 0.1, 0.2 } };
    test.iarr = new int[] { 9, 7, 5, 3 };
  }

  @Test
  public void testExample3() {
    Example3 test = new Example3();
    test.setI(45);
    Assert.assertEquals(45, test.getI());
    test.getSarr();
  }

  @Test
  public void testExample4() {
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
  public void testExample5LastUpdated() {
    LastUpdated test = new LastUpdated();
    Assert.assertNull(test.getLastUpdated());
    test.setI(5);
    Assert.assertNotNull(test.getLastUpdated());
  }

  @Test
  public void testExample5Redirect() {
    ABC abc = new ABC();
    Redirected redirected = new Redirected();
    redirected.setAbc(abc);
    abc.setA(49);
    Assert.assertEquals(49, redirected.getA());
    abc.setA(404);
    Assert.assertEquals(404, redirected.getA());
  }

}
