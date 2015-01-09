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

import com.helger.jcodemodel.*;

import java.util.*;

/**
 * Common Subexpression Elimination for Java <i>source</i> code.
 *
 * <p>This algorithm is targeted to reduce repetitive occurrences of
 * <ul>
 *   <li>field accesses
 *   <li>array element reads
 *   <li>method calls
 * </ul>
 * within {@link JBlock}, for example a method body. The idea is that Java
 * source code compiler, and even JIT bytecode compiler, sometimes are not
 * allowed to optimize these parts, because they should assume the worst case -
 * object fields might be updated concurrently, methods might have side effects.
 * This optimizer have opposite premises (if some fields or array elements
 * accessed within the optimized block are indeed concurrently updated, and
 * non-void returning methods indeed have side effects, they should be excluded
 * {@code expressionFilter} parameter of {@link #optimize(JBlock,
 * ExpressionFilter)} method).
 *
 * <p>I know theory of compiler optimizations a bit, classic PRE (partial
 * redundancy elimination) algorithm, but decided that it would be simplier
 * to implement custom utility for the needs, even that to adapt the code model
 * for existing PRE implementations: I found only a few, most noteworthy one
 * is from https://github.com/Sable/soot project, and they all seems not ready
 * for reuse. Needless to say that I didn't want to implement the full PRE
 * algorithm myself.
 *
 * <p>Algorithm is scope-based, unlike PRE which is control-flow based (account
 * branching and unconditional jumps).
 *
 * <p>Algorithm could produce code that doesn't compile (because it doesn't
 * track taken variable names, and even couldn't do that, because there could be
 * higher-level scopes, about which the algorithm is not aware), but it
 * shouldn't turn compiling and correct code into compiling, but invalid code,
 * if the premises met (single threading and absence of side effects).
 *
 * @author Roman Leventov <leventov@ya.ru>
 * @see #optimize(JBlock)
 * @see #optimize(JBlock, ExpressionFilter)
 */
public final class CSE
{
  private static final ExpressionFilter DEFAULT_FILTER = new ExpressionFilter ()
  {
    public boolean test (IJExpression expression)
    {
      return (expression instanceof JFieldRef &&
          !(((JFieldRef) expression).object () instanceof AbstractJType)) ||
          expression instanceof JArrayCompRef ||
          (expression instanceof JInvocation &&
              !"void".equals (expression.expressionType ().fullName ()));
    }
  };

  public static void optimize(JBlock block)
  {
    optimize (block, DEFAULT_FILTER);
  }

