package com.helger.jcodemodel.switchexpression;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.IJFormatter;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JSwitchExpression;

///
/// Switch case using static value(s) for selection
/// ```
/// case 1,3 ->
/// case MyEnum.OPT1 ->
/// ```
@SuppressWarnings("serial")
public class JCaseStatic extends JCaseArrow<JCaseStatic>
{

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

  @Override
  public void state(@NonNull IJFormatter f) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException();
  }

  @Override
  public List<JBlock> blocks() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException();
  }

}
