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
package com.helger.jcodemodel.writer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.jspecify.annotations.NonNull;
import org.junit.BeforeClass;
import org.junit.Test;

import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.base.string.StringHelper;
import com.helger.base.string.StringReplace;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JLambda;
import com.helger.jcodemodel.JLambdaParam;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JOp;
import com.helger.jcodemodel.JVar;
import com.helger.jcodemodel.compile.DynamicClassLoader;
import com.helger.jcodemodel.compile.MemoryCodeWriter;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.vars.JBlockVar;
import com.helger.jcodemodel.writer.settings.Parentheses.EParenthesesStrategy;

/**
 * Generate a large set of expressions, print them with every
 * {@link EParenthesesStrategy}, compile the result in memory and run it. Every strategy may only
 * differ in the parentheses it prints - the generated code must always compile and must always
 * produce exactly the same result. See <code>ParenthesesTest</code> for the textual expectations of
 * a single expression.
 *
 * @author Philip Helger
 */
public final class ParenthesesCompileTest
{
  private static final String CLASS_INT = "parens.IntExpressions";
  private static final String CLASS_BOOL = "parens.BoolExpressions";
  private static final String CLASS_STRING = "parens.StringExpressions";
  private static final String CLASS_LAMBDA = "parens.LambdaExpressions";

  /** The name of the static array field used to test the array component access */
  private static final String FIELD_ARRAY = "ARR";

  private static final Class <?> [] PARAMS_3INT = { int.class, int.class, int.class };
  private static final Class <?> [] PARAMS_3STRING = { String.class, String.class, String.class };
  private static final Class <?> [] PARAMS_1INT = { int.class };

  private static final Object [] [] ARGS_3INT = { { Integer.valueOf (1), Integer.valueOf (2), Integer.valueOf (3) },
                                                  { Integer.valueOf (7), Integer.valueOf (3), Integer.valueOf (5) },
                                                  { Integer.valueOf (-4), Integer.valueOf (6), Integer.valueOf (-2) },
                                                  { Integer.valueOf (13), Integer.valueOf (-7), Integer.valueOf (2) },
                                                  { Integer.valueOf (5), Integer.valueOf (9), Integer.valueOf (4) },
                                                  { Integer.valueOf (Integer.MAX_VALUE),
                                                    Integer.valueOf (-1),
                                                    Integer.valueOf (3) } };
  private static final Object [] [] ARGS_3STRING = { { "ab", "cde", "f" },
                                                     { "Hello", "World", "!" },
                                                     { "x", "y", "zz" } };
  private static final Object [] [] ARGS_1INT = { { Integer.valueOf (1) },
                                                  { Integer.valueOf (5) },
                                                  { Integer.valueOf (-3) } };

  /** The class loader holding the compiled code of one strategy */
  private static final Map <EParenthesesStrategy, DynamicClassLoader> LOADERS = new EnumMap <> (EParenthesesStrategy.class);
  /** The generated source of one strategy, as a map from full class name to source code */
  private static final Map <EParenthesesStrategy, Map <String, String>> SOURCES = new EnumMap <> (EParenthesesStrategy.class);

  private static ICommonsList <String> s_aIntMethods;
  private static ICommonsList <String> s_aBoolMethods;
  private static ICommonsList <String> s_aStringMethods;
  private static ICommonsList <String> s_aLambdaMethods;
  private static String s_sCompilationErrors;

  /**
   * Build one expression from a single operand.
   */
  @FunctionalInterface
  private interface IUnaryFactory
  {
    @NonNull
    IJExpression build (@NonNull IJExpression aOperand);
  }

  /**
   * Build one expression from two operands.
   */
  @FunctionalInterface
  private interface IBinaryFactory
  {
    @NonNull
    IJExpression build (@NonNull IJExpression aLeft, @NonNull IJExpression aRight);
  }

  /**
   * Build one expression from the parameters of the generated method. Used for everything that
   * needs a real variable - like <code>a++</code> or <code>a = 3</code>.
   */
  @FunctionalInterface
  private interface ILeafFactory
  {
    @NonNull
    IJExpression build (@NonNull JVar [] aParams);
  }

  private record NamedUnary (String sName, IUnaryFactory aFactory)
  {}

