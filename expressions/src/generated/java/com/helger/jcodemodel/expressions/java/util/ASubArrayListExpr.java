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

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.expressions.typed.java.lang.ASubObjectExpression;
import com.helger.jcodemodel.expressions.typed.primitives.ASubIntExpression;
import com.helger.jcodemodel.expressions.typed.primitives.BoolExpression;
import com.helger.jcodemodel.expressions.typed.primitives.VoidStatExpression;
import javax.annotation.processing.Generated;

@Generated("com.helger.jcodemodel.JCodeModel")
public abstract class ASubArrayListExpr<E, Contained extends ArrayList<E>>
    extends ASubObjectExpression<Contained>
{

    public ASubArrayListExpr(IJExpression raw) {
        super(raw);
    }

    public ASubObjectExpression<?> clone() {
        return new ASubObjectExpression<>(this.raw().invoke("clone"));
    }

    public VoidStatExpression ensureCapacity(ASubIntExpression<?, ?, ?> arg0) {
        return new VoidStatExpression(this.raw().invoke("ensureCapacity").arg(arg0));
    }

    public VoidStatExpression forEach(ASubObjectExpression<? extends Consumer<? super E>> arg0) {
        return new VoidStatExpression(this.raw().invoke("forEach").arg(arg0));
    }

    public BoolExpression removeIf(ASubObjectExpression<? extends Predicate<? super E>> arg0) {
        return new BoolExpression(this.raw().invoke("removeIf").arg(arg0));
    }

    public VoidStatExpression trimToSize() {
        return new VoidStatExpression(this.raw().invoke("trimToSize"));
    }
}
