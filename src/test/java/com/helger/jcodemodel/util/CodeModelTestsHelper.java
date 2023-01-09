/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2020 Philip Helger + contributors
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
package com.helger.jcodemodel.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.helger.jcodemodel.IJDeclaration;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.IJFormatter;
import com.helger.jcodemodel.IJGenerable;
import com.helger.jcodemodel.IJStatement;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.SourcePrintWriter;
import com.helger.jcodemodel.writer.AbstractCodeWriter;
import com.helger.jcodemodel.writer.JCMWriter;
import com.helger.jcodemodel.writer.JFormatter;
import com.helger.jcodemodel.writer.OutputStreamCodeWriter;
import com.helger.jcodemodel.writer.SingleStreamCodeWriter;

/**
 * Various utilities for codemodel tests.
 *
 * @author Aleksei Valikov
 * @author Philip Helger
 */
public final class CodeModelTestsHelper
{
  public static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;

  @Nonnull
  private static IJFormatter _createFormatter (@Nonnull final StringWriter aWriter)
  {
    return new JFormatter (new SourcePrintWriter (aWriter, JCMWriter.getDefaultNewLine ()), JCMWriter.DEFAULT_INDENT_STRING);
  }

  /** Hidden constructor. */
  private CodeModelTestsHelper ()
  {}

  /**
   * Prints an expression into a string.
   *
   * @param aGenerable
   *        expression to print into a string.
   * @return Expression formatted as a string.
   */
  @Nonnull
  public static String toString (@Nonnull final IJExpression aGenerable)
  {
    JCValueEnforcer.notNull (aGenerable, "Generable");

    try (final StringWriter aSW = new StringWriter (); final IJFormatter aFormatter = _createFormatter (aSW))
    {
      aGenerable.generate (aFormatter);
      return aSW.toString ();
    }
    catch (final IOException ex)
    {
      throw new UncheckedIOException (ex);
    }
  }

  /**
   * Prints a declaration into a string.
   *
   * @param aDeclaration
   *        declaration to print into a string.
   * @return Declaration formatted as a string.
   */
  @Nonnull
  public static String toString (@Nonnull final IJDeclaration aDeclaration)
  {
    JCValueEnforcer.notNull (aDeclaration, "Declaration");

    try (final StringWriter aSW = new StringWriter (); final IJFormatter aFormatter = _createFormatter (aSW))
    {
      aDeclaration.declare (aFormatter);
      return aSW.toString ();
    }
    catch (final IOException ex)
    {
      throw new UncheckedIOException (ex);
    }
  }

  /**
   * Prints a statement into a string.
   *
   * @param aStatement
   *        declaration to print into a string.
   * @return Declaration formatted as a string.
   */
  @Nonnull
  public static String toString (@Nonnull final IJStatement aStatement)
  {
    JCValueEnforcer.notNull (aStatement, "Statement");

    try (final StringWriter aSW = new StringWriter (); final IJFormatter aFormatter = _createFormatter (aSW))
    {
      aStatement.state (aFormatter);
      return aSW.toString ();
    }
    catch (final IOException ex)
    {
      throw new UncheckedIOException (ex);
    }
  }

  @Nonnull
  public static String declare (@Nonnull final IJDeclaration aDeclaration)
  {
    JCValueEnforcer.notNull (aDeclaration, "Declaration");

    try (final StringWriter aSW = new StringWriter (); final IJFormatter aFormatter = _createFormatter (aSW))
    {
      aDeclaration.declare (aFormatter);
      return aSW.toString ();
    }
    catch (final IOException ex)
    {
      throw new UncheckedIOException (ex);
    }
  }

  @Nonnull
  public static String generate (@Nonnull final IJGenerable aGenerable)
  {
    JCValueEnforcer.notNull (aGenerable, "Generable");

    try (final StringWriter aSW = new StringWriter (); final IJFormatter aFormatter = _createFormatter (aSW))
    {
      aGenerable.generate (aFormatter);
      return aSW.toString ();
    }
    catch (final IOException ex)
    {
      throw new UncheckedIOException (ex);
    }
  }

  /**
   * Get the content of the code model as a byte array in
   * {@link #DEFAULT_ENCODING}
   *
   * @param cm
   *        Source code model
   * @return The byte array
   */
  @Nonnull
  public static byte [] getAllBytes (@Nonnull final JCodeModel cm)
  {
    try (final ByteArrayOutputStream aBAOS = new ByteArrayOutputStream ())
    {
      new JCMWriter (cm).build (new OutputStreamCodeWriter (aBAOS, DEFAULT_ENCODING));
      return aBAOS.toByteArray ();
    }
    catch (final IOException ex)
    {
      throw new UncheckedIOException (ex);
    }
  }

