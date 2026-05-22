package com.helger.jcodemodel.switchexpression;

import java.util.List;

import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JSwitchExpression;

///
/// specify the special `case null` and `default` in a switch expression
///

public class JCaseSpecialSelector implements BlockSelection<JCaseSpecialSelector> {

  private boolean setDefault = false;

  private boolean setNull = false;

  private final JSwitchExpression parent;

  public JCaseSpecialSelector(JSwitchExpression parent, boolean isNull) {
    this.parent = parent;
    setDefault = !isNull;
    setNull = isNull;
  }

  public JCaseSpecialSelector andDefault() {
    setDefault = true;
    return this;
  }

  public JCaseSpecialSelector andNull() {
    setNull = true;
    return this;
  }

  @Override
  public List<JBlock> blocks() {
    return setDefault && setNull ? List.of(parent.defaultBlock(), parent.nullBlock())
        : List.of(setDefault ? parent.defaultBlock() : parent.nullBlock());
  }

}
