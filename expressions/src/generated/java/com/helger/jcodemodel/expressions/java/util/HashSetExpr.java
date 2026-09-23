package com.helger.jcodemodel.expressions.java.util;

import java.util.HashSet;
import com.helger.jcodemodel.IJExpression;

public final class HashSetExpr<E>
    extends ASubHashSetExpr<E, HashSet<E>>
{

    public HashSetExpr(IJExpression raw) {
        super(raw);
    }
}
