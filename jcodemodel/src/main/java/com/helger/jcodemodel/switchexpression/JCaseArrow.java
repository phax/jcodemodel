package com.helger.jcodemodel.switchexpression;

import java.util.List;

import com.helger.jcodemodel.IJStatement;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JLambdaBlock;
import com.helger.jcodemodel.JSwitchExpression;

///
/// a switch case using arrow. The case select depends on the implementation
@SuppressWarnings("serial")
public abstract class JCaseArrow<Self extends JCaseArrow<?>> implements IJStatement, BlockSelection<Self> {

  private final JSwitchExpression parent;

  public JCaseArrow(JSwitchExpression parent) {
    this.parent = parent;
  }

  public JSwitchExpression up() {
    return parent;
  }

  /**
   * List of one JBlock, statements which makes up body of this case statement
   */
  private List<JBlock> blocks = List.of(new JLambdaBlock());

  @Override
  public List<JBlock> blocks() {
    return blocks;
  }

}
