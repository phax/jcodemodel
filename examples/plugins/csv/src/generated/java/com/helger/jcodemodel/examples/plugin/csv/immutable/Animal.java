package com.helger.jcodemodel.examples.plugin.csv.immutable;

import java.time.Instant;

public class Animal {
    private final Instant dob;
    private String name;
    private final Animal[] parents;
    private Animal[] children;

    public Animal(Instant dob, Animal[] parents) {
        this.dob = dob;
        this.parents = parents;
    }

    /**
     * @return the {@link #dob}
     */
    public Instant getDob() {
        return dob;
    }

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
     * @return the {@link #parents}
     */
    public Animal[] getParents() {
        return parents;
    }

    /**
     * set the {@link #children}
     */
    public void setChildren(Animal[] children) {
        this.children = children;
    }

    /**
     * @return the {@link #children}
     */
    public Animal[] getChildren() {
        return children;
    }
}
