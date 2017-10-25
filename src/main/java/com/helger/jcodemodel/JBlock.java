/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2017 Philip Helger + contributors
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

import com.helger.jcodemodel.util.JCValueEnforcer;

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
  public static final boolean DEFAULT_VIRTUAL_BLOCK = false;
  public static final boolean DEFAULT_BRACES_REQUIRED = true;
  public static final boolean DEFAULT_INDENT_REQUIRED = true;

  /**
   * Declarations and statements contained in this block. Either
   * {@link IJStatement} or {@link IJDeclaration}.
   */
  protected final List <IJObject> m_aContentList = new ArrayList <> ();

  private boolean m_bVirtualBlock = DEFAULT_VIRTUAL_BLOCK;

  /**
   * Whether or not this block must be braced and indented
   */
  private boolean m_bBracesRequired = DEFAULT_BRACES_REQUIRED;
  private boolean m_bIndentRequired = DEFAULT_INDENT_REQUIRED;

  /**
   * Current position.
   */
  private int m_nPos;

  public JBlock ()
  {}

  /**
   * @return <code>true</code> if this is a virtual block never emitting braces
   *         or indent. The default is {@link #DEFAULT_VIRTUAL_BLOCK}
   */
  public boolean virtual ()
  {
    return m_bVirtualBlock;
  }

  /**
   * Mark this block virtual or not. Default is <code>false</code>. Virtual
   * blocks NEVER have braces and are NEVER indented!
   *
   * @param bVirtualBlock
   *        <code>true</code> to make this block a virtual block.
   * @return this for chaining
   */
  @Nonnull
  public JBlock virtual (final boolean bVirtualBlock)
  {
    m_bVirtualBlock = bVirtualBlock;
    return this;
  }

  public boolean bracesRequired ()
  {
    return m_bBracesRequired;
  }

  @Nonnull
  public JBlock bracesRequired (final boolean bBracesRequired)
  {
    m_bBracesRequired = bBracesRequired;
    return this;
  }

  public boolean indentRequired ()
  {
    return m_bIndentRequired;
  }

  @Nonnull
  public JBlock indentRequired (final boolean bIndentRequired)
  {
    m_bIndentRequired = bIndentRequired;
    return this;
  }

  /**
   * @return a read-only view of {@link IJStatement}s and {@link IJDeclaration}
   *         in this block.
   */
  @Nonnull
  public List <IJObject> getContents ()
  {
    return Collections.unmodifiableList (m_aContentList);
  }

  @Nonnull
  protected final <T extends IJObject> T internalInsert (@Nonnull final T aStatementOrDeclaration)
  {
    return internalInsertAt (m_nPos, aStatementOrDeclaration);
  }

  @Nonnull
  protected final <T extends IJObject> T internalInsertAt (final int nIndex, @Nonnull final T aStatementOrDeclaration)
  {
    JCValueEnforcer.isGE0 (nIndex, "Index");
    JCValueEnforcer.notNull (aStatementOrDeclaration, "StatementOrDeclaration");

    m_aContentList.add (nIndex, aStatementOrDeclaration);
    m_nPos++;

    if (aStatementOrDeclaration instanceof JVar)
    {
      m_bBracesRequired = true;
      m_bIndentRequired = true;
    }

    return aStatementOrDeclaration;
  }

  public void remove (final IJObject o)
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
   * @return the current position to which new statements will be inserted. For
   *         example if the value is 0, newly created instructions will be
   *         inserted at the very beginning of the block.
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
   * @param nNewPos
   *        The new position to set
   * @return the old value of the current position.
   * @throws IllegalArgumentException
   *         if the new position value is illegal.
   * @see #pos()
   */
  @Nonnegative
  public int pos (@Nonnegative final int nNewPos)
  {
    JCValueEnforcer.isTrue (nNewPos >= 0 && nNewPos <= m_aContentList.size (),
                            () -> "Illegal position provided: " + nNewPos);

    final int nOldPos = m_nPos;
    m_nPos = nNewPos;
    return nOldPos;
  }

  /**
   * @return <code>true</code> if this block is empty and does not contain any
   *         statement.
   */
  public boolean isEmpty ()
  {
    return m_aContentList.isEmpty ();
  }

  /**
   * @return The number of elements contained in the block. Always &ge; 0.
   */
  @Nonnegative
  public int size ()
  {
    return m_aContentList.size ();
  }

  /**
   * Adds a local variable declaration to this block. This enforces braces and
   * indentation to be enabled!
   *
   * @param aType
   *        JType of the variable
   * @param sName
   *        Name of the variable
   * @return Newly generated {@link JVar}
   */
  @Nonnull
  public JVar decl (@Nonnull final AbstractJType aType, @Nonnull final String sName)
  {
    return decl (JMod.NONE, aType, sName, null);
  }

  /**
   * Adds a local variable declaration to this block. This enforces braces and
   * indentation to be enabled!
   *
   * @param nMods
   *        Modifiers for the variable
   * @param aType
   *        JType of the variable
   * @param sName
   *        Name of the variable
   * @return Newly generated {@link JVar}
   */
  @Nonnull
  public JVar decl (final int nMods, @Nonnull final AbstractJType aType, @Nonnull final String sName)
  {
    return decl (nMods, aType, sName, null);
  }

  /**
   * Adds a local variable declaration to this block. This enforces braces and
   * indentation to be enabled!
   *
   * @param aType
   *        JType of the variable
   * @param sName
   *        Name of the variable
   * @param aInit
   *        Initialization expression for this variable. May be null.
   * @return Newly generated {@link JVar}
   */
  @Nonnull
  public JVar decl (@Nonnull final AbstractJType aType, @Nonnull final String sName, @Nullable final IJExpression aInit)
  {
    return decl (JMod.NONE, aType, sName, aInit);
  }

  /**
   * Adds a local variable declaration to this block. This enforces braces and
   * indentation to be enabled!
   *
   * @param nMods
   *        Modifiers for the variable
   * @param aType
   *        JType of the variable
   * @param sName
   *        Name of the variable
   * @param aInit
   *        Initialization expression for this variable. May be null.
   * @return Newly generated {@link JVar}
   */
  @Nonnull
  public JVar decl (final int nMods,
                    @Nonnull final AbstractJType aType,
                    @Nonnull final String sName,
                    @Nullable final IJExpression aInit)
  {
    final JVar v = new JVar (JMods.forVar (nMods), aType, sName, aInit);
    internalInsert (v);
    return v;
  }

  /**
   * Insert a variable before another element of this block. This enforces
   * braces and indentation to be enabled!
   *
   * @param aVar
   *        The variable to be inserted. May not be <code>null</code>.
   * @param aBefore
   *        The object before the variable should be inserted. If the passed
   *        object is not contained in this block, an
   *        {@link IndexOutOfBoundsException} is thrown.
   * @return this for chaining
   */
  @Nonnull
  public JBlock insertBefore (@Nonnull final JVar aVar, @Nonnull final Object aBefore)
  {
    final int i = m_aContentList.indexOf (aBefore);
    internalInsertAt (i, aVar);
    return this;
  }

  /**
   * Creates an assignment statement and adds it to this block.
   *
   * @param aLhs
   *        Assignable variable or field for left hand side of expression
   * @param aExpr
   *        Right hand side expression
   * @return this for chaining
   */
  @Nonnull
  public JBlock assign (@Nonnull final IJAssignmentTarget aLhs, @Nonnull final IJExpression aExpr)
  {
    internalInsert (JExpr.assign (aLhs, aExpr));
    return this;
  }

  @Nonnull
  public JBlock assignPlus (@Nonnull final IJAssignmentTarget aLhs, @Nonnull final IJExpression aExpr)
  {
    internalInsert (JExpr.assignPlus (aLhs, aExpr));
    return this;
  }

  @Nonnull
  public JBlock assignMinus (@Nonnull final IJAssignmentTarget aLhs, @Nonnull final IJExpression aExpr)
  {
    internalInsert (JExpr.assignMinus (aLhs, aExpr));
    return this;
  }

  @Nonnull
  public JBlock assignTimes (@Nonnull final IJAssignmentTarget aLhs, @Nonnull final IJExpression aExpr)
  {
    internalInsert (JExpr.assignTimes (aLhs, aExpr));
    return this;
  }

  @Nonnull
  public JBlock assignDivide (@Nonnull final IJAssignmentTarget aLhs, @Nonnull final IJExpression aExpr)
  {
    internalInsert (JExpr.assignDivide (aLhs, aExpr));
    return this;
  }

  /**
   * Creates an invocation statement and adds it to this block.
   *
   * @param aExpr
   *        {@link IJExpression} evaluating to the class or object upon which
   *        the named method will be invoked
   * @param sMethod
   *        Name of method to invoke
   * @return Newly generated {@link JInvocation}
   */
  @Nonnull
  public JInvocation invoke (@Nonnull final IJExpression aExpr, @Nonnull final String sMethod)
  {
    return internalInsert (new JInvocation (aExpr, sMethod));
  }

  /**
   * Creates an invocation statement and adds it to this block.
   *
   * @param sMethod
   *        Name of method to invoke on this
   * @return Newly generated {@link JInvocation}
   */
  @Nonnull
  public JInvocation invokeThis (@Nonnull final String sMethod)
  {
    return invoke (JExpr._this (), sMethod);
  }

  /**
   * Explicitly call the super class constructor in this block. This method may
   * only be called as the first call inside a constructor block!
   *
   * @return Newly generated super {@link JInvocation}
   * @since 3.0.1
   */
  @Nonnull
  public JInvocation invokeSuper ()
  {
    return internalInsert (JInvocation._super ());
  }

  /**
   * Creates an invocation statement and adds it to this block.
   *
   * @param aExpr
   *        {@link IJExpression} evaluating to the class or object upon which
   *        the method will be invoked
   * @param aMethod
   *        {@link JMethod} to invoke
   * @return Newly generated {@link JInvocation}
   */
  @Nonnull
  public JInvocation invoke (@Nonnull final IJExpression aExpr, @Nonnull final JMethod aMethod)
  {
    return internalInsert (new JInvocation (aExpr, aMethod));
  }

  /**
   * Creates an invocation statement and adds it to this block.
   *
   * @param aMethod
   *        {@link JMethod} to invoke on this
   * @return Newly generated {@link JInvocation}
   */
  @Nonnull
  public JInvocation invokeThis (@Nonnull final JMethod aMethod)
  {
    return invoke (JExpr._this (), aMethod);
  }

  /**
   * Creates a static invocation statement.
   *
   * @param aType
   *        Type upon which the method should be invoked
   * @param sMethod
   *        Name of method to invoke
   * @return Newly generated {@link JInvocation}
   */
  @Nonnull
  public JInvocation staticInvoke (@Nonnull final AbstractJClass aType, @Nonnull final String sMethod)
  {
    return internalInsert (new JInvocation (aType, sMethod));
  }

  /**
   * Creates an invocation statement and adds it to this block.
   *
   * @param sMethod
   *        Name of method to invoke
   * @return Newly generated {@link JInvocation}
   */
  @Nonnull
  public JInvocation invoke (@Nonnull final String sMethod)
  {
    return internalInsert (new JInvocation ((IJExpression) null, sMethod));
  }

  /**
   * Creates an invocation statement and adds it to this block.
   *
   * @param aMethod
   *        JMethod to invoke
   * @return Newly generated {@link JInvocation}
   */
  @Nonnull
  public JInvocation invoke (@Nonnull final JMethod aMethod)
  {
    return internalInsert (new JInvocation ((IJExpression) null, aMethod));
  }

  @Nonnull
  public JInvocation _new (@Nonnull final AbstractJClass aClass)
  {
    return internalInsert (new JInvocation (aClass));
  }

  @Nonnull
  public JInvocation _new (@Nonnull final AbstractJType aType)
  {
    return internalInsert (new JInvocation (aType));
  }

  /**
   * Adds an arbitrary statement to this block
   *
   * @param aStmt
   *        {@link IJStatement} to be added. May not be <code>null</code>.
   * @return this for chaining
   */
  @Nonnull
  public JBlock add (@Nonnull final IJStatement aStmt)
  {
    internalInsert (aStmt);
    return this;
  }

  /**
   * Adds an empty single line comment
   *
   * @return this for chaining
   */
  @Nonnull
  public JBlock addSingleLineComment ()
  {
    return addSingleLineComment ("");
  }

  /**
   * Adds a single line comment to this block
   *
   * @param sComment
   *        The comment string to be added. <code>null</code> is ignored, empty
   *        string lead to an empty single line comment.
   * @return this for chaining
   */
  @Nonnull
  public JBlock addSingleLineComment (@Nullable final String sComment)
  {
    if (sComment != null)
      internalInsert (new JSingleLineCommentStatement (sComment));
    return this;
  }

  /**
   * Create an If statement and add it to this block
   *
   * @param aTestExpr
   *        {@link IJExpression} to be tested to determine branching
   * @return Newly generated {@link JConditional} statement
   */
  @Nonnull
  public JConditional _if (@Nonnull final IJExpression aTestExpr)
  {
    return internalInsert (new JConditional (aTestExpr));
  }

  /**
   * Create an If statement with the respective then statement and add it to
   * this block
   *
   * @param aTestExpr
   *        {@link IJExpression} to be tested to determine branching
   * @param aThen
   *        The then-block. May not be <code>null</code>.
   * @return Newly generated {@link JConditional} statement
   */
  @Nonnull
  public JConditional _if (@Nonnull final IJExpression aTestExpr, @Nonnull final IJStatement aThen)
  {
    final JConditional aCond = new JConditional (aTestExpr);
    aCond._then ().add (aThen);
    return internalInsert (aCond);
  }

  /**
   * Create an If statement with the respective then and else statements and add
   * it to this block
   *
   * @param aTestExpr
   *        {@link IJExpression} to be tested to determine branching
   * @param aThen
   *        The then-block. May not be <code>null</code>.
   * @param aElse
   *        The else-block. May not be <code>null</code>.
   * @return Newly generated {@link JConditional} statement
   */
  @Nonnull
  public JConditional _if (@Nonnull final IJExpression aTestExpr,
                           @Nonnull final IJStatement aThen,
                           @Nonnull final IJStatement aElse)
  {
    final JConditional aCond = new JConditional (aTestExpr);
    aCond._then ().add (aThen);
    aCond._else ().add (aElse);
    return internalInsert (aCond);
  }

  /**
   * Create a For statement and add it to this block
   *
   * @return Newly generated {@link JForLoop} statement. Never <code>null</code>
   *         .
   */
  @Nonnull
  public JForLoop _for ()
  {
    return internalInsert (new JForLoop ());
  }

  /**
   * Create a While statement and add it to this block
   *
   * @param aTestExpr
   *        Test expression for the while statement
   * @return Newly generated {@link JWhileLoop} statement
   */
  @Nonnull
  public JWhileLoop _while (@Nonnull final IJExpression aTestExpr)
  {
    return internalInsert (new JWhileLoop (aTestExpr));
  }

  /**
   * Create a switch/case statement and add it to this block
   *
   * @param aTestExpr
   *        Test expression for the switch statement
   * @return Newly created {@link JSwitch}
   */
  @Nonnull
  public JSwitch _switch (@Nonnull final IJExpression aTestExpr)
  {
    return internalInsert (new JSwitch (aTestExpr));
  }

  /**
   * Create a Do statement and add it to this block
   *
   * @param aTestExpr
   *        Test expression for the while statement
   * @return Newly generated {@link JDoLoop} statement
   */
  @Nonnull
  public JDoLoop _do (@Nonnull final IJExpression aTestExpr)
  {
    return internalInsert (new JDoLoop (aTestExpr));
  }

  /**
   * Create a Try statement and add it to this block
   *
   * @return Newly generated {@link JTryBlock} statement
   */
  @Nonnull
  public JTryBlock _try ()
  {
    return internalInsert (new JTryBlock ());
  }

  /**
   * Create a return statement and add it to this block
   *
   * @return Newly created {@link JReturn} statement
   */
  @Nonnull
  public JReturn _return ()
  {
    return internalInsert (new JReturn (null));
  }

  /**
   * Create a return statement and add it to this block
   *
   * @param aExpr
   *        Expression to be returned. May be <code>null</code>.
   * @return Newly created {@link JReturn} statement
   */
  @Nonnull
  public JReturn _return (@Nullable final IJExpression aExpr)
  {
    return internalInsert (new JReturn (aExpr));
  }

  /**
   * Create a throw statement and add it to this block
   *
   * @param aExpr
   *        Expression to be thrown
   * @return Newly created {@link JThrow}
   */
  @Nonnull
  public JThrow _throw (@Nonnull final IJExpression aExpr)
  {
    return internalInsert (new JThrow (aExpr));
  }

  /**
   * Create a break statement without a label and add it to this block
   *
   * @return Newly created {@link JBreak}
   */
  @Nonnull
  public JBreak _break ()
  {
    return _break ((JLabel) null);
  }

  /**
   * Create a break statement with an optional label and add it to this block
   *
   * @param aLabel
   *        Optional label for the break statement
   * @return Newly created {@link JBreak}
   */
  @Nonnull
  public JBreak _break (@Nullable final JLabel aLabel)
  {
    return internalInsert (new JBreak (aLabel));
  }

  /**
   * Create a label, which can be referenced from <code>continue</code> and
   * <code>break</code> statements.
   *
   * @param sName
   *        Label name
   * @return Newly created {@link JLabel}
   */
  @Nonnull
  public JLabel label (@Nonnull final String sName)
  {
    final JLabel l = new JLabel (sName);
    internalInsert (l);
    return l;
  }

  /**
   * Create a continue statement without a label and add it to this block
   *
   * @return New {@link JContinue}
   */
  @Nonnull
  public JContinue _continue ()
  {
    return _continue (null);
  }

  /**
   * Create a continue statement with an optional label and add it to this block
   *
   * @param aLabel
   *        Optional label statement.
   * @return New {@link JContinue}
   */
  @Nonnull
  public JContinue _continue (@Nullable final JLabel aLabel)
  {
    return internalInsert (new JContinue (aLabel));
  }

  /**
   * Create a sub-block and add it to this block. By default braces and indent
   * are required.
   *
   * @return New {@link JBlock}
   * @see #block(boolean, boolean)
   * @see #blockSimple()
   */
  @Nonnull
  public JBlock block ()
  {
    return internalInsert (new JBlock ());
  }

  /**
   * Create a sub-block and add it to this block. By default braces and indent
   * are not required.
   *
   * @return New {@link JBlock}
   * @see #block()
   * @see #block(boolean, boolean)
   */
  @Nonnull
  public JBlock blockSimple ()
  {
    return block (false, false);
  }

  /**
   * Create a sub-block and add it to this block. This kind of block will never
   * create braces or indent!
   *
   * @return New {@link JBlock}
   * @see #block()
   * @see #block(boolean, boolean)
   */
  @Nonnull
  public JBlock blockVirtual ()
  {
    return blockSimple ().virtual (true);
  }

  /**
   * Create a sub-block and add it to this block
   *
   * @param bBracesRequired
   *        <code>true</code> if braces should be required
   * @param bIndentRequired
   *        <code>true</code> if indentation is required
   * @return New {@link JBlock}
   * @see #block()
   * @see #blockSimple()
   */
  @Nonnull
  public JBlock block (final boolean bBracesRequired, final boolean bIndentRequired)
  {
    return internalInsert (new JBlock ().bracesRequired (bBracesRequired).indentRequired (bIndentRequired));
  }

  /**
   * Creates an enhanced For statement based on j2se 1.5 JLS and add it to this
   * block
   *
   * @param aVarType
   *        Variable type
   * @param sName
   *        Variable name
   * @param aCollection
   *        Collection to be iterated
   * @return Newly generated enhanced For statement per j2se 1.5 specification
   */
  @Nonnull
  public JForEach forEach (@Nonnull final AbstractJType aVarType,
                           @Nonnull final String sName,
                           @Nonnull final IJExpression aCollection)
  {
    return internalInsert (new JForEach (aVarType, sName, aCollection));
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
    return internalInsert (new JSynchronizedBlock (aExpr));
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
    if (m_bVirtualBlock)
    {
      // Body only
      generateBody (f);
    }
    else
    {
      if (m_bBracesRequired)
      {
        f.print ('{');
        f.newline ();
      }
      if (m_bIndentRequired)
        f.indent ();
      generateBody (f);
      if (m_bIndentRequired)
        f.outdent ();
      if (m_bBracesRequired)
        f.print ('}');
    }
  }

  protected void generateBody (@Nonnull final JFormatter f)
  {
    for (final IJObject aContentElement : m_aContentList)
    {
      if (aContentElement instanceof IJDeclaration)
        f.declaration ((IJDeclaration) aContentElement);
      else
        if (aContentElement instanceof IJStatement)
          f.statement ((IJStatement) aContentElement);
        else
        {
          // For lambda expressions in JLambdaBlock
          f.generable ((IJGenerable) aContentElement);
        }
    }
  }

  public void state (@Nonnull final JFormatter f)
  {
    f.generable (this);
    if (m_bBracesRequired)
      f.newline ();
  }
}
