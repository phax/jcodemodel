package com.helger.jcodemodel.examples.plugin.csv.immutable;

import java.time.Instant;

public class Dog
    extends Animal
{
    private final String species;
    private String master;

    public Dog(Instant dob, Animal[] parents, String species) {
        super(dob, parents);
        this.species = species;
    }

    /**
     * @return the {@link #species}
     */
    public String getSpecies() {
        return species;
    }

    /**
     * set the {@link #master}
     */
    public void setMaster(String master) {
        this.master = master;
    }

    /**
     * @return the {@link #master}
     */
    public String getMaster() {
        return master;
    }
}
