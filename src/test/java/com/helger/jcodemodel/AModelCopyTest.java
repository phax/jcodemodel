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
    JCodeModel ret = new JCodeModel ();
    return ret;
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
