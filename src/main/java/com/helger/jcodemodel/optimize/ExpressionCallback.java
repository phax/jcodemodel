package com.helger.jcodemodel.optimize;

import com.helger.jcodemodel.IJAssignmentTarget;
import com.helger.jcodemodel.IJExpression;

public interface ExpressionCallback
{
  boolean visitAssignmentTarget (IJAssignmentTarget assignmentTarget);

  boolean visitExpression (IJExpression expr, ExpressionAccessor accessor);
}
