package com.helger.jcodemodel.writer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.helger.jcodemodel.JCodeModel;

/**
 *
 * CodeWriter that stores {@link OutputStream}s for the files. A call to toString() will then sort the files and append
 * their
 * content with a leading line containing the name of the file.
 *
 * @author glelouet
 *
 */
public class StringCodeWriter extends AbstractCodeWriter
{

  public StringCodeWriter (Charset aEncoding, String sNewLine)
  {
    super (aEncoding, sNewLine);
  }

  private HashMap <String, ByteArrayOutputStream> binaries = new HashMap <> ();

  @Override
  public OutputStream openBinary (String sDirName, String sFilename) throws IOException
  {
    return binaries.computeIfAbsent (sDirName + "/" + sFilename,
        o -> new ByteArrayOutputStream ());
  }

  @Override
  public void close () throws IOException
  {
  }

  public String getString ()
  {
    ArrayList <Entry <String, ByteArrayOutputStream>> coll = new ArrayList <> (binaries.entrySet ());
    Collections.sort (coll, Comparator.comparing (Entry::getKey));
    return "model:" + getNewLine ()
    + coll.stream ().map (e -> e.getKey () + getNewLine () + e.getValue ().toString (encoding ()))
    .collect (Collectors.joining (getNewLine ()));
  }

  @Override
  public String toString ()
  {
    return getString ();
  }

  /**
   * transform a {@link JCodeModel} into a {@link String} using this class.
   *
   * @param target
   *        the codemodel to export
   * @return the representation fo the codemodel.
   */
  public static String represent (JCodeModel target)
  {
    StringCodeWriter scw = new StringCodeWriter (StandardCharsets.UTF_8, "\n");
    try
    {
      new JCMWriter (target).build (scw);
    }
    catch (IOException e)
    {
      throw new UnsupportedOperationException ("catch this", e);
    }
    String ret = scw.getString ();
    return ret;
  }

}
