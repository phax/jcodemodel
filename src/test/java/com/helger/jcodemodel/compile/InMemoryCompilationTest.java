/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2025 Philip Helger + contributors
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
package com.helger.jcodemodel.compile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.junit.Test;

import com.helger.base.io.nonblocking.NonBlockingBufferedReader;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.fmt.JTextFile;
import com.helger.jcodemodel.writer.JCMWriter;

public final class InMemoryCompilationTest
{
  /**
   * create a new class in JCM that has toString() return a fixed value. check
   * if getting that class for the in-memory compiler allows to create a clas
   * with such a toString() value.
   *
   * @throws Exception
   *         If something fails
   */
  @Test
  public void testSimpleClassCreation () throws Exception
  {
    final String toStringVal = "TEST_VALUE";
    final JCodeModel cm = new JCodeModel ();

    final JDefinedClass jClass = cm._class (JMod.PUBLIC, "my.Clazz");
    final JMethod jMethodToString = jClass.method (JMod.PUBLIC, cm.ref (String.class), "toString");
    jMethodToString.annotate (Override.class);
    jMethodToString.body ()._return (JExpr.lit (toStringVal));

    final DynamicClassLoader aLoader = MemoryCodeWriter.from (cm).compile ();
    assertNotNull (aLoader);
    final Class <?> aFoundClass = aLoader.findClass (jClass.fullName ());
    assertNotNull (aFoundClass);
    assertEquals (toStringVal, aFoundClass.getConstructor ().newInstance ().toString ());
  }

  @Test
  public void testSimpleClassWithoutPackage () throws Exception
  {
    final String toStringVal = "TEST_VALUE";
    final JCodeModel cm = new JCodeModel ();

    final JDefinedClass jClass = cm._class (JMod.PUBLIC, "Clazz2");
    final JMethod jMethodToString = jClass.method (JMod.PUBLIC, cm.ref (String.class), "toString");
    jMethodToString.annotate (Override.class);
    jMethodToString.body ()._return (JExpr.lit (toStringVal));

    for (int i = 1; i < 3; ++i)
    {
      final DynamicClassLoader aLoader = MemoryCodeWriter.from (cm).compile ();
      assertNotNull (aLoader);
      final Class <?> aFoundClass = aLoader.findClass (jClass.fullName ());
      assertNotNull (aFoundClass);
      assertEquals (toStringVal, aFoundClass.getConstructor ().newInstance ().toString ());
    }
  }

  /**
   * create a new file text that contains a fixed value. Check if getting that
   * file from the in-memory platform returns the correct value, and if getting
   * it from the class loader also does.
   *
   * @throws Exception
   *         If something fails
   */
  @Test
  public void testSimpleResourceCreation () throws Exception
  {
    final String sContent = "TEST_VALUE";
    final String sDir = "my/test";
    final String sFilename = "File.txt";
    final String sFullFilename = sDir + "/" + sFilename;

    final JCodeModel cm = new JCodeModel ();
    cm.resourceDir (sDir).addResourceFile (JTextFile.createFully (sFilename, StandardCharsets.UTF_8, sContent));

    final MemoryCodeWriter aCodeWriter = new MemoryCodeWriter ();
    new JCMWriter (cm).setCharset (StandardCharsets.UTF_8).build (aCodeWriter);

    // check that in memory value is correct
    final String inMemoryString = aCodeWriter.getBinaries ().get (sFullFilename).getAsString (StandardCharsets.UTF_8);
    assertEquals (sContent, inMemoryString);

    // Check to read again
    final String inMemoryString2 = aCodeWriter.getBinaries ().get (sFullFilename).getAsString (StandardCharsets.UTF_8);
    assertNotSame (inMemoryString, inMemoryString2);
    assertEquals (inMemoryString, inMemoryString2);

    final DynamicClassLoader aLoader = aCodeWriter.compile ();
    assertNotNull (aLoader);
    try (final InputStream inCLInpuStream = aLoader.getResourceAsStream (sFullFilename))
    {
      assertNotNull (inCLInpuStream);
      try (final NonBlockingBufferedReader r = new NonBlockingBufferedReader (new InputStreamReader (inCLInpuStream,
                                                                                                     StandardCharsets.UTF_8)))
      {
        final String inCLString = r.readLine ();
        assertEquals (sContent, inCLString);
      }
    }
  }

  @Test
  public void testCompileError () throws Exception
  {
    final JCodeModel cm = new JCodeModel ();

    final JDefinedClass jClass = cm._class (JMod.PUBLIC, "com.example.TestError");
    final JMethod jMethodToString = jClass.method (JMod.PUBLIC, cm.ref (String.class), "toString");
    jMethodToString.annotate (Override.class);
    // Type error
    jMethodToString.body ()._return (JExpr.lit (42));

    final List <Diagnostic <? extends JavaFileObject>> aErrors = new ArrayList <> ();
    final DynamicClassLoader aLoader = MemoryCodeWriter.from (cm).setDiagnosticListener (aErrors::add).compile ();
    assertNull (aLoader);
    assertEquals (1, aErrors.size ());
  }
}
