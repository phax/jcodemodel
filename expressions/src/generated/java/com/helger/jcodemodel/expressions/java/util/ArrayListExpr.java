package com.helger.jcodemodel.expressions.java.util;

import java.util.ArrayList;
import com.helger.jcodemodel.IJExpression;

public final class ArrayListExpr<E>
    extends ASubArrayListExpr<E, ArrayList<E>>
{

    public ArrayListExpr(IJExpression raw) {
        super(raw);
    }
}
