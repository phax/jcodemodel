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

public class Animal {
    private final Instant dob;
    private final long id;
    private String name;
    private final Animal[] parents;
    private final List<Animal> children;

    public Animal(Instant dob,
        long id,
        Animal[] parents,
        List<Animal> children) {
        this.dob = dob;
        this.id = id;
        this.parents = parents;
        this.children = children;
    }

    /**
     * @return the {@link #dob}
     */
    public Instant getDob() {
        return dob;
    }

    /**
     * @return the {@link #id}
     */
    public long getId() {
        return id;
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
     * @return the {@link #children}
     */
    public List<Animal> getChildren() {
        return children;
    }
}
