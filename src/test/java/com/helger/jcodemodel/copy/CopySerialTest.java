package com.helger.jcodemodel.copy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

import com.helger.jcodemodel.AModelCopyTest;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.writer.StringCodeWriter;

public class CopySerialTest extends AModelCopyTest
{

  @Override
  protected JCodeModel copy (JCodeModel source)
  {
    try
    {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream ();
      new ObjectOutputStream (buffer).writeObject (source);
      ByteArrayInputStream in = new ByteArrayInputStream (buffer.toByteArray ());
      return (JCodeModel) new ObjectInputStream (in).readObject ();
    }
    catch (IOException | ClassNotFoundException e)
    {
      throw new UnsupportedOperationException ("catch this", e);
    }
  }

  @Override
  protected String represent (JCodeModel target)
  {
    return StringCodeWriter.represent (target);
  }

  @Override
  @Test
  public void execute ()
  {
    super.execute ();
  }

}
