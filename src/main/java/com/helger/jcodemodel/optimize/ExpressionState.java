/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2015 Philip Helger
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package com.helger.jcodemodel.optimize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.helger.jcodemodel.IJExpression;

class ExpressionState
{
  final IJExpression _expression;
  final BlockNode _definitionBlock;
  final Object _definitionBefore;
  final boolean _mustBeDefinedHere;
  Collection <ExpressionAccessor> _sites;
  ExpressionState _parent;
  List <ExpressionState> _children;

  ExpressionState (final IJExpression expression,
                   final BlockNode definitionBlock,
                   final Object definitionBefore,
                   final boolean mustBeDefinedHere)
  {
    _expression = expression;
    _definitionBlock = definitionBlock;
    _definitionBefore = definitionBefore;
    _mustBeDefinedHere = mustBeDefinedHere;
  }

  void addSite (final ExpressionAccessor... accessors)
  {
    if (_sites == null)
      _sites = new ArrayList <ExpressionAccessor> (5);
    Collections.addAll (_sites, accessors);
  }

  int size ()
  {
    int size = _sites != null ? _sites.size () : 0;
    if (_children != null)
    {
      for (final ExpressionState child : _children)
      {
        size += child.size ();
      }
    }
    return size;
  }

  ExpressionState definitionBase ()
  {
    if (!_mustBeDefinedHere && _sites == null && _children.size () == 1)
      return _children.get (0).definitionBase ();
    return this;
  }

  ExpressionState root ()
  {
    if (_parent == null)
      return this;
    return _parent.root ();
  }

  private void addChild (final ExpressionState state)
  {
    if (_children == null)
      _children = new ArrayList <ExpressionState> (5);
    _children.add (state);
    state._parent = this;
  }

  void link (final ExpressionState state)
  {
    if (!_expression.equals (state._expression))
      throw new IllegalArgumentException ();
    root ().addChild (state.root ());
  }

  boolean forAllSites (final ExpressionCallback action)
  {
    if (_sites != null)
    {
      for (final ExpressionAccessor site : _sites)
      {
        if (!action.visitExpression (_expression, site))
          return false;
      }
    }
    if (_children != null)
    {
      for (final ExpressionState child : _children)
      {
        if (!child.forAllSites (action))
          return false;
      }
    }
    return true;
  }
}
