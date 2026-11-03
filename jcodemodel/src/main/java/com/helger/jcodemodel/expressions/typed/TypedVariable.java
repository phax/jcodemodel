/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2026 Philip Helger + contributors
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
package com.helger.jcodemodel.expressions.typed;

import java.util.function.BiFunction;

import org.jspecify.annotations.NonNull;

import com.helger.base.enforce.ValueEnforcer;
import com.helger.jcodemodel.IJAssignmentTarget;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JAssignment;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;

/**
 * Something that can be read and assigned: a local variable, a parameter, a field or an array
 * component. Reading returns the mirror <code>M</code>, assigning requires the parameter mirror
 * <code>P</code>, so assignment conversion follows the same rules as method invocation conversion.
 * <p>
 * Compound assignments (<code>+=</code> etc.) contain an implicit cast to the variable type. The
 * operands they accept are declared by the mirror via {@link ICompoundArithmeticTarget},
 * {@link ICompoundBitwiseTarget} and {@link ICompoundShiftTarget}.
 * <p>
 * Not modelled: the narrowing of constant expressions (<code>byte b = 1;</code>).
 *
 * @param <M>
 *        The typed mirror of the value.
 * @param <P>
 *        The parameter mirror of the variable type.
 */
public class TypedVariable <M extends ITypedExpression <?>, P extends ITypedExpression <?>>
{
  private final TypedType <M, P> m_aType;
  private final IJAssignmentTarget m_aTarget;

  protected TypedVariable (@NonNull final TypedType <M, P> aType, @NonNull final IJAssignmentTarget aTarget)
  {
    ValueEnforcer.notNull (aType, "Type");
    ValueEnforcer.notNull (aTarget, "Target");
    m_aType = aType;
    m_aTarget = aTarget;
  }

  @NonNull
  private static JAssignment _compound (@NonNull final TypedVariable <?, ?> aVar,
                                        @NonNull final BiFunction <IJAssignmentTarget, IJExpression, JAssignment> aOp,
                                        @NonNull final Object aValue)
  {
    // The operand mirrors of all compound targets are typed expressions
    return aOp.apply (aVar.m_aTarget, ((ITypedExpression <?>) aValue).raw ());
  }

  @NonNull
  public static <M extends ITypedExpression <?>, P extends ITypedExpression <?>> TypedVariable <M, P> of (@NonNull final TypedType <M, P> aType,
                                                                                                          @NonNull final IJAssignmentTarget aTarget)
  {
    return new TypedVariable <> (aType, aTarget);
  }

  /**
   * Declare a local variable with an initial value.
   *
   * @param <M>
   *        The typed mirror of the value.
   * @param <P>
   *        The parameter mirror of the variable type.
   * @param aCM
   *        The code model. May not be <code>null</code>.
   * @param aBlock
   *        The block to declare the variable in. May not be <code>null</code>.
   * @param aType
   *        The variable type. May not be <code>null</code>.
   * @param sName
   *        The variable name. May neither be <code>null</code> nor empty.
   * @param aInit
   *        The initial value. May not be <code>null</code>.
   * @return The variable. Never <code>null</code>.
   */
  @NonNull
  public static <M extends ITypedExpression <?>, P extends ITypedExpression <?>> TypedVariable <M, P> decl (@NonNull final JCodeModel aCM,
                                                                                                            @NonNull final JBlock aBlock,
                                                                                                            @NonNull final TypedType <M, P> aType,
                                                                                                            @NonNull final String sName,
                                                                                                            @NonNull final P aInit)
  {
    return of (aType, aBlock.decl (aType.type (aCM), sName, aInit.raw ()));
  }

