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
