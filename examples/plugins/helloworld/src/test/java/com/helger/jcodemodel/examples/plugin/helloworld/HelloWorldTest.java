package com.helger.jcodemodel.examples.plugin.helloworld;

import org.junit.Assert;
import org.junit.Test;

import com.helger.tests.helloworld.Hello;
import com.helger.tests.helloworld.Hello2;

public class HelloWorldTest {

  @Test
  public void testValue() {
    Hello test = new Hello();
    Assert.assertEquals("world", test.value);
    Hello2 test2 = new Hello2();
    Assert.assertEquals("world2", test2.value);
  }

}
