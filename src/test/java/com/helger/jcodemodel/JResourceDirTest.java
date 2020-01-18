package com.helger.jcodemodel;

import static org.junit.Assert.fail;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.jcodemodel.fmt.JTextFile;

/**
 * Test class for class {@link JResourceDir}.
 *
 * @author guiguilechat
 * @author Philip Helger
 */
public final class JResourceDirTest
{
  @Test (expected = IllegalArgumentException.class)
  public void testAbsolutePath ()
  {
    final JCodeModel cm = new JCodeModel ();
    // this should fail
    cm.resourceDir ("/usr");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testAbsolutePath2 ()
  {
    final JCodeModel cm = new JCodeModel ();
    // this should fail
    cm.resourceDir ("\\usr");
  }

  public void testMultiSeparator ()
  {
    final JCodeModel cm = new JCodeModel ();
    // works because of internal unification
    cm.resourceDir ("usr//bla");
    cm.resourceDir ("usr////////////////////////////////////////////////////bla");
    cm.resourceDir ("usr//bla///////////////////////////////////////");
    cm.resourceDir ("usr//\\\\\\\\\\\\\\\\\\\\\\///\\\\\\\\bla\\\\\\\\\\\\/////");
  }

  @Test (expected = JResourceAlreadyExistsException.class)
  public void testResNameCollision () throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel ();
    cm.resourceDir ("my").addResourceFile (JTextFile.createFully ("File1", StandardCharsets.UTF_8, "bla"));
    // Same name in same folder - error
    cm.resourceDir ("my").addResourceFile (JTextFile.createFully ("File1", StandardCharsets.UTF_8, "bla"));
  }

  public void testResNameCollisionCaseInsensitive () throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel ();
    cm.resourceDir ("my").addResourceFile (JTextFile.createFully ("myFile", StandardCharsets.UTF_8, "bla"));
    if (JCodeModel.isFileSystemCaseSensitive ())
    {
      cm.resourceDir ("my").addResourceFile (JTextFile.createFully ("MYFILE", StandardCharsets.UTF_8, "bla"));
    }
    else
    {
      try
      {
        // Same upper case name in same folder - error
        cm.resourceDir ("my").addResourceFile (JTextFile.createFully ("MYFILE", StandardCharsets.UTF_8, "bla"));
        fail ();
      }
      catch (final JResourceAlreadyExistsException ex)
      {
        // expected
      }
    }
  }

  @Test (expected = JClassAlreadyExistsException.class)
  public void testClassNameCollision1 () throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel ();
    cm._package ("my")._class (JMod.PUBLIC, "Name");
    // this should fail
    cm.resourceDir ("my").addResourceFile (JTextFile.createFully ("Name.java", StandardCharsets.UTF_8, "bla"));
  }

  public void testClassNameCollisionCaseInsensitive () throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel ();
    cm._package ("my")._class (JMod.PUBLIC, "Name");
    if (JCodeModel.isFileSystemCaseSensitive ())
    {
      cm.resourceDir ("my").addResourceFile (JTextFile.createFully ("NAme.jaVA", StandardCharsets.UTF_8, "bla"));
    }
    else
    {
      try
      {
        // this should fail
        cm.resourceDir ("my").addResourceFile (JTextFile.createFully ("NAme.jaVA", StandardCharsets.UTF_8, "bla"));
        fail ();
      }
      catch (final JResourceAlreadyExistsException ex)
      {
        // expected
      }
    }
  }

  @Test (expected = JResourceAlreadyExistsException.class)
  public void testClassNameCollision2 () throws JCodeModelException
  {
    final JCodeModel cm = new JCodeModel ();
    cm.resourceDir ("my").addResourceFile (JTextFile.createFully ("Name.java", StandardCharsets.UTF_8, "bla"));
    // should fail
    cm._package ("my")._class (JMod.PUBLIC, "Name");
  }
}