  /**
   * Add a parameter to a method.
   *
   * @param <M>
   *        The typed mirror of the value.
   * @param <P>
   *        The parameter mirror of the variable type.
   * @param aMethod
   *        The method to add the parameter to. May not be <code>null</code>.
   * @param aType
   *        The parameter type. May not be <code>null</code>.
   * @param sName
   *        The parameter name. May neither be <code>null</code> nor empty.
   * @return The parameter as variable. Never <code>null</code>.
   */
  @NonNull
  public static <M extends ITypedExpression <?>, P extends ITypedExpression <?>> TypedVariable <M, P> param (@NonNull final JMethod aMethod,
                                                                                                             @NonNull final TypedType <M, P> aType,
                                                                                                             @NonNull final String sName)
  {
    return of (aType, aMethod.param (aType.type (aMethod.owner ()), sName));
  }

  /**
   * @return The untyped assignment target. Never <code>null</code>.
   */
  @NonNull
  public IJAssignmentTarget target ()
  {
    return m_aTarget;
  }

  /**
   * @return The current value. Never <code>null</code>.
   */
  @NonNull
  public M get ()
  {
    return m_aType.wrap (m_aTarget);
  }

  /**
   * Add the statement <code>this = value;</code> to a block.
   *
   * @param aBlock
   *        The block to add the statement to. May not be <code>null</code>.
   * @param aValue
   *        The value to assign. May not be <code>null</code>.
   * @return The passed block. Never <code>null</code>.
   */
  @NonNull
  public JBlock assign (@NonNull final JBlock aBlock, @NonNull final P aValue)
  {
    return aBlock.assign (m_aTarget, aValue.raw ());
  }

  /**
   * @param aValue
   *        The value to assign. May not be <code>null</code>.
   * @return The expression <code>(this = value)</code>, which has the type of the variable.
   */
  @NonNull
  public M assignExpr (@NonNull final P aValue)
  {
    return m_aType.wrap (JExpr.assign (m_aTarget, aValue.raw ()));
  }

  /**
   * <code>v++</code> on a numeric variable. The result has the type of the variable, e.g.
   * <code>char</code> for a <code>char</code> variable.
   *
   * @param <M>
   *        The typed mirror of the value.
   * @param aVar
   *        The variable. May not be <code>null</code>.
   * @return <code>v++</code>
   */
  @NonNull
  public static <M extends IDoubleConvertible <?>> M postincr (@NonNull final TypedVariable <M, ?> aVar)
  {
    return aVar.m_aType.wrap (JExpr.incr (aVar.m_aTarget));
  }

  /**
   * @param <M>
   *        The typed mirror of the value.
   * @param aVar
   *        The variable. May not be <code>null</code>.
   * @return <code>++v</code>
   * @see #postincr(TypedVariable)
   */
  @NonNull
  public static <M extends IDoubleConvertible <?>> M preincr (@NonNull final TypedVariable <M, ?> aVar)
  {
    return aVar.m_aType.wrap (JExpr.preincr (aVar.m_aTarget));
  }

  /**
   * @param <M>
   *        The typed mirror of the value.
   * @param aVar
   *        The variable. May not be <code>null</code>.
   * @return <code>v--</code>
   * @see #postincr(TypedVariable)
   */
  @NonNull
  public static <M extends IDoubleConvertible <?>> M postdecr (@NonNull final TypedVariable <M, ?> aVar)
  {
    return aVar.m_aType.wrap (JExpr.decr (aVar.m_aTarget));
  }

  /**
   * @param <M>
   *        The typed mirror of the value.
   * @param aVar
   *        The variable. May not be <code>null</code>.
   * @return <code>--v</code>
   * @see #postincr(TypedVariable)
   */
  @NonNull
  public static <M extends IDoubleConvertible <?>> M predecr (@NonNull final TypedVariable <M, ?> aVar)
  {
    return aVar.m_aType.wrap (JExpr.predecr (aVar.m_aTarget));
  }

