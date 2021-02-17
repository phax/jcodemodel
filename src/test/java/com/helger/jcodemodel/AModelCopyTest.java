package com.helger.jcodemodel;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

import org.junit.Assert;

import com.helger.jcodemodel.exceptions.JCodeModelException;

public abstract class AModelCopyTest
{

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

  protected abstract JCodeModel copy (JCodeModel source);

  protected abstract String represent (JCodeModel target);

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
    });
  }

}
