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
package com.helger.jcodemodel.tests.tryresource;

import org.junit.Test;

import com.helger.jcodemodel.tests.tryresource.ConciseTryTestGen.NoErrorCloseable;

public class ConciseTryTest {

  public class CloseMemory implements NoErrorCloseable {

    public int closed = 0;

    @Override
    public void close() {
      closed++;
    }

  }

  @Test
  public void testConciseTry() {
    CloseMemory cm = new CloseMemory();
    org.junit.Assert.assertEquals(0, cm.closed);
    new BasicTry().close(cm);
    org.junit.Assert.assertEquals(1, cm.closed);
  }

}
