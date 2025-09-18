package com.helger.jcodemodel.plugin.maven.generators.flatstruct;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.IJStatement;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JVar;

/**
 * interface to make a field containered (canned).
 */
public interface FieldCanner {

  AbstractJType makeType(JCodeModel model, AbstractJType type);

  /**
   * create a field in the class, as the actual container. Can be overriden eg to
   * add init call
   *
   * @param model
   * @param jdc
   * @param fieldName
   * @param type
   * @param jmods
   * @return
   */
  default JFieldVar makeType(JDefinedClass jdc, String fieldName, AbstractJType type, int jmods) {
    return jdc.field(
        jmods,
        makeType(jdc.owner(), type),
        fieldName);
  }

  /**
   * create the assignemnt to the field for the setter. <br />
   * Typically, fv=param with no canner, or fv.[canSetter](param), or even fv=new
   * [canimplementation](param) .<br />
   * returning null prevents the default setter method
   */
  IJStatement makeSetter(JVar param, JFieldVar fv);

  /**
   * create the way to return the actual value in the container. <br />
   * typically, (field) with no canner, or field.canGetter() .<br />
   * returning null prevents the default getter method
   */
  IJExpression makeGetter(JFieldVar fv);

  /**
   * for dev to instantiate
   */
  default void addAdditional(JFieldVar field, FieldOptions options) {
    if (options.isGetter()) {
      addAdditionalGetter(field, options);
    }
    if (options.isSetter() && !options.isFinal()) {
      addAdditionalSetter(field, options);
    }
  }

  default void addAdditionalGetter(JFieldVar field, FieldOptions options) {

  }

  default void addAdditionalSetter(JFieldVar field, FieldOptions options) {

  }

}
