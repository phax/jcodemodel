package com.helger.jcodemodel.examples.plugin.csv.example4;

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
