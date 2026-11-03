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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.Test;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JForLoop;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.compile.DynamicClassLoader;
import com.helger.jcodemodel.compile.MemoryCodeWriter;
import com.helger.jcodemodel.util.CodeModelTestsHelper;

/**
 * Generates code with the typed expressions, compiles it and runs it.
 */
public final class TypedExpressionGenerationTest
{
  @Test
  public void testExpressionText ()
  {
    final JCodeModel cm = new JCodeModel ();
    final JMethod aMethod = cm.anonymousClass (Object.class).method (JMod.PUBLIC, cm.VOID, "m");
    final StringExpression aStr = StringExpression.param (aMethod, "s");

    assertEquals ("s.charAt(s.length()- 1)",
                  CodeModelTestsHelper.toString (aStr.charAt (aStr.length ().minus (1)).raw ()));
  }

  @Test
  @SuppressWarnings ("unchecked")
  public void testGenerateCompileAndRun () throws Exception
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass aClass = cm._class ("com.example.typed.Generated");

    // static boolean isPalindrome (String s)
    {
      final JMethod aMethod = aClass.method (JMod.PUBLIC | JMod.STATIC, cm.BOOLEAN, "isPalindrome");
      final StringExpression aStr = StringExpression.param (aMethod, "s");
      final JForLoop aLoop = aMethod.body ()._for ();
      final IntExpression aIdx = IntExpression.of (aLoop.init (cm.INT, "i", IntExpression.lit (0).raw ()));
      aLoop.test (aIdx.lt (aStr.length ().div (2)).raw ());
      aLoop.update (aIdx.raw ().incr ());
      aLoop.body ()
           ._if (aStr.charAt (aIdx).ne (aStr.charAt (aStr.length ().minus (1).minus (aIdx))).raw ())
           ._then ()
           ._return (BooleanExpression.lit (false).raw ());
      aMethod.body ()._return (BooleanExpression.lit (true).raw ());
    }

    // static char at (String s, Integer idx): the Integer is unboxed implicitly, as in Java
    {
      final JMethod aMethod = aClass.method (JMod.PUBLIC | JMod.STATIC, cm.CHAR, "at");
      final StringExpression aStr = StringExpression.param (aMethod, "s");
      final IntegerExpression aIdx = IntegerExpression.param (aMethod, "idx");
      aMethod.body ()._return (aStr.charAt (aIdx).raw ());
    }

    // static long next (Character c): Character + long is long
    {
      final JMethod aMethod = aClass.method (JMod.PUBLIC | JMod.STATIC, cm.LONG, "next");
      final CharacterExpression aChar = CharacterExpression.param (aMethod, "c");
      final LongExpression aResult = aChar.plus (1L);
      aMethod.body ()._return (aResult.raw ());
    }

    // static int totalLength (String [] a)
    {
      final JMethod aMethod = aClass.method (JMod.PUBLIC | JMod.STATIC, cm.INT, "totalLength");
      final var aArray = ArrayExpression.param (aMethod, ArrayType.of (String.class, StringExpression::of), "a");
      final var aSum = TypedVariable.decl (cm, aMethod.body (), TypedType.INT, "sum", IntExpression.lit (0));
      final JForLoop aLoop = aMethod.body ()._for ();
      final var aIdx = TypedVariable.of (TypedType.INT, aLoop.init (cm.INT, "i", IntExpression.lit (0).raw ()));
      aLoop.test (aIdx.get ().lt (aArray.length ()).raw ());
      aLoop.update (TypedVariable.postincr (aIdx).raw ());
      aSum.assign (aLoop.body (), aSum.get ().plus (aArray.component (aIdx.get ()).length ()));
      aMethod.body ()._return (aSum.get ().raw ());
    }

    // static int [] squares (int n): stores into array components
    {
      final JMethod aMethod = aClass.method (JMod.PUBLIC | JMod.STATIC, cm.INT.array (), "squares");
      final IntExpression aN = IntExpression.param (aMethod, "n");
      final var aResult = TypedVariable.decl (cm,
                                              aMethod.body (),
                                              ArrayType.INT.asType (),
                                              "ret",
                                              ArrayExpression.newArray (cm, ArrayType.INT, aN));
      final JForLoop aLoop = aMethod.body ()._for ();
      final var aIdx = TypedVariable.of (TypedType.INT, aLoop.init (cm.INT, "i", IntExpression.lit (0).raw ()));
      aLoop.test (aIdx.get ().lt (aN).raw ());
      aLoop.update (TypedVariable.preincr (aIdx).raw ());
      aResult.get ().componentVar (aIdx.get ()).assign (aLoop.body (), aIdx.get ().mul (aIdx.get ()));
      aMethod.body ()._return (aResult.get ().raw ());
    }

