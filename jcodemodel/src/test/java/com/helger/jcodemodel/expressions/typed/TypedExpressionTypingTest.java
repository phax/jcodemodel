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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

import org.jspecify.annotations.NonNull;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.base.enforce.ValueEnforcer;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.util.CodeModelTestsHelper;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

/**
 * Checks the typed expressions against javac itself: every snippet is written twice, once with
 * plain Java types and operators and once with the typed mirrors, and both are compiled in one
 * javac run. The test compares whether each variant compiles and which type it produces.
 */
public final class TypedExpressionTypingTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (TypedExpressionTypingTest.class);

  private static final String PKG = "com.helger.jcodemodel.expressions.typed.";
  private static final String REJECTED = "<rejected>";

  /** Java array type and its typed mirror */
  private static final String [] [] ARRAYS = { { "int []", "ArrayExpression <int [], IntExpression, IIntConvertible <?>>" },
                                              { "int [] []",
                                                "ArrayExpression <int [] [], ArrayExpression <int [], IntExpression, IIntConvertible <?>>, ITypedExpression <? extends int []>>" },
                                              { "String []", "ArrayExpression <String [], StringExpression, ITypedExpression <? extends String>>" },
                                              { "Integer []", "ArrayExpression <Integer [], IntegerExpression, ITypedExpression <? extends Integer>>" },
                                              { "Object []", "ArrayExpression <Object [], ObjectExpression <Object>, ITypedExpression <? extends Object>>" } };

  /** Java value type and its typed mirror */
  private static final String [] [] VALUES = _concat (new String [] [] { { "byte", "ByteExpression" },
                                              { "short", "ShortExpression" },
                                              { "char", "CharExpression" },
                                              { "int", "IntExpression" },
                                              { "long", "LongExpression" },
                                              { "float", "FloatExpression" },
                                              { "double", "DoubleExpression" },
                                              { "boolean", "BooleanExpression" },
                                              { "Integer", "IntegerExpression" },
                                              { "Character", "CharacterExpression" },
                                              { "Boolean", "BooleanObjExpression" },
                                              { "Byte", "ByteObjExpression" },
                                              { "Short", "ShortObjExpression" },
                                              { "Long", "LongObjExpression" },
                                              { "Float", "FloatObjExpression" },
                                              { "Double", "DoubleObjExpression" },
                                              { "String", "StringExpression" },
                                                                         { "Object", "ObjectExpression <Object>" } },
                                                     ARRAYS);

  /** Java parameter type and its typed parameter mirror */
  private static final String [] [] PARAMS = { { "byte", "IByteConvertible <?>" },
                                              { "short", "IShortConvertible <?>" },
                                              { "char", "ICharConvertible <?>" },
                                              { "int", "IIntConvertible <?>" },
                                              { "long", "ILongConvertible <?>" },
                                              { "float", "IFloatConvertible <?>" },
                                              { "double", "IDoubleConvertible <?>" },
                                              { "boolean", "IBooleanConvertible <?>" },
                                              { "Integer", "ITypedExpression <? extends Integer>" },
                                              { "Character", "ITypedExpression <? extends Character>" },
                                              { "Boolean", "ITypedExpression <? extends Boolean>" },
                                              { "Byte", "ITypedExpression <? extends Byte>" },
                                              { "Short", "ITypedExpression <? extends Short>" },
                                              { "Long", "ITypedExpression <? extends Long>" },
                                              { "Float", "ITypedExpression <? extends Float>" },
                                              { "Double", "ITypedExpression <? extends Double>" },
                                              { "Number", "ITypedExpression <? extends Number>" },
                                              { "Comparable <Integer>",
                                                "ITypedExpression <? extends Comparable <Integer>>" },
                                              { "java.io.Serializable",
                                                "ITypedExpression <? extends java.io.Serializable>" },
                                              { "String", "ITypedExpression <? extends String>" },
                                              { "CharSequence", "ITypedExpression <? extends CharSequence>" },
                                              { "Object", "ITypedExpression <?>" },
                                              { "Cloneable", "ITypedExpression <? extends Cloneable>" },
                                              { "int []", "ITypedExpression <? extends int []>" },
                                              { "long []", "ITypedExpression <? extends long []>" },
                                              { "int [] []", "ITypedExpression <? extends int [] []>" },
                                              { "Object []", "ITypedExpression <? extends Object []>" },
                                              { "Number []", "ITypedExpression <? extends Number []>" },
                                              { "CharSequence []", "ITypedExpression <? extends CharSequence []>" } };

  /** Java variable type and its typed variable */
  private static final String [] [] VARIABLES = { { "byte", "TypedVariable <ByteExpression, IByteConvertible <?>>" },
                                                 { "short", "TypedVariable <ShortExpression, IShortConvertible <?>>" },
                                                 { "char", "TypedVariable <CharExpression, ICharConvertible <?>>" },
                                                 { "int", "TypedVariable <IntExpression, IIntConvertible <?>>" },
                                                 { "long", "TypedVariable <LongExpression, ILongConvertible <?>>" },
                                                 { "float", "TypedVariable <FloatExpression, IFloatConvertible <?>>" },
                                                 { "double", "TypedVariable <DoubleExpression, IDoubleConvertible <?>>" },
                                                 { "boolean", "TypedVariable <BooleanExpression, IBooleanConvertible <?>>" },
                                                 { "Integer",
                                                   "TypedVariable <IntegerExpression, ITypedExpression <? extends Integer>>" },
                                                 { "Character",
                                                   "TypedVariable <CharacterExpression, ITypedExpression <? extends Character>>" },
                                                 { "Boolean", "TypedVariable <BooleanObjExpression, ITypedExpression <? extends Boolean>>" },
                                                 { "Byte", "TypedVariable <ByteObjExpression, ITypedExpression <? extends Byte>>" },
                                                 { "Short", "TypedVariable <ShortObjExpression, ITypedExpression <? extends Short>>" },
                                                 { "Long", "TypedVariable <LongObjExpression, ITypedExpression <? extends Long>>" },
                                                 { "Float", "TypedVariable <FloatObjExpression, ITypedExpression <? extends Float>>" },
                                                 { "Double", "TypedVariable <DoubleObjExpression, ITypedExpression <? extends Double>>" },
                                                 { "String",
                                                   "TypedVariable <StringExpression, ITypedExpression <? extends String>>" },
                                                 { "Object",
                                                   "TypedVariable <ObjectExpression <Object>, ITypedExpression <? extends Object>>" },
                                                 { "int []",
                                                   "TypedVariable <ArrayExpression <int [], IntExpression, IIntConvertible <?>>, ITypedExpression <? extends int []>>" },
                                                 { "String []",
                                                   "TypedVariable <ArrayExpression <String [], StringExpression, ITypedExpression <? extends String>>, ITypedExpression <? extends String []>>" },
                                                 { "Object []",
                                                   "TypedVariable <ArrayExpression <Object [], ObjectExpression <Object>, ITypedExpression <? extends Object>>, ITypedExpression <? extends Object []>>" } };

  /** Java increment/decrement operator, whether it is a prefix, and the typed method name */
  private static final String [] [] INC_DEC_OPS = { { "++", "false", "postincr" },
                                                   { "++", "true", "preincr" },
                                                   { "--", "false", "postdecr" },
                                                   { "--", "true", "predecr" } };

  /** Java binary operator and the typed method names that may implement it */
  private static final String [] [] BINARY_OPS = { { "+", "plus" },
                                                  { "-", "minus" },
                                                  { "*", "mul" },
                                                  { "/", "div" },
                                                  { "%", "mod" },
                                                  { "&", "band" },
                                                  { "|", "bor" },
                                                  { "^", "xor" },
                                                  { "<<", "shl" },
                                                  { ">>", "shr" },
                                                  { ">>>", "shrz" },
                                                  { "<", "lt" },
                                                  { "<=", "lte" },
                                                  { ">", "gt" },
                                                  { ">=", "gte" },
                                                  { "==", "eq", "eqRef" },
                                                  { "!=", "ne", "neRef" },
                                                  { "&&", "cand" },
                                                  { "||", "cor" } };

  private static final String [] [] UNARY_OPS = { { "-", "minus" }, { "~", "complement" }, { "!", "not" } };

  /** javac result type to the expected typed mirror, as printed by javac without blanks */
  private static final Map <String, String> RESULT_MIRRORS = Map.ofEntries (Map.entry ("byte", "ByteExpression"),
                                                                             Map.entry ("short", "ShortExpression"),
                                                                             Map.entry ("char", "CharExpression"),
                                                                             Map.entry ("int", "IntExpression"),
                                                                             Map.entry ("long", "LongExpression"),
                                                                             Map.entry ("float", "FloatExpression"),
                                                                             Map.entry ("double", "DoubleExpression"),
                                                                             Map.entry ("boolean", "BooleanExpression"),
                                                                             Map.entry ("java.lang.Integer",
                                                                                        "IntegerExpression"),
                                                                             Map.entry ("java.lang.Character",
                                                                                        "CharacterExpression"),
                                                                             Map.entry ("java.lang.Boolean", "BooleanObjExpression"),
                                                                             Map.entry ("java.lang.Byte", "ByteObjExpression"),
                                                                             Map.entry ("java.lang.Short", "ShortObjExpression"),
                                                                             Map.entry ("java.lang.Long", "LongObjExpression"),
                                                                             Map.entry ("java.lang.Float", "FloatObjExpression"),
                                                                             Map.entry ("java.lang.Double", "DoubleObjExpression"),
                                                                             Map.entry ("java.lang.String",
                                                                                        "StringExpression"),
                                                                             Map.entry ("java.lang.Object",
                                                                                        "ObjectExpression<java.lang.Object>"),
                                                                             Map.entry ("java.lang.String[]",
                                                                                        "ArrayExpression<java.lang.String[],StringExpression,ITypedExpression<?extendsjava.lang.String>>"),
                                                                             Map.entry ("java.lang.Object[]",
                                                                                        "ArrayExpression<java.lang.Object[],ObjectExpression<java.lang.Object>,ITypedExpression<?extendsjava.lang.Object>>"),
                                                                             Map.entry ("java.lang.Integer[]",
                                                                                        "ArrayExpression<java.lang.Integer[],IntegerExpression,ITypedExpression<?extendsjava.lang.Integer>>"),
                                                                             Map.entry ("int[][]",
                                                                                        "ArrayExpression<int[][],ArrayExpression<int[],IntExpression,IIntConvertible<?>>,ITypedExpression<?extendsint[]>>"),
                                                                             Map.entry ("int[]",
                                                                                        "ArrayExpression<int[],IntExpression,IIntConvertible<?>>"));

  private static final class StringSource extends SimpleJavaFileObject
  {
    private final String m_sCode;

    StringSource (@NonNull final String sClassName, @NonNull final String sCode)
    {
      super (URI.create ("string:///" + sClassName + Kind.SOURCE.extension), Kind.SOURCE);
      m_sCode = sCode;
    }

    @Override
    public CharSequence getCharContent (final boolean bIgnoreEncodingErrors)
    {
      return m_sCode;
    }
  }

  /** One case: the Java snippet and the alternative typed snippets */
  private record Case (String sDescription, String sJavaClass, List <String> aTypedClasses)
  {}

  private static String [] [] _concat (@NonNull final String [] [] aFirst, @NonNull final String [] [] aSecond)
  {
    final String [] [] ret = Arrays.copyOf (aFirst, aFirst.length + aSecond.length);
    System.arraycopy (aSecond, 0, ret, aFirst.length, aSecond.length);
    return ret;
  }

  private static String _classPathOf (@NonNull final Class <?> aClass)
  {
    try
    {
      return Paths.get (aClass.getProtectionDomain ().getCodeSource ().getLocation ().toURI ()).toString ();
    }
    catch (final URISyntaxException ex)
    {
      throw new IllegalStateException ("Failed to determine the class path of " + aClass, ex);
    }
  }

  private static String _className (@NonNull final JavaFileObject aSource)
  {
    final String sPath = aSource.toUri ().getPath ();
    return sPath.substring (1, sPath.length () - JavaFileObject.Kind.SOURCE.extension.length ());
  }

  /**
   * Compile all sources in one javac run without writing class files.
   *
   * @return class name to the type of the local variable <code>r</code>, or {@link #REJECTED} if
   *         the class does not compile.
   */
  private static Map <String, String> _compile (@NonNull final Map <String, String> aSources)
  {
    final JavaCompiler aCompiler = ToolProvider.getSystemJavaCompiler ();
    assertNotNull ("A JDK is required", aCompiler);

    final Set <String> aClassPath = new LinkedHashSet <> ();
    aClassPath.add (_classPathOf (ITypedExpression.class));
    aClassPath.add (_classPathOf (NonNull.class));
    aClassPath.add (_classPathOf (ValueEnforcer.class));
    aClassPath.add (System.getProperty ("java.class.path"));

    final List <JavaFileObject> aFiles = new ArrayList <> ();
    aSources.forEach ( (sName, sCode) -> aFiles.add (new StringSource (sName, sCode)));

    final DiagnosticCollector <JavaFileObject> aDiagnostics = new DiagnosticCollector <> ();
    final JavacTask aTask = (JavacTask) aCompiler.getTask (null,
                                                           null,
                                                           aDiagnostics,
                                                           List.of ("-proc:none",
                                                                    "-Xmaxerrs",
                                                                    "1000000",
                                                                    "-classpath",
                                                                    String.join (File.pathSeparator, aClassPath)),
                                                           null,
                                                           aFiles);
    final Map <String, String> ret = new HashMap <> ();
    try
    {
      final Iterable <? extends CompilationUnitTree> aUnits = aTask.parse ();
      aTask.analyze ();
      final Trees aTrees = Trees.instance (aTask);
      for (final CompilationUnitTree aUnit : aUnits)
      {
        final String sClassName = _className (aUnit.getSourceFile ());
        ret.put (sClassName, "");
        new TreePathScanner <Void, Void> ()
        {
          @Override
          public Void visitVariable (final VariableTree aVar, final Void aParam)
          {
            if (aVar.getName ().contentEquals ("r"))
              ret.put (sClassName, aTrees.getTypeMirror (getCurrentPath ()).toString ());
            return super.visitVariable (aVar, aParam);
          }
        }.scan (aUnit, null);
      }
    }
    catch (final IOException ex)
    {
      throw new UncheckedIOException (ex);
    }

    for (final Diagnostic <? extends JavaFileObject> aDiag : aDiagnostics.getDiagnostics ())
      if (aDiag.getKind () == Diagnostic.Kind.ERROR)
      {
        assertNotNull ("Error without source: " + aDiag, aDiag.getSource ());
        ret.put (_className (aDiag.getSource ()), REJECTED);
      }
    return ret;
  }

  private static String _typedOutcome (@NonNull final Case aCase, @NonNull final Map <String, String> aResults)
  {
    for (final String sTypedClass : aCase.aTypedClasses ())
    {
      final String sType = aResults.get (sTypedClass);
      if (!REJECTED.equals (sType))
        return sType.replace (PKG, "").replace (" ", "");
    }
    return REJECTED;
  }

  private static String _javaOutcome (@NonNull final Case aCase, @NonNull final Map <String, String> aResults)
  {
    final String sType = aResults.get (aCase.sJavaClass ());
    if (REJECTED.equals (sType) || sType.isEmpty ())
      return sType;
    final String sMirror = RESULT_MIRRORS.get (sType);
    // A Java type without mirror, e.g. an intersection type, never equals a typed outcome
    return sMirror != null ? sMirror : "<unmapped " + sType + ">";
  }

  private static String _source (@NonNull final String sClassName, @NonNull final String sBody)
  {
    return "import " + PKG + "*;\nimport java.util.function.*;\nclass " + sClassName + " {\n" + sBody + "\n}\n";
  }

  @Test
  public void testArgumentPassingMatchesJavac ()
  {
    final Map <String, String> aSources = new LinkedHashMap <> ();
    final List <Case> aCases = new ArrayList <> ();
    for (final String [] aParam : PARAMS)
      for (final String [] aValue : VALUES)
      {
        final int nIndex = aCases.size ();
        aSources.put ("J" + nIndex,
                      _source ("J" + nIndex,
                               "static void m (" +
                                              aParam[0] +
                                              " p) {}\nstatic void t (" +
                                              aValue[0] +
                                              " a) { m (a); }"));
        aSources.put ("T" + nIndex,
                      _source ("T" + nIndex,
                               "static void m (" +
                                              aParam[1] +
                                              " p) {}\nstatic void t (" +
                                              aValue[1] +
                                              " a) { m (a); }"));
        aCases.add (new Case (aValue[0] + " -> " + aParam[0], "J" + nIndex, List.of ("T" + nIndex)));
      }

    final Map <String, String> aResults = _compile (aSources);
    final List <String> aMismatches = new ArrayList <> ();
    for (final Case aCase : aCases)
    {
      final boolean bJava = !REJECTED.equals (aResults.get (aCase.sJavaClass ()));
      final boolean bTyped = !REJECTED.equals (_typedOutcome (aCase, aResults));
      if (bJava != bTyped)
        aMismatches.add (aCase.sDescription () + ": javac " + (bJava ? "accepts" : "rejects"));
    }
    LOGGER.info ("Checked " + aCases.size () + " argument conversions against javac");
    assertEquals ("Argument conversions differing from javac: " + aMismatches, 0, aMismatches.size ());
  }

  private static List <String> _exactMismatches (@NonNull final List <Case> aCases,
                                                 @NonNull final Map <String, String> aResults)
  {
    final List <String> ret = new ArrayList <> ();
    int nAccepted = 0;
    for (final Case aCase : aCases)
    {
      final String sJava = _javaOutcome (aCase, aResults);
      if (!REJECTED.equals (sJava))
        nAccepted++;
      final String sTyped = _typedOutcome (aCase, aResults);
      if (!sJava.equals (sTyped))
        ret.add (aCase.sDescription () + ": javac " + sJava + ", typed " + sTyped);
    }
    LOGGER.info (nAccepted + " of " + aCases.size () + " cases are valid Java");
    return ret;
  }

  @Test
  public void testVariablesMatchJavac ()
  {
    final Map <String, String> aSources = new LinkedHashMap <> ();
    final List <Case> aCases = new ArrayList <> ();
    for (final String [] aVar : VARIABLES)
    {
      for (final String [] aValue : VALUES)
      {
        final int nIndex = aCases.size ();
        aSources.put ("J" + nIndex,
                      _source ("J" + nIndex,
                               "static void t (" + aVar[0] + " a, " + aValue[0] + " v) { var r = (a = v); }"));
        aSources.put ("T" + nIndex,
                      _source ("T" + nIndex,
                               "static void t (" + aVar[1] + " a, " + aValue[1] + " v) { var r = a.assignExpr (v); }"));
        aCases.add (new Case (aVar[0] + " = " + aValue[0], "J" + nIndex, List.of ("T" + nIndex)));
      }
      for (final String [] aOp : INC_DEC_OPS)
      {
        final int nIndex = aCases.size ();
        final boolean bPrefix = Boolean.parseBoolean (aOp[1]);
        aSources.put ("J" + nIndex,
                      _source ("J" + nIndex,
                               "static void t (" +
                                              aVar[0] +
                                              " a) { var r = " +
                                              (bPrefix ? aOp[0] + "a" : "a" + aOp[0]) +
                                              "; }"));
        aSources.put ("T" + nIndex,
                      _source ("T" + nIndex,
                               "static void t (" + aVar[1] + " a) { var r = TypedVariable." + aOp[2] + " (a); }"));
        aCases.add (new Case (bPrefix ? aOp[0] + aVar[0] : aVar[0] + aOp[0], "J" + nIndex, List.of ("T" + nIndex)));
      }
    }

    final List <String> aMismatches = _exactMismatches (aCases, _compile (aSources));
    LOGGER.info ("Checked " + aCases.size () + " variable assignments and increments against javac");
    assertEquals ("Variable operations differing from javac: " + aMismatches, 0, aMismatches.size ());
  }

  /** Description, Java snippet, typed snippet */
  private static final String [] [] LAMBDA_CASES = { { "Function<String,Integer> lambda to Function<? super String,? extends Number>",
                                                      "static void m (Function <? super String, ? extends Number> f) {}\nstatic void t () { m (s -> s.length ()); }",
                                                      "static void m (ITypedExpression <? extends Function <? super String, ? extends Number>> f) {}\nstatic void t () { m (FunctionExpression.lambda (TypedType.STRING, \"s\", s -> s.length ())); }" },
                                                    { "Function<String,Integer> lambda to Function<? super String,? extends String>",
                                                      "static void m (Function <? super String, ? extends String> f) {}\nstatic void t () { m (s -> s.length ()); }",
                                                      "static void m (ITypedExpression <? extends Function <? super String, ? extends String>> f) {}\nstatic void t () { m (FunctionExpression.lambda (TypedType.STRING, \"s\", s -> s.length ())); }" },
                                                    { "Predicate with boolean body",
                                                      "static void t () { Predicate <String> r = s -> s.isEmpty (); }",
                                                      "static void t () { PredicateExpression <String> r = PredicateExpression.lambda (TypedType.STRING, \"s\", s -> s.isEmpty ()); }" },
                                                    { "Predicate with int body",
                                                      "static void t () { Predicate <String> r = s -> s.length (); }",
                                                      "static void t () { var r = PredicateExpression.lambda (TypedType.STRING, \"s\", s -> s.length ()); }" },
                                                    { "Predicate over a primitive",
                                                      "static void t () { Predicate <int> r = i -> i > 0; }",
                                                      "static void t () { var r = PredicateExpression.lambda (TypedType.INT, \"i\", i -> i.gt (IntExpression.lit (0))); }" },
                                                    { "Predicate<Object> to Predicate<? super String>",
                                                      "static void m (Predicate <? super String> p) {}\nstatic void t (Predicate <Object> a) { m (a); }",
                                                      "static void m (ITypedExpression <? extends Predicate <? super String>> p) {}\nstatic void t (PredicateExpression <Object> a) { m (a); }" },
                                                    { "Predicate<String> to Predicate<? super Object>",
                                                      "static void m (Predicate <? super Object> p) {}\nstatic void t (Predicate <String> a) { m (a); }",
                                                      "static void m (ITypedExpression <? extends Predicate <? super Object>> p) {}\nstatic void t (PredicateExpression <String> a) { m (a); }" },
                                                    { "Predicate<String> assigned to Predicate<Object> variable",
                                                      "static void t (Predicate <Object> v, Predicate <String> a) { v = a; }",
                                                      "static void t (TypedVariable <PredicateExpression <Object>, ITypedExpression <? extends Predicate <Object>>> v, PredicateExpression <String> a) { v.assignExpr (a); }" },
                                                    { "Predicate<CharSequence>.test(String)",
                                                      "static void t (Predicate <CharSequence> p, String s) { boolean r = p.test (s); }",
                                                      "static void t (PredicateExpression <CharSequence> p, StringExpression s) { BooleanExpression r = p.test (s); }" },
                                                    { "Predicate<String>.test(Integer)",
                                                      "static void t (Predicate <String> p, Integer i) { boolean r = p.test (i); }",
                                                      "static void t (PredicateExpression <String> p, IntegerExpression i) { BooleanExpression r = p.test (i); }" },
                                                    { "Function<String,Integer>.apply(s) + 1",
                                                      "static void t (Function <String, Integer> f, String s) { int r = f.apply (s) + 1; }",
                                                      "static void t (FunctionExpression <String, Integer> f, StringExpression s) { IntExpression r = f.apply (s, TypedType.INTEGER).plus (1); }" },
                                                    { "Supplier<Integer> of an int",
                                                      "static void t () { Supplier <Integer> r = () -> 5; }",
                                                      "static void t () { SupplierExpression <Integer> r = SupplierExpression.lambda (IntExpression.lit (5)); }" },
                                                    { "Supplier<Long> of an int",
                                                      "static void t () { Supplier <Long> r = () -> 5; }",
                                                      "static void t () { SupplierExpression <Long> r = SupplierExpression.lambda (IntExpression.lit (5)); }" },
                                                    { "Function with void body",
                                                      "static void t () { Function <String, Object> r = s -> System.out.println (s); }",
                                                      "static void t () { var r = FunctionExpression.lambda (TypedType.STRING, \"s\", s -> (VoidExpression) null); }" },
                                                    { "void as argument",
                                                      "static void m (Object o) {}\nstatic void t (Consumer <String> c, String s) { m (c.accept (s)); }",
                                                      "static void m (ITypedExpression <?> o) {}\nstatic void t (ConsumerExpression <String> c, StringExpression s) { m (c.accept (s)); }" },
                                                    { "String + void",
                                                      "static void t (Consumer <String> c, String s) { var r = s + c.accept (s); }",
                                                      "static void t (ConsumerExpression <String> c, StringExpression s) { var r = s.plus (c.accept (s)); }" } };

  @Test
  public void testLambdasMatchJavac ()
  {
    final Map <String, String> aSources = new LinkedHashMap <> ();
    final List <Case> aCases = new ArrayList <> ();
    for (final String [] aCase : LAMBDA_CASES)
    {
      final int nIndex = aCases.size ();
      aSources.put ("J" + nIndex, _source ("J" + nIndex, aCase[1]));
      aSources.put ("T" + nIndex, _source ("T" + nIndex, aCase[2]));
      aCases.add (new Case (aCase[0], "J" + nIndex, List.of ("T" + nIndex)));
    }

    final Map <String, String> aResults = _compile (aSources);
    final List <String> aMismatches = new ArrayList <> ();
    int nAccepted = 0;
    for (final Case aCase : aCases)
    {
      final boolean bJava = !REJECTED.equals (aResults.get (aCase.sJavaClass ()));
      final boolean bTyped = !REJECTED.equals (_typedOutcome (aCase, aResults));
      if (bJava)
        nAccepted++;
      if (bJava != bTyped)
        aMismatches.add (aCase.sDescription () + ": javac " + (bJava ? "accepts" : "rejects"));
    }
    LOGGER.info ("Checked " + aCases.size () + " lambda cases against javac, " + nAccepted + " are valid Java");
    assertEquals ("Lambda cases differing from javac: " + aMismatches, 0, aMismatches.size ());
  }

  /** Target classes for instanceof */
  private static final String [] INSTANCEOF_TARGETS = { "String",
                                                        "Integer",
                                                        "Number",
                                                        "CharSequence",
                                                        "Comparable",
                                                        "Runnable",
                                                        "Object",
                                                        "int []",
                                                        "Object []",
                                                        "String []" };

  @Test
  public void testInstanceOfIsSound ()
  {
    final boolean bSameTypePatternAllowed = Runtime.version ().feature () >= 21;
    final Map <String, String> aSources = new LinkedHashMap <> ();
    final List <Case> aCases = new ArrayList <> ();
    final Map <Case, Boolean> aIsPattern = new HashMap <> ();
    for (final String sTarget : INSTANCEOF_TARGETS)
      for (final String [] aValue : VALUES)
        for (final boolean bPattern : new boolean [] { false, true })
        {
          final int nIndex = aCases.size ();
          final String sCM = "com.helger.jcodemodel.JCodeModel cm, ";
          aSources.put ("J" + nIndex,
                        _source ("J" + nIndex,
                                 "static void t (" +
                                                aValue[0] +
                                                " a) { var r = a instanceof " +
                                                sTarget +
                                                (bPattern ? " b" : "") +
                                                "; }"));
          aSources.put ("T" + nIndex,
                        _source ("T" + nIndex,
                                 "static void t (" +
                                                sCM +
                                                aValue[1] +
                                                " a) { var r = a." +
                                                (bPattern ? "instanceOfPattern (cm, " +
                                                            sTarget +
                                                            ".class, \"b\", ObjectExpression::of).test ()"
                                                          : "instanceOf (cm, " + sTarget + ".class)") +
                                                "; }"));
          final Case aCase = new Case (aValue[0] + " instanceof " + sTarget + (bPattern ? " b" : ""),
                                       "J" + nIndex,
                                       List.of ("T" + nIndex));
          aCases.add (aCase);
          aIsPattern.put (aCase, Boolean.valueOf (bPattern));
        }

    final Map <String, String> aResults = _compile (aSources);
    final List <String> aUnsound = new ArrayList <> ();
    final List <String> aGaps = new ArrayList <> ();
    int nAccepted = 0;
    for (final Case aCase : aCases)
    {
      final String sJava = _javaOutcome (aCase, aResults);
      final String sTyped = _typedOutcome (aCase, aResults);
      if (!REJECTED.equals (sJava))
        nAccepted++;
      if (!sJava.equals (sTyped))
        if (REJECTED.equals (sTyped))
          aGaps.add (aCase.sDescription ());
        else
          aUnsound.add (aCase.sDescription () + ": javac " + sJava + ", typed " + sTyped);
    }
    LOGGER.info ("Checked " +
                 aCases.size () +
                 " instanceof cases against javac, " +
                 nAccepted +
                 " are valid Java, " +
                 aGaps.size () +
                 " are not offered: " +
                 aGaps);
    if (!bSameTypePatternAllowed)
    {
      // javac before 21 rejects a pattern of the static type of the expression, see instanceOfPattern
      LOGGER.info ("Accepted by the typed API but rejected by this javac: " + aUnsound);
      aUnsound.removeIf (x -> {
        final String [] aParts = x.substring (0, x.indexOf (':')).split (" instanceof ");
        return aParts[1].equals (aParts[0] + " b");
      });
    }
    assertEquals ("instanceof cases accepted by the typed API but rejected by javac: " + aUnsound,
                  0,
                  aUnsound.size ());
  }

  @Test
  public void testConditionalIsSound ()
  {
    final Map <String, String> aSources = new LinkedHashMap <> ();
    final List <Case> aCases = new ArrayList <> ();
    for (final String [] aThen : VALUES)
      for (final String [] aElse : VALUES)
      {
        final int nIndex = aCases.size ();
        aSources.put ("J" + nIndex,
                      _source ("J" + nIndex,
                               "static void t (boolean c, " +
                                              aThen[0] +
                                              " a, " +
                                              aElse[0] +
                                              " b) { var r = c ? a : b; }"));
        aSources.put ("T" + nIndex + "_1",
                      _source ("T" + nIndex + "_1",
                               "static void t (BooleanExpression c, " +
                                                    aThen[1] +
                                                    " a, " +
                                                    aElse[1] +
                                                    " b) { var r = c.cond (a, b); }"));
        aSources.put ("T" + nIndex + "_2",
                      _source ("T" + nIndex + "_2",
                               "static void t (BooleanExpression c, " +
                                                    aThen[1] +
                                                    " a, " +
                                                    aElse[1] +
                                                    " b) { var r = c.cond (TypedType.object (Object.class), a, b); }"));
        aCases.add (new Case (aThen[0] + " : " + aElse[0], "J" + nIndex, List.of ("T" + nIndex + "_1", "T" + nIndex + "_2")));
      }

    final Map <String, String> aResults = _compile (aSources);
    final List <String> aUnsound = new ArrayList <> ();
    final List <String> aGaps = new ArrayList <> ();
    int nAccepted = 0;
    for (final Case aCase : aCases)
    {
      final String sJava = _javaOutcome (aCase, aResults);
      final String sTyped = _typedOutcome (aCase, aResults);
      if (!REJECTED.equals (sJava))
        nAccepted++;
      if (!sJava.equals (sTyped))
        if (REJECTED.equals (sTyped))
          aGaps.add (aCase.sDescription () + " (" + sJava + ")");
        else
          aUnsound.add (aCase.sDescription () + ": javac " + sJava + ", typed " + sTyped);
    }
    LOGGER.info ("Checked " +
                 aCases.size () +
                 " conditionals against javac, " +
                 nAccepted +
                 " are valid Java, " +
                 aGaps.size () +
                 " are not offered: " +
                 aGaps);
    assertEquals ("Conditionals accepted by the typed API with a different result than javac: " + aUnsound,
                  0,
                  aUnsound.size ());
  }

  @Test
  public void testArrayAccessMatchesJavac ()
  {
    final Map <String, String> aSources = new LinkedHashMap <> ();
    final List <Case> aCases = new ArrayList <> ();
    for (final String [] aArray : ARRAYS)
    {
      int nIndex = aCases.size ();
      aSources.put ("J" + nIndex, _source ("J" + nIndex, "static void t (" + aArray[0] + " a) { var r = a.length; }"));
      aSources.put ("T" + nIndex,
                    _source ("T" + nIndex, "static void t (" + aArray[1] + " a) { var r = a.length (); }"));
      aCases.add (new Case (aArray[0] + ".length", "J" + nIndex, List.of ("T" + nIndex)));

      for (final String [] aIndex : VALUES)
      {
        nIndex = aCases.size ();
        aSources.put ("J" + nIndex,
                      _source ("J" + nIndex,
                               "static void t (" + aArray[0] + " a, " + aIndex[0] + " i) { var r = a [i]; }"));
        aSources.put ("T" + nIndex,
                      _source ("T" + nIndex,
                               "static void t (" +
                                              aArray[1] +
                                              " a, " +
                                              aIndex[1] +
                                              " i) { var r = a.component (i); }"));
        aCases.add (new Case (aArray[0] + " [" + aIndex[0] + "]", "J" + nIndex, List.of ("T" + nIndex)));
      }
    }

    final List <String> aMismatches = _exactMismatches (aCases, _compile (aSources));
    LOGGER.info ("Checked " + aCases.size () + " array accesses against javac");
    assertEquals ("Array accesses differing from javac: " + aMismatches, 0, aMismatches.size ());
  }

  @Test
  public void testOperatorsAreSound ()
  {
    final Map <String, String> aSources = new LinkedHashMap <> ();
    final List <Case> aCases = new ArrayList <> ();
    for (final String [] aOp : BINARY_OPS)
      for (final String [] aLeft : VALUES)
        for (final String [] aRight : VALUES)
        {
          final int nIndex = aCases.size ();
          final String sSignature = "static void t (";
          aSources.put ("J" + nIndex,
                        _source ("J" + nIndex,
                                 sSignature +
                                                aLeft[0] +
                                                " a, " +
                                                aRight[0] +
                                                " b) { var r = a " +
                                                aOp[0] +
                                                " b; }"));
          final List <String> aTyped = new ArrayList <> ();
          for (int i = 1; i < aOp.length; ++i)
          {
            final String sName = "T" + nIndex + "_" + i;
            aSources.put (sName,
                          _source (sName,
                                   sSignature +
                                          aLeft[1] +
                                          " a, " +
                                          aRight[1] +
                                          " b) { var r = a." +
                                          aOp[i] +
                                          " (b); }"));
            aTyped.add (sName);
          }
          aCases.add (new Case (aLeft[0] + " " + aOp[0] + " " + aRight[0], "J" + nIndex, aTyped));
        }
    for (final String [] aOp : UNARY_OPS)
      for (final String [] aValue : VALUES)
      {
        final int nIndex = aCases.size ();
        aSources.put ("J" + nIndex,
                      _source ("J" + nIndex, "static void t (" + aValue[0] + " a) { var r = " + aOp[0] + "a; }"));
        aSources.put ("T" + nIndex,
                      _source ("T" + nIndex,
                               "static void t (" + aValue[1] + " a) { var r = a." + aOp[1] + " (); }"));
        aCases.add (new Case (aOp[0] + aValue[0], "J" + nIndex, List.of ("T" + nIndex)));
      }

    final Map <String, String> aResults = _compile (aSources);
    final List <String> aUnsound = new ArrayList <> ();
    final List <String> aGaps = new ArrayList <> ();
    int nAgreed = 0;
    for (final Case aCase : aCases)
    {
      final String sJava = _javaOutcome (aCase, aResults);
      final String sTyped = _typedOutcome (aCase, aResults);
      if (sJava.equals (sTyped))
        nAgreed++;
      else
        if (REJECTED.equals (sTyped))
          aGaps.add (aCase.sDescription ());
        else
          aUnsound.add (aCase.sDescription () + ": javac " + sJava + ", typed " + sTyped);
    }
    LOGGER.info ("Checked " +
                 aCases.size () +
                 " operator applications against javac: " +
                 nAgreed +
                 " agree, " +
                 aGaps.size () +
                 " valid in Java but not offered by the typed API: " +
                 aGaps);

    // Soundness: the typed API never accepts what javac rejects, and never infers a different type
    assertEquals ("Typed operators differing from javac: " + aUnsound, 0, aUnsound.size ());
    // The only intended gaps are == and != with a boxed or reference operand on the left
    for (final String sGap : aGaps)
      assertTrue ("Unexpected gap: " + sGap, sGap.contains (" == ") || sGap.contains (" != "));
  }

  /**
   * A method reference case: the lambda, the method reference created from the same call, the
   * expected method reference text (<code>null</code> if creating it must fail) and optionally the
   * method reference that javac must reject.
   */
  private record MethodRefCase (String sDescription,
                                String sTarget,
                                IJExpression aLambda,
                                Supplier <IJExpression> aMethodRef,
                                String sExpected,
                                String sJavacRejects)
  {}

  @Test
  public void testMethodRefsMatchLambdas ()
  {
    final JCodeModel cm = JCodeModel.createUnified ();
    final StringExpression aT = StringExpression.of (JExpr.ref ("t"));
    final StringExpression aU = StringExpression.of (JExpr.ref ("u"));
    final ConsumerExpression <String> aC = ConsumerExpression.of (JExpr.ref ("c"));
    final List <MethodRefCase> aRefCases = List.of (new MethodRefCase ("unbound String::length",
                                                                       "Function <String, Integer>",
                                                                       FunctionExpression.lambda (TypedType.STRING,
                                                                                                  "s",
                                                                                                  StringExpression::length)
                                                                                         .raw (),
                                                                       () -> FunctionExpression.methodRef (cm,
                                                                                                           TypedType.STRING,
                                                                                                           StringExpression::length)
                                                                                               .raw (),
                                                                       "java.lang.String::length",
                                                                       null),
                                                    new MethodRefCase ("unbound String::length to a wider result",
                                                                       "Function <String, ? extends Number>",
                                                                       FunctionExpression.lambda (TypedType.STRING,
                                                                                                  "s",
                                                                                                  StringExpression::length)
                                                                                         .raw (),
                                                                       () -> FunctionExpression.methodRef (cm,
                                                                                                           TypedType.STRING,
                                                                                                           StringExpression::length)
                                                                                               .raw (),
                                                                       "java.lang.String::length",
                                                                       null),
                                                    new MethodRefCase ("unbound String::isEmpty",
                                                                       "Predicate <String>",
                                                                       PredicateExpression.lambda (TypedType.STRING,
                                                                                                   "s",
                                                                                                   StringExpression::isEmpty)
                                                                                          .raw (),
                                                                       () -> PredicateExpression.methodRef (cm,
                                                                                                            TypedType.STRING,
                                                                                                            StringExpression::isEmpty)
                                                                                                .raw (),
                                                                       "java.lang.String::isEmpty",
                                                                       null),
                                                    new MethodRefCase ("unbound Object::hashCode",
                                                                       "Function <Object, Integer>",
                                                                       FunctionExpression.lambda (TypedType.object (Object.class),
                                                                                                  "o",
                                                                                                  ObjectExpression::callHashCode)
                                                                                         .raw (),
                                                                       () -> FunctionExpression.methodRef (cm,
                                                                                                           TypedType.object (Object.class),
                                                                                                           ObjectExpression::callHashCode)
                                                                                               .raw (),
                                                                       "java.lang.Object::hashCode",
                                                                       null),
                                                    new MethodRefCase ("unbound Integer::intValue",
                                                                       "Function <Integer, Integer>",
                                                                       FunctionExpression.lambda (TypedType.INTEGER,
                                                                                                  "i",
                                                                                                  IntegerExpression::intValue)
                                                                                         .raw (),
                                                                       () -> FunctionExpression.methodRef (cm,
                                                                                                           TypedType.INTEGER,
                                                                                                           IntegerExpression::intValue)
                                                                                               .raw (),
                                                                       "java.lang.Integer::intValue",
                                                                       null),
                                                    new MethodRefCase ("unbound Integer::hashCode is ambiguous",
                                                                       "Function <Integer, Integer>",
                                                                       FunctionExpression.lambda (TypedType.INTEGER,
                                                                                                  "i",
                                                                                                  IntegerExpression::callHashCode)
                                                                                         .raw (),
                                                                       () -> FunctionExpression.methodRef (cm,
                                                                                                           TypedType.INTEGER,
                                                                                                           IntegerExpression::callHashCode)
                                                                                               .raw (),
                                                                       null,
                                                                       "Integer::hashCode"),
                                                    new MethodRefCase ("unbound Integer::toString is ambiguous",
                                                                       "Function <Integer, String>",
                                                                       FunctionExpression.lambda (TypedType.INTEGER,
                                                                                                  "i",
                                                                                                  IntegerExpression::callToString)
                                                                                         .raw (),
                                                                       () -> FunctionExpression.methodRef (cm,
                                                                                                           TypedType.INTEGER,
                                                                                                           IntegerExpression::callToString)
                                                                                               .raw (),
                                                                       null,
                                                                       "Integer::toString"),
                                                    new MethodRefCase ("static String::valueOf",
                                                                       "Function <Object, String>",
                                                                       FunctionExpression.lambda (TypedType.object (Object.class),
                                                                                                  "o",
                                                                                                  o -> StringExpression.of (cm.ref (String.class)
                                                                                                                              .staticInvoke ("valueOf")
                                                                                                                              .arg (o.raw ())))
                                                                                         .raw (),
                                                                       () -> FunctionExpression.methodRef (cm,
                                                                                                           TypedType.object (Object.class),
                                                                                                           o -> StringExpression.of (cm.ref (String.class)
                                                                                                                                       .staticInvoke ("valueOf")
                                                                                                                                       .arg (o.raw ())))
                                                                                               .raw (),
                                                                       "java.lang.String::valueOf",
                                                                       null),
                                                    new MethodRefCase ("static Integer::parseInt",
                                                                       "Function <String, Integer>",
                                                                       FunctionExpression.lambda (TypedType.STRING,
                                                                                                  "s",
                                                                                                  s -> IntExpression.of (cm.ref (Integer.class)
                                                                                                                           .staticInvoke ("parseInt")
                                                                                                                           .arg (s.raw ())))
                                                                                         .raw (),
                                                                       () -> FunctionExpression.methodRef (cm,
                                                                                                           TypedType.STRING,
                                                                                                           s -> IntExpression.of (cm.ref (Integer.class)
                                                                                                                                    .staticInvoke ("parseInt")
                                                                                                                                    .arg (s.raw ())))
                                                                                               .raw (),
                                                                       "java.lang.Integer::parseInt",
                                                                       null),
                                                    new MethodRefCase ("static Integer::toString is ambiguous",
                                                                       "Function <Integer, String>",
                                                                       FunctionExpression.lambda (TypedType.INTEGER,
                                                                                                  "i",
                                                                                                  i -> StringExpression.of (cm.ref (Integer.class)
                                                                                                                              .staticInvoke ("toString")
                                                                                                                              .arg (i.raw ())))
                                                                                         .raw (),
                                                                       () -> FunctionExpression.methodRef (cm,
                                                                                                           TypedType.INTEGER,
                                                                                                           i -> StringExpression.of (cm.ref (Integer.class)
                                                                                                                                       .staticInvoke ("toString")
                                                                                                                                       .arg (i.raw ())))
                                                                                               .raw (),
                                                                       null,
                                                                       "Integer::toString"),
                                                    new MethodRefCase ("bound t::concat",
                                                                       "Function <String, String>",
                                                                       FunctionExpression.lambda (TypedType.STRING,
                                                                                                  "s",
                                                                                                  aT::concat)
                                                                                         .raw (),
                                                                       () -> FunctionExpression.methodRef (cm,
                                                                                                           TypedType.STRING,
                                                                                                           aT::concat)
                                                                                               .raw (),
                                                                       "t::concat",
                                                                       null),
                                                    new MethodRefCase ("bound t::indexOf picks the String overload",
                                                                       "Function <String, Integer>",
                                                                       FunctionExpression.lambda (TypedType.STRING,
                                                                                                  "s",
                                                                                                  s -> aT.indexOf (s))
                                                                                         .raw (),
                                                                       () -> FunctionExpression.methodRef (cm,
                                                                                                           TypedType.STRING,
                                                                                                           s -> aT.indexOf (s))
                                                                                               .raw (),
                                                                       "t::indexOf",
                                                                       null),
                                                    new MethodRefCase ("bound (t + u)::concat",
                                                                       "Function <String, String>",
                                                                       FunctionExpression.lambda (TypedType.STRING,
                                                                                                  "s",
                                                                                                  s -> aT.plus (aU).concat (s))
                                                                                         .raw (),
                                                                       () -> FunctionExpression.methodRef (cm,
                                                                                                           TypedType.STRING,
                                                                                                           s -> aT.plus (aU)
                                                                                                                  .concat (s))
                                                                                               .raw (),
                                                                       "(t + u)::concat",
                                                                       null),
                                                    new MethodRefCase ("bound c::accept",
                                                                       "Consumer <String>",
                                                                       ConsumerExpression.lambda (TypedType.STRING,
                                                                                                  "s",
                                                                                                  (aBlock, s) -> aC.accept (s)
                                                                                                                   .addTo (aBlock))
                                                                                         .raw (),
                                                                       () -> ConsumerExpression.methodRef (cm,
                                                                                                           TypedType.STRING,
                                                                                                           aC::accept)
                                                                                               .raw (),
                                                                       "c::accept",
                                                                       null),
                                                    new MethodRefCase ("constructor StringBuilder::new",
                                                                       "Function <String, StringBuilder>",
                                                                       FunctionExpression.lambda (TypedType.STRING,
                                                                                                  "s",
                                                                                                  s -> ObjectExpression.<StringBuilder> of (JExpr._new (cm.ref (StringBuilder.class))
                                                                                                                                                 .arg (s.raw ())))
                                                                                         .raw (),
                                                                       () -> FunctionExpression.methodRef (cm,
                                                                                                           TypedType.STRING,
                                                                                                           s -> ObjectExpression.<StringBuilder> of (JExpr._new (cm.ref (StringBuilder.class))
                                                                                                                                                          .arg (s.raw ())))
                                                                                               .raw (),
                                                                       "java.lang.StringBuilder::new",
                                                                       null),
                                                    new MethodRefCase ("supplier StringBuilder::new",
                                                                       "Supplier <StringBuilder>",
                                                                       SupplierExpression.lambda (ObjectExpression.<StringBuilder> of (JExpr._new (cm.ref (StringBuilder.class))))
                                                                                         .raw (),
                                                                       () -> SupplierExpression.methodRef (ObjectExpression.<StringBuilder> of (JExpr._new (cm.ref (StringBuilder.class))))
                                                                                               .raw (),
                                                                       "java.lang.StringBuilder::new",
                                                                       null),
                                                    new MethodRefCase ("supplier of a generic constructor",
                                                                       "Supplier <java.util.List <String>>",
                                                                       SupplierExpression.lambda (ObjectExpression.<ArrayList <String>> of (JExpr._new (cm.ref (ArrayList.class)
                                                                                                                                                        .narrow (String.class))))
                                                                                         .raw (),
                                                                       () -> SupplierExpression.methodRef (ObjectExpression.<ArrayList <String>> of (JExpr._new (cm.ref (ArrayList.class)
                                                                                                                                                                 .narrow (String.class))))
                                                                                               .raw (),
                                                                       "java.util.ArrayList::new",
                                                                       null),
                                                    new MethodRefCase ("supplier t::length",
                                                                       "Supplier <Integer>",
                                                                       SupplierExpression.lambda (aT.length ()).raw (),
                                                                       () -> SupplierExpression.methodRef (aT.length ()).raw (),
                                                                       "t::length",
                                                                       null),
                                                    new MethodRefCase ("supplier (t + u)::length",
                                                                       "Supplier <Integer>",
                                                                       SupplierExpression.lambda (aT.plus (aU).length ()).raw (),
                                                                       () -> SupplierExpression.methodRef (aT.plus (aU).length ())
                                                                                               .raw (),
                                                                       "(t + u)::length",
                                                                       null),
                                                    new MethodRefCase ("receiver uses the parameter",
                                                                       "Function <String, String>",
                                                                       FunctionExpression.lambda (TypedType.STRING,
                                                                                                  "s",
                                                                                                  s -> s.concat (s))
                                                                                         .raw (),
                                                                       () -> FunctionExpression.methodRef (cm,
                                                                                                           TypedType.STRING,
                                                                                                           s -> s.concat (s))
                                                                                               .raw (),
                                                                       null,
                                                                       null),
                                                    new MethodRefCase ("parameter not passed",
                                                                       "Function <String, String>",
                                                                       FunctionExpression.lambda (TypedType.STRING,
                                                                                                  "s",
                                                                                                  s -> aT.concat (aU))
                                                                                         .raw (),
                                                                       () -> FunctionExpression.methodRef (cm,
                                                                                                           TypedType.STRING,
                                                                                                           s -> aT.concat (aU))
                                                                                               .raw (),
                                                                       null,
                                                                       null),
                                                    new MethodRefCase ("not an invocation",
                                                                       "Function <String, Integer>",
                                                                       FunctionExpression.lambda (TypedType.STRING,
                                                                                                  "s",
                                                                                                  s -> s.length ().plus (1))
                                                                                         .raw (),
                                                                       () -> FunctionExpression.methodRef (cm,
                                                                                                           TypedType.STRING,
                                                                                                           s -> s.length ()
                                                                                                                 .plus (1))
                                                                                               .raw (),
                                                                       null,
                                                                       null));

    final String sParams = "(String t, String u, Consumer <String> c)";
    final Map <String, String> aSources = new LinkedHashMap <> ();
    final List <String> aMismatches = new ArrayList <> ();
    int nRefs = 0;
    for (int i = 0; i < aRefCases.size (); i++)
    {
      final MethodRefCase aCase = aRefCases.get (i);
      aSources.put ("L" + i,
                    _source ("L" + i,
                             "static void t " +
                                   sParams +
                                   " { " +
                                   aCase.sTarget () +
                                   " r = " +
                                   CodeModelTestsHelper.toString (aCase.aLambda ()) +
                                   "; }"));
      String sRef;
      try
      {
        sRef = CodeModelTestsHelper.toString (aCase.aMethodRef ().get ());
        nRefs++;
      }
      catch (final IllegalArgumentException ex)
      {
        sRef = null;
      }
      if (sRef == null ? aCase.sExpected () != null : !sRef.equals (aCase.sExpected ()))
        aMismatches.add (aCase.sDescription () + ": expected " + aCase.sExpected () + ", created " + sRef);
      if (sRef != null)
        aSources.put ("M" + i, _source ("M" + i, "static void t " + sParams + " { " + aCase.sTarget () + " r = " + sRef + "; }"));
      if (aCase.sJavacRejects () != null)
        aSources.put ("J" + i,
                      _source ("J" + i,
                               "static void t " +
                                     sParams +
                                     " { " +
                                     aCase.sTarget () +
                                     " r = " +
                                     aCase.sJavacRejects () +
                                     "; }"));
    }

    final Map <String, String> aResults = _compile (aSources);
    for (int i = 0; i < aRefCases.size (); i++)
    {
      final MethodRefCase aCase = aRefCases.get (i);
      if (REJECTED.equals (aResults.get ("L" + i)))
        aMismatches.add (aCase.sDescription () + ": the lambda does not compile");
      if (aSources.containsKey ("M" + i) && REJECTED.equals (aResults.get ("M" + i)))
        aMismatches.add (aCase.sDescription () + ": the method reference does not compile");
      if (aSources.containsKey ("J" + i) && !REJECTED.equals (aResults.get ("J" + i)))
        aMismatches.add (aCase.sDescription () + ": javac accepts " + aCase.sJavacRejects ());
    }
    LOGGER.info ("Checked " + aRefCases.size () + " method reference cases against javac, " + nRefs + " were created");
    assertEquals ("Method references differing from lambdas: " + aMismatches, 0, aMismatches.size ());
  }
}
