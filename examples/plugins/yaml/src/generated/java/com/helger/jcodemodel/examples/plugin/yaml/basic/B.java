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
