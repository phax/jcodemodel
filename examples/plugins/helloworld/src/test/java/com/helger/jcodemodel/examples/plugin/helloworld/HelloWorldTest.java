package com.helger.jcodemodel.examples.plugin.helloworld;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.helger.tests.helloworld.Hello;
import com.helger.tests.helloworld.Hello2;

public class HelloWorldTest {

  @Test
  public void testValue() {
    Hello test = new Hello();
    Assert.assertEquals("world", test.value);
    File filejava = new File("src/generated/java/com/helger/tests/helloworld/Hello.java");
    Assert.assertTrue("missing file " + filejava.getAbsolutePath(), filejava.isFile());

    Hello2 test2 = new Hello2();
    Assert.assertEquals("world2", test2.value);
    File filejava2 = new File("src/generated/java2/com/helger/tests/helloworld/Hello2.java");
    Assert.assertTrue("missing file " + filejava2.getAbsolutePath(), filejava2.isFile());
  }

}
