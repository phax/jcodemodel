package com.helger.jcodemodel.vars;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IVariableInitializer;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JMods;

public class JFieldVar extends com.helger.jcodemodel.JFieldVar
{

  public JFieldVar (@NonNull JDefinedClass aOwnerClass, @NonNull JMods aMods, @NonNull AbstractJType aType, @NonNull String sName,
      @Nullable IVariableInitializer aInit)
  {
    super (aOwnerClass, aMods, aType, sName, aInit);
  }

}
