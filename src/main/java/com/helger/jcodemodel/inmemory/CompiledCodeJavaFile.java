package com.helger.jcodemodel.inmemory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;

/**
 * represents java file object that is produced by the javac compiler. Holds the
 * name of the class (with '.' to separate packages) and the binary file
 * produces by the compiler.
 *
 * @author glelouet
 * @author trung
 */
public class CompiledCodeJavaFile extends SimpleJavaFileObject
{
  private final NonBlockingByteArrayOutputStream m_aBAOS = new NonBlockingByteArrayOutputStream ();
  private final String m_sClassName;

  public CompiledCodeJavaFile (final String className) throws Exception
  {
    super (new URI (className), Kind.CLASS);
    m_sClassName = className;
  }

  public String getClassName ()
  {
    return m_sClassName;
  }

  @Override
  public OutputStream openOutputStream () throws IOException
  {
    return m_aBAOS;
  }

  public byte [] getByteCode ()
  {
    return m_aBAOS.toByteArray ();
  }
}
