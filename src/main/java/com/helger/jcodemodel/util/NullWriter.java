/*
 * Copyright 2015 Philip Helger.
 */
package com.helger.jcodemodel.util;

import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
public class NullWriter extends Writer {
  private static final NullWriter INSTANCE = new NullWriter ();
  
  public static NullWriter getInstance ()
  {
    return INSTANCE;
  }

  private NullWriter ()
  {
  }

  @Override
  public void write (char[] cbuf, int off, int len) throws IOException
  {
  }

  @Override
  public void flush () throws IOException
  {
  }

  @Override
  public void close () throws IOException
  {
  }
}
