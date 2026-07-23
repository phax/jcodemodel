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
package com.helger.jcodemodel.tests.switchexpression;

import javax.annotation.processing.Generated;

@Generated("com.helger.jcodemodel.JCodeModel")
public class ESwitch {

    public static int daysIn(EnumMonths em) {
        return switch (em) {
            case JAN, MAR -> 
                31;
            case FEB -> 
                28;
            default -> {
                throw new UnsupportedOperationException();
            }
        }
        ;
    }

    public static int daysIn(EPeriod ep) {
        return switch (ep) {
            case YEAR -> 
                365;
            case WEEK -> 
                7;
            case MONTH -> 
                throw new UnsupportedOperationException("a month can have 28, 30 or 31 days.");
            default -> {
                throw new UnsupportedOperationException();
            }
        }
        ;
    }
}
