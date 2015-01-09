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
