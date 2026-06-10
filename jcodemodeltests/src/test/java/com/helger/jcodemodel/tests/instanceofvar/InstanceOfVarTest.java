package com.helger.jcodemodel.tests.instanceofvar;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class InstanceOfVarTest {

  @Test
  public void testToInt() {
    Assert.assertEquals(0, ExampleInstanceOfVar.toInt(null));

    // strings
    Assert.assertEquals(0, ExampleInstanceOfVar.toInt(""));
    Assert.assertEquals(0, ExampleInstanceOfVar.toInt("\t "));
    Assert.assertEquals(3, ExampleInstanceOfVar.toInt("\tabc "));

    // collection
    Assert.assertEquals(0, ExampleInstanceOfVar.toInt(Set.of()));
    Assert.assertEquals(1, ExampleInstanceOfVar.toInt(List.of("")));

    // map
    Assert.assertEquals(0, ExampleInstanceOfVar.toInt(Map.of()));
    Assert.assertEquals(2, ExampleInstanceOfVar.toInt(Map.of(1, 1, 2, 4)));

    // numbers
    Assert.assertEquals(0, ExampleInstanceOfVar.toInt(0));
    Assert.assertEquals(0, ExampleInstanceOfVar.toInt(0.0));
    Assert.assertEquals(0, ExampleInstanceOfVar.toInt(new BigDecimal(0L)));
    Assert.assertEquals(42, ExampleInstanceOfVar.toInt(42));
    Assert.assertEquals(2, ExampleInstanceOfVar.toInt(2.1));

    // Object

    Assert.assertEquals(0, ExampleInstanceOfVar.toInt(new Object()));

  }

}
