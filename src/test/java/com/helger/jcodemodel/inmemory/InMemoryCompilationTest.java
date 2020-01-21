package com.helger.jcodemodel.inmemory;

import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.junit.Test;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JCodeModelException;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.util.EFileSystemConvention;

public class InMemoryCompilationTest {

  @Test
  public void testSimpleClassCreation()
      throws JCodeModelException, ClassNotFoundException, InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
    JCodeModel codeModel = new JCodeModel(EFileSystemConvention.LINUX);
    JDefinedClass definedClass = codeModel._class(JMod.PUBLIC, "my.Clazz");
    JMethod toStringMeth = definedClass.method(JMod.PUBLIC, codeModel.ref(String.class), "toString");
    String toStringVal = "1337";
    toStringMeth.body()._return(JExpr.lit(toStringVal));
    DynamicClassLoader loader = DynamicClassLoader.generate(codeModel);
    Class<?> foundClass = loader.findClass(definedClass.fullName());
    Assert.assertEquals(toStringVal, foundClass.getConstructor().newInstance().toString());
  }

}
