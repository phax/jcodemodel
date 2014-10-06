package com.helger.jcodemodel.optimize;

import com.helger.jcodemodel.JBlock;

public interface Loop
{
  ExpressionContainer statementsExecutedOnce ();

  ExpressionContainer statementsExecutedOnEachIteration ();

  JBlock body ();
}