  private record NamedBinary (String sName, IBinaryFactory aFactory)
  {}

  private record NamedLeaf (String sName, ILeafFactory aFactory)
  {}

  @NonNull
  private static String _sourceKey (@NonNull final String sClassName)
  {
    return StringReplace.replaceAll (sClassName, '.', '/') + ".java";
  }

  /**
   * Add a single method <code>static &lt;ret&gt; name (&lt;param&gt; a, &lt;param&gt; b,
   * &lt;param&gt; c)</code> that returns the provided expression.
   *
   * @return The name of the created method
   */
  @NonNull
  private static String _addMethod (@NonNull final JDefinedClass aClass,
                                    @NonNull final AbstractJType aReturnType,
                                    @NonNull final AbstractJType aParamType,
                                    @NonNull final String sName,
                                    @NonNull final ILeafFactory aBody)
  {
    final JMethod aMethod = aClass.method (JMod.PUBLIC | JMod.STATIC, aReturnType, sName);
    final JVar [] aParams = { aMethod.param (aParamType, "a"),
                              aMethod.param (aParamType, "b"),
                              aMethod.param (aParamType, "c") };
    aMethod.body ()._return (aBody.build (aParams));
    return sName;
  }

  /**
   * Nest every operator into every operand position of every other operator, so that all
   * precedence and associativity combinations of a family are covered.
   *
   * @param aBaseOperands
   *        Creates the three operands of the family from the method parameters. For an
   *        <code>int</code> family these are the parameters themselves, for a
   *        <code>boolean</code> family they are comparisons of the parameters.
   * @return The names of all created methods
   */
  @NonNull
  private static ICommonsList <String> _addAllCombinations (@NonNull final JDefinedClass aClass,
                                                            @NonNull final AbstractJType aReturnType,
                                                            @NonNull final AbstractJType aParamType,
                                                            @NonNull final Function <JVar [], IJExpression []> aBaseOperands,
                                                            @NonNull final List <NamedBinary> aBinaries,
                                                            @NonNull final List <NamedUnary> aUnaries,
                                                            @NonNull final List <NamedLeaf> aLeaves)
  {
    final ICommonsList <String> ret = new CommonsArrayList <> ();

    // A binary operator with a binary operator as its left resp. right operand
    for (final NamedBinary aOuter : aBinaries)
      for (final NamedBinary aInner : aBinaries)
      {
        final String sBase = "bb_" + aOuter.sName () + "_" + aInner.sName ();
        ret.add (_addMethod (aClass, aReturnType, aParamType, sBase + "_l", p -> {
          final IJExpression [] o = aBaseOperands.apply (p);
          return aOuter.aFactory ().build (aInner.aFactory ().build (o[0], o[1]), o[2]);
        }));
        ret.add (_addMethod (aClass, aReturnType, aParamType, sBase + "_r", p -> {
          final IJExpression [] o = aBaseOperands.apply (p);
          return aOuter.aFactory ().build (o[0], aInner.aFactory ().build (o[1], o[2]));
        }));
      }

    // A unary operator around a binary operator and vice versa
    for (final NamedUnary aUnary : aUnaries)
      for (final NamedBinary aBinary : aBinaries)
      {
        ret.add (_addMethod (aClass,
                             aReturnType,
                             aParamType,
                             "ub_" + aUnary.sName () + "_" + aBinary.sName (),
                             p -> {
                               final IJExpression [] o = aBaseOperands.apply (p);
                               return aUnary.aFactory ().build (aBinary.aFactory ().build (o[0], o[1]));
                             }));
        final String sBase = "bu_" + aBinary.sName () + "_" + aUnary.sName ();
        ret.add (_addMethod (aClass, aReturnType, aParamType, sBase + "_l", p -> {
          final IJExpression [] o = aBaseOperands.apply (p);
          return aBinary.aFactory ().build (aUnary.aFactory ().build (o[0]), o[1]);
        }));
        ret.add (_addMethod (aClass, aReturnType, aParamType, sBase + "_r", p -> {
          final IJExpression [] o = aBaseOperands.apply (p);
          return aBinary.aFactory ().build (o[0], aUnary.aFactory ().build (o[1]));
        }));
      }

    // Stacked unary operators - they must never be glued into a single token
    for (final NamedUnary aOuter : aUnaries)
      for (final NamedUnary aInner : aUnaries)
        ret.add (_addMethod (aClass,
                             aReturnType,
                             aParamType,
                             "uu_" + aOuter.sName () + "_" + aInner.sName (),
                             p -> aOuter.aFactory ().build (aInner.aFactory ().build (aBaseOperands.apply (p)[0]))));

    // An operator with a leaf - a literal, an increment or an assignment - as its operand
    for (final NamedBinary aBinary : aBinaries)
      for (final NamedLeaf aLeaf : aLeaves)
      {
        final String sBase = "bl_" + aBinary.sName () + "_" + aLeaf.sName ();
        ret.add (_addMethod (aClass,
                             aReturnType,
                             aParamType,
                             sBase + "_l",
                             p -> aBinary.aFactory ()
                                         .build (aLeaf.aFactory ().build (p), aBaseOperands.apply (p)[2])));
        ret.add (_addMethod (aClass,
                             aReturnType,
                             aParamType,
                             sBase + "_r",
                             p -> aBinary.aFactory ()
                                         .build (aBaseOperands.apply (p)[2], aLeaf.aFactory ().build (p))));
      }
    for (final NamedUnary aUnary : aUnaries)
      for (final NamedLeaf aLeaf : aLeaves)
        ret.add (_addMethod (aClass,
                             aReturnType,
                             aParamType,
                             "ul_" + aUnary.sName () + "_" + aLeaf.sName (),
                             p -> aUnary.aFactory ().build (aLeaf.aFactory ().build (p))));

    return ret;
  }

