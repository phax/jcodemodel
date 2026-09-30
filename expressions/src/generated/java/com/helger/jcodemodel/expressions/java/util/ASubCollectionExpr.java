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

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.expressions.ITypedExpression;
import com.helger.jcodemodel.expressions.typed.java.lang.ObjectExpression;
import com.helger.jcodemodel.expressions.typed.primitives.ArrayExpression;
import com.helger.jcodemodel.expressions.typed.primitives.BoolExpression;
import com.helger.jcodemodel.expressions.typed.primitives.IntExpression;
import com.helger.jcodemodel.expressions.typed.primitives.VoidStatExpression;
import javax.annotation.processing.Generated;

@Generated("com.helger.jcodemodel.JCodeModel")
public abstract class ASubCollectionExpr<E, Contained extends Collection<E>>
    extends ObjectExpression<Contained>
{

    public ASubCollectionExpr(IJExpression raw) {
        super(raw);
    }

    public BoolExpression add(ITypedExpression<? extends E> arg0) {
        return new BoolExpression(this.raw().invoke("add").arg(arg0));
    }

    public BoolExpression addAll(ITypedExpression<? extends Collection<? extends E>> arg0) {
        return new BoolExpression(this.raw().invoke("addAll").arg(arg0));
    }

    public VoidStatExpression clear() {
        return new VoidStatExpression(this.raw().invoke("clear"));
    }

    public BoolExpression contains(ITypedExpression<?> arg0) {
        return new BoolExpression(this.raw().invoke("contains").arg(arg0));
    }

    public BoolExpression containsAll(ITypedExpression<? extends Collection<?>> arg0) {
        return new BoolExpression(this.raw().invoke("containsAll").arg(arg0));
    }

    public BoolExpression isEmpty() {
        return new BoolExpression(this.raw().invoke("isEmpty"));
    }

    public ObjectExpression<Iterator<E>> iterator() {
        return new ObjectExpression<>(this.raw().invoke("iterator"));
    }

    public ObjectExpression<Stream<E>> parallelStream() {
        return new ObjectExpression<>(this.raw().invoke("parallelStream"));
    }

    public BoolExpression remove(ITypedExpression<?> arg0) {
        return new BoolExpression(this.raw().invoke("remove").arg(arg0));
    }

    public BoolExpression removeAll(ITypedExpression<? extends Collection<?>> arg0) {
        return new BoolExpression(this.raw().invoke("removeAll").arg(arg0));
    }

    public BoolExpression removeIf(ITypedExpression<? extends Predicate<? super E>> arg0) {
        return new BoolExpression(this.raw().invoke("removeIf").arg(arg0));
    }

    public BoolExpression retainAll(ITypedExpression<? extends Collection<?>> arg0) {
        return new BoolExpression(this.raw().invoke("retainAll").arg(arg0));
    }

    public IntExpression size() {
        return new IntExpression(this.raw().invoke("size"));
    }

    public ObjectExpression<Spliterator<E>> spliterator() {
        return new ObjectExpression<>(this.raw().invoke("spliterator"));
    }

    public ObjectExpression<Stream<E>> stream() {
        return new ObjectExpression<>(this.raw().invoke("stream"));
    }

    public ArrayExpression<Object> toArray() {
        return new ArrayExpression<>(this.raw().invoke("toArray"));
    }

    public<T> ArrayExpression<T> toArray(ArrayExpression<? extends T> arg0) {
        return new ArrayExpression<>(this.raw().invoke("toArray").arg(arg0));
    }

    public<T> ArrayExpression<T> toArray_1(ITypedExpression<? extends IntFunction<? extends T[]>> arg0) {
        return new ArrayExpression<>(this.raw().invoke("toArray").arg(arg0));
    }
}
