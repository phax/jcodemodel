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
package com.helger.jcodemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A block of Java code, which may contain statements and local declarations.
 * <p>
 * {@link JBlock} contains a large number of factory methods that creates new
 * statements/declarations. Those newly created statements/declarations are
 * inserted into the {@link #pos() "current position"}. The position advances
 * one every time you add a new instruction.
 */
public class JBlock implements IJGenerable, IJStatement
{
  /**
   * Declarations and statements contained in this block. Either
   * {@link IJStatement} or {@link IJDeclaration}.
   */
  private final List <Object> m_aContentList = new ArrayList <Object> ();

  /**
   * Whether or not this block must be braced and indented
   */
  private boolean m_BracesRequired = true;
  private boolean m_bIndentRequire = true;

  /**
   * Current position.
   */
  private int m_nPos;

  protected JBlock ()
  {
    this (true, true);
  }

  protected JBlock (final boolean bBracesRequired, final boolean bIndentRequired)
  {
    m_BracesRequired = bBracesRequired;
    m_bIndentRequire = bIndentRequired;
  }

  /**
   * Returns a read-only view of {@link IJStatement}s and {@link IJDeclaration}
   * in this block.
   */
  @Nonnull
  public List <Object> getContents ()
  {
    return Collections.unmodifiableList (m_aContentList);
  }

  @Nonnull
  private <T> T _insert (@Nonnull final T aStatementOrDeclaration)
  {
    if (aStatementOrDeclaration == null)
      throw new NullPointerException ("statementOrDeclaration");

    m_aContentList.add (m_nPos, aStatementOrDeclaration);
    m_nPos++;
    return aStatementOrDeclaration;
  }

  public void remove (final Object o)
  {
    m_aContentList.remove (o);
  }

  public void remove (@Nonnegative final int index)
  {
    m_aContentList.remove (index);
  }

  /**
   * Remove all elements.
   */
  public void removeAll ()
  {
    m_aContentList.clear ();
    m_nPos = 0;
  }

  /**
   * Gets the current position to which new statements will be inserted. For
   * example if the value is 0, newly created instructions will be inserted at
   * the very beginning of the block.
   *
   * @see #pos(int)
   */
  @Nonnegative
  public int pos ()
  {
    return m_nPos;
  }

  /**
   * Sets the current position.
   *
   * @return the old value of the current position.
   * @throws IllegalArgumentException
   *         if the new position value is illegal.
   * @see #pos()
   */
  @Nonnegative
  public int pos (@Nonnegative final int newPos)
  {
    final int r = m_nPos;
    if (newPos > m_aContentList.size () || newPos < 0)
      throw new IllegalArgumentException ("Illegal position provided: " + newPos);
    m_nPos = newPos;
    return r;
  }

  /**
   * Returns true if this block is empty and does not contain any statement.
   */
  public boolean isEmpty ()
  {
    return m_aContentList.isEmpty ();
  }

  /**
   * Adds a local variable declaration to this block
   *
   * @param type
   *        JType of the variable
   * @param name
   *        Name of the variable
   * @return Newly generated JVar
   */
  @Nonnull
  public JVar decl (@Nonnull final AbstractJType type, @Nonnull final String name)
  {
    return decl (JMod.NONE, type, name, null);
  }

  @Nonnull
  public JVar decl (@Nonnull final String name, @Nonnull final IJExpression init)
  {
    return decl (JMod.NONE, init.expressionType (), name, init);
  }

  /**
   * Adds a local variable declaration to this block
   *
   * @param mods
   *        Modifiers for the variable
   * @param type
   *        JType of the variable
   * @param name
   *        Name of the variable
   * @return Newly generated JVar
   */
  @Nonnull
  public JVar decl (final int mods, @Nonnull final AbstractJType type, @Nonnull final String name)
  {
    return decl (mods, type, name, null);
  }

  /**
   * Adds a local variable declaration to this block
   *
   * @param type
   *        JType of the variable
   * @param name
   *        Name of the variable
   * @param init
   *        Initialization expression for this variable. May be null.
   * @return Newly generated JVar
   */
  @Nonnull
  public JVar decl (@Nonnull final AbstractJType type, @Nonnull final String name, @Nullable final IJExpression init)
  {
    return decl (JMod.NONE, type, name, init);
  }

  /**
   * Adds a local variable declaration to this block
   *
   * @param mods
   *        Modifiers for the variable
   * @param type
   *        JType of the variable
   * @param name
   *        Name of the variable
   * @param init
   *        Initialization expression for this variable. May be null.
   * @return Newly generated JVar
   */
  @Nonnull
  public JVar decl (final int mods,
                    @Nonnull final AbstractJType type,
                    @Nonnull final String name,
                    @Nullable final IJExpression init)
  {
    final JVar v = new JVar (JMods.forVar (mods), type, name, init);
    _insert (v);
    m_BracesRequired = true;
    m_bIndentRequire = true;
    return v;
  }

  public JBlock insertBefore (final JVar var, final Object before)
  {
    final int i = m_aContentList.indexOf (before);
    m_aContentList.add (i, var);
    m_nPos++;
    m_BracesRequired = true;
    m_bIndentRequire = true;
    return this;
  }

  /**
   * Creates an assignment statement and adds it to this block.
   *
   * @param lhs
   *        Assignable variable or field for left hand side of expression
   * @param exp
   *        Right hand side expression
   */
  @Nonnull
  public JBlock assign (@Nonnull final IJAssignmentTarget lhs, @Nonnull final IJExpression exp)
  {
    _insert (JExpr.assign (lhs, exp));
    return this;
  }

  @Nonnull
  public JBlock assignPlus (@Nonnull final IJAssignmentTarget lhs, @Nonnull final IJExpression exp)
  {
    _insert (JExpr.assignPlus (lhs, exp));
    return this;
  }

  @Nonnull
  public JBlock assignMinus (@Nonnull final IJAssignmentTarget lhs, @Nonnull final IJExpression exp)
  {
    _insert (JExpr.assignMinus (lhs, exp));
    return this;
  }

  @Nonnull
  public JBlock assignTimes (@Nonnull final IJAssignmentTarget lhs, @Nonnull final IJExpression exp)
  {
    _insert (JExpr.assignTimes (lhs, exp));
    return this;
  }

  @Nonnull
  public JBlock assignDivide (@Nonnull final IJAssignmentTarget lhs, @Nonnull final IJExpression exp)
  {
    _insert (JExpr.assignDivide (lhs, exp));
    return this;
  }

  /**
   * Creates an invocation statement and adds it to this block.
   *
   * @param expr
   *        {@link IJExpression} evaluating to the class or object upon which
   *        the named method will be invoked
   * @param method
   *        Name of method to invoke
   * @return Newly generated {@link JInvocation}
   */
  @Nonnull
  public JInvocation invoke (@Nonnull final IJExpression expr, @Nonnull final String method)
  {
    return _insert (new JInvocation (expr, method));
  }

  /**
   * Creates an invocation statement and adds it to this block.
   *
   * @param method
   *        Name of method to invoke on this
   * @return Newly generated {@link JInvocation}
   */
  @Nonnull
  public JInvocation invokeThis (@Nonnull final String method)
  {
    return invoke (JExpr._this (), method);
  }

  /**
   * Creates an invocation statement and adds it to this block.
   *
   * @param expr
   *        {@link IJExpression} evaluating to the class or object upon which
   *        the method will be invoked
   * @param method
   *        {@link JMethod} to invoke
   * @return Newly generated {@link JInvocation}
   */
  @Nonnull
  public JInvocation invoke (@Nonnull final IJExpression expr, @Nonnull final JMethod method)
  {
    return _insert (new JInvocation (expr, method));
  }

  /**
   * Creates an invocation statement and adds it to this block.
   *
   * @param method
   *        {@link JMethod} to invoke on this
   * @return Newly generated {@link JInvocation}
   */
  @Nonnull
  public JInvocation invokeThis (@Nonnull final JMethod method)
  {
    return invoke (JExpr._this (), method);
  }

  /**
   * Creates a static invocation statement.
   */
  @Nonnull
  public JInvocation staticInvoke (@Nonnull final AbstractJClass type, @Nonnull final String method)
  {
    return _insert (new JInvocation (type, method));
  }

  /**
   * Creates an invocation statement and adds it to this block.
   *
   * @param method
   *        Name of method to invoke
   * @return Newly generated JInvocation
   */
  @Nonnull
  public JInvocation invoke (@Nonnull final String method)
  {
    return _insert (new JInvocation ((IJExpression) null, method));
  }

  /**
   * Creates an invocation statement and adds it to this block.
   *
   * @param method
   *        JMethod to invoke
   * @return Newly generated JInvocation
   */
  @Nonnull
  public JInvocation invoke (@Nonnull final JMethod method)
  {
    return _insert (new JInvocation ((IJExpression) null, method));
  }

  @Nonnull
  public JInvocation _new (@Nonnull final AbstractJClass c)
  {
    return _insert (new JInvocation (c));
  }

  @Nonnull
  public JInvocation _new (@Nonnull final AbstractJType t)
  {
    return _insert (new JInvocation (t));
  }

  /**
   * Adds a statement to this block
   *
   * @param s
   *        JStatement to be added
   * @return This block
   */
  @Nonnull
  public JBlock add (@Nonnull final IJStatement s)
  {
    // ## Needed?
    _insert (s);
    return this;
  }

  /**
   * Create an If statement and add it to this block
   *
   * @param expr
   *        JExpression to be tested to determine branching
   * @return Newly generated conditional statement
   */
  @Nonnull
  public JConditional _if (@Nonnull final IJExpression expr)
  {
    return _insert (new JConditional (expr));
  }

  /**
   * Create a For statement and add it to this block
   *
   * @return Newly generated For statement. Never <code>null</code>.
   */
  @Nonnull
  public JForLoop _for ()
  {
    return _insert (new JForLoop ());
  }

  /**
   * Create a While statement and add it to this block
   *
   * @return Newly generated While statement
   */
  @Nonnull
  public JWhileLoop _while (@Nonnull final IJExpression test)
  {
    return _insert (new JWhileLoop (test));
  }

  /**
   * Create a switch/case statement and add it to this block
   */
  @Nonnull
  public JSwitch _switch (@Nonnull final IJExpression test)
  {
    return _insert (new JSwitch (test));
  }

  /**
   * Create a Do statement and add it to this block
   *
   * @return Newly generated Do statement
   */
  @Nonnull
  public JDoLoop _do (@Nonnull final IJExpression test)
  {
    return _insert (new JDoLoop (test));
  }

  /**
   * Create a Try statement and add it to this block
   *
   * @return Newly generated Try statement
   */
  @Nonnull
  public JTryBlock _try ()
  {
    return _insert (new JTryBlock ());
  }

  /**
   * Create a return statement and add it to this block
   */
  @Nonnull
  public JReturn _return ()
  {
    return _insert (new JReturn (null));
  }

  /**
   * Create a return statement and add it to this block
   */
  @Nonnull
  public JReturn _return (@Nullable final IJExpression exp)
  {
    return _insert (new JReturn (exp));
  }

  /**
   * Create a throw statement and add it to this block
   */
  @Nonnull
  public JThrow _throw (@Nonnull final IJExpression exp)
  {
    return _insert (new JThrow (exp));
  }

  /**
   * Create a break statement and add it to this block
   */
  @Nonnull
  public JBreak _break ()
  {
    return _break ((JLabel) null);
  }

  @Nonnull
  public JBreak _break (@Nullable final JLabel label)
  {
    return _insert (new JBreak (label));
  }

  /**
   * Create a label, which can be referenced from <code>continue</code> and
   * <code>break</code> statements.
   */
  @Nonnull
  public JLabel label (@Nonnull final String name)
  {
    final JLabel l = new JLabel (name);
    _insert (l);
    return l;
  }

  @Nonnull
  public JContinue _continue ()
  {
    return _continue (null);
  }

  /**
   * Create a continue statement and add it to this block
   */
  @Nonnull
  public JContinue _continue (@Nullable final JLabel label)
  {
    return _insert (new JContinue (label));
  }

  /**
   * Create a sub-block and add it to this block
   */
  @Nonnull
  public JBlock block ()
  {
    return _insert (new JBlock (true, true));
  }

  /**
   * Creates an enhanced For statement based on j2se 1.5 JLS and add it to this
   * block
   *
   * @return Newly generated enhanced For statement per j2se 1.5 specification
   */
  @Nonnull
  public JForEach forEach (@Nonnull final AbstractJType aVarType,
                           @Nonnull final String sName,
                           @Nonnull final IJExpression aCollection)
  {
    return _insert (new JForEach (aVarType, sName, aCollection));
  }

  /**
   * Create a synchronized block statement and add it to this block
   *
   * @param aExpr
   *        The expression to synchronize on. May not be <code>null</code>.
   * @return Newly generated synchronized block. Never <code>null</code>.
   * @since 2.7.10
   */
  @Nonnull
  public JSynchronizedBlock synchronizedBlock (@Nonnull final IJExpression aExpr)
  {
    return _insert (new JSynchronizedBlock (aExpr));
  }

  /**
   * Creates a "literal" statement directly.
   * <p>
   * Specified string is printed as-is. This is useful as a short-cut.
   * <p>
   * For example, you can invoke this method as:
   * <code>directStatement("a=b+c;")</code>.
   *
   * @param sSource
   *        The source code to state. May not be <code>null</code>.
   * @return The created direct statement.
   */
  @Nonnull
  public IJStatement directStatement (@Nonnull final String sSource)
  {
    final JDirectStatement aStatement = new JDirectStatement (sSource);
    add (aStatement);
    return aStatement;
  }

  public void generate (@Nonnull final JFormatter f)
  {
    if (m_BracesRequired)
    {
      f.print ('{');
      f.newline ();
    }
    if (m_bIndentRequire)
      f.indent ();
    generateBody (f);
    if (m_bIndentRequire)
      f.outdent ();
    if (m_BracesRequired)
      f.print ('}');
  }

  void generateBody (@Nonnull final JFormatter f)
  {
    for (final Object aContentElement : m_aContentList)
    {
      if (aContentElement instanceof IJDeclaration)
        f.declaration ((IJDeclaration) aContentElement);
      else
        f.statement ((IJStatement) aContentElement);
    }
  }

  public void state (@Nonnull final JFormatter f)
  {
    f.generable (this);
    if (m_BracesRequired)
      f.newline ();
  }
}