  @NonNull
  private static ICommonsList <String> _buildIntClass (@NonNull final JCodeModel aCM) throws JCodeModelException
  {
    final JDefinedClass aClass = aCM._class (JMod.PUBLIC, CLASS_INT);
    aClass.field (JMod.PRIVATE | JMod.STATIC | JMod.FINAL,
                  aCM.INT.array (),
                  FIELD_ARRAY,
                  JExpr.newArray (aCM.INT).add (JExpr.lit (3)).add (JExpr.lit (5)));

    final ICommonsList <NamedBinary> aBinaries = new CommonsArrayList <> ();
    aBinaries.add (new NamedBinary ("plus", JOp::plus));
    aBinaries.add (new NamedBinary ("minus", JOp::minus));
    aBinaries.add (new NamedBinary ("mul", JOp::mul));
    aBinaries.add (new NamedBinary ("div", JOp::div));
    aBinaries.add (new NamedBinary ("mod", JOp::mod));
    aBinaries.add (new NamedBinary ("shl", JOp::shl));
    aBinaries.add (new NamedBinary ("shr", JOp::shr));
    aBinaries.add (new NamedBinary ("shrz", JOp::shrz));
    aBinaries.add (new NamedBinary ("band", JOp::band));
    aBinaries.add (new NamedBinary ("bor", JOp::bor));
    aBinaries.add (new NamedBinary ("xor", JOp::xor));
    aBinaries.add (new NamedBinary ("condLt", (x, y) -> JExpr.cond (JOp.lt (x, y), x, y)));
    aBinaries.add (new NamedBinary ("condNe", (x, y) -> JExpr.cond (JOp.ne (x, y), x, y)));
    aBinaries.add (new NamedBinary ("max", (x, y) -> aCM.ref (Math.class).staticInvoke ("max").arg (x).arg (y)));

    final ICommonsList <NamedUnary> aUnaries = new CommonsArrayList <> ();
    aUnaries.add (new NamedUnary ("neg", JOp::minus));
    aUnaries.add (new NamedUnary ("cpl", JOp::complement));
    aUnaries.add (new NamedUnary ("castInt", x -> JExpr.cast (aCM.INT, x)));
    aUnaries.add (new NamedUnary ("castLongInt", x -> JExpr.cast (aCM.INT, JExpr.cast (aCM.LONG, x))));
    aUnaries.add (new NamedUnary ("boxIntValue", x -> JExpr.cast (aCM.ref (Integer.class), x).invoke ("intValue")));
    aUnaries.add (new NamedUnary ("abs", x -> aCM.ref (Math.class).staticInvoke ("abs").arg (x)));
    aUnaries.add (new NamedUnary ("condPos", x -> JExpr.cond (JOp.gt (x, JExpr.lit (0)), x, JOp.minus (x))));
    aUnaries.add (new NamedUnary ("arrayIdx",
                                  x -> JExpr.ref (FIELD_ARRAY).component (JOp.band (x, JExpr.lit (1)))));
    aUnaries.add (new NamedUnary ("toStringLength",
                                  x -> JExpr.cast (aCM.ref (Object.class), x)
                                            .invoke ("toString")
                                            .invoke ("length")));

    final ICommonsList <NamedLeaf> aLeaves = new CommonsArrayList <> ();
    aLeaves.add (new NamedLeaf ("varA", p -> p[0]));
    aLeaves.add (new NamedLeaf ("litNeg", p -> JExpr.lit (-1)));
    aLeaves.add (new NamedLeaf ("litPos", p -> JExpr.lit (7)));
    aLeaves.add (new NamedLeaf ("postincrA", p -> JOp.postincr (p[0])));
    aLeaves.add (new NamedLeaf ("preincrB", p -> JOp.preincr (p[1])));
    aLeaves.add (new NamedLeaf ("postdecrC", p -> JOp.postdecr (p[2])));
    aLeaves.add (new NamedLeaf ("predecrA", p -> JOp.predecr (p[0])));
    aLeaves.add (new NamedLeaf ("assignA", p -> JExpr.assign (p[0], JExpr.lit (3))));
    aLeaves.add (new NamedLeaf ("assignPlusB", p -> JExpr.assignPlus (p[1], JExpr.lit (2))));

    return _addAllCombinations (aClass,
                                aCM.INT,
                                aCM.INT,
                                p -> new IJExpression [] { p[0], p[1], p[2] },
                                aBinaries,
                                aUnaries,
                                aLeaves);
  }

