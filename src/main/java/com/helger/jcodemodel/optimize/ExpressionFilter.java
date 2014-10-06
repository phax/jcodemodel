package com.helger.jcodemodel.optimize;

import com.helger.jcodemodel.IJExpression;

public interface ExpressionFilter
{
  boolean test (IJExpression expression);
}
