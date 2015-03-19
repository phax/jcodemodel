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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJAssignmentTarget;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JArrayCompRef;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JFieldRef;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JMods;
import com.helger.jcodemodel.JVar;

/**
 * Common Subexpression Elimination for Java <i>source</i> code.
 * <p>
 * This algorithm is targeted to reduce repetitive occurrences of
 * <ul>
 * <li>field accesses
 * <li>array element reads
 * <li>method calls
 * </ul>
 * within {@link JBlock}, for example a method body. The idea is that Java
 * source code compiler, and even JIT bytecode compiler, sometimes are not
 * allowed to optimize these parts, because they should assume the worst case -
 * object fields might be updated concurrently, methods might have side effects.
 * This optimizer have opposite premises (if some fields or array elements
 * accessed within the optimized block are indeed concurrently updated, and
 * non-void returning methods indeed have side effects, they should be excluded
 * {@code expressionFilter} parameter of
 * {@link #optimize(JBlock, ExpressionFilter)} method).
 * <p>
 * I know theory of compiler optimizations a bit, classic PRE (partial
 * redundancy elimination) algorithm, but decided that it would be simplier to
 * implement custom utility for the needs, even that to adapt the code model for
 * existing PRE implementations: I found only a few, most noteworthy one is from
 * https://github.com/Sable/soot project, and they all seems not ready for
 * reuse. Needless to say that I didn't want to implement the full PRE algorithm
 * myself.
 * <p>
 * Algorithm is scope-based, unlike PRE which is control-flow based (account
 * branching and unconditional jumps).
 * <p>
 * Algorithm could produce code that doesn't compile (because it doesn't track
 * taken variable names, and even couldn't do that, because there could be
 * higher-level scopes, about which the algorithm is not aware), but it
 * shouldn't turn compiling and correct code into compiling, but invalid code,
 * if the premises met (single threading and absence of side effects).
 *
 * @author Roman Leventov &lt;leventov@ya.ru&gt;
 * @see #optimize(JBlock)
 * @see #optimize(JBlock, ExpressionFilter)
 */
public final class CSE
{
  private static final ExpressionFilter DEFAULT_FILTER = new ExpressionFilter ()
  {
    public boolean test (final IJExpression expression)
    {
      return (expression instanceof JFieldRef && !(((JFieldRef) expression).object () instanceof AbstractJType)) ||
             expression instanceof JArrayCompRef ||
             (expression instanceof JInvocation && !"void".equals (expression.expressionType ().fullName ()));
    }
  };

  public static void optimize (final JBlock block)
  {
    optimize (block, DEFAULT_FILTER);
  }

  public static void optimize (final JBlock block, final ExpressionFilter filter)
  {
    final CSE cse = new CSE (filter, new ArrayList <ExpressionState> (), block);
    cse.extractSubExpressions (cse._outContext);
    final List <ExpressionState> sortedSubExpressions = sortSubExpressions (cse._commonSubExpressions);
    final Map <String, Integer> nameCounts = new HashMap <String, Integer> ();
    for (final ExpressionState state : sortedSubExpressions)
    {
      final ExpressionState definitionBase = state.definitionBase ();
      final JBlock definitionBlock = definitionBase._definitionBlock._block;
      final IJExpression expr = state._expression;
      final String basicExprName = expr.expressionName ();
      String exprName = basicExprName;
      Integer nameCount = nameCounts.get (exprName);
      if (nameCount != null)
      {
        nameCount = Integer.valueOf (nameCount.intValue () + 1);
        exprName += nameCount;
      }
      else
      {
        nameCount = Integer.valueOf (1);
      }
      final JVar var = new JVar (JMods.forVar (JMod.FINAL), expr.expressionType (), exprName, expr);
      if (expr instanceof JFieldRef)
      {
        ((JFieldRef) expr).explicitThis (true);
      }
      final boolean anyUpdated = state.forAllSites (new ExpressionCallback ()
      {
        public boolean visitAssignmentTarget (final IJAssignmentTarget assignmentTarget)
        {
          // do nothing
          return true;
        }

        public boolean visitExpression (final IJExpression expr2, final ExpressionAccessor accessor)
        {
          if (accessor.get ().equals (expr2))
          {
            accessor.set (var);
            return true;
          }
          return false;
        }
      });
      if (anyUpdated)
      {
        nameCounts.put (basicExprName, nameCount);
        definitionBlock.insertBefore (var, definitionBase._definitionBefore);
      }
    }
  }