  /**
   * Add the statement <code>v += value;</code> to a block.
   *
   * @param <M>
   *        The typed mirror of the variable.
   * @param <C>
   *        The operand mirror accepted by the variable, see {@link ICompoundArithmeticTarget}.
   * @param aBlock
   *        The block to add the statement to. May not be <code>null</code>.
   * @param aVar
   *        The variable. May not be <code>null</code>.
   * @param aValue
   *        The operand. May not be <code>null</code>.
   * @return The passed block. Never <code>null</code>.
   */
  @NonNull
  public static <M extends ITypedExpression <?> & ICompoundArithmeticTarget <C>, C> JBlock plusAssign (@NonNull final JBlock aBlock,
                                                                                                       @NonNull final TypedVariable <M, ?> aVar,
                                                                                                       @NonNull final C aValue)
  {
    return aBlock.add (_compound (aVar, JExpr::assignPlus, aValue));
  }

  /**
   * @param <M>
   *        The typed mirror of the variable.
   * @param <C>
   *        The operand mirror accepted by the variable, see {@link ICompoundArithmeticTarget}.
   * @param aVar
   *        The variable. May not be <code>null</code>.
   * @param aValue
   *        The operand. May not be <code>null</code>.
   * @return The expression <code>(v += value)</code>, which has the type of the variable.
   */
  @NonNull
  public static <M extends ITypedExpression <?> & ICompoundArithmeticTarget <C>, C> M plusAssignExpr (@NonNull final TypedVariable <M, ?> aVar,
                                                                                                      @NonNull final C aValue)
  {
    return aVar.m_aType.wrap (_compound (aVar, JExpr::assignPlus, aValue));
  }

  /**
   * Add the statement <code>v -= value;</code> to a block.
   *
   * @param <M>
   *        The typed mirror of the variable.
   * @param <C>
   *        The operand mirror accepted by the variable, see {@link ICompoundArithmeticTarget}.
   * @param aBlock
   *        The block to add the statement to. May not be <code>null</code>.
   * @param aVar
   *        The variable. May not be <code>null</code>.
   * @param aValue
   *        The operand. May not be <code>null</code>.
   * @return The passed block. Never <code>null</code>.
   */
  @NonNull
  public static <M extends ITypedExpression <?> & ICompoundArithmeticTarget <C>, C> JBlock minusAssign (@NonNull final JBlock aBlock,
                                                                                                        @NonNull final TypedVariable <M, ?> aVar,
                                                                                                        @NonNull final C aValue)
  {
    return aBlock.add (_compound (aVar, JExpr::assignMinus, aValue));
  }

  /**
   * @param <M>
   *        The typed mirror of the variable.
   * @param <C>
   *        The operand mirror accepted by the variable, see {@link ICompoundArithmeticTarget}.
   * @param aVar
   *        The variable. May not be <code>null</code>.
   * @param aValue
   *        The operand. May not be <code>null</code>.
   * @return The expression <code>(v -= value)</code>, which has the type of the variable.
   */
  @NonNull
  public static <M extends ITypedExpression <?> & ICompoundArithmeticTarget <C>, C> M minusAssignExpr (@NonNull final TypedVariable <M, ?> aVar,
                                                                                                       @NonNull final C aValue)
  {
    return aVar.m_aType.wrap (_compound (aVar, JExpr::assignMinus, aValue));
  }

  /**
   * Add the statement <code>v *= value;</code> to a block.
   *
   * @param <M>
   *        The typed mirror of the variable.
   * @param <C>
   *        The operand mirror accepted by the variable, see {@link ICompoundArithmeticTarget}.
   * @param aBlock
   *        The block to add the statement to. May not be <code>null</code>.
   * @param aVar
   *        The variable. May not be <code>null</code>.
   * @param aValue
   *        The operand. May not be <code>null</code>.
   * @return The passed block. Never <code>null</code>.
   */
  @NonNull
  public static <M extends ITypedExpression <?> & ICompoundArithmeticTarget <C>, C> JBlock mulAssign (@NonNull final JBlock aBlock,
                                                                                                      @NonNull final TypedVariable <M, ?> aVar,
                                                                                                      @NonNull final C aValue)
  {
    return aBlock.add (_compound (aVar, JExpr::assignTimes, aValue));
  }

