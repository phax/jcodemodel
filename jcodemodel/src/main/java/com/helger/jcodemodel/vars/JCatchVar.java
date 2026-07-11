package com.helger.jcodemodel.vars;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJFormatter;

///
///  A variable that is declared in a catch block
///
///  - only allowed mod is final
///  - type is required
///  - No init expression///
///
public class JCatchVar extends JArgVar {

  /// additional types for the variable
  private List<AbstractJType> m_lTypes = new ArrayList<>();

  public JCatchVar(boolean final_, @NonNull AbstractJType aType, @NonNull String sName) {
    super(final_, aType, sName);
  }

  public JCatchVar addType(AbstractJType type) {
    if (type != null) {
      m_lTypes.add(type);
    }
    return this;
  }

  @Override
  protected void bindType(@NonNull IJFormatter f) {
    f.generable(type());
    for (AbstractJType t : m_lTypes) {
      f.print(" | ").generable(t);
    }
  }


}
