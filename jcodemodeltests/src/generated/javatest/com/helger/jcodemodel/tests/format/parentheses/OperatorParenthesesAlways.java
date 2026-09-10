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
public class OperatorParenthesesAlways {

    /**
     * test precedence of ternary operator and mathematical operations
     */
    public static int multIfSameOddityElseAdd(int a, int b) {
        return (((a)%(2)) == ((b)%(2)))?(a)*(b):((a)+(b));
    }

    /**
     * test precedence of multiple ternary op
     */
    public static char representBools(boolean a, boolean b) {
        return (a)?(b)?'3':('2'):((b)?'1':('0'));
    }

    /**
     * test precedence of multiple ternary operations with other operations
     */
    public static String concat(String a, String b) {
        return ((a) == (null))?b:(((b) == (null))?a:((a)+(b)));
    }

    /**
     * test precedence of unary operations
     */
    public static int bitwiseImply(int a, int b) {
        return (~(a))|(b);
    }

    /**
     * test precedence of array component and ternary operator
     */
    public static int arrIdxCoalesce(int[] a, int[] b, int i) {
        return ((((a) == (null))||(((a).length)<= (i)))?b:(a))[i];
    }

    /**
     * test precedence of array component and comparison
     */
    public static boolean isSortedAsc(int[] arr) {
        if (((arr) == (null))||(((arr).length)<= (1))) {
            return true;
        }
        for (int i = ((arr).length)-(2); (i)>= (0); (i)--) {
            if (((arr)[i])>((arr)[(i)+(1)])) {
                return false;
            }
        }
        return true;
    }

    /**
     * a left associative operator must keep its right operand grouped
     */
    public static int subChain(int a, int b, int c) {
        return (a)-((b)-(c));
    }

    /**
     * same as subChain, for the multiplicative level
     */
    public static int divChain(int a, int b, int c) {
        return (a)/((b)/(c));
    }

    /**
     * same as subChain, for the shift level
     */
    public static int shiftChain(int a, int b, int c) {
        return (a)>>((b)>>(c));
    }

    /**
     * an operator used as the target of a method call must be grouped
     */
    public static int sumLength(String a, String b) {
        return ((a)+(b)).length();
    }

    /**
     * a cast used as the target of a method call must be grouped
     */
    public static int castLength(Object o) {
        return ((String)(o)).length();
    }

    /**
     * a ternary used as the target of a method call must be grouped
     */
    public static int condLength(boolean t, String a, String b) {
        return ((t)?a:(b)).length();
    }

    /**
     * an assignment used as the target of a method call must be grouped
     */
    public static int assignLength(String a) {
        String b = null;
        return (b = (a)).length();
    }

    /**
     * a cast used as the target of an array access must be grouped
     */
    public static int castArrayComponent(Object o, int i) {
        return ((int[])(o))[i];
    }

    /**
     * a cast used as the target of a field access must be grouped
     */
    public static int castArrayLength(Object o) {
        return ((int[])(o)).length;
    }

    /**
     * stacked unary operators must never be glued into a single token
     */
    public static int negNeg(int a) {
        return -(-(a));
    }

    /**
     * a negative literal must not be glued to the operator before it
     */
    public static int minusNegLiteral(int a) {
        return (a)-(-1);
    }

    /**
     * a reference type cast of a unary minus must be grouped, because "(Integer) -a" is parsed as a subtraction
     */
    public static Integer castNeg(int a) {
        return (Integer)(-(a));
    }

    /**
     * the test of a while loop is always parenthesized
     */
    public static int countDownWhile(int n) {
        int i = 0;
        while ((n)>(0)) {
            n = ((n)-(1));
            i = ((i)+(1));
        }
        return i;
    }

    /**
     * the test of a do loop is always parenthesized
     */
    public static int doubleUntilDo(int n) {
        do {
            n = ((n)*(2));
        } while ((n)<(100));
        return n;
    }

    /**
     * the test of a switch statement is always parenthesized
     */
    public static int switchOnSum(int a, int b) {
        switch ((a)+(b)) {
            case  0 :
            {
                return  10;
            }
            case  1 :
            {
                return  11;
            }
            default:
            {
                return -1;
            }
        }
    }
}
