package com.helger.jcodemodel;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

import javax.annotation.Nonnull;

/**
 * A special version of {@link java.io.PrintWriter} that has a customizable new
 * line string.
 *
 * @author Philip Helger
 */
public final class SourcePrintWriter extends FilterWriter
{
  private final String m_sNewLine;

  public SourcePrintWriter (@Nonnull final Writer aWrappedWriter, @Nonnull final String sNewLine)
  {
    super (aWrappedWriter);
    m_sNewLine = sNewLine;
  }

  private void _handleException (@Nonnull final IOException ex, @Nonnull final String sSource)
  {
    System.err.println ("Error on Writer: " + sSource);
    ex.printStackTrace ();
  }

  private void _write (final char c)
  {
    try
    {
      super.write (c);
    }
    catch (final IOException ex)
    {
      _handleException (ex, "write char");
    }
  }

  private void _write (@Nonnull final String sStr)
  {
    try
    {
      super.write (sStr, 0, sStr.length ());
    }
    catch (final IOException ex)
    {
      _handleException (ex, "write String");
    }
  }

  public void print (final char c)
  {
    _write (c);
  }

  public void print (@Nonnull final String sStr)
  {
    _write (sStr);
  }

  public void println ()
  {
    _write (m_sNewLine);
  }

  public void println (final String sStr)
  {
    _write (sStr);
    _write (m_sNewLine);
  }

  @Override
  public void close ()
  {
    try
    {
      super.close ();
    }
    catch (final IOException ex)
    {
      _handleException (ex, "close");
    }
  }
}
