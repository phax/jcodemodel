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
package com.helger.jcodemodel.tests.samevars;

import org.junit.Assert;
import org.junit.Test;

public class SameVarsTest {

  @Test
  public void testSimpleClass() {
    Assert.assertEquals(3, SameVarsSimpleClass.i);
    Assert.assertEquals(0, SameVarsSimpleClass.j);
    Assert.assertEquals(1, SameVarsSimpleClass.k);

    SameVarsSimpleClass.a1 = new char[] { 'a' };
    SameVarsSimpleClass.a2 = new char[] { 'a' };
    SameVarsSimpleClass.a3 = new char[][] { { 'a' } };

  }

}
