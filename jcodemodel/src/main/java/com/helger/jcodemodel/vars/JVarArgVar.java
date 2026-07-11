package com.helger.jcodemodel.vars;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJFormatter;

///
/// A variable that is the last argument of a method signature, with variable length.
///
/// same constraints as for the [JArgVar]
///
public class JVarArgVar extends JArgVar {

  ///
  /// @param aType the array type that this variable has.
  public JVarArgVar(boolean final_, AbstractJType aType, @NonNull String sName) {
    super(final_, aType, sName);
  }

  @Override
  protected void bindType(@NonNull IJFormatter f) {
    f.generable(type().elementType());
    f.print("... ");
  }


}
