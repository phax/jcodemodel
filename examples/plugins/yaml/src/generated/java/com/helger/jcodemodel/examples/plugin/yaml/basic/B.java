package com.helger.jcodemodel.examples.plugin.yaml.basic;

public class B
    extends A
{
    private int b;

    public B(int a) {
        super(a);
    }

    /**
     * set the {@link #b}
     */
    public void setB(int b) {
        this.b = b;
    }

    /**
     * @return the {@link #b}
     */
    public int getB() {
        return b;
    }
}