  private static List <ExpressionState> sortSubExpressions (final Collection <ExpressionState> commonSubExpressions)
  {
    final Map <ExpressionState, Integer> roots = new HashMap <ExpressionState, Integer> ();
    for (final ExpressionState subExpression : commonSubExpressions)
    {
      final ExpressionState root = subExpression.root ();
      if (roots.containsKey (root))
        continue;
      class SubExpressionCounter implements ExpressionCallback
      {
        int _count = 0;

        public boolean visitAssignmentTarget (final IJAssignmentTarget assignmentTarget)
        {
          _count++;
          return true;
        }

        public boolean visitExpression (final IJExpression expr, final ExpressionAccessor accessor)
        {
          _count++;
          return true;
        }
      }
      final SubExpressionCounter counter = new SubExpressionCounter ();
      root._expression.forAllSubExpressions (counter);
      roots.put (root, Integer.valueOf (counter._count));
    }
    final List <Map.Entry <ExpressionState, Integer>> rootsAsList = new ArrayList <Map.Entry <ExpressionState, Integer>> (roots.entrySet ());
    Collections.sort (rootsAsList, new Comparator <Map.Entry <ExpressionState, Integer>> ()
    {
      public int compare (final Map.Entry <ExpressionState, Integer> o1, final Map.Entry <ExpressionState, Integer> o2)
      {
        int cmp = o1.getValue ().intValue () - o2.getValue ().intValue ();
        if (cmp != 0)
          return cmp;
        final BlockNode block1 = o1.getKey ()._definitionBlock;
        final BlockNode block2 = o2.getKey ()._definitionBlock;
        cmp = block1.compareTo (block2);
        if (cmp != 0)
          return cmp;
        final List <Object> statements = block1._block.getContents ();
        final Object def1Before = o1.getKey ()._definitionBefore;
        final Object def2Before = o2.getKey ()._definitionBefore;
        if (def1Before == def2Before)
          return 0;
        for (final Object statement : statements)
        {
          if (statement == def1Before)
            return -1;
          if (statement == def2Before)
            return 1;
        }
        return 0;
      }
    });
    final List <ExpressionState> sortedSubExpressions = new ArrayList <ExpressionState> (rootsAsList.size ());
    for (final Map.Entry <ExpressionState, Integer> e : rootsAsList)
    {
      sortedSubExpressions.add (e.getKey ());
    }
    return sortedSubExpressions;
  }

  private final ExpressionFilter _filter;
  private final Collection <ExpressionState> _commonSubExpressions;
  final BlockNode _block;
  private Context _outContext = new Context ();
  private Context _currentContext = new Context ();
  private final Set <IJAssignmentTarget> _modified = new HashSet <IJAssignmentTarget> ();

  CSE (final ExpressionFilter filter, final Collection <ExpressionState> commonSubExpressions, final JBlock block)
  {
    _filter = filter;
    _commonSubExpressions = commonSubExpressions;
    _block = BlockNode.root (block);
    optimize ();
  }

  CSE (final CSE parent, final JBlock block)
  {
    _filter = parent._filter;
    _commonSubExpressions = parent._commonSubExpressions;
    _block = parent._block.child (block);
    optimize ();
  }

  private void optimize ()
  {
    for (final Object blockElement : _block._block.getContents ())
    {
      if (blockElement instanceof ExpressionContainer)
      {
        final ExpressionContainer exprContainer = (ExpressionContainer) blockElement;
        processExpressionContainer (exprContainer, _block, blockElement);
      }
      else
        if (blockElement instanceof BranchingStatement)
        {
          processConditionalStatement ((BranchingStatement) blockElement);
        }
        else
          if (blockElement instanceof Loop)
          {
            processLoop ((Loop) blockElement);
          }
          else
            if (blockElement instanceof JBlock)
            {
              applySubContext (new CSE (this, (JBlock) blockElement), blockElement, true);
            }
    }
    extractSubExpressions (_currentContext);
    _currentContext.clear ();
  }

  private void processLoop (final Loop loop)
  {
    processExpressionContainer (loop.statementsExecutedOnce (), _block, loop);

    // this run to gather modified
    final CSE loopCSE = new CSE (this, loop.body ());
    loopCSE.processExpressionContainer (loop.statementsExecutedOnEachIteration (), _block, loop);

    loopCSE._outContext = new Context ();
    loopCSE._currentContext = new Context ();
    // this run to gather outContext with respect to full modified
    loopCSE.optimize ();
    loopCSE.processExpressionContainer (loop.statementsExecutedOnEachIteration (), _block, loop);

    final Context loopOutContext = loopCSE._outContext;
    _commonSubExpressions.addAll (loopOutContext.values ());
    applySubContext (loopCSE, loop, false);
  }

