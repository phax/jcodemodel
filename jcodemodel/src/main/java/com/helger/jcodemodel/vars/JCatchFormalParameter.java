package com.helger.jcodemodel.vars;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJFormatter;

///
///  A variable that is declared in a catch block
///
///  - only allowed mod is final
///  - type is required, even though the actual type should be exception
///  - No init expression
///
/// its type must be an abstractJClass since generics are not allowed
///
/// Its internal type is the initial one, then set to null when new types are added, since it needs be deduced at compile.
///
/// @see https://docs.oracle.com/javase/specs/jls/se25/html/jls-14.html#jls-14.20-510
///
public class JCatchFormalParameter extends JArgVar {

  /// list of types for the variable
  private List<AbstractJType> m_lTypes = new ArrayList<>();

  public JCatchFormalParameter(boolean final_, @NonNull AbstractJClass aType, @NonNull String sName) {
    super(final_, aType, sName);
    m_lTypes.add(aType);
  }

  public JCatchFormalParameter addType(AbstractJClass type) {
    if (type != null) {
      type(type);
      m_lTypes.add(type);
    }
    return this;
  }

  @Override
  public AbstractJClass type() {
    return (AbstractJClass) super.type();
  }

  @Override
  protected void bindType(@NonNull IJFormatter f) {
    f.generable(m_lTypes, " | ", f.settings().wrap.catchClause.types);
  }

  @Override
  public String separator() {
    throw new UnsupportedOperationException("can't declare two vars in a catch block");
  }

}