  /**
   * @param <M>
   *        The typed mirror of the variable.
   * @param <C>
   *        The operand mirror accepted by the variable, see {@link ICompoundArithmeticTarget}.
   * @param aVar
   *        The variable. May not be <code>null</code>.
   * @param aValue
   *        The operand. May not be <code>null</code>.
   * @return The expression <code>(v *= value)</code>, which has the type of the variable.
   */
  @NonNull
  public static <M extends ITypedExpression <?> & ICompoundArithmeticTarget <C>, C> M mulAssignExpr (@NonNull final TypedVariable <M, ?> aVar,
                                                                                                     @NonNull final C aValue)
  {
    return aVar.m_aType.wrap (_compound (aVar, JExpr::assignTimes, aValue));
  }

  /**
   * Add the statement <code>v /= value;</code> to a block.
   *
   * @param <M>
   *        The typed mirror of the variable.
   * @param <C>
   *        The operand mirror accepted by the variable, see {@link ICompoundArithmeticTarget}.
   * @param aBlock
   *        The block to add the statement to. May not be <code>null</code>.
   * @param aVar
   *        The variable. May not be <code>null</code>.
   * @param aValue
   *        The operand. May not be <code>null</code>.
   * @return The passed block. Never <code>null</code>.
   */
  @NonNull
  public static <M extends ITypedExpression <?> & ICompoundArithmeticTarget <C>, C> JBlock divAssign (@NonNull final JBlock aBlock,
                                                                                                      @NonNull final TypedVariable <M, ?> aVar,
                                                                                                      @NonNull final C aValue)
  {
    return aBlock.add (_compound (aVar, JExpr::assignDivide, aValue));
  }

  /**
   * @param <M>
   *        The typed mirror of the variable.
   * @param <C>
   *        The operand mirror accepted by the variable, see {@link ICompoundArithmeticTarget}.
   * @param aVar
   *        The variable. May not be <code>null</code>.
   * @param aValue
   *        The operand. May not be <code>null</code>.
   * @return The expression <code>(v /= value)</code>, which has the type of the variable.
   */
  @NonNull
  public static <M extends ITypedExpression <?> & ICompoundArithmeticTarget <C>, C> M divAssignExpr (@NonNull final TypedVariable <M, ?> aVar,
                                                                                                     @NonNull final C aValue)
  {
    return aVar.m_aType.wrap (_compound (aVar, JExpr::assignDivide, aValue));
  }

  /**
   * Add the statement <code>v %= value;</code> to a block.
   *
   * @param <M>
   *        The typed mirror of the variable.
   * @param <C>
   *        The operand mirror accepted by the variable, see {@link ICompoundArithmeticTarget}.
   * @param aBlock
   *        The block to add the statement to. May not be <code>null</code>.
   * @param aVar
   *        The variable. May not be <code>null</code>.
   * @param aValue
   *        The operand. May not be <code>null</code>.
   * @return The passed block. Never <code>null</code>.
   */
  @NonNull
  public static <M extends ITypedExpression <?> & ICompoundArithmeticTarget <C>, C> JBlock modAssign (@NonNull final JBlock aBlock,
                                                                                                      @NonNull final TypedVariable <M, ?> aVar,
                                                                                                      @NonNull final C aValue)
  {
    return aBlock.add (_compound (aVar, JExpr::assignMod, aValue));
  }

  /**
   * @param <M>
   *        The typed mirror of the variable.
   * @param <C>
   *        The operand mirror accepted by the variable, see {@link ICompoundArithmeticTarget}.
   * @param aVar
   *        The variable. May not be <code>null</code>.
   * @param aValue
   *        The operand. May not be <code>null</code>.
   * @return The expression <code>(v %= value)</code>, which has the type of the variable.
   */
  @NonNull
  public static <M extends ITypedExpression <?> & ICompoundArithmeticTarget <C>, C> M modAssignExpr (@NonNull final TypedVariable <M, ?> aVar,
                                                                                                     @NonNull final C aValue)
  {
    return aVar.m_aType.wrap (_compound (aVar, JExpr::assignMod, aValue));
  }

