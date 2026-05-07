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
package com.helger.jcodemodel.examples.plugin.csv.lastupdated;

import java.time.Instant;

public class LastUpdated {
    private int i;
    /**
     * last time the class was directly set a field using a setter
     */
    private Instant lastUpdated = null;
    private String s;

    /**
     * set the {@link #i}
     */
    public void setI(int i) {
        this.i = i;
        this.lastUpdated = Instant.now();
    }

    /**
     * @return the {@link #lastUpdated}
     */
    public Instant getLastUpdated() {
        return lastUpdated;
    }

    /**
     * @return the {@link #i}
     */
    public int getI() {
        return i;
    }

    /**
     * set the {@link #s}
     */
    public void setS(String s) {
        this.s = s;
        this.lastUpdated = Instant.now();
    }

    /**
     * @return the {@link #s}
     */
    public String getS() {
        return s;
    }
}
