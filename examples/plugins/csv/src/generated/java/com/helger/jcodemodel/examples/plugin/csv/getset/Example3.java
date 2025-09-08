package com.helger.jcodemodel.examples.plugin.csv.getset;

public class Example3 {
    private int i;
    protected String[] sarr;

    /**
     * set the {@link #i}
     */
    public void setI(int i) {
        this.i = i;
    }

    /**
     * @return the {@link #i}
     */
    public int getI() {
        return i;
    }

    /**
     * @return the {@link #sarr}
     */
    public String[] getSarr() {
        return sarr;
    }
}