  /**
   * Add the statement <code>v &amp;= value;</code> to a block.
   *
   * @param <M>
   *        The typed mirror of the variable.
   * @param <C>
   *        The operand mirror accepted by the variable, see {@link ICompoundBitwiseTarget}.
   * @param aBlock
   *        The block to add the statement to. May not be <code>null</code>.
   * @param aVar
   *        The variable. May not be <code>null</code>.
   * @param aValue
   *        The operand. May not be <code>null</code>.
   * @return The passed block. Never <code>null</code>.
   */
  @NonNull
  public static <M extends ITypedExpression <?> & ICompoundBitwiseTarget <C>, C> JBlock bandAssign (@NonNull final JBlock aBlock,
                                                                                                    @NonNull final TypedVariable <M, ?> aVar,
                                                                                                    @NonNull final C aValue)
  {
    return aBlock.add (_compound (aVar, JExpr::assignBand, aValue));
  }

  /**
   * @param <M>
   *        The typed mirror of the variable.
   * @param <C>
   *        The operand mirror accepted by the variable, see {@link ICompoundBitwiseTarget}.
   * @param aVar
   *        The variable. May not be <code>null</code>.
   * @param aValue
   *        The operand. May not be <code>null</code>.
   * @return The expression <code>(v &amp;= value)</code>, which has the type of the variable.
   */
  @NonNull
  public static <M extends ITypedExpression <?> & ICompoundBitwiseTarget <C>, C> M bandAssignExpr (@NonNull final TypedVariable <M, ?> aVar,
                                                                                                   @NonNull final C aValue)
  {
    return aVar.m_aType.wrap (_compound (aVar, JExpr::assignBand, aValue));
  }

  /**
   * Add the statement <code>v |= value;</code> to a block.
   *
   * @param <M>
   *        The typed mirror of the variable.
   * @param <C>
   *        The operand mirror accepted by the variable, see {@link ICompoundBitwiseTarget}.
   * @param aBlock
   *        The block to add the statement to. May not be <code>null</code>.
   * @param aVar
   *        The variable. May not be <code>null</code>.
   * @param aValue
   *        The operand. May not be <code>null</code>.
   * @return The passed block. Never <code>null</code>.
   */
  @NonNull
  public static <M extends ITypedExpression <?> & ICompoundBitwiseTarget <C>, C> JBlock borAssign (@NonNull final JBlock aBlock,
                                                                                                   @NonNull final TypedVariable <M, ?> aVar,
                                                                                                   @NonNull final C aValue)
  {
    return aBlock.add (_compound (aVar, JExpr::assignBor, aValue));
  }

  /**
   * @param <M>
   *        The typed mirror of the variable.
   * @param <C>
   *        The operand mirror accepted by the variable, see {@link ICompoundBitwiseTarget}.
   * @param aVar
   *        The variable. May not be <code>null</code>.
   * @param aValue
   *        The operand. May not be <code>null</code>.
   * @return The expression <code>(v |= value)</code>, which has the type of the variable.
   */
  @NonNull
  public static <M extends ITypedExpression <?> & ICompoundBitwiseTarget <C>, C> M borAssignExpr (@NonNull final TypedVariable <M, ?> aVar,
                                                                                                  @NonNull final C aValue)
  {
    return aVar.m_aType.wrap (_compound (aVar, JExpr::assignBor, aValue));
  }

