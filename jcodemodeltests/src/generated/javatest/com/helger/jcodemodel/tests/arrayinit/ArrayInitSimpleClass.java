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
package com.helger.jcodemodel.tests.arrayinit;

import javax.annotation.processing.Generated;

@Generated("com.helger.jcodemodel.JCodeModel")
public class ArrayInitSimpleClass {
    public static final int[] i = { 0, 1, 2, 3, 4, 5 };
    public static final int[] i0 = {};
    public static final char[] c = {'a'};
    public static final double[] d = { 0.0, 0.1, 0.2, 0.3 };
    public static final String[] s = {null, "s", "sss"};
    public static final Object[] o = {i, i0, c, d, s };
}