  @NonNull
  private static ICommonsList <String> _buildBoolClass (@NonNull final JCodeModel aCM) throws JCodeModelException
  {
    final JDefinedClass aClass = aCM._class (JMod.PUBLIC, CLASS_BOOL);

    final ICommonsList <NamedBinary> aBinaries = new CommonsArrayList <> ();
    aBinaries.add (new NamedBinary ("cand", JOp::cand));
    aBinaries.add (new NamedBinary ("cor", JOp::cor));
    aBinaries.add (new NamedBinary ("band", JOp::band));
    aBinaries.add (new NamedBinary ("bor", JOp::bor));
    aBinaries.add (new NamedBinary ("xor", JOp::xor));
    aBinaries.add (new NamedBinary ("eq", JOp::eq));
    aBinaries.add (new NamedBinary ("ne", JOp::ne));

    final ICommonsList <NamedUnary> aUnaries = new CommonsArrayList <> ();
    aUnaries.add (new NamedUnary ("not", JOp::not));
    aUnaries.add (new NamedUnary ("castBool", x -> JExpr.cast (aCM.BOOLEAN, x)));
    aUnaries.add (new NamedUnary ("condSwap", x -> JExpr.cond (x, JExpr.FALSE, JExpr.TRUE)));

    final ICommonsList <NamedLeaf> aLeaves = new CommonsArrayList <> ();
    aLeaves.add (new NamedLeaf ("aLtB", p -> JOp.lt (p[0], p[1])));
    aLeaves.add (new NamedLeaf ("aGteC", p -> JOp.gte (p[0], p[2])));
    aLeaves.add (new NamedLeaf ("aEqB", p -> JOp.eq (p[0], p[1])));
    aLeaves.add (new NamedLeaf ("aNeZero", p -> JOp.ne (p[0], JExpr.lit (0))));
    aLeaves.add (new NamedLeaf ("litTrue", p -> JExpr.TRUE));
    aLeaves.add (new NamedLeaf ("instanceOf",
                                p -> JOp._instanceof (JExpr.cast (aCM.ref (Object.class), p[0]),
                                                      aCM.ref (Integer.class))));

    return _addAllCombinations (aClass,
                                aCM.BOOLEAN,
                                aCM.INT,
                                p -> new IJExpression [] { JOp.lt (p[0], p[1]),
                                                           JOp.gt (p[1], p[2]),
                                                           JOp.ne (p[0], p[2]) },
                                aBinaries,
                                aUnaries,
                                aLeaves);
  }

