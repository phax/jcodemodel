/*
 * Copyright 2015 Philip Helger.
 */
package com.helger.jcodemodel.util;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.Nonnull;

/**
 * An implementation of {@link Writer} that discards all input :)
 *
 * @author Victor Nazarov &lt;asviraspossible@gmail.com&gt;
 */
public class NullWriter extends Writer
{
  private static final NullWriter INSTANCE = new NullWriter ();

  @Nonnull
  public static NullWriter getInstance ()
  {
    return INSTANCE;
  }

  private NullWriter ()
  {}

  @Override
  public void write (final char [] cbuf, final int off, final int len) throws IOException
  {}

  @Override
  public void flush () throws IOException
  {}

  @Override
  public void close () throws IOException
  {}
}
