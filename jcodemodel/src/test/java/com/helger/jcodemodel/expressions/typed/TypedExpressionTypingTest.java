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
  private static final String [] [] ARRAYS = { { "int []", "ArrayExpression <int [], IntExpression>" },
                                              { "int [] []",
                                                "ArrayExpression <int [] [], ArrayExpression <int [], IntExpression>>" },
                                              { "String []", "ArrayExpression <String [], StringExpression>" },
                                              { "Integer []", "ArrayExpression <Integer [], IntegerExpression>" },
                                              { "Object []", "ArrayExpression <Object [], ObjectExpression <Object>>" } };

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
                                                                             Map.entry ("java.lang.String",
                                                                                        "StringExpression"),
                                                                             Map.entry ("java.lang.Object",
                                                                                        "ObjectExpression<java.lang.Object>"),
                                                                             Map.entry ("int[]",
                                                                                        "ArrayExpression<int[],IntExpression>"));

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
    assertNotNull ("No mirror for result type " + sType, sMirror);
    return sMirror;
  }

  private static String _source (@NonNull final String sClassName, @NonNull final String sBody)
  {
    return "import " + PKG + "*;\nclass " + sClassName + " {\n" + sBody + "\n}\n";
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

    final Map <String, String> aResults = _compile (aSources);
    final List <String> aMismatches = new ArrayList <> ();
    for (final Case aCase : aCases)
    {
      final String sJava = _javaOutcome (aCase, aResults);
      final String sTyped = _typedOutcome (aCase, aResults);
      if (!sJava.equals (sTyped))
        aMismatches.add (aCase.sDescription () + ": javac " + sJava + ", typed " + sTyped);
    }
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
}
