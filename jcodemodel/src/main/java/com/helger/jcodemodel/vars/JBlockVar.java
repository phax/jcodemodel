package com.helger.jcodemodel.vars;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JMods;
import com.helger.jcodemodel.JVar;

///
/// A variable that is declared as part of a block.
///
///
public class JBlockVar extends JVar {

  public JBlockVar(@NonNull JMods aMods, AbstractJType aType, @NonNull String sName, @Nullable IJExpression aInitExpr) {
    super(aMods, aType, sName, aInitExpr);
  }

}
