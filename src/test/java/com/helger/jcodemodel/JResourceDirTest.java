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
