package com.helger.jcodemodel.optimize;

import com.helger.jcodemodel.JBlock;

import java.util.List;

public interface BranchingStatementVisitor
{
  void visit (ExpressionContainer conditionalExpression);

  void visit (JBlock block);

  void visit (List<JBlock> branches);
}
