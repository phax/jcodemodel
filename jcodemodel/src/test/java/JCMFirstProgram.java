import java.io.File;
import java.io.IOException;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.writer.JCMWriter;

public class JCMFirstProgram {

  public static void main(String... args) throws JCodeModelException, IOException {
    var jcm = new JCodeModel();
    jcm._class("JCMFirstClass");
    new JCMWriter(jcm).build(new File("."));
  }

}