    // static int [] [] grid (): a two-dimensional array literal
    {
      final JMethod aMethod = aClass.method (JMod.PUBLIC | JMod.STATIC, cm.INT.array ().array (), "grid");
      final var aRow1 = ArrayExpression.newArrayOf (cm, ArrayType.INT, IntExpression.lit (1), IntExpression.lit (2));
      final var aRow2 = ArrayExpression.newArrayOf (cm, ArrayType.INT, IntExpression.lit (3));
      aMethod.body ()._return (ArrayExpression.newArrayOf (cm, ArrayType.INT.array (), aRow1, aRow2).raw ());
    }

    // static Predicate <String> nonEmpty ()
    {
      final JMethod aMethod = aClass.method (JMod.PUBLIC | JMod.STATIC,
                                             PredicateExpression.type (TypedType.STRING).type (cm),
                                             "nonEmpty");
      aMethod.body ()._return (PredicateExpression.lambda (TypedType.STRING, "s", s -> s.isEmpty ().not ()).raw ());
    }

    // static Function <String, Integer> len ()
    {
      final JMethod aMethod = aClass.method (JMod.PUBLIC | JMod.STATIC,
                                             FunctionExpression.type (TypedType.STRING, TypedType.INTEGER).type (cm),
                                             "len");
      aMethod.body ()._return (FunctionExpression.lambda (TypedType.STRING, "s", StringExpression::length).raw ());
    }

    // static int count (String [] a, Predicate <String> p)
    {
      final JMethod aMethod = aClass.method (JMod.PUBLIC | JMod.STATIC, cm.INT, "count");
      final var aArray = ArrayExpression.param (aMethod, ArrayType.of (String.class, StringExpression::of), "a");
      final var aPredicate = TypedVariable.param (aMethod, PredicateExpression.type (TypedType.STRING), "p");
      final var aCount = TypedVariable.decl (cm, aMethod.body (), TypedType.INT, "n", IntExpression.lit (0));
      final JForLoop aLoop = aMethod.body ()._for ();
      final var aIdx = TypedVariable.of (TypedType.INT, aLoop.init (cm.INT, "i", IntExpression.lit (0).raw ()));
      aLoop.test (aIdx.get ().lt (aArray.length ()).raw ());
      aLoop.update (TypedVariable.postincr (aIdx).raw ());
      aCount.assign (aLoop.body ()._if (aPredicate.get ().test (aArray.component (aIdx.get ())).raw ())._then (),
                     aCount.get ().plus (1));
      aMethod.body ()._return (aCount.get ().raw ());
    }

    // static void each (String [] a, Consumer <String> c): accept is void and can only become a statement
    {
      final JMethod aMethod = aClass.method (JMod.PUBLIC | JMod.STATIC, cm.VOID, "each");
      final var aArray = ArrayExpression.param (aMethod, ArrayType.of (String.class, StringExpression::of), "a");
      final var aConsumer = TypedVariable.param (aMethod, ConsumerExpression.type (TypedType.STRING), "c");
      final JForLoop aLoop = aMethod.body ()._for ();
      final var aIdx = TypedVariable.of (TypedType.INT, aLoop.init (cm.INT, "i", IntExpression.lit (0).raw ()));
      aLoop.test (aIdx.get ().lt (aArray.length ()).raw ());
      aLoop.update (TypedVariable.postincr (aIdx).raw ());
      aConsumer.get ().accept (aArray.component (aIdx.get ()).plus ("!")).addTo (aLoop.body ());
    }

    // static int lengthOrZero (Object o): if (o instanceof String s && !s.isEmpty ()) return s.length ();
    {
      final JMethod aMethod = aClass.method (JMod.PUBLIC | JMod.STATIC, cm.INT, "lengthOrZero");
      final var aObj = TypedVariable.param (aMethod, TypedType.object (Object.class), "o").get ();
      final var aPattern = aObj.instanceOfPattern (cm, String.class, "s", StringExpression::of);
      aMethod.body ()
             ._if (aPattern.test ().cand (aPattern.var ().isEmpty ().not ()).raw ())
             ._then ()
             ._return (aPattern.var ().length ().raw ());
      aMethod.body ()._return (IntExpression.lit (0).raw ());
    }

