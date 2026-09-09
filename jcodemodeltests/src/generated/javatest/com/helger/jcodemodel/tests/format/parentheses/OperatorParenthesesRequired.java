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
package com.helger.jcodemodel.tests.format.parentheses;

import javax.annotation.processing.Generated;

@Generated("com.helger.jcodemodel.JCodeModel")
public class OperatorParenthesesRequired {

    /**
     * test precedence of ternary operator and mathematical operations
     */
    public static int multIfSameOddityElseAdd(int a, int b) {
        return a% 2 == b% 2 ?a*b:a + b;
    }

    /**
     * test precedence of multiple ternary op
     */
    public static char representBools(boolean a, boolean b) {
        return a?(b?'3':'2'):(b?'1':'0');
    }

    /**
     * test precedence of multiple ternary operations with other operations
     */
    public static String concat(String a, String b) {
        return a == null?b:(b == null?a:a + b);
    }

    /**
     * test precedence of unary operations
     */
    public static int bitwiseImply(int a, int b) {
        return ~a|b;
    }

    /**
     * test precedence of array component and ternary operator
     */
    public static int arrIdxCoalesce(int[] a, int[] b, int i) {
        return (a == null||a.length<= i?b:a)[i];
    }

    /**
     * test precedence of array component and comparison
     */
    public static boolean isSortedAsc(int[] arr) {
        if (arr == null||arr.length<= 1) {
            return true;
        }
        for (int i = arr.length - 2; i >= 0; i --) {
            if (arr[i]>arr[i + 1 ]) {
                return false;
            }
        }
        return true;
    }
}
