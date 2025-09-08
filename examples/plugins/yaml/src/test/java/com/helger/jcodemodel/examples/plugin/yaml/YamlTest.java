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
    A a = new A(25);
    Assert.assertEquals(25, a.getA());

    B b = new B(28);
    b.setB(30);
    Assert.assertEquals(28, b.getA());
    Assert.assertEquals(30, b.getB());

    C c = new C();
    c.setRedir(b);
    Assert.assertEquals(30, c.getB());

    new D();
  }

}
