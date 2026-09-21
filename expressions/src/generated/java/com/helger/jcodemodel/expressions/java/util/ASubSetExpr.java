package com.helger.jcodemodel.expressions.java.util;

import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.expressions.typed.java.lang.ASubObjectExpression;
import com.helger.jcodemodel.expressions.typed.primitives.ArrayExpression;
import com.helger.jcodemodel.expressions.typed.primitives.BoolExpression;
import com.helger.jcodemodel.expressions.typed.primitives.IntExpression;
import com.helger.jcodemodel.expressions.typed.primitives.VoidStatExpression;

public abstract class ASubSetExpr<E extends java.lang.Object, Contained extends Set<E>>
    extends ASubObjectExpression<Contained>
{

    public ASubSetExpr(IJExpression raw) {
        super(raw);
    }

    public BoolExpression add(ASubObjectExpression<java.lang.Object> arg0) {
        return new BoolExpression(this.raw().invoke("add").arg(arg0));
    }

    public BoolExpression addAll(ASubCollectionExpr arg0) {
        return new BoolExpression(this.raw().invoke("addAll").arg(arg0));
    }

    public VoidStatExpression clear() {
        return new VoidStatExpression(this.raw().invoke("clear"));
    }

    public BoolExpression contains(ASubObjectExpression<java.lang.Object> arg0) {
        return new BoolExpression(this.raw().invoke("contains").arg(arg0));
    }

    public BoolExpression containsAll(ASubCollectionExpr arg0) {
        return new BoolExpression(this.raw().invoke("containsAll").arg(arg0));
    }

    public BoolExpression equals_(ASubObjectExpression<java.lang.Object> arg0) {
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

    public BoolExpression remove(ASubObjectExpression<java.lang.Object> arg0) {
        return new BoolExpression(this.raw().invoke("remove").arg(arg0));
    }

    public BoolExpression removeAll(ASubCollectionExpr arg0) {
        return new BoolExpression(this.raw().invoke("removeAll").arg(arg0));
    }

    public BoolExpression retainAll(ASubCollectionExpr arg0) {
        return new BoolExpression(this.raw().invoke("retainAll").arg(arg0));
    }

    public IntExpression size() {
        return new IntExpression(this.raw().invoke("size"));
    }

    public ASubObjectExpression<Spliterator<E>> spliterator() {
        return new ASubObjectExpression<>(this.raw().invoke("spliterator"));
    }

    public ArrayExpression<java.lang.Object> toArray() {
        return new ArrayExpression<>(this.raw().invoke("toArray"));
    }

    public ArrayExpression<java.lang.Object> toArray(ArrayExpression<java.lang.Object> arg0) {
        return new ArrayExpression<>(this.raw().invoke("toArray").arg(arg0));
    }
}