  private void processConditionalStatement (final BranchingStatement branchingStatement)
  {
    branchingStatement.apply (new BranchingStatementVisitor ()
    {
      public void visit (final ExpressionContainer conditionalExpression)
      {
        processExpressionContainer (conditionalExpression, _block, branchingStatement);
      }

      public void visit (final JBlock subBlock)
      {
        applySubContext (new CSE (CSE.this, subBlock), branchingStatement, true);
      }

      public void visit (final List <JBlock> branches)
      {
        if (branches.size () == 1)
        {
          visit (branches.get (0));
          return;
        }
        final List <CSE> branchCSEs = new ArrayList <CSE> ();
        for (final JBlock branch : branches)
        {
          branchCSEs.add (new CSE (CSE.this, branch));
        }
        final List <Context> branchContexts = new ArrayList <Context> (branches.size ());
        for (final CSE branchCSE : branchCSEs)
        {
          branchContexts.add (branchCSE._outContext);
        }
        final Context firstBranchContext = branchContexts.get (0);
        firstBranchExpressionLoop: for (final Iterator <ExpressionState> iterator = firstBranchContext.values ()
                                                                                                      .iterator (); iterator.hasNext ();)
        {
          final ExpressionState state = iterator.next ();
          for (int i = 1; i < branchContexts.size (); i++)
          {
            final Context branchContext = branchContexts.get (i);
            if (!branchContext.containsKey (state._expression))
              continue firstBranchExpressionLoop;
          }
          // expression is present in all branches
          iterator.remove ();
          applyState (branchingStatement, state, true);
          for (int i = 1; i < branchContexts.size (); i++)
          {
            applyState (branchingStatement, branchContexts.get (i).remove (state._expression), true);
          }
        }
        for (final Context branchContext : branchContexts)
        {
          extractSubExpressions (branchContext);
        }
        for (final CSE branchCSE : branchCSEs)
        {
          applySubModifications (branchCSE);
        }
      }
    });
  }

  private void applySubContext (final CSE subCSE, final Object blockElement, final boolean temporary)
  {
    for (final ExpressionState state : subCSE._outContext.values ())
    {
      applyState (blockElement, state, !temporary);
    }
    applySubModifications (subCSE);
  }

  private void applySubModifications (final CSE subCSE)
  {
    for (final IJAssignmentTarget modified : subCSE._modified)
    {
      invalidate (modified);
    }
  }

  private void applyState (final Object blockElement, final ExpressionState state, final boolean mustBeDefinedHere)
  {
    if (modified (state._expression, _modified))
    {
      _currentContext.add (state, _block, blockElement, mustBeDefinedHere);
    }
    else
    {
      _outContext.add (state, _block, blockElement, mustBeDefinedHere);
    }
  }

  private void processExpressionContainer (final ExpressionContainer exprContainer,
                                           final BlockNode block,
                                           final Object currentStatement)
  {
    exprContainer.forAllSubExpressions (new ExpressionCallback ()
    {
      public boolean visitAssignmentTarget (final IJAssignmentTarget assignmentTarget)
      {
        invalidate (assignmentTarget);
        return true;
      }

      public boolean visitExpression (final IJExpression expr, final ExpressionAccessor accessor)
      {
        if (_filter.test (expr))
        {
          if (modified (expr, _modified))
          {
            _currentContext.add (expr, block, currentStatement, accessor);
          }
          else
          {
            _outContext.add (expr, block, currentStatement, accessor);
          }
        }
        return true;
      }
    });
  }

  private void invalidate (final IJAssignmentTarget assignmentTarget)
  {
    _modified.add (assignmentTarget);
    final Set <IJAssignmentTarget> assignmentTargetAsSingletonSet = Collections.singleton (assignmentTarget);
    for (final Iterator <ExpressionState> iterator = _currentContext.values ().iterator (); iterator.hasNext ();)
    {
      final ExpressionState state = iterator.next ();
      if (modified (state._expression, assignmentTargetAsSingletonSet))
      {
        iterator.remove ();
        if (state.size () > 1)
          _commonSubExpressions.add (state);
      }
    }
  }

  static boolean modified (final IJExpression expr, final Set <IJAssignmentTarget> modified)
  {
    if (expr instanceof IJAssignmentTarget && modified.contains (expr))
      return true;
    return !expr.forAllSubExpressions (new ExpressionCallback ()
    {

      public boolean visitAssignmentTarget (final IJAssignmentTarget assignmentTarget)
      {
        // do nothing
        return true;
      }

      public boolean visitExpression (final IJExpression expr2, final ExpressionAccessor accessor)
      {
        return !(expr2 instanceof IJAssignmentTarget && modified.contains (expr2));
      }
    });
  }

  private void extractSubExpressions (final Context context)
  {
    for (final ExpressionState state : context.values ())
    {
      if (state.size () > 1)
        _commonSubExpressions.add (state);
    }
  }
}
