package com.helger.jcodemodel.optimize;

//TODO remove this API: if-else, try-catch and switch are too different between
// each other to have a common API
public interface BranchingStatement
{
  void apply (BranchingStatementVisitor visitor);
}