  public static void optimize(JBlock block, ExpressionFilter filter)
  {
    CSE cse = new CSE (filter, new ArrayList<ExpressionState> (), block);
    cse.extractSubExpressions (cse.outContext);
    List<ExpressionState> sortedSubExpressions =
        sortSubExpressions (cse.commonSubExpressions);
    Map<String, Integer> nameCounts = new HashMap<String, Integer> ();
    for (ExpressionState state : sortedSubExpressions)
    {
      ExpressionState definitionBase = state.definitionBase ();
      JBlock definitionBlock = definitionBase.definitionBlock.block;
      IJExpression expr = state.expression;
      String basicExprName = expr.expressionName ();
      String exprName = basicExprName;
      Integer nameCount = nameCounts.get (exprName);
      if (nameCount != null)
      {
        nameCount += 1;
        exprName += nameCount;
      }
      else
      {
        nameCount = 1;
      }
      final JVar var = new JVar (JMods.forVar (JMod.FINAL),
          expr.expressionType (), exprName, expr);
      if (expr instanceof JFieldRef)
      {
        ((JFieldRef) expr).explicitThis (true);
      }
      boolean anyUpdated = state.forAllSites (new ExpressionCallback ()
      {
        public boolean visitAssignmentTarget (
            IJAssignmentTarget assignmentTarget)
        {
          // do nothing
          return true;
        }

        public boolean visitExpression (IJExpression expr,
                                        ExpressionAccessor accessor)
        {
          if (accessor.get ().equals (expr))
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
        definitionBlock.insertBefore (var, definitionBase.definitionBefore);
      }
    }
  }

  private static List<ExpressionState> sortSubExpressions (
      Collection<ExpressionState> commonSubExpressions)
  {
    Map<ExpressionState, Integer> roots =
        new HashMap<ExpressionState, Integer> ();
    for (ExpressionState subExpression : commonSubExpressions)
    {
      ExpressionState root = subExpression.root ();
      if (roots.containsKey (root))
        continue;
      class SubExpressionCounter implements ExpressionCallback
      {
        int count = 0;

        public boolean visitAssignmentTarget (
            IJAssignmentTarget assignmentTarget)
        {
          count++;
          return true;
        }

        public boolean visitExpression (IJExpression expr,
                                        ExpressionAccessor accessor)
        {
          count++;
          return true;
        }
      }
      SubExpressionCounter counter = new SubExpressionCounter ();
      root.expression.forAllSubExpressions (counter);
      roots.put (root, counter.count);
    }
    List<Map.Entry<ExpressionState, Integer>> rootsAsList =
        new ArrayList<Map.Entry<ExpressionState, Integer>> (roots.entrySet ());
    Collections.sort (rootsAsList,
        new Comparator<Map.Entry<ExpressionState, Integer>> ()
    {
      public int compare (Map.Entry<ExpressionState, Integer> o1,
                          Map.Entry<ExpressionState, Integer> o2)
      {
        int cmp = o1.getValue () - o2.getValue ();
        if (cmp != 0)
          return cmp;
        BlockNode block1 = o1.getKey ().definitionBlock;
        BlockNode block2 = o2.getKey ().definitionBlock;
        cmp = block1.compareTo (block2);
        if (cmp != 0)
          return cmp;
        List<Object> statements = block1.block.getContents ();
        Object def1Before = o1.getKey ().definitionBefore;
        Object def2Before = o2.getKey ().definitionBefore;
        if (def1Before == def2Before)
          return 0;
        for (Object statement : statements)
        {
          if (statement == def1Before)
            return -1;
          if (statement == def2Before)
            return 1;
        }
        return 0;
      }
    });
    List<ExpressionState> sortedSubExpressions =
        new ArrayList<ExpressionState> (rootsAsList.size ());
    for (Map.Entry<ExpressionState, Integer> e : rootsAsList)
    {
      sortedSubExpressions.add (e.getKey ());
    }
    return sortedSubExpressions;
  }

  private final ExpressionFilter filter;
  private final Collection<ExpressionState> commonSubExpressions;
  final BlockNode block;
  private Context outContext = new Context ();
  private Context currentContext = new Context ();
  private Set<IJAssignmentTarget> modified =
      new HashSet<IJAssignmentTarget> ();

  CSE (
      ExpressionFilter filter,
      Collection<ExpressionState> commonSubExpressions, JBlock block)
  {
    this.filter = filter;
    this.commonSubExpressions = commonSubExpressions;
    this.block = BlockNode.root (block);
    optimize ();
  }

  CSE (CSE parent, JBlock block)
  {
    this.filter = parent.filter;
    this.commonSubExpressions = parent.commonSubExpressions;
    this.block = parent.block.child (block);
    optimize ();
  }

  private void optimize ()
  {
    for (final Object blockElement : block.block.getContents ())
    {
      if (blockElement instanceof ExpressionContainer)
      {
        final ExpressionContainer exprContainer =
            (ExpressionContainer) blockElement;
        processExpressionContainer (exprContainer, block, blockElement);
      }
      else if (blockElement instanceof BranchingStatement)
      {
        processConditionalStatement ((BranchingStatement) blockElement);
      }
      else if (blockElement instanceof Loop)
      {
        processLoop ((Loop) blockElement);
      }
      else if (blockElement instanceof JBlock)
      {
        applySubContext (new CSE (this, (JBlock) blockElement),
            blockElement, true);
      }
    }
    extractSubExpressions (currentContext);
    currentContext.clear ();
  }

  private void processLoop (Loop loop)
  {
    processExpressionContainer (loop.statementsExecutedOnce (), block, loop);

    // this run to gather modified
    final CSE loopCSE = new CSE (this, loop.body ());
    loopCSE.processExpressionContainer (
        loop.statementsExecutedOnEachIteration (), block, loop);

    loopCSE.outContext = new Context ();
    loopCSE.currentContext = new Context ();
    // this run to gather outContext with respect to full modified
    loopCSE.optimize ();
    loopCSE.processExpressionContainer (
        loop.statementsExecutedOnEachIteration (), block, loop);

    Context loopOutContext = loopCSE.outContext;
    commonSubExpressions.addAll (loopOutContext.values ());
    applySubContext (loopCSE, loop, false);
  }

  private void processConditionalStatement (
      final BranchingStatement branchingStatement)
  {
    branchingStatement.apply (new BranchingStatementVisitor ()
    {
      public void visit (ExpressionContainer conditionalExpression)
      {
        processExpressionContainer (conditionalExpression,
            block, branchingStatement);
      }

      public void visit (JBlock subBlock)
      {
        applySubContext (new CSE (CSE.this, subBlock),
            branchingStatement, true);
      }

      public void visit (List<JBlock> branches)
      {
        if (branches.size () == 1)
        {
          visit (branches.get (0));
          return;
        }
        List<CSE> branchCSEs = new ArrayList<CSE> ();
        for (JBlock branch : branches)
        {
          branchCSEs.add (new CSE (CSE.this, branch));
        }
        List<Context> branchContexts =
            new ArrayList<Context> (branches.size ());
        for (CSE branchCSE : branchCSEs)
        {
          branchContexts.add (branchCSE.outContext);
        }
        Context firstBranchContext = branchContexts.get (0);
        firstBranchExpressionLoop:
        for (Iterator<ExpressionState> iterator =
                 firstBranchContext.values ().iterator ();
             iterator.hasNext (); )
        {
          ExpressionState state = iterator.next ();
          for (int i = 1; i < branchContexts.size (); i++)
          {
            Context branchContext = branchContexts.get (i);
            if (!branchContext.containsKey (state.expression))
              continue firstBranchExpressionLoop;
          }
          // expression is present in all branches
          iterator.remove ();
          applyState (branchingStatement, state, true);
          for (int i = 1; i < branchContexts.size (); i++)
          {
            applyState (branchingStatement,
                branchContexts.get (i).remove (state.expression), true);
          }
        }
        for (Context branchContext : branchContexts)
        {
          extractSubExpressions (branchContext);
        }
        for (CSE branchCSE : branchCSEs)
        {
          applySubModifications (branchCSE);
        }
      }
    });
  }

  private void applySubContext (CSE subCSE, Object blockElement,
                                boolean temporary)
  {
    for (ExpressionState state : subCSE.outContext.values ())
    {
      applyState (blockElement, state, !temporary);
    }
    applySubModifications (subCSE);
  }

  private void applySubModifications (CSE subCSE)
  {
    for (IJAssignmentTarget modified : subCSE.modified)
    {
      invalidate (modified);
    }
  }

  private void applyState (Object blockElement, ExpressionState state,
                           boolean mustBeDefinedHere)
  {
    if (modified (state.expression, modified))
    {
      currentContext.add (state, block, blockElement, mustBeDefinedHere);
    }
    else
    {
      outContext.add (state, block, blockElement, mustBeDefinedHere);
    }
  }

  private void processExpressionContainer (ExpressionContainer exprContainer,
                                           final BlockNode block,
                                           final Object currentStatement)
  {
    exprContainer.forAllSubExpressions (new ExpressionCallback ()
    {
      public boolean visitAssignmentTarget (IJAssignmentTarget assignmentTarget)
      {
        invalidate (assignmentTarget);
        return true;
      }

      public boolean visitExpression (IJExpression expr,
                                      ExpressionAccessor accessor)
      {
        if (filter.test (expr))
        {
          if (modified (expr, modified))
          {
            currentContext.add (expr, block, currentStatement, accessor);
          }
          else
          {
            outContext.add (expr, block, currentStatement, accessor);
          }
        }
        return true;
      }
    });
  }

  private void invalidate (IJAssignmentTarget assignmentTarget)
  {
    modified.add (assignmentTarget);
    Set<IJAssignmentTarget> assignmentTargetAsSingletonSet =
        Collections.singleton (assignmentTarget);
    for (Iterator<ExpressionState> iterator =
             currentContext.values ().iterator ();
         iterator.hasNext (); )
    {
      ExpressionState state = iterator.next ();
      if (modified (state.expression, assignmentTargetAsSingletonSet))
      {
        iterator.remove ();
        if (state.size () > 1)
          commonSubExpressions.add (state);
      }
    }
  }

  static boolean modified (IJExpression expr,
                           final Set<IJAssignmentTarget> modified)
  {
    if (expr instanceof IJAssignmentTarget && modified.contains (expr))
      return true;
    return !expr.forAllSubExpressions (
        new ExpressionCallback ()
        {

          public boolean visitAssignmentTarget (
              IJAssignmentTarget assignmentTarget)
          {
            // do nothing
            return true;
          }

          public boolean visitExpression (IJExpression expr,
                                          ExpressionAccessor accessor)
          {
            return !(expr instanceof IJAssignmentTarget &&
                modified.contains (expr));
          }
        });
  }

  private void extractSubExpressions(Context context)
  {
    for (ExpressionState state : context.values ())
    {
      if (state.size () > 1)
        commonSubExpressions.add (state);
    }
  }
}
