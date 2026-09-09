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
package com.helger.jcodemodel.tests.lazy;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

public class JLazyTest {

  @Test
  public void testStatic() {
    Set<Integer> values =
        IntStream.rangeClosed(0, 1000).parallel()
            .mapToObj(i -> GeneratedLazyClass.getSyncStatic())
            .collect(Collectors.toSet());
    Assert.assertEquals(1, values.size());
  }

  @Test
  public void testInstance() {
    GeneratedLazyClass test = new GeneratedLazyClass();
    Set<Integer> values =
        IntStream.rangeClosed(0, 10000).parallel()
            .mapToObj(i -> test.getSyncInstance())
            .collect(Collectors.toSet());
    Assert.assertEquals("received " + values, 1, values.size());
  }

}
