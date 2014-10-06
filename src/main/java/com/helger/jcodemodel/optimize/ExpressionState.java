package com.helger.jcodemodel.optimize;

import com.helger.jcodemodel.IJExpression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

class ExpressionState
{
  final IJExpression expression;
  final BlockNode definitionBlock;
  final Object definitionBefore;
  final boolean mustBeDefinedHere;
  Collection<ExpressionAccessor> sites;
  ExpressionState parent;
  List<ExpressionState> children;

  ExpressionState (IJExpression expression,
                   BlockNode definitionBlock, Object definitionBefore,
                   boolean mustBeDefinedHere)
  {
    this.expression = expression;
    this.definitionBlock = definitionBlock;
    this.definitionBefore = definitionBefore;
    this.mustBeDefinedHere = mustBeDefinedHere;
  }

  void addSite (ExpressionAccessor... accessors)
  {
    if (sites == null)
      sites = new ArrayList<ExpressionAccessor> (5);
    Collections.addAll (sites, accessors);
  }

  int size ()
  {
    int size = sites != null ? sites.size () : 0;
    if (children != null)
    {
      for (ExpressionState child : children)
      {
        size += child.size ();
      }
    }
    return size;
  }

  ExpressionState definitionBase()
  {
    if (!mustBeDefinedHere && sites == null && children.size () == 1)
      return children.get (0).definitionBase ();
    return this;
  }

  ExpressionState root ()
  {
    if (parent == null)
      return this;
    return parent.root ();
  }

  private void addChild (ExpressionState state)
  {
    if (children == null)
      children = new ArrayList<ExpressionState> (5);
    children.add (state);
    state.parent = this;
  }

  void link (ExpressionState state)
  {
    if (!expression.equals (state.expression))
      throw new IllegalArgumentException ();
    root ().addChild (state.root ());
  }

  boolean forAllSites (ExpressionCallback action)
  {
    if (sites != null)
    {
      for (ExpressionAccessor site : sites)
      {
        if (!action.visitExpression (expression, site))
          return false;
      }
    }
    if (children != null)
    {
      for (ExpressionState child : children)
      {
        if (!child.forAllSites (action))
          return false;
      }
    }
    return true;
  }
}
