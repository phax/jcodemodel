package com.helger.jcodemodel.vars;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJDeclaration;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.IJFormatter;
import com.helger.jcodemodel.JMods;
import com.helger.jcodemodel.JVar;
import com.helger.jcodemodel.writer.settings.Wrap.ListWrapping;

///
/// A variable that is declared as part of a block.
///
/// example :
/// ```java
/// int i=0;
/// ```
///
public class JBlockVar extends JVar implements IJDeclaration {

  private final List<JSameVar> childrenVar = new ArrayList<>();

  public JBlockVar(@NonNull JMods aMods, AbstractJType aType, @NonNull String sName, @Nullable IJExpression aInitExpr) {
    super(aMods, aType, sName, aInitExpr);
  }

  /// @return a stream of this and children variables.
  public Stream<JVar> streamVars() {
    return Stream.concat(Stream.of(this), childrenVar.stream());
  }

  @Override
  public void declare(@NonNull IJFormatter f) {
    if (childrenVar.isEmpty()) {
      super.declare(f);
    } else {
      f.vars(
          streamVars().toList(),
          extractWrappingOptions(f))
          .print(';').newline();
    }
  }

  /// extract the wrapping options for this type of var. Present here to be
  /// overridden in the fieldVar
  protected ListWrapping extractWrappingOptions(IJFormatter f) {
    return null;
  }

  /// add and return a new var with same type and mods, but given name and init.
  ///
  /// Note that the dimension is added on top of this' type. For example
  /// ```java
  /// int [] i={0}, j[][], k;
  /// ```
  /// makes i an int[], j an int[][][] (dim 2), and k an int[] (dim 1).
  ///
  /// @param dim the additional dimension of the array, based on the type of this
  public JSameVar andVar(String name, int dim, IJExpression aInitExpr) {
    JSameVar ret = new JSameVar(this, name, aInitExpr, dim);
    childrenVar.add(ret);
    return ret;
  }

  /// add and return a new var with same type and mods, but given name and init.
  ///
  /// dimension is set to 0, meaning the new variable type is the same as this.
  public JSameVar andVar(String name, IJExpression aInitExpr) {
    return andVar(name, 0, aInitExpr);
  }

  /// add and return a new var with same type and mods, but given name and
  /// dimension.
  ///
  /// init is set to null, so nonexistant assignment.
  public JSameVar andVar(String name, int dim) {
    return andVar(name, dim, null);
  }

  /// add and return a new var with same type, same mods, but no init and given
  /// name. Also with dimension 0
  public JSameVar andVar(String name) {
    return andVar(name, null);
  }

  @Override
  public String separator() {
    return ",";
  }

}
