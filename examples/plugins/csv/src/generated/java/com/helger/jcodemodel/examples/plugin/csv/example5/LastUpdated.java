package com.helger.jcodemodel.examples.plugin.csv.example5;

import java.time.Instant;

public class LastUpdated {
    private int i;
    /**
     * last time the class was directly set a field using a setter
     */
    private Instant lastUpdated = null;
    private String s;

    /**
     * set the {@link #i}
     */
    public void setI(int i) {
        this.i = i;
        this.lastUpdated = Instant.now();
    }

    /**
     * @return the {@link #lastUpdated}
     */
    public Instant getLastUpdated() {
        return lastUpdated;
    }

    /**
     * @return the {@link #i}
     */
    public int getI() {
        return i;
    }

    /**
     * set the {@link #s}
     */
    public void setS(String s) {
        this.s = s;
        this.lastUpdated = Instant.now();
    }

    /**
     * @return the {@link #s}
     */
    public String getS() {
        return s;
    }
}
