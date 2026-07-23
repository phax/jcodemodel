
package com.helger.jcodemodel.vars;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JMods;
import com.helger.jcodemodel.JVar;

/// Pattern variable, declared when matching another variable to a type
///
/// ```java
/// if( o instanceof int i) {} // the var is i, type int, no init
/// switch(o){case Integer i -> {} } //var is i, type Integer
/// ```
///
///  - only allowed mod is final
///  - type is required
///  - init is null
///
/// @see https://docs.oracle.com/javase/specs/jls/se17/html/jls-14.html#jls-14.30.1
///
public class JPatternVar extends JVar
{
  public JPatternVar (
      boolean isFinal,
      @NonNull AbstractJType aType,
      @NonNull String sName)
  {
    super (JMods.forVar (isFinal ? JMod.FINAL : JMod.NONE), aType, sName, null);
  }

  @Override
  public @NonNull JPatternVar init (@Nullable IJExpression aInitExpr)
  {
    if (aInitExpr != null)
      throw new UnsupportedOperationException (getClass ().getSimpleName () + " can't receive a non-null init");
    return this;
  }

}