  @Nonnull
  private static CompilationUnit _parseWithJavaParser (final byte [] aBytes,
                                                       @Nullable final String sDirName,
                                                       final String sFilename) throws IOException
  {
    if (false)
    {
      System.out.println (new String (aBytes, DEFAULT_ENCODING));
    }

    final String sRealDirName = sDirName == null ? "" : sDirName;
    System.out.println ("Parsing " +
                        JCStringHelper.replaceAll (sRealDirName, '/', '.') +
                        (sRealDirName.length () > 0 ? "." : "") +
                        (sFilename.endsWith (".java") ? sFilename.substring (0, sFilename.length () - 5) : sFilename));

    try (final ByteArrayInputStream bis = new ByteArrayInputStream (aBytes))
    {
      // Parse what was written
      final ParseResult <CompilationUnit> ret = new JavaParser ().parse (bis, DEFAULT_ENCODING);
      return ret.getResult ().get ();
    }
  }

  @Nonnull
  private static org.eclipse.jdt.core.dom.CompilationUnit _parseWithJDT (final String sUnitName, final char [] aCode)
  {
    final ASTParser parser = ASTParser.newParser (AST.JLS16);
    parser.setResolveBindings (true);
    parser.setStatementsRecovery (true);
    parser.setBindingsRecovery (true);
    parser.setKind (ASTParser.K_COMPILATION_UNIT);
    parser.setSource (aCode);
    parser.setUnitName (sUnitName);
    final Map <String, String> aOptions = new HashMap <> ();
    aOptions.put (CompilerOptions.OPTION_Source, "1.8");
    parser.setCompilerOptions (aOptions);

    final IProgressMonitor aPM = null;
    final org.eclipse.jdt.core.dom.CompilationUnit astRoot = (org.eclipse.jdt.core.dom.CompilationUnit) parser.createAST (aPM);
    if (astRoot == null)
    {
      throw new IllegalStateException ("Failed to compile:\n" + new String (aCode));
    }
    if (false)
    {
      System.out.println (astRoot.toString ());
    }
    final IProblem [] aProblems = astRoot.getProblems ();
    if (aProblems != null && aProblems.length > 0)
    {
      throw new IllegalStateException ("Compilation problems " + Arrays.toString (aProblems));
    }
    return astRoot;
  }

  public static void parseCodeModel (@Nonnull final JCodeModel cm)
  {
    try
    {
      new JCMWriter (cm).build (new AbstractCodeWriter (DEFAULT_ENCODING, "\n")
      {
        @Override
        public OutputStream openBinary (final String sDirName, final String sFilename) throws IOException
        {
          return new ByteArrayOutputStream ()
          {
            @Override
            public void close () throws IOException
            {
              super.close ();

              final byte [] aBytes = toByteArray ();

              // Get result as bytes and parse
              _parseWithJavaParser (aBytes, sDirName, sFilename);
              _parseWithJDT (sFilename, new String (aBytes, DEFAULT_ENCODING).toCharArray ());
            }
          };
        }

        @Override
        public void close () throws IOException
        {}
      });
    }
    catch (final IOException ex)
    {
      throw new UncheckedIOException (ex);
    }
  }

  @Nonnegative
  private static int _count (final Iterator <?> x)
  {
    int ret = 0;
    while (x.hasNext ())
    {
      x.next ();
      ret++;
    }
    return ret;
  }

  @Nonnull
  public static CompilationUnit parseAndGetSingleClassCodeModel (@Nonnull final JCodeModel cm)
  {
    assert cm != null;
    assert _count (cm.packages ()) == 1;
    assert cm.packages ().next ().classes ().size () == 1;
    final byte [] aBytes = getAllBytes (cm);
    try
    {
      return _parseWithJavaParser (aBytes, null, "full-jcodemodel");
    }
    catch (final IOException ex)
    {
      throw new UncheckedIOException (ex);
    }
  }

  public static void printCodeModel (@Nonnull final JCodeModel cm)
  {
    try
    {
      new JCMWriter (cm).build (new SingleStreamCodeWriter (System.out));
    }
    catch (final IOException ex)
    {
      throw new UncheckedIOException (ex);
    }
  }
}
