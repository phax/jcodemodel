package com.helger.jcodemodel.switchexpression;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.IJFormatter;
import com.helger.jcodemodel.IJStatement;
import com.helger.jcodemodel.JLambdaParam;
import com.helger.jcodemodel.JSwitchExpression;
import com.helger.jcodemodel.JThrow;

// switch case using a pattern as the selection
/// ```
/// 	case char c when c>='&' && c<='z'-> {countChars++; yield 1+c-'a';}
/// ```
@SuppressWarnings("serial")
public class JCasePattern extends JCaseArrow<JCasePattern> {

  private final AbstractJType type;

  private final String varName;

  public JCasePattern(JSwitchExpression parent, AbstractJType type, String varName) {
    super(parent);
    this.type = type;
    this.varName = varName;
  }

  private final List<IJExpression> guards = new ArrayList<>();

  public JCasePattern when(Function<JLambdaParam, IJExpression> maker) {
    guards.add(maker.apply(param()));
    return this;
  }

  private JLambdaParam param = null;

  public JLambdaParam param() {
    if (param == null) {
      param = new JLambdaParam(type, varName);
    }
    return param;
  }

  public JCasePattern addOn(Function<JLambdaParam, IJStatement> maker) {
    return add(maker.apply(param()));
  }

  public JCasePattern _throwsOn(Function<JLambdaParam, IJExpression> maker) {
    return add(new JThrow(maker.apply(param())));
  }

  public JCasePattern yieldOn(Function<JLambdaParam, IJExpression> maker) {
    return super.yield(maker.apply(param()));
  }

  @Override
  public void state(@NonNull IJFormatter f) {
    f.indent();
    f.print("case ").declaration(param);
    boolean first = true;
    for (IJExpression ije : guards) {
      if (first) {
        f.print(" when ");
      } else {
        f.print(" && ");
      }
      f.generable(ije);
      first = false;
    }
    stateBody(f);
    f.outdent();

  }


}
