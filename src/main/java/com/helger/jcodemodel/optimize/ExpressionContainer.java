package com.helger.jcodemodel.optimize;

public interface ExpressionContainer
{
  boolean forAllSubExpressions (ExpressionCallback callback);
}
