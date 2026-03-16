package com.helger.jcodemodel.tests.record;

public record PointStatic(int x, int y) {
    public static final PointStatic ORIGIN = new PointStatic(0, 0);

    public static PointStatic of(int x, int y) {
        return new PointStatic(x, y);
    }
}
