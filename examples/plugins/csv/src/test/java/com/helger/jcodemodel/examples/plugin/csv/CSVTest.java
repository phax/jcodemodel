package com.helger.jcodemodel.examples.plugin.csv;


import org.junit.Test;

public class CSVTest {

  @Test
  public void testValue() {
    new Example1();

    Example1b test1 = new Example1b();
    test1.i = 3;
    test1.c = 'c';
    test1.s = "s";
    test1.sarr = new String[] { "array" };

    Example2 test2 = new Example2();
    test2.darr = new double[][] { { 0.1, 0.2 } };
    test2.iarr = new int[] { 9, 7, 5, 3 };
  }

}
