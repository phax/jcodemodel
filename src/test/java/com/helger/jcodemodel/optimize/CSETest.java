package com.helger.jcodemodel.optimize;

import com.helger.jcodemodel.*;
import com.helger.jcodemodel.tests.util.CodeModelTestsUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;

import static com.helger.jcodemodel.JExpr._new;
import static com.helger.jcodemodel.JExpr.lit;
import static com.helger.jcodemodel.JExpr.ref;

public class CSETest
{

  /**
   * Run tests one by one and verify they do something sensible.
   */

  private static void println(JCodeModel cm, JBlock b, IJExpression expression)
  {
    b.invoke (cm.ref (System.class).staticRef ("out"), "println")
        .arg (expression).hintType (cm.VOID);
  }

  @Test
  public void testFieldRefObjectInvalidation ()
      throws JClassAlreadyExistsException, IOException
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass cls = cm._class ("TestFieldRefObjectInvalidation");
    final JMethod m = cls.method (JMod.PUBLIC, cm.VOID, "foo");
    JBlock b = m.body ();

    JVar a = b.decl ("a", JExpr.newArray (cm.INT, 42));
    AbstractJExpressionImpl aLength = ref (a, "length").hintType (cm.INT);

    println (cm, b, aLength);
    println (cm, b, aLength);

    b.assign (a, JExpr.newArray (cm.INT, 21));
    println (cm, b, aLength);

