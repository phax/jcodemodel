package com.helger.jcodemodel.supplementary.issues;

import java.io.IOException;

import org.junit.Test;

import com.helger.jcodemodel.JClassAlreadyExistsException;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.writer.SingleStreamCodeWriter;

/**
 * Test for https://github.com/phax/jcodemodel/issues/30
 *
 * @author Philip Helger
 */
public final class Issue30FuncTest
{
  @Test
  public void test () throws JClassAlreadyExistsException, IOException
  {
    final JCodeModel cm = new JCodeModel ();

    final JPackage aPkg1 = cm._package ("id.myapp");
    final JDefinedClass aClass_R = aPkg1._class ("R");
    final JDefinedClass aClass_id = aClass_R._class (JMod.PUBLIC | JMod.STATIC, "id");
    final JFieldVar aFieldItem = aClass_id.field (JMod.PUBLIC |
                                                  JMod.STATIC |
                                                  JMod.FINAL,
                                                  cm.INT,
                                                  "myItem",
                                                  JExpr.lit (1));
    final JDefinedClass aClass_menu = aClass_R._class (JMod.PUBLIC | JMod.STATIC, "menu");
    final JFieldVar aFieldMenu = aClass_menu.field (JMod.PUBLIC |
                                                    JMod.STATIC |
                                                    JMod.FINAL,
                                                    cm.INT,
                                                    "myMenu",
                                                    JExpr.lit (2));

    final JPackage aPkg2 = cm._package ("demo");
    final JDefinedClass aClassAct = aPkg2._class ("HelloAndroidActivity_");
    final JMethod aMethodCreate = aClassAct.method (JMod.PUBLIC, cm.BOOLEAN, "onCreateOptionsMenu");
    aMethodCreate.body ().add (JExpr.ref ("menuInflater").invoke ("inflate").arg (aFieldMenu));
    final JMethod aMethodSelected = aClassAct.method (JMod.PUBLIC, cm.BOOLEAN, "onOptionsItemSelected");
    aMethodSelected.body ()._if (JExpr.ref ("itemId_").eq (aFieldItem));

    cm.build (new SingleStreamCodeWriter (System.out));
  }
}
