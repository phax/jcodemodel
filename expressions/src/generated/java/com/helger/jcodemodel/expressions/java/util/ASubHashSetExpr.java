package com.helger.jcodemodel.expressions.java.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Spliterator;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.expressions.typed.java.lang.ASubObjectExpression;
import com.helger.jcodemodel.expressions.typed.primitives.ArrayExpression;
import com.helger.jcodemodel.expressions.typed.primitives.BoolExpression;
import com.helger.jcodemodel.expressions.typed.primitives.IntExpression;
import com.helger.jcodemodel.expressions.typed.primitives.VoidStatExpression;

public abstract class ASubHashSetExpr<E, Contained extends HashSet<E>>
    extends ASubObjectExpression<Contained>
{

    public ASubHashSetExpr(IJExpression raw) {
        super(raw);
    }

    public BoolExpression add(ASubObjectExpression<E> arg0) {
        return new BoolExpression(this.raw().invoke("add").arg(arg0));
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

    public BoolExpression isEmpty() {
        return new BoolExpression(this.raw().invoke("isEmpty"));
    }

    public ASubObjectExpression<Iterator<E>> iterator() {
        return new ASubObjectExpression<>(this.raw().invoke("iterator"));
    }

    public BoolExpression remove(ASubObjectExpression<Object> arg0) {
        return new BoolExpression(this.raw().invoke("remove").arg(arg0));
    }

    public IntExpression size() {
        return new IntExpression(this.raw().invoke("size"));
    }

    public ASubObjectExpression<Spliterator<E>> spliterator() {
        return new ASubObjectExpression<>(this.raw().invoke("spliterator"));
    }

    public ArrayExpression<Object> toArray() {
        return new ArrayExpression<>(this.raw().invoke("toArray"));
    }

    public<T> ArrayExpression<T> toArray(ArrayExpression<T> arg0) {
        return new ArrayExpression<>(this.raw().invoke("toArray").arg(arg0));
    }
}
