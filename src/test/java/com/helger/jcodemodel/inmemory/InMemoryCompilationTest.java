package com.helger.jcodemodel.inmemory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

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
      try (final BufferedReader r = new BufferedReader (new InputStreamReader (inCLInpuStream)))
      {
        final String inCLString = r.readLine ();
        assertEquals (sContent, inCLString);
      }
    }
  }
}
