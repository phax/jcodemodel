package com.helger.jcodemodel.supplementary.issues;

import org.junit.Test;

import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JDirectClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.util.CodeModelTestsHelper;

/**
 * Test for https://github.com/phax/jcodemodel/issues/30
 *
 * @author Philip Helger
 */
public final class Issue30FuncTest
{
  @Test
  public void test () throws Exception
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
    aMethodCreate.body ().add (JExpr.ref ("menuInflater").invoke ("inflate").arg (aFieldMenu.fieldRef ()));
    final JMethod aMethodSelected = aClassAct.method (JMod.PUBLIC, cm.BOOLEAN, "onOptionsItemSelected");
    aMethodSelected.body ()._if (JExpr.ref ("itemId_").eq (aFieldItem.fieldRef ()));

    // Multiple packages - print only
    CodeModelTestsHelper.printCodeModel (cm);
  }

  @Test
  public void testRegression1 () throws Exception
  {
    final JCodeModel cm = new JCodeModel ();

    final JPackage aPkg1 = cm._package ("id.myapp.activity");

    final JDefinedClass testClass = aPkg1._class ("TestClass");

    final JDirectClass androidR = cm.directClass ("android.R");
    final JDirectClass androidRId = androidR._class ("id");
    final JDirectClass myR = cm.directClass ("id.myapp.R");
    final JDirectClass myRId = myR._class ("id");

    final JBlock constructorBody = testClass.constructor (JMod.PUBLIC).body ();
    constructorBody.decl (cm.INT, "myInt", androidRId.staticRef ("someId"));
    constructorBody.decl (cm.INT, "myInt2", myRId.staticRef ("otherId"));

    CodeModelTestsHelper.printCodeModel (cm);
    CodeModelTestsHelper.parseCodeModel (cm);
  }

  @Test
  public void testRegression1VerySpecialCase () throws Exception
  {
    final JCodeModel cm = new JCodeModel ();

    final JPackage aPkg1 = cm._package ("id.myapp.activity");

    // Class is named like imported class
    final JDefinedClass testClass = aPkg1._class ("R");

    final JDirectClass androidR = cm.directClass ("android.R");
    final JDirectClass androidRId = androidR._class ("id");
    final JDirectClass myR = cm.directClass ("id.myapp.R");
    final JDirectClass myRId = myR._class ("id");

    final JBlock constructorBody = testClass.constructor (JMod.PUBLIC).body ();
    constructorBody.decl (cm.INT, "myInt", androidRId.staticRef ("someId"));
    constructorBody.decl (cm.INT, "myInt2", myRId.staticRef ("otherId"));

    CodeModelTestsHelper.printCodeModel (cm);
    CodeModelTestsHelper.parseCodeModel (cm);
  }
}
