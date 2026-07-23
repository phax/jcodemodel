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
package com.helger.jcodemodel.tests.format.catchclause;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import javax.annotation.processing.Generated;

@Generated("com.helger.jcodemodel.JCodeModel")
public class DisabledWrap {

    public static final void multiplecatch() {
        try {
        } catch (final ClassCastException ex) {
        } catch (final ArithmeticException exception1) {
        } catch (final ArrayIndexOutOfBoundsException | ArrayStoreException | BufferOverflowException | BufferUnderflowException ex) {
        }
    }
}
