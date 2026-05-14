package com.helger.jcodemodel.switchexpression;

import java.util.List;

import com.helger.jcodemodel.IJFormatter;
import com.helger.jcodemodel.IJObject;
import com.helger.jcodemodel.IJStatement;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JLambdaBlock;
import com.helger.jcodemodel.JSwitchExpression;
import com.helger.jcodemodel.JThrow;
import com.helger.jcodemodel.JYield;

///
/// a switch case using arrow. The case select depends on the implementation
/// [BlockSelection] allows to chain them, but requires to provide a list of blocks
@SuppressWarnings("serial")
public abstract class JCaseArrow<Self extends JCaseArrow<?>> implements IJStatement, BlockSelection<Self> {

  private final JSwitchExpression parent;

  public JCaseArrow(JSwitchExpression parent) {
    this.parent = parent;
  }

  public JSwitchExpression up() {
    return parent;
  }

  private JLambdaBlock block = new JLambdaBlock();

  public JLambdaBlock getBlock() {
    return block;
  }

  private List<JBlock> blocks = List.of(block);

  @Override
  public List<JBlock> blocks() {
    return blocks;
  }

  /// generate the arrow and body in the formatter
  protected void stateBody(IJFormatter f) {
    f.print(" -> ").newline();
    if (block.getContents().size() == 1) {
      IJObject firstContent = block.getContents().get(0);
      if (firstContent instanceof JYield jy) {
        f.indent();
        f.generable(jy.expr()).print(";").newline();
        f.outdent();
        return;
      } else if (firstContent instanceof JThrow jt) {
        f.indent();
        f.statement(jt);
        f.outdent();
        return;
      }
    }
    f.statement(getBlock());
  }
}
