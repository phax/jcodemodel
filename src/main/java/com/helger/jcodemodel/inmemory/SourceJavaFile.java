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

  /** create a {@link SimpleJavaFileObject} based on a .java file */
  public SourceJavaFile (String fileName, String contents) throws Exception
  {
    super (URI.create ("string:///" + fileName), Kind.SOURCE);
    this.contents = contents;
  }

  private final String contents;

  @Override
  public CharSequence getCharContent (boolean ignoreEncodingErrors) throws IOException
  {
    return contents;
  }
}