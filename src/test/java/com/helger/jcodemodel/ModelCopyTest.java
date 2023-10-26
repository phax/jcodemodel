package com.helger.jcodemodel;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Test;

import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.writer.StringCodeWriter;

/**
 * abstract method for a test of copying. Such a test consist in creating a source codemodel, copying it, check the
 * equality of the representation of the copy and the source ; then apply several modifications on the source, each
 * result in a model varying from the copy ; then apply those modifications to the copy, which in the end should have
 * the same representation as the source.<br />
 *
 * This class defines
 * <ul>
 * <li>{@link #execute()} that should be called in the test,</li>
 * <li>{@link #createCM()} that creates a base code model,</li>
 * <li>{@link #copy(JCodeModel)} that defines how to copy a {@link JCodeModel},</li>
 * <li>{@link #represent(JCodeModel)} to make a representation of a codemodel,</li>
 * <li> {@link #modifications()} that lists a series of modifications to apply.</li>
 * </ul>
 *
 * @author glelouet
 */
public class ModelCopyTest
{

  @Test
  public void execute ()
  {
    JCodeModel cm = createCM ();
    JCodeModel copy = copy (cm);
    Assert.assertEquals (represent (cm), represent (copy));
    for (Consumer <JCodeModel> m : modifications ())
    {
      m.accept (cm);
      Assert.assertNotEquals (represent (cm), represent (copy));
    }
    for (Consumer <JCodeModel> m : modifications ())
      m.accept (copy);
    Assert.assertEquals (represent (cm), represent (copy));
  }

  protected JCodeModel createCM ()
  {
    try
    {
      JCodeModel ret = new JCodeModel ();

      // create an interface IMyClass with method default String string(){return "yes";}
      JDefinedClass itf = ret._class ("my.pckg.IMyClass", EClassType.INTERFACE);
      JMethod methd = itf.method (JMod.PUBLIC | JMod.DEFAULT, ret.ref (String.class), "string");
      methd.body ()._return (JExpr.lit ("yes"));

      // create an implementation of that interface, for which toString() returns the string();
      JDefinedClass imp = itf._package ()._class (JMod.PUBLIC, "Impl")._implements (itf);
      imp.method (JMod.PUBLIC, ret.ref (String.class), "toString").body ()._return (JExpr.invoke (methd));
      return ret;
    }
    catch (JCodeModelException e)
    {
      throw new UnsupportedOperationException ("catch this", e);
    }
  }

  protected JCodeModel copy (JCodeModel source)
  {
    return source.copy ();
  }

  protected String represent (JCodeModel target)
  {
    return StringCodeWriter.represent (target);
  }

  public Collection <Consumer <JCodeModel>> modifications ()
  {
    return Arrays.asList (cm ->
    {
      try
      {
        cm._class (JMod.PUBLIC, "MyClass");
      }
      catch (JCodeModelException e)
      {
        throw new UnsupportedOperationException ("catch this", e);
      }
    }, cm ->
    {
      JDefinedClass cl = cm._getClass ("my.pckg.IMyClass");
      cl.method (JMod.DEFAULT, cm.BOOLEAN, "yes").body ()._return (JExpr.TRUE);
    }, cm ->
    {
      try
      {
        JDefinedClass cl = cm._getClass ("my.pckg.IMyClass");
        JDefinedClass newcl = cl._class (JMod.STATIC | JMod.PUBLIC, "InternalIntClass");
        JFieldVar fld = newcl.field (JMod.PRIVATE, cm.INT, "intField");
        JMethod cons = newcl.constructor (JMod.PUBLIC);
        JVar consParam = cons.param (cm.INT, "number");
        cons.body ().assign (fld, consParam);
        newcl.method (JMod.PUBLIC, cm.INT, "incr").body ()._return (JExpr.preincr (fld));
      }
      catch (JCodeModelException e)
      {
        throw new UnsupportedOperationException ("catch this", e);
      }
    });
  }

}
