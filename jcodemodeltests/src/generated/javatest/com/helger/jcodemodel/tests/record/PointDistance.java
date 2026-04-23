package com.helger.jcodemodel.tests.record;

public record PointDistance(int x, int y) {

    public double distance() {
        return Math.sqrt(((x*x)+(y*y)));
    }
}