  /**
   * Add the statement <code>v ^= value;</code> to a block.
   *
   * @param <M>
   *        The typed mirror of the variable.
   * @param <C>
   *        The operand mirror accepted by the variable, see {@link ICompoundBitwiseTarget}.
   * @param aBlock
   *        The block to add the statement to. May not be <code>null</code>.
   * @param aVar
   *        The variable. May not be <code>null</code>.
   * @param aValue
   *        The operand. May not be <code>null</code>.
   * @return The passed block. Never <code>null</code>.
   */
  @NonNull
  public static <M extends ITypedExpression <?> & ICompoundBitwiseTarget <C>, C> JBlock xorAssign (@NonNull final JBlock aBlock,
                                                                                                   @NonNull final TypedVariable <M, ?> aVar,
                                                                                                   @NonNull final C aValue)
  {
    return aBlock.add (_compound (aVar, JExpr::assignXor, aValue));
  }

  /**
   * @param <M>
   *        The typed mirror of the variable.
   * @param <C>
   *        The operand mirror accepted by the variable, see {@link ICompoundBitwiseTarget}.
   * @param aVar
   *        The variable. May not be <code>null</code>.
   * @param aValue
   *        The operand. May not be <code>null</code>.
   * @return The expression <code>(v ^= value)</code>, which has the type of the variable.
   */
  @NonNull
  public static <M extends ITypedExpression <?> & ICompoundBitwiseTarget <C>, C> M xorAssignExpr (@NonNull final TypedVariable <M, ?> aVar,
                                                                                                  @NonNull final C aValue)
  {
    return aVar.m_aType.wrap (_compound (aVar, JExpr::assignXor, aValue));
  }

  /**
   * Add the statement <code>v &lt;&lt;= value;</code> to a block.
   *
   * @param <M>
   *        The typed mirror of the variable.
   * @param <C>
   *        The operand mirror accepted by the variable, see {@link ICompoundShiftTarget}.
   * @param aBlock
   *        The block to add the statement to. May not be <code>null</code>.
   * @param aVar
   *        The variable. May not be <code>null</code>.
   * @param aValue
   *        The operand. May not be <code>null</code>.
   * @return The passed block. Never <code>null</code>.
   */
  @NonNull
  public static <M extends ITypedExpression <?> & ICompoundShiftTarget <C>, C> JBlock shlAssign (@NonNull final JBlock aBlock,
                                                                                                 @NonNull final TypedVariable <M, ?> aVar,
                                                                                                 @NonNull final C aValue)
  {
    return aBlock.add (_compound (aVar, JExpr::assignShl, aValue));
  }

  /**
   * @param <M>
   *        The typed mirror of the variable.
   * @param <C>
   *        The operand mirror accepted by the variable, see {@link ICompoundShiftTarget}.
   * @param aVar
   *        The variable. May not be <code>null</code>.
   * @param aValue
   *        The operand. May not be <code>null</code>.
   * @return The expression <code>(v &lt;&lt;= value)</code>, which has the type of the variable.
   */
  @NonNull
  public static <M extends ITypedExpression <?> & ICompoundShiftTarget <C>, C> M shlAssignExpr (@NonNull final TypedVariable <M, ?> aVar,
                                                                                                @NonNull final C aValue)
  {
    return aVar.m_aType.wrap (_compound (aVar, JExpr::assignShl, aValue));
  }

  /**
   * Add the statement <code>v &gt;&gt;= value;</code> to a block.
   *
   * @param <M>
   *        The typed mirror of the variable.
   * @param <C>
   *        The operand mirror accepted by the variable, see {@link ICompoundShiftTarget}.
   * @param aBlock
   *        The block to add the statement to. May not be <code>null</code>.
   * @param aVar
   *        The variable. May not be <code>null</code>.
   * @param aValue
   *        The operand. May not be <code>null</code>.
   * @return The passed block. Never <code>null</code>.
   */
  @NonNull
  public static <M extends ITypedExpression <?> & ICompoundShiftTarget <C>, C> JBlock shrAssign (@NonNull final JBlock aBlock,
                                                                                                 @NonNull final TypedVariable <M, ?> aVar,
                                                                                                 @NonNull final C aValue)
  {
    return aBlock.add (_compound (aVar, JExpr::assignShr, aValue));
  }

