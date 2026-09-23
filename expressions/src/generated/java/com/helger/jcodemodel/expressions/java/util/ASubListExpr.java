package com.helger.jcodemodel.expressions.java.util;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.UnaryOperator;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.expressions.typed.java.lang.ASubObjectExpression;
import com.helger.jcodemodel.expressions.typed.primitives.ASubIntExpression;
import com.helger.jcodemodel.expressions.typed.primitives.ArrayExpression;
import com.helger.jcodemodel.expressions.typed.primitives.BoolExpression;
import com.helger.jcodemodel.expressions.typed.primitives.IntExpression;
import com.helger.jcodemodel.expressions.typed.primitives.VoidStatExpression;

public abstract class ASubListExpr<E extends java.lang.Object, Contained extends List<E>>
    extends ASubObjectExpression<Contained>
{

    public ASubListExpr(IJExpression raw) {
        super(raw);
    }

    public BoolExpression add(ASubObjectExpression<java.lang.Object> arg0) {
        return new BoolExpression(this.raw().invoke("add").arg(arg0));
    }

    public VoidStatExpression add(ASubIntExpression arg0, ASubObjectExpression<java.lang.Object> arg1) {
        return new VoidStatExpression(this.raw().invoke("add").arg(arg0).arg(arg1));
    }

    public BoolExpression addAll(ASubCollectionExpr arg0) {
        return new BoolExpression(this.raw().invoke("addAll").arg(arg0));
    }

    public BoolExpression addAll(ASubIntExpression arg0, ASubCollectionExpr arg1) {
        return new BoolExpression(this.raw().invoke("addAll").arg(arg0).arg(arg1));
    }

    public VoidStatExpression addFirst(ASubObjectExpression<java.lang.Object> arg0) {
        return new VoidStatExpression(this.raw().invoke("addFirst").arg(arg0));
    }

    public VoidStatExpression addLast(ASubObjectExpression<java.lang.Object> arg0) {
        return new VoidStatExpression(this.raw().invoke("addLast").arg(arg0));
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

    public ASubObjectExpression<E> get(ASubIntExpression arg0) {
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

    public IntExpression indexOf(ASubObjectExpression<java.lang.Object> arg0) {
        return new IntExpression(this.raw().invoke("indexOf").arg(arg0));
    }

    public BoolExpression isEmpty() {
        return new BoolExpression(this.raw().invoke("isEmpty"));
    }

    public ASubObjectExpression<Iterator<E>> iterator() {
        return new ASubObjectExpression<>(this.raw().invoke("iterator"));
    }

    public IntExpression lastIndexOf(ASubObjectExpression<java.lang.Object> arg0) {
        return new IntExpression(this.raw().invoke("lastIndexOf").arg(arg0));
    }

    public ASubObjectExpression<ListIterator<E>> listIterator() {
        return new ASubObjectExpression<>(this.raw().invoke("listIterator"));
    }

    public ASubObjectExpression<ListIterator<E>> listIterator(ASubIntExpression arg0) {
        return new ASubObjectExpression<>(this.raw().invoke("listIterator").arg(arg0));
    }

    public ASubObjectExpression<E> remove(ASubIntExpression arg0) {
        return new ASubObjectExpression<>(this.raw().invoke("remove").arg(arg0));
    }

    public BoolExpression remove(ASubObjectExpression<java.lang.Object> arg0) {
        return new BoolExpression(this.raw().invoke("remove").arg(arg0));
    }

    public BoolExpression removeAll(ASubCollectionExpr arg0) {
        return new BoolExpression(this.raw().invoke("removeAll").arg(arg0));
    }

    public ASubObjectExpression<E> removeFirst() {
        return new ASubObjectExpression<>(this.raw().invoke("removeFirst"));
    }

    public ASubObjectExpression<E> removeLast() {
        return new ASubObjectExpression<>(this.raw().invoke("removeLast"));
    }

    public VoidStatExpression replaceAll(ASubObjectExpression<UnaryOperator> arg0) {
        return new VoidStatExpression(this.raw().invoke("replaceAll").arg(arg0));
    }

    public BoolExpression retainAll(ASubCollectionExpr arg0) {
        return new BoolExpression(this.raw().invoke("retainAll").arg(arg0));
    }

    public ListExpr<E> reversed() {
        return new ListExpr<>(this.raw().invoke("reversed"));
    }

    public ASubObjectExpression<E> set(ASubIntExpression arg0, ASubObjectExpression<java.lang.Object> arg1) {
        return new ASubObjectExpression<>(this.raw().invoke("set").arg(arg0).arg(arg1));
    }

    public IntExpression size() {
        return new IntExpression(this.raw().invoke("size"));
    }

    public VoidStatExpression sort(ASubObjectExpression<Comparator> arg0) {
        return new VoidStatExpression(this.raw().invoke("sort").arg(arg0));
    }

    public ASubObjectExpression<Spliterator<E>> spliterator() {
        return new ASubObjectExpression<>(this.raw().invoke("spliterator"));
    }

    public ListExpr<E> subList(ASubIntExpression arg0, ASubIntExpression arg1) {
        return new ListExpr<>(this.raw().invoke("subList").arg(arg0).arg(arg1));
    }

    public ArrayExpression<java.lang.Object> toArray() {
        return new ArrayExpression<>(this.raw().invoke("toArray"));
    }

    public<T> ArrayExpression<T> toArray(ArrayExpression<java.lang.Object> arg0) {
        return new ArrayExpression<>(this.raw().invoke("toArray").arg(arg0));
    }
}
