package com.helger.jcodemodel.optimize;

import com.helger.jcodemodel.IJExpression;

public interface ExpressionAccessor
{
  void set (IJExpression newExpression);
  IJExpression get ();
}
