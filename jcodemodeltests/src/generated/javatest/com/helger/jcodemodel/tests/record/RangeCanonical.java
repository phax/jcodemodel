package com.helger.jcodemodel.tests.record;

public record RangeCanonical(int lo, int hi) {

    public RangeCanonical(int lo, int hi) {
        if (lo >hi) {
            throw new IllegalArgumentException("lo must be < hi");
        }
        this.lo = lo;
        this.hi = hi;
    }
}