  @NonNull
  private static ICommonsList <String> _buildStringClass (@NonNull final JCodeModel aCM) throws JCodeModelException
  {
    final JDefinedClass aClass = aCM._class (JMod.PUBLIC, CLASS_STRING);
    final AbstractJType aString = aCM.ref (String.class);

    final ICommonsList <NamedBinary> aBinaries = new CommonsArrayList <> ();
    aBinaries.add (new NamedBinary ("concat", JOp::plus));
    aBinaries.add (new NamedBinary ("concatMethod", (x, y) -> x.invoke ("concat").arg (y)));
    aBinaries.add (new NamedBinary ("condLonger",
                                    (x, y) -> JExpr.cond (JOp.gt (x.invoke ("length"), y.invoke ("length")), x, y)));

    final ICommonsList <NamedUnary> aUnaries = new CommonsArrayList <> ();
    aUnaries.add (new NamedUnary ("trim", x -> x.invoke ("trim")));
    aUnaries.add (new NamedUnary ("upper", x -> x.invoke ("toUpperCase")));
    aUnaries.add (new NamedUnary ("castString", x -> JExpr.cast (aString, x)));

    final ICommonsList <NamedLeaf> aLeaves = new CommonsArrayList <> ();
    aLeaves.add (new NamedLeaf ("varA", p -> p[0]));
    aLeaves.add (new NamedLeaf ("litX", p -> JExpr.lit ("x")));
    aLeaves.add (new NamedLeaf ("assignA", p -> JExpr.assign (p[0], JExpr.lit ("z"))));
    aLeaves.add (new NamedLeaf ("substringA", p -> p[0].invoke ("substring").arg (JExpr.lit (1))));

    return _addAllCombinations (aClass,
                                aString,
                                aString,
                                p -> new IJExpression [] { p[0], p[1], p[2] },
                                aBinaries,
                                aUnaries,
                                aLeaves);
  }

  /**
   * A lambda binds looser than an assignment, so the body of a lambda swallows a trailing
   * assignment while a lambda used as the right hand side of an assignment must be grouped.
   */
  @NonNull
  private static ICommonsList <String> _buildLambdaClass (@NonNull final JCodeModel aCM) throws JCodeModelException
  {
    final JDefinedClass aClass = aCM._class (JMod.PUBLIC, CLASS_LAMBDA);
    final AbstractJType aIntUnaryOperator = aCM.ref (java.util.function.IntUnaryOperator.class);

    final ICommonsList <String> ret = new CommonsArrayList <> ();
    {
      // int[] v = new int[1];
      // IntUnaryOperator f = x -> v[0] = x + a;
      // return f.applyAsInt (a) + v[0];
      final JMethod aMethod = aClass.method (JMod.PUBLIC | JMod.STATIC, aCM.INT, "lambdaBodyAssignment");
      final JVar aParamA = aMethod.param (aCM.INT, "a");
      final JBlockVar aV = aMethod.body ().decl (aCM.INT.array (), "v", JExpr.newArray (aCM.INT, 1));
      final JLambda aLambda = new JLambda ();
      final JLambdaParam aX = aLambda.addParam ("x");
      aLambda.body ().lambdaExpr (JExpr.assign (aV.component (0), JOp.plus (aX, aParamA)));
      final JBlockVar aF = aMethod.body ().decl (aIntUnaryOperator, "f", aLambda);
      aMethod.body ()._return (JOp.plus (aF.invoke ("applyAsInt").arg (aParamA), aV.component (0)));
      ret.add (aMethod.name ());
    }
    {
      // IntUnaryOperator f = null;
      // f = x -> x * a;
      // return f.applyAsInt (a);
      final JMethod aMethod = aClass.method (JMod.PUBLIC | JMod.STATIC, aCM.INT, "lambdaAsAssignmentValue");
      final JVar aParamA = aMethod.param (aCM.INT, "a");
      final JBlockVar aF = aMethod.body ().decl (aIntUnaryOperator, "f", JExpr._null ());
      final JLambda aLambda = new JLambda ();
      final JLambdaParam aX = aLambda.addParam ("x");
      aLambda.body ().lambdaExpr (JOp.mul (aX, aParamA));
      aMethod.body ().add (JExpr.assign (aF, aLambda));
      aMethod.body ()._return (aF.invoke ("applyAsInt").arg (aParamA));
      ret.add (aMethod.name ());
    }
    {
      // IntUnaryOperator f = x -> x < 0 ? -x : x + a;
      // return f.applyAsInt (a);
      final JMethod aMethod = aClass.method (JMod.PUBLIC | JMod.STATIC, aCM.INT, "lambdaBodyTernary");
      final JVar aParamA = aMethod.param (aCM.INT, "a");
      final JLambda aLambda = new JLambda ();
      final JLambdaParam aX = aLambda.addParam ("x");
      aLambda.body ()
             .lambdaExpr (JExpr.cond (JOp.lt (aX, JExpr.lit (0)), JOp.minus (aX), JOp.plus (aX, aParamA)));
      final JBlockVar aF = aMethod.body ().decl (aIntUnaryOperator, "f", aLambda);
      aMethod.body ()._return (aF.invoke ("applyAsInt").arg (aParamA));
      ret.add (aMethod.name ());
    }
    return ret;
  }

