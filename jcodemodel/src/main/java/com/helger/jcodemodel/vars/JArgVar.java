package com.helger.jcodemodel.vars;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJFormatter;
import com.helger.jcodemodel.JAnnotationUse;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JMods;
import com.helger.jcodemodel.JVar;

///
/// A variable that is declared in the signature of a method
///
///  - only allowed mod is final
///  - type is required
///  - No init expression
///
public class JArgVar extends JVar {

  public JArgVar(boolean final_, @NonNull AbstractJType aType, @NonNull String sName) {
    super(JMods.forVar(final_ ? JMod.NONE : JMod.FINAL), aType, sName, null);
  }

  @Override
  public void bind(@NonNull final IJFormatter f) {
    for (final JAnnotationUse annotation : annotations()) {
      f.generable(annotation);
      f.print(' ');
    }
    f.generable(mods());
    bindType(f);
    f.id(name());
  }

  // just here to be overridden for varargs
  protected void bindType(@NonNull final IJFormatter f) {
    f.generable(type());
  }

}
