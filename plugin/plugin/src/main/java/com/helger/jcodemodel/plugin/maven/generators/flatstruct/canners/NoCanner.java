package com.helger.jcodemodel.plugin.maven.generators.flatstruct.canners;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.IJStatement;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JVar;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FieldCanner;

public class NoCanner implements FieldCanner {

  @Override
  public AbstractJType makeType(JCodeModel model, AbstractJType type) {
    return type;
  }

  @Override
  public IJStatement makeSetter(JVar param, JFieldVar fv) {
    return JExpr.assign(JExpr.refthis(fv), param);
  }

  @Override
  public IJExpression makeGetter(JFieldVar fv) {
    return fv;
  }
}
