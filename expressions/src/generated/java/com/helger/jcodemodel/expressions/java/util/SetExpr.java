package com.helger.jcodemodel.expressions.java.util;

import java.util.Set;
import com.helger.jcodemodel.IJExpression;

public final class SetExpr<E extends Object>
    extends ASubSetExpr<E, Set<E>>
{

    public SetExpr(IJExpression raw) {
        super(raw);
    }
}
