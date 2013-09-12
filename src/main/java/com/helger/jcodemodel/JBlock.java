/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
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

import javax.annotation.Nonnull;

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
  private final List <Object> content = new ArrayList <Object> ();

  /**
   * Whether or not this block must be braced and indented
   */
  private boolean bracesRequired = true;
  private boolean indentRequired = true;

  /**
   * Current position.
   */
  private int pos;

  protected JBlock ()
  {
    this (true, true);
  }

  protected JBlock (final boolean bracesRequired, final boolean indentRequired)
  {
    this.bracesRequired = bracesRequired;
    this.indentRequired = indentRequired;
  }

  /**
   * Returns a read-only view of {@link IJStatement}s and {@link IJDeclaration} in
   * this block.
   */
  @Nonnull
  public List <Object> getContents ()
  {
    return Collections.unmodifiableList (content);
  }

  @Nonnull
  private <T> T _insert (@Nonnull final T statementOrDeclaration)
  {
    content.add (pos, statementOrDeclaration);
    pos++;
    return statementOrDeclaration;
  }

  public void remove (final Object o)
  {
    content.remove (o);
  }

  public void remove (final int index)
  {
    content.remove (index);
  }

  public void removeAll ()
  {
    content.clear ();
  }

  /**
   * Gets the current position to which new statements will be inserted. For
   * example if the value is 0, newly created instructions will be inserted at
   * the very beginning of the block.
   * 
   * @see #pos(int)
   */
  public int pos ()
  {
    return pos;
  }

  /**
   * Sets the current position.
   * 
   * @return the old value of the current position.
   * @throws IllegalArgumentException
   *         if the new position value is illegal.
   * @see #pos()
   */
  public int pos (final int newPos)
  {
    final int r = pos;
    if (newPos > content.size () || newPos < 0)
      throw new IllegalArgumentException ();
    pos = newPos;

    return r;
  }

  /**
   * Returns true if this block is empty and does not contain any statement.
   */
  public boolean isEmpty ()
  {
    return content.isEmpty ();
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
  public JVar decl (final AbstractJType type, final String name)
  {
    return decl (JMod.NONE, type, name, null);
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
  public JVar decl (final AbstractJType type, final String name, final IJExpression init)
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
  public JVar decl (final int mods, final AbstractJType type, final String name, final IJExpression init)
  {
    final JVar v = new JVar (JMods.forVar (mods), type, name, init);
    _insert (v);
    bracesRequired = true;
    indentRequired = true;
    return v;
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
    _insert (new JAssignment (lhs, exp));
    return this;
  }

  @Nonnull
  public JBlock assignPlus (@Nonnull final IJAssignmentTarget lhs, @Nonnull final IJExpression exp)
  {
    _insert (new JAssignment (lhs, exp, "+"));
    return this;
  }

  /**
   * Creates an invocation statement and adds it to this block.
   * 
   * @param expr
   *        JExpression evaluating to the class or object upon which the named
   *        method will be invoked
   * @param method
   *        Name of method to invoke
   * @return Newly generated JInvocation
   */
  @Nonnull
  public JInvocation invoke (final IJExpression expr, final String method)
  {
    final JInvocation i = new JInvocation (expr, method);
    _insert (i);
    return i;
  }

  /**
   * Creates an invocation statement and adds it to this block.
   * 
   * @param expr
   *        JExpression evaluating to the class or object upon which the method
   *        will be invoked
   * @param method
   *        JMethod to invoke
   * @return Newly generated JInvocation
   */
  public JInvocation invoke (final IJExpression expr, final JMethod method)
  {
    return _insert (new JInvocation (expr, method));
  }

  /**
   * Creates a static invocation statement.
   */
  public JInvocation staticInvoke (final AbstractJClass type, final String method)
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
  public JInvocation invoke (final String method)
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
  public JInvocation invoke (final JMethod method)
  {
    return _insert (new JInvocation ((IJExpression) null, method));
  }

  /**
   * Adds a statement to this block
   * 
   * @param s
   *        JStatement to be added
   * @return This block
   */
  public JBlock add (final IJStatement s)
  { // ## Needed?
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
  public JConditional _if (final IJExpression expr)
  {
    return _insert (new JConditional (expr));
  }

  /**
   * Create a For statement and add it to this block
   * 
   * @return Newly generated For statement
   */
  public JForLoop _for ()
  {
    return _insert (new JForLoop ());
  }

  /**
   * Create a While statement and add it to this block
   * 
   * @return Newly generated While statement
   */
  public JWhileLoop _while (final IJExpression test)
  {
    return _insert (new JWhileLoop (test));
  }

  /**
   * Create a switch/case statement and add it to this block
   */
  public JSwitch _switch (final IJExpression test)
  {
    return _insert (new JSwitch (test));
  }

  /**
   * Create a Do statement and add it to this block
   * 
   * @return Newly generated Do statement
   */
  public JDoLoop _do (final IJExpression test)
  {
    return _insert (new JDoLoop (test));
  }

  /**
   * Create a Try statement and add it to this block
   * 
   * @return Newly generated Try statement
   */
  public JTryBlock _try ()
  {
    return _insert (new JTryBlock ());
  }

  /**
   * Create a return statement and add it to this block
   */
  public void _return ()
  {
    _insert (new JReturn (null));
  }

  /**
   * Create a return statement and add it to this block
   */
  public void _return (final IJExpression exp)
  {
    _insert (new JReturn (exp));
  }

  /**
   * Create a throw statement and add it to this block
   */
  public void _throw (final IJExpression exp)
  {
    _insert (new JThrow (exp));
  }

  /**
   * Create a break statement and add it to this block
   */
  public void _break ()
  {
    _break (null);
  }

  public void _break (final JLabel label)
  {
    _insert (new JBreak (label));
  }

  /**
   * Create a label, which can be referenced from <code>continue</code> and
   * <code>break</code> statements.
   */
  public JLabel label (final String name)
  {
    final JLabel l = new JLabel (name);
    _insert (l);
    return l;
  }

  /**
   * Create a continue statement and add it to this block
   */
  public void _continue (final JLabel label)
  {
    _insert (new JContinue (label));
  }

  public void _continue ()
  {
    _continue (null);
  }

  /**
   * Create a sub-block and add it to this block
   */
  public JBlock block ()
  {
    final JBlock b = new JBlock ();
    b.bracesRequired = false;
    b.indentRequired = false;
    return _insert (b);
  }

  /**
   * Creates a "literal" statement directly.
   * <p>
   * Specified string is printed as-is. This is useful as a short-cut.
   * <p>
   * For example, you can invoke this method as:
   * <code>directStatement("a=b+c;")</code>.
   */
  public IJStatement directStatement (final String source)
  {
    final IJStatement s = new IJStatement ()
    {
      public void state (final JFormatter f)
      {
        f.print (source).newline ();
      }
    };
    add (s);
    return s;
  }

  public void generate (final JFormatter f)
  {
    if (bracesRequired)
      f.print ('{').newline ();
    if (indentRequired)
      f.indent ();
    generateBody (f);
    if (indentRequired)
      f.outdent ();
    if (bracesRequired)
      f.print ('}');
  }

  void generateBody (final JFormatter f)
  {
    for (final Object o : content)
    {
      if (o instanceof IJDeclaration)
        f.declaration ((IJDeclaration) o);
      else
        f.statement ((IJStatement) o);
    }
  }

  /**
   * Creates an enhanced For statement based on j2se 1.5 JLS and add it to this
   * block
   * 
   * @return Newly generated enhanced For statement per j2se 1.5 specification
   */
  public JForEach forEach (final AbstractJType varType, final String name, final IJExpression collection)
  {
    return _insert (new JForEach (varType, name, collection));

  }

  public void state (final JFormatter f)
  {
    f.generable (this);
    if (bracesRequired)
      f.newline ();
  }

}
