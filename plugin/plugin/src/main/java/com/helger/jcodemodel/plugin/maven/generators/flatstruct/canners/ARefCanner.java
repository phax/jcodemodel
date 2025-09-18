package com.helger.jcodemodel.plugin.maven.generators.flatstruct.canners;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.IJStatement;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JVar;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FieldCanner;

public abstract class ARefCanner implements FieldCanner {

  public final Class<?> refClass;

  public ARefCanner(Class<?> refClass) {
    this.refClass = refClass;
  }

  @Override
  public AbstractJType makeType(JCodeModel model, AbstractJType type) {
    return model.ref(refClass).narrow(type);
  }

  @Override
  public IJExpression makeGetter(JFieldVar fv) {
    return fv.invoke("get");
  }

  @Override
  public IJStatement makeSetter(JVar param, JFieldVar fv) {
    return JExpr.refthis(fv).assign(fv.type()._new().arg(param));
  }


}
