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
