package com.helger.jcodemodel.inmemory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

/**
 * represents java file object that is produced by the javac compiler. Holds the
 * name of the class (with '.' to separate packages) and the binary file
 * produces by the compiler.
 *
 * @author glelouet
 * @author trung
 *
 */
public class CompiledCodeJavaFile extends SimpleJavaFileObject
{
  private ByteArrayOutputStream baos = new ByteArrayOutputStream ();
  private String className;

  public CompiledCodeJavaFile (String className) throws Exception
  {
    super (new URI (className), Kind.CLASS);
    this.className = className;
  }

  public String getClassName ()
  {
    return className;
  }

  @Override
  public OutputStream openOutputStream () throws IOException
  {
    return baos;
  }

  public byte[] getByteCode ()
  {
    return baos.toByteArray ();
  }
}