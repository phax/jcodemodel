package com.helger.jcodemodel.examples.plugin.yaml.basic;

public class C {
    private B redir;

    /**
     * set the {@link #redir}
     */
    public void setRedir(B redir) {
        this.redir = redir;
    }

    /**
     * @return the {@link #redir}
     */
    public B getRedir() {
        return redir;
    }

    public void setNbChildren(int nbChildren) {
        redir.setNbChildren(nbChildren);
    }

    public int getNbChildren() {
        return redir.getNbChildren();
    }

    public void setDistances(double[][] distances) {
        redir.setDistances(distances);
    }

    public double[][] getDistances() {
        return redir.getDistances();
    }
}
