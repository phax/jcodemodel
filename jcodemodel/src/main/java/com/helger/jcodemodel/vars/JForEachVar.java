package com.helger.jcodemodel.vars;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.IJFormatter;
import com.helger.jcodemodel.JAnnotationUse;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JMods;
import com.helger.jcodemodel.JVar;

///
/// a variable that is declared as part of a `for( type name : iterable)`
///
/// when declared, it uses the required expression with ':' instead of '=', and does not add a semicolon.
///
///  - only allowed mod is final.
///  - type can be null. In that case, "var" is used.
///  - name needs be provided. Can be relaxed later for "_"
///  - The init expression **must** be null
///  - a new **collection** property is used instead to store the iteration
///
public class JForEachVar extends JVar {

  protected IJExpression collection;

  public JForEachVar(boolean final_,
      @Nullable AbstractJType aType,
      @NonNull String sName,
      @NonNull IJExpression aCollection) {
    super(JMods.forVar(final_ ? JMod.FINAL : JMod.NONE), aType, sName, null);
    collection = aCollection;
  }

  public IJExpression collection() {
    return collection;
  }

  @Override
  public @NonNull JForEachVar init(@Nullable IJExpression aInitExpr) {
    if (aInitExpr != null) {
      throw new UnsupportedOperationException(getClass().getSimpleName() + " can't receive a non-null init");
    }
    return this;
  }

  @Override
  public void bind(@NonNull final IJFormatter f) {
    for (final JAnnotationUse annotation : annotations()) {
      f.generable(annotation);
      f.print(' ');
    }
    f.generable(mods());
    if (type() != null) {
      f.generable(type());
    } else {
      f.print("var");
    }
    f
        .id(name())
        .print(':')
        .generable(collection());
  }

  @Override
  public String separator() {
    throw new UnsupportedOperationException("can't declare two vars in a foreach loop");
  }

}
