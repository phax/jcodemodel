package com.helger.jcodemodel.examples.plugin.yaml;

import org.junit.Assert;
import org.junit.Test;

import com.helger.jcodemodel.examples.plugin.yaml.basic.A;
import com.helger.jcodemodel.examples.plugin.yaml.basic.B;
import com.helger.jcodemodel.examples.plugin.yaml.basic.C;
import com.helger.jcodemodel.examples.plugin.yaml.basic.D;

public class YamlTest {

  @Test
  public void testBasic() {
    A a = new A(25L);
    Assert.assertEquals(25L, a.getUuid());

    B b = new B(28L);
    b.setNbChildren(30);
    Assert.assertEquals(28L, b.getUuid());
    Assert.assertEquals(30, b.getNbChildren());

    C c = new C();
    c.setRedir(b);
    Assert.assertEquals(30, c.getNbChildren());
    double[][] distances = new double[2][];
    c.setDistances(distances);
    Assert.assertSame(distances, b.getDistances());

    new D();
  }

}
