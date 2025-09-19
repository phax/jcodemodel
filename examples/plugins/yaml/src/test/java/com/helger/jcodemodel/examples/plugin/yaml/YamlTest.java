/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.jcodemodel.examples.plugin.yaml;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;

import com.helger.jcodemodel.examples.plugin.yaml.basic.A;
import com.helger.jcodemodel.examples.plugin.yaml.basic.B;
import com.helger.jcodemodel.examples.plugin.yaml.basic.C;
import com.helger.jcodemodel.examples.plugin.yaml.basic.Empty1;
import com.helger.jcodemodel.examples.plugin.yaml.basic.Empty2;
import com.helger.jcodemodel.examples.plugin.yaml.concrete.ConcreteList;
import com.helger.jcodemodel.examples.plugin.yaml.concrete.ConcreteMap;
import com.helger.jcodemodel.examples.plugin.yaml.concrete.ConcreteSet;

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

    new Empty1();
    new Empty2();
  }

  @Test
  public void testConcrete() {
    // just check that the implementation are indeed
    Assert.assertTrue(new ConcreteList() instanceof LinkedList<?>);
    Assert.assertTrue(new ConcreteMap() instanceof LinkedHashMap<?, ?>);
    Assert.assertTrue(new ConcreteSet() instanceof LinkedHashSet<?>);
  }

}
