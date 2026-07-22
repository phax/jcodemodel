package com.helger.jcodemodel.vars;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJFormatter;
import com.helger.jcodemodel.IVariableInitializer;
import com.helger.jcodemodel.JVar;

/// vars that are the same as another one.
///
/// example
/// ```java
/// int i=0, j, k=1;
/// ```
/// Here i is a block variable, j and k are "same" var.
public class JSameVar extends JVar {

  private final JVar parent;

  /// number of additional array dimension on top of the parent.
  private final int dim;

  public JSameVar(JVar parent, String sName, IVariableInitializer aInitExpr, int dim) {
    super(parent.mods(), typeArray(parent.type(), dim), sName, aInitExpr);
    this.parent = parent;
    this.dim = dim;
  }

  public JSameVar(JVar parent, String sName, IVariableInitializer aInitExpr) {
    this(parent, sName, aInitExpr, 0);
  }

  public JVar parentVar() {
    return parent;
  }

  @Override
  public void bind(@NonNull IJFormatter f) {
    f.id(name());
    for (int i = 0; i < dim; i++) {
      f.print("[]");
    }
    if (init() != null) {
      f.print('=').generable(init());
    }
  }

  @Override
  public String separator() {
    return ",";
  }

  static AbstractJType typeArray(AbstractJType type, int dim) {
    while (dim > 0) {
      type = type.array();
      dim--;
    }
    return type;
  }

}
