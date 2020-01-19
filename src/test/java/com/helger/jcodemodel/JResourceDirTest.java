/**
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
package com.helger.jcodemodel;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.jcodemodel.fmt.JTextFile;
import com.helger.jcodemodel.util.EFileSystemConvention;

/**
 * Test class for class {@link JResourceDir}.
 *
 * @author guiguilechat
 * @author Philip Helger
 */
public final class JResourceDirTest
{
  @Test
  public void testAbsolutePath () throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel ();
    try
    {
      // this should fail
      cm.resourceDir ("/usr");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }

    try
    {
      // this should fail
      cm.resourceDir ("\\usr");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {
      // expected
    }
  }

  @Test
  public void testMultipleSeparator () throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel ();
    // works because of internal unifications
    cm.resourceDir ("usr//bla");
    cm.resourceDir ("usr////////////////////////////////////////////////////bla");
    cm.resourceDir ("usr//bla///////////////////////////////////////");
    cm.resourceDir ("usr//\\\\\\\\\\\\\\\\\\\\\\///\\\\\\\\bla\\\\\\\\\\\\/////");
  }

  @Test
  public void testDirectVsSubDir () throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel ();
    final JResourceDir rd = cm.resourceDir ("a/b");
    assertNotNull (rd);
    final JResourceDir rd2 = cm.resourceDir ("a").subDir ("b");
    assertSame (rd, rd2);
    final JResourceDir rd3 = cm.resourceDir ("a").subDir ("b/c");
    assertSame (rd2, rd3.parent ());
  }

  @Test
  public void testCollisionFilenameFilenameLinux () throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel ().setFileSystemConvention (EFileSystemConvention.LINUX);
    cm.resourceDir ("my").addResourceFile (JTextFile.createFully ("File1", StandardCharsets.UTF_8, "bla"));
    try
    {
      // Same name in same folder - error
      cm.resourceDir ("my").addResourceFile (JTextFile.createFully ("File1", StandardCharsets.UTF_8, "bla"));
      fail ();
    }
    catch (final JResourceAlreadyExistsException ex)
    {
      // expected
    }
    // Different casing is okay
    cm.resourceDir ("my").addResourceFile (JTextFile.createFully ("file1", StandardCharsets.UTF_8, "bla"));
  }

  @Test
  public void testCollisionFilenameFilenameWindows () throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel ().setFileSystemConvention (EFileSystemConvention.WINDOWS);
    cm.resourceDir ("my").addResourceFile (JTextFile.createFully ("File1", StandardCharsets.UTF_8, "bla"));
    try
    {
      // Same name in same folder - error
      cm.resourceDir ("my").addResourceFile (JTextFile.createFully ("File1", StandardCharsets.UTF_8, "bla"));
      fail ();
    }
    catch (final JResourceAlreadyExistsException ex)
    {
      // expected
    }
    try
    {
      // Different casing is still an error
      cm.resourceDir ("my").addResourceFile (JTextFile.createFully ("file1", StandardCharsets.UTF_8, "bla"));
      fail ();
    }
    catch (final JResourceAlreadyExistsException ex)
    {
      // expected
    }
  }

  @Test
  public void testCollisionFilenameDirNameLinux () throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel ().setFileSystemConvention (EFileSystemConvention.LINUX);
    cm.resourceDir ("my").addResourceFile (JTextFile.createFully ("name", StandardCharsets.UTF_8, "bla"));
    try
    {
      // should fail
      cm.resourceDir ("my").subDir ("name");
      fail ();
    }
    catch (final JResourceAlreadyExistsException ex)
    {
      // expected
    }
    // Different case should work
    cm.resourceDir ("my").subDir ("Name");
  }

  @Test
  public void testCollisionFilenameDirNameWindows () throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel ().setFileSystemConvention (EFileSystemConvention.WINDOWS);
    cm.resourceDir ("my").addResourceFile (JTextFile.createFully ("name", StandardCharsets.UTF_8, "bla"));
    try
    {
      // should fail
      cm.resourceDir ("my").subDir ("name");
      fail ();
    }
    catch (final JResourceAlreadyExistsException ex)
    {
      // expected
    }
    try
    {
      // Different case does not help
      cm.resourceDir ("my").subDir ("Name");
      fail ();
    }
    catch (final JResourceAlreadyExistsException ex)
    {
      // expected
    }
  }

  @Test
  public void testCollisionFilenameClassNameLinux () throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel ().setFileSystemConvention (EFileSystemConvention.LINUX);
    cm.resourceDir ("my").addResourceFile (JTextFile.createFully ("Name.java", StandardCharsets.UTF_8, "bla"));
    try
    {
      // should fail
      cm._package ("my")._class (JMod.PUBLIC, "Name");
      fail ();
    }
    catch (final JResourceAlreadyExistsException ex)
    {
      // expected
    }
    // Different case should work
    cm._package ("my")._class (JMod.PUBLIC, "name");
  }

  @Test
  public void testCollisionFilenameClassNameWindows () throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel ().setFileSystemConvention (EFileSystemConvention.WINDOWS);
    cm.resourceDir ("my").addResourceFile (JTextFile.createFully ("Name.java", StandardCharsets.UTF_8, "bla"));
    try
    {
      // should fail
      cm._package ("my")._class (JMod.PUBLIC, "Name");
      fail ();
    }
    catch (final JResourceAlreadyExistsException ex)
    {
      // expected
    }
    try
    {
      // Different case doesn't help
      cm._package ("my")._class (JMod.PUBLIC, "name");
      fail ();
    }
    catch (final JResourceAlreadyExistsException ex)
    {
      // expected
    }
  }

  @Test
  public void testCollisionClassNameFilenameLinux () throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel ().setFileSystemConvention (EFileSystemConvention.LINUX);
    cm._package ("my")._class (JMod.PUBLIC, "Name");
    try
    {
      // this should fail
      cm.resourceDir ("my").addResourceFile (JTextFile.createFully ("Name.java", StandardCharsets.UTF_8, "bla"));
      fail ();
    }
    catch (final JClassAlreadyExistsException ex)
    {
      // expected
    }
    // Adding a different case is totally fine
    cm.resourceDir ("my").addResourceFile (JTextFile.createFully ("NAme.jaVA", StandardCharsets.UTF_8, "bla"));
  }

  @Test
  public void testCollisionClassNameFilenameWindows () throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel ().setFileSystemConvention (EFileSystemConvention.WINDOWS);
    cm._package ("my")._class (JMod.PUBLIC, "Name");
    try
    {
      // this should fail
      cm.resourceDir ("my").addResourceFile (JTextFile.createFully ("Name.java", StandardCharsets.UTF_8, "bla"));
      fail ();
    }
    catch (final JClassAlreadyExistsException ex)
    {
      // expected
    }
    try
    {
      // Adding a different case doesn't help either
      cm.resourceDir ("my").addResourceFile (JTextFile.createFully ("NAme.jaVA", StandardCharsets.UTF_8, "bla"));
      fail ();
    }
    catch (final JClassAlreadyExistsException ex)
    {
      // expected
    }
  }

  @Test
  public void testCollisionClassNameDirNameLinux () throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel ().setFileSystemConvention (EFileSystemConvention.LINUX);
    cm._package ("my")._class (JMod.PUBLIC, "Name");
    try
    {
      // this should fail
      cm.resourceDir ("my").subDir ("Name.java");
      fail ();
    }
    catch (final JClassAlreadyExistsException ex)
    {
      // expected
    }
    // Adding a different case is totally fine
    cm.resourceDir ("my").subDir ("NAme.jaVA");
  }

  @Test
  public void testCollisionClassNameDirNameWindows () throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel ().setFileSystemConvention (EFileSystemConvention.WINDOWS);
    cm._package ("my")._class (JMod.PUBLIC, "Name");
    try
    {
      // this should fail
      cm.resourceDir ("my").subDir ("Name.java");
      fail ();
    }
    catch (final JClassAlreadyExistsException ex)
    {
      // expected
    }
    try
    {
      // Adding a different case doesn't help either
      cm.resourceDir ("my").subDir ("NAme.jaVA");
      fail ();
    }
    catch (final JClassAlreadyExistsException ex)
    {
      // expected
    }
  }

  @Test
  public void testCollisionDirNameFilenameLinux () throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel ().setFileSystemConvention (EFileSystemConvention.LINUX);
    cm.resourceDir ("my").subDir ("name");
    try
    {
      // should fail
      cm.resourceDir ("my").addResourceFile (JTextFile.createFully ("name", StandardCharsets.UTF_8, "bla"));
      fail ();
    }
    catch (final JResourceAlreadyExistsException ex)
    {
      // expected
    }
    // Different case should work
    cm.resourceDir ("my").addResourceFile (JTextFile.createFully ("Name", StandardCharsets.UTF_8, "bla"));
  }

  @Test
  public void testCollisionDirNameFilenameWindows () throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel ().setFileSystemConvention (EFileSystemConvention.WINDOWS);
    cm.resourceDir ("my").subDir ("name");
    try
    {
      // should fail
      cm.resourceDir ("my").addResourceFile (JTextFile.createFully ("name", StandardCharsets.UTF_8, "bla"));
      fail ();
    }
    catch (final JResourceAlreadyExistsException ex)
    {
      // expected
    }
    try
    {
      // Different case doesn't help
      cm.resourceDir ("my").addResourceFile (JTextFile.createFully ("Name", StandardCharsets.UTF_8, "bla"));
      fail ();
    }
    catch (final JResourceAlreadyExistsException ex)
    {
      // expected
    }
  }

  @Test
  public void testCollisionDirNameClassNameLinux () throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel ().setFileSystemConvention (EFileSystemConvention.LINUX);
    cm.resourceDir ("my").subDir ("Name.java");
    try
    {
      // should fail
      cm._package ("my")._class (JMod.PUBLIC, "Name");
      fail ();
    }
    catch (final JResourceAlreadyExistsException ex)
    {
      // expected
    }
    // Different case should work
    cm._package ("my")._class (JMod.PUBLIC, "name");
  }

  @Test
  public void testCollisionDirNameClassNameWindows () throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel ().setFileSystemConvention (EFileSystemConvention.WINDOWS);
    cm.resourceDir ("my").subDir ("Name.java");
    try
    {
      // should fail
      cm._package ("my")._class (JMod.PUBLIC, "Name");
      fail ();
    }
    catch (final JResourceAlreadyExistsException ex)
    {
      // expected
    }
    try
    {
      // Different case doesn't help either
      cm._package ("my")._class (JMod.PUBLIC, "name");
      fail ();
    }
    catch (final JResourceAlreadyExistsException ex)
    {
      // expected
    }
  }
}
