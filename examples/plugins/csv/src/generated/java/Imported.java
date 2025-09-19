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
     * set the {@link #modelArr}
     */
    public void setModelArr(JCodeModel[] modelArr) {
        this.modelArr = modelArr;
    }
}
