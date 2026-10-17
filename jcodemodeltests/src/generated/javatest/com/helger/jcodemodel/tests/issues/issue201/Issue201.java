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
package com.helger.jcodemodel.tests.issues.issue201;

import javax.annotation.processing.Generated;

@Generated("com.helger.jcodemodel.JCodeModel")
public class Issue201 {

    public static void test(int i) {
        switch (i) {
            case 1 ->
                i = 42;
            case 2 ->
            {
                i = 40;
                i = 40;
            }
            default ->
                i = i = i = i = i = i = i = i = i = i = i = i = i = i = i = i = i = i = i = i = i = i = i = i = i = i = -1;
        }
        i = switch (i) {
            case 1 ->
                42;
            case 2 ->
            {
                i = i++;
                yield 40;
            }
            default ->
                i;
        };
    }
}
