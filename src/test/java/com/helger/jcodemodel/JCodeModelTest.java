/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2021 Philip Helger + contributors
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.helger.jcodemodel.exceptions.JCaseSensitivityChangeException;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.exceptions.JInvalidFileNameException;
import com.helger.jcodemodel.fmt.JTextFile;
import com.helger.jcodemodel.util.CodeModelTestsHelper;
import com.helger.jcodemodel.util.EFileSystemConvention;
import com.helger.jcodemodel.util.IFileSystemConvention;

/**
 * @author Kohsuke Kawaguchi
 * @author Philip Helger
 */
public final class JCodeModelTest
{
  @Test
  public void testParseArray () throws Exception
  {
    final JCodeModel cm = JCodeModel.createUnified ();
    assertNotNull (cm.parseType ("java.util.ArrayList<java.lang.String[]>[]"));
    assertNotNull (cm.parseType ("java.util.ArrayList<java.util.ArrayList<java.util.ArrayList<java.lang.String[]>[]>[]>[]"));
    assertNotNull (cm.parseType ("java.util.Comparator<? super java.lang.CharSequence[]>[]"));
  }

  @Test
  public void testIssue28 () throws Exception
  {
    final JCodeModel cm = JCodeModel.createUnified ();

    final JDefinedClass aEnumClass = cm._package ("com.helger.issue28")._enum ("DummyEnum");
    aEnumClass.enumConstant ("CONSTANT");

    cm._package ("com.helger.issue28.other")
      ._class ("Class")
      .constructor (JMod.PUBLIC)
      .body ()
      .add (JExpr.enumConstantRef (aEnumClass, "CONSTANT").invoke ("toString"));
    CodeModelTestsHelper.parseCodeModel (cm);
    CodeModelTestsHelper.compileCodeModel (cm);
  }

  @Test
  public void testRefClass ()
  {
    final JCodeModel cm = JCodeModel.createUnified ();
    assertSame (cm.INT, cm._ref (int.class));
  }

  @Test
  public void testCODEMODEL24 () throws JCodeModelException
  {
    final JCodeModel cm = JCodeModel.createUnified ();
    final JDefinedClass jClass = cm._class ("dummy", EClassType.INTERFACE);
    assertEquals ("dummy", jClass.name ());
    assertEquals (EClassType.INTERFACE, jClass.getClassType ());
  }

  @Test
  public void testEmptyNarrowed () throws JCodeModelException
  {
    final JCodeModel cm = JCodeModel.createUnified ();
    final JDefinedClass jClass = cm._class ("EmptyNarrowed", EClassType.CLASS);
    final AbstractJClass hashMap = cm.ref (java.util.HashMap.class).narrowEmpty ();
    jClass.field (JMod.PRIVATE, cm.ref (Map.class).narrow (String.class).narrow (String.class), "strMap", JExpr._new (hashMap));

    CodeModelTestsHelper.parseCodeModel (cm);
    CodeModelTestsHelper.compileCodeModel (cm);
  }

  @Test
  public void testIssue71 () throws Exception
  {
    final JCodeModel cm = JCodeModel.createUnified ();
    final JDefinedClass aOtherByteClass = cm._package ("com.helger.issue71")._class ("Byte");
    final JDefinedClass aFooClass = cm._package ("com.helger.issue71")._class ("Foo");
    final JDefinedClass aClass2 = cm._package ("com.helger.issue71.second")._class ("Class2");
    // The reference in the second class may not be imported:
    aClass2.method (JMod.PUBLIC, aOtherByteClass, "testByte").body ()._return (JExpr._null ());
    // Whereas the Foo class may be imported
    aClass2.method (JMod.PUBLIC, aFooClass, "testFoo").body ()._return (JExpr._null ());

    CodeModelTestsHelper.parseCodeModel (cm);
    CodeModelTestsHelper.compileCodeModel (cm);
  }

  @Test
  public void testIssue71v2 () throws Exception
  {
    final JCodeModel cm = JCodeModel.createUnified ();
    final JDefinedClass cl = cm._package ("jcodemodel")._class ("MyClass");
    final JDefinedClass clo = cl._class (JMod.PUBLIC | JMod.STATIC, "Object");
    cl.method (JMod.PUBLIC, cm.VOID, "call").param (cm.ref (Object.class), "obj");
    cl.method (JMod.PUBLIC, cm.VOID, "call").param (clo, "obj");
    final JDefinedClass cln = cl._class (JMod.PUBLIC | JMod.STATIC, "Number");
    cl.method (JMod.PUBLIC, cm.VOID, "call").param (cm.ref (Number.class), "obj");
    cl.method (JMod.PUBLIC, cm.VOID, "call").param (cln, "obj");
    cl.method (JMod.PUBLIC, cm.VOID, "call").param (cm.ref (Byte.class), "obj");
    cl.method (JMod.PUBLIC, cm.VOID, "call").param (cm.ref (Long.class), "obj");

    CodeModelTestsHelper.parseCodeModel (cm);
    CodeModelTestsHelper.compileCodeModel (cm);
  }

  @Test
  public void testChangePlatform () throws JCodeModelException
  {
    JCodeModel cm = JCodeModel.createUnified ();

    cm._class (JMod.PUBLIC, "my.Precious");
    try
    {
      // should fail, the package "my" is translated to a dir.
      cm.setFileSystemConvention (EFileSystemConvention.WINDOWS);
      Assert.fail ();
    }
    catch (final JCaseSensitivityChangeException jcsce)
    {
      // correct
    }

    cm = JCodeModel.createUnified ();
    cm.resourceDir ("my").addResourceFile (JTextFile.createFully ("File1", StandardCharsets.UTF_8, "bla"));
    try
    {
      // should fail, because the windows FS is not case sensitive.
      cm.setFileSystemConvention (EFileSystemConvention.WINDOWS);
      Assert.fail ();
    }
    catch (final JCaseSensitivityChangeException jcsce)
    {
      // correct
    }

    // should pass, accept any resource name
    cm.setFileSystemConvention (new IFileSystemConvention ()
    {

      @Override
      public boolean isValidFilename (final String sPath)
      {
        return true;
      }

      @Override
      public boolean isValidDirectoryName (final String sPath)
      {
        return true;
      }

      @Override
      public boolean isCaseSensistive ()
      {
        return true;
      }
    });
    try
    {
      // should fail, existing dir "my" and file "File1" are not accepted.
      cm.setFileSystemConvention (new IFileSystemConvention ()
      {
        @Override
        public boolean isValidFilename (final String sPath)
        {
          return false;
        }

        @Override
        public boolean isValidDirectoryName (final String sPath)
        {
          return false;
        }

        @Override
        public boolean isCaseSensistive ()
        {
          return true;
        }
      });
      Assert.fail ();
    }
    catch (final JInvalidFileNameException ifne)
    {
      // correct
    }
  }
}