  /**
   * @param <M>
   *        The typed mirror of the variable.
   * @param <C>
   *        The operand mirror accepted by the variable, see {@link ICompoundShiftTarget}.
   * @param aVar
   *        The variable. May not be <code>null</code>.
   * @param aValue
   *        The operand. May not be <code>null</code>.
   * @return The expression <code>(v &gt;&gt;= value)</code>, which has the type of the variable.
   */
  @NonNull
  public static <M extends ITypedExpression <?> & ICompoundShiftTarget <C>, C> M shrAssignExpr (@NonNull final TypedVariable <M, ?> aVar,
                                                                                                @NonNull final C aValue)
  {
    return aVar.m_aType.wrap (_compound (aVar, JExpr::assignShr, aValue));
  }

  /**
   * Add the statement <code>v &gt;&gt;&gt;= value;</code> to a block.
   *
   * @param <M>
   *        The typed mirror of the variable.
   * @param <C>
   *        The operand mirror accepted by the variable, see {@link ICompoundShiftTarget}.
   * @param aBlock
   *        The block to add the statement to. May not be <code>null</code>.
   * @param aVar
   *        The variable. May not be <code>null</code>.
   * @param aValue
   *        The operand. May not be <code>null</code>.
   * @return The passed block. Never <code>null</code>.
   */
  @NonNull
  public static <M extends ITypedExpression <?> & ICompoundShiftTarget <C>, C> JBlock shrzAssign (@NonNull final JBlock aBlock,
                                                                                                  @NonNull final TypedVariable <M, ?> aVar,
                                                                                                  @NonNull final C aValue)
  {
    return aBlock.add (_compound (aVar, JExpr::assignShrz, aValue));
  }

  /**
   * @param <M>
   *        The typed mirror of the variable.
   * @param <C>
   *        The operand mirror accepted by the variable, see {@link ICompoundShiftTarget}.
   * @param aVar
   *        The variable. May not be <code>null</code>.
   * @param aValue
   *        The operand. May not be <code>null</code>.
   * @return The expression <code>(v &gt;&gt;&gt;= value)</code>, which has the type of the variable.
   */
  @NonNull
  public static <M extends ITypedExpression <?> & ICompoundShiftTarget <C>, C> M shrzAssignExpr (@NonNull final TypedVariable <M, ?> aVar,
                                                                                                 @NonNull final C aValue)
  {
    return aVar.m_aType.wrap (_compound (aVar, JExpr::assignShrz, aValue));
  }

  /**
   * Add the statement <code>s += value;</code> to a <code>String</code> variable, which accepts
   * any operand that is not <code>void</code>.
   *
   * @param aBlock
   *        The block to add the statement to. May not be <code>null</code>.
   * @param aVar
   *        The variable. May not be <code>null</code>.
   * @param aValue
   *        The operand. May not be <code>null</code>.
   * @return The passed block. Never <code>null</code>.
   */
  @NonNull
  public static JBlock plusAssign (@NonNull final JBlock aBlock,
                                   @NonNull final TypedVariable <StringExpression, ?> aVar,
                                   @NonNull final ITypedExpression <?> aValue)
  {
    return aBlock.add (_compound (aVar, JExpr::assignPlus, aValue));
  }

  /**
   * @param aVar
   *        The <code>String</code> variable. May not be <code>null</code>.
   * @param aValue
   *        The operand, which may be anything but <code>void</code>. May not be <code>null</code>.
   * @return The expression <code>(s += value)</code> of type <code>String</code>.
   */
  @NonNull
  public static StringExpression plusAssignExpr (@NonNull final TypedVariable <StringExpression, ?> aVar,
                                                 @NonNull final ITypedExpression <?> aValue)
  {
    return aVar.m_aType.wrap (_compound (aVar, JExpr::assignPlus, aValue));
  }
}
