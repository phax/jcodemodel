package com.helger.jcodemodel.plugin.maven.generators.flatstruct;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.IJStatement;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JVar;

/**
 * interface to make a field containered (canned).
 */
public interface FieldCanner {

  AbstractJType makeType(JCodeModel model, AbstractJType type);

  /**
   * typically, field=param with no canner, or field.canSetter(param) .
   *
   * @param fv
   */
  IJStatement makeSetter(JVar param, JFieldVar fv);

  /**
   * typically, (field) with no canner, or field.canGetter() .
   */
  IJExpression makeGetter(JFieldVar fv);

  /**
   * @return null if no property getter to add ; or the complete method. Can't be
   *         more specific here because it depends on a lot of things
   */
  default JMethod makePropertyGetter() {
    return null;
  }

}
