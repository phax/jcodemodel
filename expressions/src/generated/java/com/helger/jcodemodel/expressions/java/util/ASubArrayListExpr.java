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
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.expressions.typed.java.lang.ASubObjectExpression;
import com.helger.jcodemodel.expressions.typed.primitives.ASubIntExpression;
import com.helger.jcodemodel.expressions.typed.primitives.ArrayExpression;
import com.helger.jcodemodel.expressions.typed.primitives.BoolExpression;
import com.helger.jcodemodel.expressions.typed.primitives.IntExpression;
import com.helger.jcodemodel.expressions.typed.primitives.VoidStatExpression;

public abstract class ASubArrayListExpr<E, Contained extends ArrayList<E>>
    extends ASubObjectExpression<Contained>
{

    public ASubArrayListExpr(IJExpression raw) {
        super(raw);
    }

    public BoolExpression add(ASubObjectExpression<E> arg0) {
        return new BoolExpression(this.raw().invoke("add").arg(arg0));
    }

    public VoidStatExpression add(ASubIntExpression<?, ?, ?> arg0, ASubObjectExpression<E> arg1) {
        return new VoidStatExpression(this.raw().invoke("add").arg(arg0).arg(arg1));
    }

    public BoolExpression addAll(ASubCollectionExpr<? extends E, Collection<? extends E>> arg0) {
        return new BoolExpression(this.raw().invoke("addAll").arg(arg0));
    }

    public BoolExpression addAll(ASubIntExpression<?, ?, ?> arg0, ASubCollectionExpr<? extends E, Collection<? extends E>> arg1) {
        return new BoolExpression(this.raw().invoke("addAll").arg(arg0).arg(arg1));
    }

    public VoidStatExpression addFirst(ASubObjectExpression<E> arg0) {
        return new VoidStatExpression(this.raw().invoke("addFirst").arg(arg0));
    }

    public VoidStatExpression addLast(ASubObjectExpression<E> arg0) {
        return new VoidStatExpression(this.raw().invoke("addLast").arg(arg0));
    }

    public VoidStatExpression clear() {
        return new VoidStatExpression(this.raw().invoke("clear"));
    }

    public ASubObjectExpression<?> clone() {
        return new ASubObjectExpression<>(this.raw().invoke("clone"));
    }

    public BoolExpression contains(ASubObjectExpression<Object> arg0) {
        return new BoolExpression(this.raw().invoke("contains").arg(arg0));
    }

    public VoidStatExpression ensureCapacity(ASubIntExpression<?, ?, ?> arg0) {
        return new VoidStatExpression(this.raw().invoke("ensureCapacity").arg(arg0));
    }

    public BoolExpression equals_(ASubObjectExpression<Object> arg0) {
        return new BoolExpression(this.raw().invoke("equals").arg(arg0));
    }

    public VoidStatExpression forEach(ASubObjectExpression<? extends Consumer<? super E>> arg0) {
        return new VoidStatExpression(this.raw().invoke("forEach").arg(arg0));
    }

    public ASubObjectExpression<E> get(ASubIntExpression<?, ?, ?> arg0) {
        return new ASubObjectExpression<>(this.raw().invoke("get").arg(arg0));
    }

    public ASubObjectExpression<E> getFirst() {
        return new ASubObjectExpression<>(this.raw().invoke("getFirst"));
    }

    public ASubObjectExpression<E> getLast() {
        return new ASubObjectExpression<>(this.raw().invoke("getLast"));
    }

    public IntExpression hashCode_() {
        return new IntExpression(this.raw().invoke("hashCode"));
    }

    public IntExpression indexOf(ASubObjectExpression<Object> arg0) {
        return new IntExpression(this.raw().invoke("indexOf").arg(arg0));
    }

    public BoolExpression isEmpty() {
        return new BoolExpression(this.raw().invoke("isEmpty"));
    }

    public ASubObjectExpression<Iterator<E>> iterator() {
        return new ASubObjectExpression<>(this.raw().invoke("iterator"));
    }

    public IntExpression lastIndexOf(ASubObjectExpression<Object> arg0) {
        return new IntExpression(this.raw().invoke("lastIndexOf").arg(arg0));
    }

    public ASubObjectExpression<ListIterator<E>> listIterator() {
        return new ASubObjectExpression<>(this.raw().invoke("listIterator"));
    }

    public ASubObjectExpression<ListIterator<E>> listIterator(ASubIntExpression<?, ?, ?> arg0) {
        return new ASubObjectExpression<>(this.raw().invoke("listIterator").arg(arg0));
    }

    public ASubObjectExpression<E> remove(ASubIntExpression<?, ?, ?> arg0) {
        return new ASubObjectExpression<>(this.raw().invoke("remove").arg(arg0));
    }

    public BoolExpression remove_1(ASubObjectExpression<Object> arg0) {
        return new BoolExpression(this.raw().invoke("remove").arg(arg0));
    }

    public BoolExpression removeAll(ASubCollectionExpr<?, Collection<?>> arg0) {
        return new BoolExpression(this.raw().invoke("removeAll").arg(arg0));
    }

    public ASubObjectExpression<E> removeFirst() {
        return new ASubObjectExpression<>(this.raw().invoke("removeFirst"));
    }

    public BoolExpression removeIf(ASubObjectExpression<? extends Predicate<? super E>> arg0) {
        return new BoolExpression(this.raw().invoke("removeIf").arg(arg0));
    }

    public ASubObjectExpression<E> removeLast() {
        return new ASubObjectExpression<>(this.raw().invoke("removeLast"));
    }

    public VoidStatExpression replaceAll(ASubObjectExpression<? extends UnaryOperator<E>> arg0) {
        return new VoidStatExpression(this.raw().invoke("replaceAll").arg(arg0));
    }

    public BoolExpression retainAll(ASubCollectionExpr<?, Collection<?>> arg0) {
        return new BoolExpression(this.raw().invoke("retainAll").arg(arg0));
    }

    public ASubObjectExpression<E> set(ASubIntExpression<?, ?, ?> arg0, ASubObjectExpression<E> arg1) {
        return new ASubObjectExpression<>(this.raw().invoke("set").arg(arg0).arg(arg1));
    }

    public IntExpression size() {
        return new IntExpression(this.raw().invoke("size"));
    }

    public VoidStatExpression sort(ASubObjectExpression<? extends Comparator<? super E>> arg0) {
        return new VoidStatExpression(this.raw().invoke("sort").arg(arg0));
    }

    public ASubObjectExpression<Spliterator<E>> spliterator() {
        return new ASubObjectExpression<>(this.raw().invoke("spliterator"));
    }

    public ListExpr<E> subList(ASubIntExpression<?, ?, ?> arg0, ASubIntExpression<?, ?, ?> arg1) {
        return new ListExpr<>(this.raw().invoke("subList").arg(arg0).arg(arg1));
    }

    public ArrayExpression<Object> toArray() {
        return new ArrayExpression<>(this.raw().invoke("toArray"));
    }

    public<T> ArrayExpression<T> toArray(ArrayExpression<T> arg0) {
        return new ArrayExpression<>(this.raw().invoke("toArray").arg(arg0));
    }

    public VoidStatExpression trimToSize() {
        return new VoidStatExpression(this.raw().invoke("trimToSize"));
    }
}
