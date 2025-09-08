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

    public void setB(int b) {
        redir.setB(b);
    }

    public int getB() {
        return redir.getB();
    }
}