  @NonNull
  private static String _diagnosticsToString (@NonNull final List <Diagnostic <? extends JavaFileObject>> aDiagnostics)
  {
    final StringBuilder aSB = new StringBuilder ();
    for (final Diagnostic <? extends JavaFileObject> aDiagnostic : aDiagnostics)
      aSB.append ('\n').append (aDiagnostic.toString ());
    return aSB.toString ();
  }

  @BeforeClass
  public static void generateAndCompile () throws Exception
  {
    final JCodeModel aCM = new JCodeModel ();
    s_aIntMethods = _buildIntClass (aCM);
    s_aBoolMethods = _buildBoolClass (aCM);
    s_aStringMethods = _buildStringClass (aCM);
    s_aLambdaMethods = _buildLambdaClass (aCM);

    final StringBuilder aAllErrors = new StringBuilder ();
    for (final EParenthesesStrategy eStrategy : EParenthesesStrategy.values ())
    {
      final FormatterSettings aSettings = new FormatterSettings ();
      aSettings.parentheses.global = eStrategy;

      final MemoryCodeWriter aCodeWriter = new MemoryCodeWriter ();
      new JCMWriter (aCM).withSettings (aSettings).build (aCodeWriter);

      final Map <String, String> aClassSources = new HashMap <> ();
      for (final Map.Entry <String, NonBlockingByteArrayOutputStream> aEntry : aCodeWriter.getBinaries ().entrySet ())
        aClassSources.put (aEntry.getKey (), aEntry.getValue ().getAsString (aCodeWriter.encoding ()));
      SOURCES.put (eStrategy, aClassSources);

      final List <Diagnostic <? extends JavaFileObject>> aErrors = new ArrayList <> ();
      final DynamicClassLoader aDCL = aCodeWriter.setDiagnosticListener (x -> {
        if (x.getKind () == Diagnostic.Kind.ERROR)
          aErrors.add (x);
      }).compile ();
      if (!aErrors.isEmpty ())
        aAllErrors.append ("\nThe strategy ")
                  .append (eStrategy)
                  .append (" created code that does not compile:")
                  .append (_diagnosticsToString (aErrors));
      if (aDCL != null)
        LOADERS.put (eStrategy, aDCL);
    }
    s_sCompilationErrors = aAllErrors.toString ();
  }

  /**
   * @return The source line of the provided method, so that a failure shows the expression that
   *         caused it.
   */
  @NonNull
  private static String _sourceOf (@NonNull final EParenthesesStrategy eStrategy,
                                   @NonNull final String sClassName,
                                   @NonNull final String sMethodName)
  {
    final String sSource = SOURCES.get (eStrategy).get (_sourceKey (sClassName));
    if (sSource != null)
    {
      final String [] aLines = StringHelper.getExplodedArray ('\n', sSource);
      for (int i = 0; i < aLines.length; ++i)
        if (aLines[i].contains (" " + sMethodName + "("))
        {
          final StringBuilder aSB = new StringBuilder ();
          for (int j = i; j < aLines.length && j < i + 5; ++j)
            aSB.append (aLines[j].trim ()).append (' ');
          return aSB.toString ().trim ();
        }
    }
    return "<source of '" + sMethodName + "' not found>";
  }