    CSE.optimize (b);
    System.out.println (CodeModelTestsUtils.declare (cls));
  }

  @Test
  public void testArrayIndexingInvalidation ()
      throws JClassAlreadyExistsException, IOException
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass cls = cm._class ("TestMethodCallArgumentInvalidation");
    final JMethod m = cls.method (JMod.PUBLIC, cm.VOID, "foo");
    JBlock b = m.body ();

    JVar a = b.decl ("a", JExpr.newArray (cm.INT, 42));
    JVar i = b.decl ("i", lit (42));

    println (cm, b, a.component (i));
    println (cm, b, a.component (i));

    b.assign (i, lit (21));
    println (cm, b, a.component (i));
    println (cm, b, a.component (i));

    b.assign (a, JExpr.newArray (cm.INT, 21));
    println (cm, b, a.component (i));

    CSE.optimize (b);
    System.out.println (CodeModelTestsUtils.declare (cls));
  }

  @Test
  public void testMethodCallInvalidation ()
      throws JClassAlreadyExistsException, IOException
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass cls = cm._class ("TestMethodCallArgumentInvalidation");
    final JMethod m = cls.method (JMod.PUBLIC, cm.VOID, "foo");
    JBlock b = m.body ();

    JVar map = b.decl ("map", _new (cm._ref (HashSet.class)));
    JVar i = b.decl ("i", lit (42));

    println (cm, b, map.invoke ("contains").arg (i).hintType (cm.BOOLEAN));
    println (cm, b, map.invoke ("contains").arg (i).hintType (cm.BOOLEAN));

    b.assign (i, lit (21));
    println (cm, b, map.invoke ("contains").arg (i).hintType (cm.BOOLEAN));
    println (cm, b, map.invoke ("contains").arg (i).hintType (cm.BOOLEAN));

    b.assign (map, _new (cm._ref (HashSet.class)));
    println (cm, b, map.invoke ("contains").arg (i).hintType (cm.BOOLEAN));

    CSE.optimize (b);
    System.out.println (CodeModelTestsUtils.declare (cls));
  }

  @Test
  public void testNestedSubExpressions() throws JClassAlreadyExistsException, IOException
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass cls = cm._class ("TestNestedSubExpressions");
    final JMethod m = cls.method (JMod.PUBLIC, cm.VOID, "foo");
    JBlock b = m.body ();

    b.invoke (cm.ref (System.class).staticRef ("out"), "println")
        .arg (
            ref ("arrayField").hintType (cm.LONG.array ())
            .component (ref ("indexField").hintType (cm.INT)))
        .hintType (cm.VOID);
    b.invoke (cm.ref (System.class).staticRef ("out"), "println")
        .arg (
            ref ("arrayField").hintType (cm.LONG.array ())
                .component (ref ("indexField").hintType (cm.INT)))
        .hintType (cm.VOID);

    CSE.optimize (b);
    System.out.println (CodeModelTestsUtils.declare (cls));
  }

  @Test
  public void testSubBlockDefinitionPull() throws JClassAlreadyExistsException
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass cls = cm._class ("TestSubBlockDefinitionPull");
    final JMethod m = cls.method (JMod.PUBLIC, cm.VOID, "foo");
    JBlock b = m.body ();

    JVar a = b.decl ("a", JExpr.newArray (cm.INT, 42));
    AbstractJExpressionImpl aLength = ref (a, "length").hintType (cm.INT);

    JBlock subBlock = b.block ();
    // without dummy definition jCodeModel won't create a sub block
    subBlock.decl ("i", lit (1));
    println (cm, subBlock, aLength);
    println (cm, b, aLength);

    CSE.optimize (b);
    System.out.println (CodeModelTestsUtils.declare (cls));
  }

  @Test
  public void testInsideSubBlockInvalidation() throws JClassAlreadyExistsException
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass cls = cm._class ("TestInsideSubBlockInvalidation");
    final JMethod m = cls.method (JMod.PUBLIC, cm.VOID, "foo");
    JBlock b = m.body ();

    JVar a = b.decl ("a", JExpr.newArray (cm.INT, 42));
    AbstractJExpressionImpl aLength = ref (a, "length").hintType (cm.INT);

    println (cm, b, aLength);

    JBlock subBlock = b.block ();
    // without dummy definition jCodeModel won't create a sub block
    subBlock.decl ("i", lit (1));
    subBlock.assign (a, JExpr.newArray (cm.INT, 21));
    println (cm, subBlock, aLength);
    println (cm, subBlock, aLength);

    println (cm, b, aLength);

    CSE.optimize (b);
    System.out.println (CodeModelTestsUtils.declare (cls));
  }

  @Test
  public void testIfBranchesPull() throws JClassAlreadyExistsException
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass cls = cm._class ("TestIfBranchesPull");
    final JMethod m = cls.method (JMod.PUBLIC, cm.VOID, "foo");
    JBlock b = m.body ();

    JVar a = b.decl ("a", JExpr.newArray (cm.INT, 42));
    AbstractJExpressionImpl aLength = ref (a, "length").hintType (cm.INT);

    JConditional cond = b._if (cls.staticInvoke ("test"));
    println (cm, cond._then (), aLength);
    println (cm, cond._else (), aLength);

    b.assign (a, JExpr.newArray (cm.INT, 21));

    cond = b._if (cls.staticInvoke ("test"));
    println (cm, cond._then (), aLength);
    cond._else ().decl ("i", lit (1));

    cond = b._if (cls.staticInvoke ("test"));
    JConditional cond2 = cond._then ()._if (cls.staticInvoke ("test"));
    println (cm, cond2._then (), aLength);
    println (cm, cond2._else (), aLength);

    JConditional cond3 = cond._else ()._if (cls.staticInvoke ("test"));
    println (cm, cond3._then (), aLength);
    println (cm, cond3._else (), aLength);

    b.assign (a, JExpr.newArray (cm.INT, 42));

    cond = b._if (cls.staticInvoke ("test"));
    cond2 = cond._then ()._if (cls.staticInvoke ("test"));
    println (cm, cond2._then (), aLength);
    println (cm, cond2._else (), aLength);

    cond3 = cond._else ()._if (cls.staticInvoke ("test"));
    println (cm, cond3._then (), aLength);
    cond3._else ().decl ("i", lit (1));

    CSE.optimize (b);
    System.out.println (CodeModelTestsUtils.declare (cls));
  }

  @Test
  public void testForLoop() throws JClassAlreadyExistsException
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass cls = cm._class ("TestForLoop");
    final JMethod m = cls.method (JMod.PUBLIC, cm.VOID, "foo");
    JBlock b = m.body ();

    JVar a = b.decl ("a", JExpr.newArray (cm.INT, 42));
    JVar j = b.decl ("j", lit (42));

    JForLoop _for = b._for ();
    JVar i = _for.init (cm.INT, "i", lit (0));
    _for.test (i.lt (lit (10)));
    _for.update (i.incr ());
    JBlock forBody = _for.body ();
    println (cm, forBody, a.component (i));
    println (cm, forBody, a.component (j));

    CSE.optimize (b);
    System.out.println (CodeModelTestsUtils.declare (cls));
  }
}
