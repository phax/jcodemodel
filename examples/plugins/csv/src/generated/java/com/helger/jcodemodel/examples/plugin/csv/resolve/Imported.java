/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 * 
 *             http://www.apache.org/licenses/LICENSE-2.0
 * 
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.helger.jcodemodel.examples.plugin.csv.resolve;

import com.helger.jcodemodel.JCodeModel;

public class Imported {
    private JCodeModel model;
    private JCodeModel[] modelArr;

    /**
     * set the {@link #model}
     */
    public void setModel(JCodeModel model) {
        this.model = model;
    }

    /**
     * @return the {@link #model}
     */
    public JCodeModel getModel() {
        return model;
    }

    /**
     * set the {@link #modelArr}
     */
    public void setModelArr(JCodeModel[] modelArr) {
        this.modelArr = modelArr;
    }

    /**
     * @return the {@link #modelArr}
     */
    public JCodeModel[] getModelArr() {
        return modelArr;
    }
}
