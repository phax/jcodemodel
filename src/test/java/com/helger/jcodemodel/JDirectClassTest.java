package com.helger.jcodemodel;

import java.io.IOException;

import org.junit.Test;

import com.helger.jcodemodel.writer.SingleStreamCodeWriter;

/**
 * Test class for class {@link JDirectClass}.
 *
 * @author Philip Helger
 */
public final class JDirectClassTest
{
  @Test
  public void testBasic () throws JClassAlreadyExistsException, IOException
  {
    final JCodeModel cm = new JCodeModel ();

    final JDirectClass rClassId = cm.directClass ("id.aa.R")._class ("id");
    final JDirectClass rClassMenu = cm.directClass ("id.aa.R")._class ("menu");

    final JFieldRef myItem = rClassId.staticRef ("myItem");
    final JFieldRef myMenu = rClassMenu.staticRef ("myMenu");

    final JPackage aPkg2 = cm._package ("id.aa");
    final JDefinedClass aClassAct = aPkg2._class ("HelloAndroidActivity_");
    final JMethod aMethodCreate = aClassAct.method (JMod.PUBLIC, cm.BOOLEAN, "onCreateOptionsMenu");
    aMethodCreate.body ().add (JExpr.ref ("menuInflater").invoke ("inflate").arg (myMenu));
    final JMethod aMethodSelected = aClassAct.method (JMod.PUBLIC, cm.BOOLEAN, "onOptionsItemSelected");
    aMethodSelected.body ()._if (JExpr.ref ("itemId_").eq (myItem));

    cm.build (new SingleStreamCodeWriter (System.out));
  }
}
