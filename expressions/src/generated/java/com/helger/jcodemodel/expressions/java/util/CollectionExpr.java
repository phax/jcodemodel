package com.helger.jcodemodel.expressions.java.util;

import java.util.Collection;
import com.helger.jcodemodel.IJExpression;

public final class CollectionExpr<E extends Object>
    extends ASubCollectionExpr<E, Collection<E>>
{

    public CollectionExpr(IJExpression raw) {
        super(raw);
    }
}
