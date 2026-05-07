/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * 		you may not use this file except in compliance with the License.
 * 		You may obtain a copy of the License at
 * 
 * 						http://www.apache.org/licenses/LICENSE-2.0
 * 
 * 		Unless required by applicable law or agreed to in writing, software
 * 		distributed under the License is distributed on an "AS IS" BASIS,
 * 		WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 		See the License for the specific language governing permissions and
 * 		limitations under the License.
 */
package com.helger.jcodemodel.examples.plugin.csv.immutable;

import java.time.Instant;
import java.util.List;

public class Dog
    extends Animal
{
    private final String species;
    private String master;

    public Dog(Instant dob,
        long id,
        Animal[] parents,
        List<Animal> children,
        String species) {
        super(dob, id, parents, children);
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
