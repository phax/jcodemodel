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
package com.helger.jcodemodel.examples.plugin.csv.redirect;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJFormatter;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JCatchBlock;
import com.helger.jcodemodel.JVar;

public class Redirected {
    private ABC abc;
    private JCatchBlock jCatchBlock;

    /**
     * set the {@link #abc}
     */
    public void setAbc(ABC abc) {
        this.abc = abc;
    }

    /**
     * set the {@link #jCatchBlock}
     */
    public void setJCatchBlock(JCatchBlock jCatchBlock) {
        this.jCatchBlock = jCatchBlock;
    }

    public void setA(int a) {
        abc.setA(a);
    }

    public int getA() {
        return abc.getA();
    }

    public void setB(boolean b) {
        abc.setB(b);
    }

    public boolean getB() {
        return abc.getB();
    }

    public void setC(char c) {
        abc.setC(c);
    }

    public char getC() {
        return abc.getC();
    }

    public JBlock body() {
        return jCatchBlock.body();
    }

    public AbstractJClass exception() {
        return jCatchBlock.exception();
    }

    public void generate(IJFormatter arg0) {
        jCatchBlock.generate(arg0);
    }

    public JVar param() {
        return jCatchBlock.param();
    }

    public JVar param(String arg0) {
        return jCatchBlock.param(arg0);
    }
}
