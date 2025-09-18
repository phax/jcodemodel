package com.helger.jcodemodel.examples.plugin.yaml.references;

import java.lang.ref.WeakReference;

public class WeakRefTest {
    private WeakReference<String> hash;

    /**
     * set the {@link #hash}
     */
    public void setHash(String hash) {
        this.hash = new WeakReference<String>(hash);
    }

    /**
     * @return the {@link #hash}
     */
    public String getHash() {
        return hash.get();
    }
}
