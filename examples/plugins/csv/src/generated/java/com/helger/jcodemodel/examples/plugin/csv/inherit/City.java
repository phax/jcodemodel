package com.helger.jcodemodel.examples.plugin.csv.inherit;

public class City
    extends Point
{
    private String name;
    private int zip;

    /**
     * set the {@link #name}
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the {@link #name}
     */
    public String getName() {
        return name;
    }

    /**
     * set the {@link #zip}
     */
    public void setZip(int zip) {
        this.zip = zip;
    }

    /**
     * @return the {@link #zip}
     */
    public int getZip() {
        return zip;
    }
}
