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
package com.helger.jcodemodel.plugin.maven.generators.flatstruct;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.helger.jcodemodel.plugin.maven.generators.flatstruct.IFlatStructRecord.EEncapsulation;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.IFlatStructRecord.Encapsulated;

public class FlatStructRecordTest
{

  @Test
  public void testParse ()
  {
    final Encapsulated test1 = Encapsulated.parse ("String[]");
    assertEquals ("String", test1.baseClassName ());
    assertEquals (EEncapsulation.ARRAY, test1.encapsulations ().get (0));

    final Encapsulated test2 = Encapsulated.parse ("int [ ][ ] map");
    assertEquals ("int", test2.baseClassName ());
    assertEquals (EEncapsulation.ARRAY, test2.encapsulations ().get (0));
    assertEquals (EEncapsulation.ARRAY, test2.encapsulations ().get (1));
    assertEquals (EEncapsulation.MAP, test2.encapsulations ().get (2));

    final Encapsulated test3 = Encapsulated.parse ("double []set[]");
    assertEquals ("double", test3.baseClassName ());
    assertEquals (EEncapsulation.ARRAY, test3.encapsulations ().get (0));
    assertEquals (EEncapsulation.SET, test3.encapsulations ().get (1));
    assertEquals (EEncapsulation.ARRAY, test3.encapsulations ().get (2));

    final Encapsulated test4 = Encapsulated.parse ("java.lang.String MAP   ");
    assertEquals ("java.lang.String", test4.baseClassName ());
    assertEquals (EEncapsulation.MAP, test4.encapsulations ().get (0));
  }

}
