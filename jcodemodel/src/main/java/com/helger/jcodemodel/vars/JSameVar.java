package com.helger.jcodemodel.vars;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.IJFormatter;
import com.helger.jcodemodel.JVar;

/// vars that are the same as another one.
///
/// example
/// ```java
/// int i=0, j, k=1;
/// ```
/// Here i is a block variable, j and k are "same" var.
public class JSameVar extends JVar {

  public JSameVar(JVar parent, String sName, IJExpression aInitExpr) {
    super(parent.mods(), parent.type(), sName, aInitExpr);
    this.parent = parent;
  }

  private final JVar parent;

  public JVar parentVar() {
    return parent;
  }

  @Override
  public void bind(@NonNull IJFormatter f) {
    f.id(name());
    if (init() != null) {
      f.print('=').generable(init());
    }
  }

  @Override
  public String separator() {
    return ",";
  }

}