    // static long max (int a, long b): the conditional promotes to long
    {
      final JMethod aMethod = aClass.method (JMod.PUBLIC | JMod.STATIC, cm.LONG, "max");
      final IntExpression aA = IntExpression.param (aMethod, "a");
      final LongExpression aB = LongExpression.param (aMethod, "b");
      final LongExpression aMax = aA.gt (aB).cond (aA, aB);
      aMethod.body ()._return (aMax.raw ());
    }

    // static long weigh (Long a, Short b, Boolean c): implicit unboxing of all wrappers
    {
      final JMethod aMethod = aClass.method (JMod.PUBLIC | JMod.STATIC, cm.LONG, "weigh");
      final LongObjExpression aA = LongObjExpression.param (aMethod, "a");
      final ShortObjExpression aB = ShortObjExpression.param (aMethod, "b");
      final BooleanObjExpression aC = BooleanObjExpression.param (aMethod, "c");
      final LongExpression aResult = aC.cond (aA.plus (aB), aA.minus ().shl (aB));
      aMethod.body ()._return (aResult.raw ());
    }

    final DynamicClassLoader aLoader = MemoryCodeWriter.from (cm).compile ();
    final Class <?> aCompiled = aLoader.loadClass ("com.example.typed.Generated");

    final Method aIsPalindrome = aCompiled.getMethod ("isPalindrome", String.class);
    assertEquals (Boolean.TRUE, aIsPalindrome.invoke (null, "abba"));
    assertEquals (Boolean.TRUE, aIsPalindrome.invoke (null, "kayak"));
    assertEquals (Boolean.FALSE, aIsPalindrome.invoke (null, "abca"));

    assertEquals (Character.valueOf ('c'),
                  aCompiled.getMethod ("at", String.class, Integer.class).invoke (null, "abc", Integer.valueOf (2)));
    assertEquals (Long.valueOf ('b'),
                  aCompiled.getMethod ("next", Character.class).invoke (null, Character.valueOf ('a')));
    assertEquals (Integer.valueOf (6),
                  aCompiled.getMethod ("totalLength", String [].class)
                           .invoke (null, (Object) new String [] { "a", "bc", "def" }));
    final Object aNonEmpty = aCompiled.getMethod ("nonEmpty").invoke (null);
    assertEquals (Integer.valueOf (2),
                  aCompiled.getMethod ("count", String [].class, Predicate.class)
                           .invoke (null, new String [] { "", "a", "bc" }, aNonEmpty));
    assertEquals (Integer.valueOf (3), ((Function <String, ?>) aCompiled.getMethod ("len").invoke (null)).apply ("abc"));
    final List <String> aCollected = new ArrayList <> ();
    final Consumer <String> aCollector = aCollected::add;
    aCompiled.getMethod ("each", String [].class, Consumer.class)
             .invoke (null, new String [] { "x", "y" }, aCollector);
    assertEquals (List.of ("x!", "y!"), aCollected);
    final Method aLengthOrZero = aCompiled.getMethod ("lengthOrZero", Object.class);
    assertEquals (Integer.valueOf (3), aLengthOrZero.invoke (null, "abc"));
    assertEquals (Integer.valueOf (0), aLengthOrZero.invoke (null, ""));
    assertEquals (Integer.valueOf (0), aLengthOrZero.invoke (null, Integer.valueOf (5)));
    final Method aMax = aCompiled.getMethod ("max", int.class, long.class);
    assertEquals (Long.valueOf (7), aMax.invoke (null, Integer.valueOf (7), Long.valueOf (3)));
    assertEquals (Long.valueOf (9), aMax.invoke (null, Integer.valueOf (7), Long.valueOf (9)));
    final Method aWeigh = aCompiled.getMethod ("weigh", Long.class, Short.class, Boolean.class);
    assertEquals (Long.valueOf (12), aWeigh.invoke (null, Long.valueOf (10), Short.valueOf ((short) 2), Boolean.TRUE));
    assertEquals (Long.valueOf (-40), aWeigh.invoke (null, Long.valueOf (10), Short.valueOf ((short) 2), Boolean.FALSE));
    assertArrayEquals (new int [] { 0, 1, 4, 9 },
                       (int []) aCompiled.getMethod ("squares", int.class).invoke (null, Integer.valueOf (4)));
    assertArrayEquals (new int [] [] { { 1, 2 }, { 3 } }, (int [] []) aCompiled.getMethod ("grid").invoke (null));
  }
}
