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
 */
package com.helger.jcodemodel.expressions.java.util;

import java.util.UUID;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.expressions.typed.java.lang.ASubObjectExpression;
import com.helger.jcodemodel.expressions.typed.primitives.IntExpression;
import com.helger.jcodemodel.expressions.typed.primitives.LngExpression;
import javax.annotation.processing.Generated;

@Generated("com.helger.jcodemodel.JCodeModel")
public final class UUIDExpr
    extends ASubObjectExpression<UUID>
{

    public UUIDExpr(IJExpression raw) {
        super(raw);
    }

    public IntExpression clockSequence() {
        return new IntExpression(this.raw().invoke("clockSequence"));
    }

    public IntExpression compareTo(UUIDExpr arg0) {
        return new IntExpression(this.raw().invoke("compareTo").arg(arg0));
    }

    public LngExpression getLeastSignificantBits() {
        return new LngExpression(this.raw().invoke("getLeastSignificantBits"));
    }

    public LngExpression getMostSignificantBits() {
        return new LngExpression(this.raw().invoke("getMostSignificantBits"));
    }

    public LngExpression node() {
        return new LngExpression(this.raw().invoke("node"));
    }

    public LngExpression timestamp() {
        return new LngExpression(this.raw().invoke("timestamp"));
    }

    public IntExpression variant() {
        return new IntExpression(this.raw().invoke("variant"));
    }

    public IntExpression version() {
        return new IntExpression(this.raw().invoke("version"));
    }
}
