package com.helger.jcodemodel.examples.plugin.json.basic;

public class C {
    private B redirect;

    /**
     * set the {@link #redirect}
     */
    public void setRedirect(B redirect) {
        this.redirect = redirect;
    }

    /**
     * @return the {@link #redirect}
     */
    public B getRedirect() {
        return redirect;
    }

    public void setB(int b) {
        redirect.setB(b);
    }

    public int getB() {
        return redirect.getB();
    }
}
