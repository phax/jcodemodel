package com.helger.jcodemodel.inmemory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
import com.helger.jcodemodel.util.EFileSystemConvention;
import com.helger.jcodemodel.writer.JCMWriter;

public class InMemoryCompilationTest
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

    final DynamicClassLoader loader = MemoryCodeWriter.from (cm).compile ();
    final Class <?> foundClass = loader.findClass (jClass.fullName ());
    assertEquals (toStringVal, foundClass.getConstructor ().newInstance ().toString ());
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
    final String toStringVal = "TEST_VALUE";
    final String fileDir = "my/test";
    final String fileName = "File.txt";
    final String fileFullName = fileDir + "/" + fileName;
    final JCodeModel codeModel = new JCodeModel (EFileSystemConvention.LINUX);
    codeModel.resourceDir (fileDir).addResourceFile (JTextFile.createFully (fileName, StandardCharsets.UTF_8, toStringVal));
    final MemoryCodeWriter codeWriter = new MemoryCodeWriter ();
    new JCMWriter (codeModel).build (codeWriter);
    final String inMemoryString = codeWriter.getBinaries ().get (fileFullName).toString ();
    // check that in memory value is correct
    assertEquals (toStringVal, inMemoryString);
    final DynamicClassLoader dynCL = codeWriter.compile ();
    final InputStream inCLInpuStream = dynCL.getResourceAsStream (fileFullName);
    assertNotNull (inCLInpuStream);
    final String inCLString = new BufferedReader (new InputStreamReader (inCLInpuStream)).readLine ();
    assertEquals (toStringVal, inCLString);
  }
}
