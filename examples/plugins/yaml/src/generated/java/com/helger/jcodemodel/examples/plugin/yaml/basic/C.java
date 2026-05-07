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

import java.util.List;
import java.util.Set;

public class C {
    private B redir;
    public Set<List<List<Object>>> list;

    /**
     * set the {@link #redir}
     */
    public void setRedir(B redir) {
        this.redir = redir;
    }

    /**
     * @return the {@link #redir}
     */
    public B getRedir() {
        return redir;
    }

    /**
     * set the {@link #list}
     */
    public void setList(Set<List<List<Object>>> list) {
        this.list = list;
    }

    /**
     * @return the {@link #list}
     */
    public Set<List<List<Object>>> getList() {
        return list;
    }

    public void setNbChildren(int nbChildren) {
        redir.setNbChildren(nbChildren);
    }

    public int getNbChildren() {
        return redir.getNbChildren();
    }

    public void setDistances(double[][] distances) {
        redir.setDistances(distances);
    }

    public double[][] getDistances() {
        return redir.getDistances();
    }
}
