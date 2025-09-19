package com.helger.jcodemodel.examples.plugin.yaml.basic;

public class B
    extends A
{
    private int nbChildren;
    private double[][] distances;

    public B(long uuid) {
        super(uuid);
    }

    /**
     * set the {@link #nbChildren}
     */
    public void setNbChildren(int nbChildren) {
        this.nbChildren = nbChildren;
    }

    /**
     * @return the {@link #nbChildren}
     */
    public int getNbChildren() {
        return nbChildren;
    }

    /**
     * set the {@link #distances}
     */
    public void setDistances(double[][] distances) {
        this.distances = distances;
    }

    /**
     * @return the {@link #distances}
     */
    public double[][] getDistances() {
        return distances;
    }
}
