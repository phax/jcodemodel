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
package com.helger.jcodemodel.examples.plugin.csv.inherit;

public class City
    extends Point
{
    private String name;
    private int zip;

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
     * set the {@link #zip}
     */
    public void setZip(int zip) {
        this.zip = zip;
    }

    /**
     * @return the {@link #zip}
     */
    public int getZip() {
        return zip;
    }
}
