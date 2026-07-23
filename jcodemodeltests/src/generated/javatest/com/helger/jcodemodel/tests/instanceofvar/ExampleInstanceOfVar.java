/**
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
 * 
 */
package com.helger.jcodemodel.tests.instanceofvar;

import java.util.Collection;
import java.util.Map;
import javax.annotation.processing.Generated;

@Generated("com.helger.jcodemodel.JCodeModel")
public class ExampleInstanceOfVar {

    public static int toInt(Object o) {
        if (o == null) {
            return  0;
        }
        if ((o instanceof String s)&&(!s.isBlank())) {
            return s.strip().length();
        }
        if ((o instanceof Collection c)) {
            return c.size();
        }
        if ((o instanceof Map m)) {
            return m.size();
        }
        if ((o instanceof Number n)) {
            return n.intValue();
        }
        return  0;
    }
}
