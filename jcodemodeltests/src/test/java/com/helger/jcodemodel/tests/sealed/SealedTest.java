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
package com.helger.jcodemodel.tests.sealed;

import org.junit.Assert;
import org.junit.Test;

public class SealedTest {

  @Test
  public void testBasicExample() {
    Assert.assertTrue(SealedInterface.class.isSealed());
    Assert.assertFalse(ImplFinal.class.isSealed());
    Assert.assertFalse(ImplNonsealed.class.isSealed());
    Assert.assertTrue(ImplSealed.class.isSealed());
    Assert.assertFalse(ImplSealedChildFinal.class.isSealed());
    Assert.assertFalse(ImplSealedChildNonsealed.class.isSealed());
  }

}
