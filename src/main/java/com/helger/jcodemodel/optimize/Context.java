package com.helger.jcodemodel.optimize;

import com.helger.jcodemodel.IJExpression;

import java.util.HashMap;

final class Context extends HashMap<IJExpression, ExpressionState>
{

  void add (IJExpression expression,
            BlockNode definitionBlock, Object definitionBefore,
            ExpressionAccessor... accessors)
  {
    ExpressionState state = get (expression);
    if (state == null)
    {
      state = new ExpressionState (expression,
          definitionBlock, definitionBefore, false);
      put (expression, state);
    }
    state.addSite (accessors);
  }

  void add(ExpressionState state,
           BlockNode definitionBlock, Object definitionBefore,
           boolean mustBeDefinedHere)
  {
    IJExpression expression = state.expression;
    ExpressionState s = get (expression);
    if (s == null)
    {
      s = new ExpressionState (expression,
          definitionBlock, definitionBefore, mustBeDefinedHere);
      put (expression, s);
    }
    s.link (state);
  }

  public boolean addIfPresent (IJExpression expression,
                               ExpressionAccessor... accessors)
  {
    ExpressionState state = get (expression);
    if (state != null)
    {
      state.addSite (accessors);
      return true;
    }
    return false;
  }
}