  /**
   * @return The result of the invocation, or the name of the thrown exception. Both are equally
   *         valid results - they must simply be the same for all strategies.
   */
  @NonNull
  private static String _invoke (@NonNull final Method aMethod, @NonNull final Object [] aArgs)
  {
    try
    {
      return String.valueOf (aMethod.invoke (null, aArgs));
    }
    catch (final InvocationTargetException ex)
    {
      return "threw " + ex.getCause ().getClass ().getName ();
    }
    catch (final IllegalAccessException ex)
    {
      throw new IllegalStateException (ex);
    }
  }

  /**
   * Invoke every method of the provided class in every compiled version and compare the results
   * with the ones of {@link EParenthesesStrategy#REQUIRED}.
   */
  private static void _assertSameBehaviour (@NonNull final String sClassName,
                                            @NonNull final List <String> aMethodNames,
                                            @NonNull final Class <?> [] aParamTypes,
                                            @NonNull final Object [] [] aAllArgs) throws Exception
  {
    assertEquals ("", s_sCompilationErrors);
    assertTrue ("No method was generated for " + sClassName, !aMethodNames.isEmpty ());

    final Map <EParenthesesStrategy, Class <?>> aClasses = new EnumMap <> (EParenthesesStrategy.class);
    for (final Map.Entry <EParenthesesStrategy, DynamicClassLoader> aEntry : LOADERS.entrySet ())
      aClasses.put (aEntry.getKey (), aEntry.getValue ().loadClass (sClassName));

    for (final String sMethodName : aMethodNames)
    {
      final Map <EParenthesesStrategy, Method> aMethods = new EnumMap <> (EParenthesesStrategy.class);
      for (final Map.Entry <EParenthesesStrategy, Class <?>> aEntry : aClasses.entrySet ())
        aMethods.put (aEntry.getKey (), aEntry.getValue ().getDeclaredMethod (sMethodName, aParamTypes));

      for (final Object [] aArgs : aAllArgs)
      {
        final String sExpected = _invoke (aMethods.get (EParenthesesStrategy.REQUIRED), aArgs.clone ());
        for (final EParenthesesStrategy eStrategy : EParenthesesStrategy.values ())
          if (eStrategy != EParenthesesStrategy.REQUIRED)
          {
            final String sActual = _invoke (aMethods.get (eStrategy), aArgs.clone ());
            assertEquals (sClassName +
                          "." +
                          sMethodName +
                          " " +
                          Arrays.toString (aArgs) +
                          " differs between REQUIRED and " +
                          eStrategy +
                          "\n  REQUIRED: " +
                          _sourceOf (EParenthesesStrategy.REQUIRED, sClassName, sMethodName) +
                          "\n  " +
                          eStrategy +
                          ": " +
                          _sourceOf (eStrategy, sClassName, sMethodName),
                          sExpected,
                          sActual);
          }
      }
    }
  }

  /**
   * All strategies must compile - this is already asserted while generating, the test only makes
   * the failure visible on its own.
   */
  @Test
  public void testAllStrategiesCompile ()
  {
    assertEquals ("", s_sCompilationErrors);
    for (final EParenthesesStrategy eStrategy : EParenthesesStrategy.values ())
      assertNotNull ("The strategy " + eStrategy + " created code that does not compile", LOADERS.get (eStrategy));
  }

  @Test
  public void testIntExpressions () throws Exception
  {
    _assertSameBehaviour (CLASS_INT, s_aIntMethods, PARAMS_3INT, ARGS_3INT);
  }

  @Test
  public void testBooleanExpressions () throws Exception
  {
    _assertSameBehaviour (CLASS_BOOL, s_aBoolMethods, PARAMS_3INT, ARGS_3INT);
  }

  @Test
  public void testStringExpressions () throws Exception
  {
    _assertSameBehaviour (CLASS_STRING, s_aStringMethods, PARAMS_3STRING, ARGS_3STRING);
  }

  @Test
  public void testLambdaExpressions () throws Exception
  {
    _assertSameBehaviour (CLASS_LAMBDA, s_aLambdaMethods, PARAMS_1INT, ARGS_1INT);
  }
}
