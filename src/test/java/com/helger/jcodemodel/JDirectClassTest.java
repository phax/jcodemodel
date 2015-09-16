package com.helger.jcodemodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.jcodemodel.util.CodeModelTestsHelper;

/**
 * Test class for class {@link JDirectClass}.
 *
 * @author Philip Helger
 */
public final class JDirectClassTest
{
  @Test
  public void testBasic () throws Exception
  {
    final JCodeModel cm = new JCodeModel ();

    final JDirectClass rClassId = cm.directClass ("id.aa.R")._class ("id");
    assertEquals ("id", rClassId.name ());
    assertEquals ("id.aa", rClassId._package ().name ());
    assertEquals ("id.aa.R.id", rClassId.fullName ());
    final JDirectClass rClassMenu = cm.directClass ("id.aa.R")._class ("menu");
    assertEquals ("menu", rClassMenu.name ());
    assertEquals ("id.aa", rClassMenu._package ().name ());
    assertEquals ("id.aa.R.menu", rClassMenu.fullName ());

    final JFieldRef myItem = rClassId.staticRef ("myItem");
    final JFieldRef myMenu = rClassMenu.staticRef ("myMenu");

    final JPackage aPkg2 = cm._package ("id.aa");
    final JDefinedClass aClassAct = aPkg2._class ("HelloAndroidActivity_");
    final JMethod aMethodCreate = aClassAct.method (JMod.PUBLIC, cm.BOOLEAN, "onCreateOptionsMenu");
    aMethodCreate.body ().add (JExpr.ref ("menuInflater").invoke ("inflate").arg (myMenu));
    final JMethod aMethodSelected = aClassAct.method (JMod.PUBLIC, cm.BOOLEAN, "onOptionsItemSelected");
    aMethodSelected.body ()._if (JExpr.ref ("itemId_").eq (myItem));

    CodeModelTestsHelper.parseCodeModel (cm);
  }

  @Test
  public void testGenerics () throws Exception
  {
    final JCodeModel cm = new JCodeModel ();

    final AbstractJClass aNarrowedClass = (AbstractJClass) cm.parseType ("com.test.GenericFragmentArguments<S,P>");
    assertTrue (aNarrowedClass instanceof JNarrowedClass);
    assertTrue (aNarrowedClass.erasure () instanceof JDirectClass);
    assertEquals ("com.test", aNarrowedClass._package ().name ());
    assertEquals ("GenericFragmentArguments<S,P>", aNarrowedClass.name ());
    assertEquals ("GenericFragmentArguments", aNarrowedClass.erasure ().name ());
    assertEquals ("com.test.GenericFragmentArguments<S,P>", aNarrowedClass.fullName ());
    assertEquals ("com.test.GenericFragmentArguments", aNarrowedClass.erasure ().fullName ());

    cm._class ("UsingClass").method (JMod.PUBLIC, cm.VOID, "test").body ().add (JExpr._new (aNarrowedClass));

    CodeModelTestsHelper.parseCodeModel (cm);
  }
}
