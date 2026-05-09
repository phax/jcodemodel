package com.helger.jcodemodel.switchexpression;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.IJFormatter;
import com.helger.jcodemodel.JEnumConstant;
import com.helger.jcodemodel.JSwitchExpression;

///
/// Switch case using static value(s) for selection
/// ```
/// case 1,3 ->
/// case MyEnum.OPT1 ->
/// ```
@SuppressWarnings("serial")
public class JCaseStatic extends JCaseArrow<JCaseStatic> {

  private final List<IJExpression> labels;

  public JCaseStatic(@NonNull JSwitchExpression parent, IJExpression aLabel) {
    super(parent);
    labels = new ArrayList<>(List.of(aLabel));
  }

  /// add a label to the list of existing ones
  public JCaseStatic or(IJExpression aLabel) {
    labels.add(aLabel);
    return this;
  }

  /// alias for [#or]
  public JCaseStatic _case(IJExpression aLabel) {
    return or(aLabel);
  }

  /// copy of [JCase]
  @Override
  public void state(@NonNull final IJFormatter f) {
    f.indent();
    f.print("case ");
    boolean first = true;
    for (IJExpression ije : labels) {
      IJExpression aLabelName;
      // Hack for #41 :)
      if (ije instanceof JEnumConstant) {
        // Just use the name, but not the type of the enum
        aLabelName = f1 -> f1.print(((JEnumConstant) ije).name());
      } else {
        aLabelName = ije;
      }
      if (!first) {
        f.print(", ");
      }
      f.generable(aLabelName);
      first = false;
    }
    stateBody(f);
    f.outdent();
  }

}
