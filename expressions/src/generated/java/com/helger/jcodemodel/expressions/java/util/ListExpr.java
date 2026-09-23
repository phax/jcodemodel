package com.helger.jcodemodel.expressions.java.util;

import java.util.List;
import com.helger.jcodemodel.IJExpression;

public final class ListExpr<E>
    extends ASubListExpr<E, List<E>>
{

    public ListExpr(IJExpression raw) {
        super(raw);
    }
}
