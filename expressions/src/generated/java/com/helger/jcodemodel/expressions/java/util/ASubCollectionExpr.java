package com.helger.jcodemodel.expressions.java.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.expressions.typed.java.lang.ASubObjectExpression;
import com.helger.jcodemodel.expressions.typed.primitives.ArrayExpression;
import com.helger.jcodemodel.expressions.typed.primitives.BoolExpression;
import com.helger.jcodemodel.expressions.typed.primitives.IntExpression;
import com.helger.jcodemodel.expressions.typed.primitives.VoidStatExpression;

public abstract class ASubCollectionExpr<E, Contained extends Collection<E>>
    extends ASubObjectExpression<Contained>
{

    public ASubCollectionExpr(IJExpression raw) {
        super(raw);
    }

    public BoolExpression add(ASubObjectExpression<E> arg0) {
        return new BoolExpression(this.raw().invoke("add").arg(arg0));
    }

    public BoolExpression addAll(ASubCollectionExpr<? extends E, Collection<? extends E>> arg0) {
        return new BoolExpression(this.raw().invoke("addAll").arg(arg0));
    }

    public VoidStatExpression clear() {
        return new VoidStatExpression(this.raw().invoke("clear"));
    }

    public BoolExpression contains(ASubObjectExpression<Object> arg0) {
        return new BoolExpression(this.raw().invoke("contains").arg(arg0));
    }

    public BoolExpression containsAll(ASubCollectionExpr<?, Collection<?>> arg0) {
        return new BoolExpression(this.raw().invoke("containsAll").arg(arg0));
    }

    public BoolExpression equals_(ASubObjectExpression<Object> arg0) {
        return new BoolExpression(this.raw().invoke("equals").arg(arg0));
    }

    public IntExpression hashCode_() {
        return new IntExpression(this.raw().invoke("hashCode"));
    }

    public BoolExpression isEmpty() {
        return new BoolExpression(this.raw().invoke("isEmpty"));
    }

    public ASubObjectExpression<Iterator<E>> iterator() {
        return new ASubObjectExpression<>(this.raw().invoke("iterator"));
    }

    public ASubObjectExpression<Stream<E>> parallelStream() {
        return new ASubObjectExpression<>(this.raw().invoke("parallelStream"));
    }

    public BoolExpression remove(ASubObjectExpression<Object> arg0) {
        return new BoolExpression(this.raw().invoke("remove").arg(arg0));
    }

    public BoolExpression removeAll(ASubCollectionExpr<?, Collection<?>> arg0) {
        return new BoolExpression(this.raw().invoke("removeAll").arg(arg0));
    }

    public BoolExpression removeIf(ASubObjectExpression<? extends Predicate<? super E>> arg0) {
        return new BoolExpression(this.raw().invoke("removeIf").arg(arg0));
    }

    public BoolExpression retainAll(ASubCollectionExpr<?, Collection<?>> arg0) {
        return new BoolExpression(this.raw().invoke("retainAll").arg(arg0));
    }

    public IntExpression size() {
        return new IntExpression(this.raw().invoke("size"));
    }

    public ASubObjectExpression<Spliterator<E>> spliterator() {
        return new ASubObjectExpression<>(this.raw().invoke("spliterator"));
    }

    public ASubObjectExpression<Stream<E>> stream() {
        return new ASubObjectExpression<>(this.raw().invoke("stream"));
    }

    public ArrayExpression<Object> toArray() {
        return new ArrayExpression<>(this.raw().invoke("toArray"));
    }

    public<T> ArrayExpression<T> toArray(ArrayExpression<T> arg0) {
        return new ArrayExpression<>(this.raw().invoke("toArray").arg(arg0));
    }

    public<T> ArrayExpression<T> toArray_1(ASubObjectExpression<? extends IntFunction<T[]>> arg0) {
        return new ArrayExpression<>(this.raw().invoke("toArray").arg(arg0));
    }
}
