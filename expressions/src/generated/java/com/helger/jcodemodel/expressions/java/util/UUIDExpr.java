package com.helger.jcodemodel.expressions.java.util;

import java.util.UUID;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.expressions.typed.java.lang.ASubObjectExpression;
import com.helger.jcodemodel.expressions.typed.java.lang.StringExpression;
import com.helger.jcodemodel.expressions.typed.primitives.BoolExpression;
import com.helger.jcodemodel.expressions.typed.primitives.IntExpression;
import com.helger.jcodemodel.expressions.typed.primitives.LngExpression;

public final class UUIDExpr
    extends ASubObjectExpression<UUID>
{

    public UUIDExpr(IJExpression raw) {
        super(raw);
    }

    public IntExpression clockSequence() {
        return new IntExpression(this.raw().invoke("clockSequence"));
    }

    public IntExpression compareTo(UUIDExpr arg0) {
        return new IntExpression(this.raw().invoke("compareTo").arg(arg0));
    }

    public BoolExpression equals_(ASubObjectExpression<Object> arg0) {
        return new BoolExpression(this.raw().invoke("equals").arg(arg0));
    }

    public LngExpression getLeastSignificantBits() {
        return new LngExpression(this.raw().invoke("getLeastSignificantBits"));
    }

    public LngExpression getMostSignificantBits() {
        return new LngExpression(this.raw().invoke("getMostSignificantBits"));
    }

    public IntExpression hashCode_() {
        return new IntExpression(this.raw().invoke("hashCode"));
    }

    public LngExpression node() {
        return new LngExpression(this.raw().invoke("node"));
    }

    public LngExpression timestamp() {
        return new LngExpression(this.raw().invoke("timestamp"));
    }

    public StringExpression toString_() {
        return new StringExpression(this.raw().invoke("toString"));
    }

    public IntExpression variant() {
        return new IntExpression(this.raw().invoke("variant"));
    }

    public IntExpression version() {
        return new IntExpression(this.raw().invoke("version"));
    }
}
