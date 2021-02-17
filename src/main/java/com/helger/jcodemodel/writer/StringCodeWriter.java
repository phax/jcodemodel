package com.helger.jcodemodel.writer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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

}
