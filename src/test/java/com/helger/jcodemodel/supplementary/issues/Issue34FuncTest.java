package com.helger.jcodemodel.supplementary.issues;

import org.junit.Test;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.util.CodeModelTestsHelper;

/**
 * Test for https://github.com/phax/jcodemodel/issues/34
 *
 * @author Philip Helger
 */
public final class Issue34FuncTest
{
  @Test
  public void testDefaultMethod () throws Exception
  {
    final JCodeModel generator = new JCodeModel ();

    final JDefinedClass aInterface = generator._package ("issue34")._interface ("IDefaultMethod");
    final JMethod m = aInterface.method (JMod.DEFAULT, generator.ref (String.class), "testWithDefault");
    m.body ()._return (JExpr.lit ("foo"));

    CodeModelTestsHelper.parseCodeModel (generator);
  }
}
