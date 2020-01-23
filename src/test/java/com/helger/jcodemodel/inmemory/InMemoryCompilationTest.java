package com.helger.jcodemodel.inmemory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JCodeModelException;
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
   */
  @Test
  public void testSimpleClassCreation ()
      throws JCodeModelException, ClassNotFoundException, InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
  {
    String toStringVal = "TEST_VALUE";
    JCodeModel codeModel = new JCodeModel (EFileSystemConvention.LINUX);
    JDefinedClass definedClass = codeModel._class (JMod.PUBLIC, "my.Clazz");
    JMethod toStringMeth = definedClass.method (JMod.PUBLIC, codeModel.ref (String.class), "toString");
    toStringMeth.body ()._return (JExpr.lit (toStringVal));
    DynamicClassLoader loader = MemoryCodeWriter.from (codeModel).compile ();
    Class <?> foundClass = loader.findClass (definedClass.fullName ());
    Assert.assertEquals (toStringVal, foundClass.getConstructor ().newInstance ().toString ());
  }

  /**
   * create a new file text that contains a fixed value. Check if getting that
   * file from the in-memory platform returns the correct value, and if getting
   * it from the class loader also does.
   *
   * @throws JCodeModelException
   * @throws IOException
   */
  @Test
  public void testSimpleResourceCreation () throws JCodeModelException, IOException
  {
    String toStringVal = "TEST_VALUE";
    String fileDir = "my/test";
    String fileName = "File.txt";
    String fileFullName = fileDir + "/" + fileName;
    JCodeModel codeModel = new JCodeModel (EFileSystemConvention.LINUX);
    codeModel.resourceDir (fileDir)
    .addResourceFile (JTextFile.createFully (fileName, StandardCharsets.UTF_8, toStringVal));
    MemoryCodeWriter codeWriter = new MemoryCodeWriter ();
    new JCMWriter (codeModel).build (codeWriter);
    String inMemoryString = codeWriter.getBinaries ().get (fileFullName).toString ();
    // check that in memory value is correct
    Assert.assertEquals (toStringVal, inMemoryString);
    DynamicClassLoader dynCL = codeWriter.compile ();
    InputStream inCLInpuStream = dynCL.getResourceAsStream (fileFullName);
    Assert.assertNotNull (inCLInpuStream);
    String inCLString = new BufferedReader (new InputStreamReader (inCLInpuStream)).readLine ();
    Assert.assertEquals (toStringVal, inCLString);
  }

}
