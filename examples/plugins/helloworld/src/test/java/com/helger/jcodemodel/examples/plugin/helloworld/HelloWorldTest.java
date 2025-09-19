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
    Assert.assertEquals("world2", test2.value2);
    File filejava2 = new File("src/generated/java2/com/helger/tests/helloworld/Hello2.java");
    Assert.assertTrue("missing file " + filejava2.getAbsolutePath(), filejava2.isFile());
  }

}
