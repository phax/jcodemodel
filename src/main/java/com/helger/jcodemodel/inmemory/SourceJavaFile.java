package com.helger.jcodemodel.inmemory;

import java.io.IOException;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

/**
 * represent a compilation object to be given to java compiler, based on a the
 * name of the .java file and its content.
 *
 * @author glelouet
 */
public class SourceJavaFile extends SimpleJavaFileObject
{
  private final String m_sContent;

  /**
   * create a {@link SimpleJavaFileObject} based on a .java file
   *
   * @param sFilename
   *        Filename
   * @param sContent
   *        Content of the Java file
   */
  public SourceJavaFile (final String sFilename, final String sContent)
  {
    super (URI.create ("string:///" + sFilename), Kind.SOURCE);
    this.m_sContent = sContent;
  }

  @Override
  public CharSequence getCharContent (final boolean ignoreEncodingErrors) throws IOException
  {
    return m_sContent;
  }
}
