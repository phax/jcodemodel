package com.helger.jcodemodel.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;
import com.helger.jcodemodel.JCodeModel;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * CodeWriter that stores {@link OutputStream}s for the files. A call to toString() will then sort
 * the files and append their content with a leading line containing the name of the file.
 *
 * @author glelouet
 */
public class StringCodeWriter extends AbstractCodeWriter
{
  private final Map <String, NonBlockingByteArrayOutputStream> m_aBinaries = new HashMap <> ();

  public StringCodeWriter (@Nullable final Charset aEncoding, @Nonnull final String sNewLine)
  {
    super (aEncoding, sNewLine);
  }

  @Override
  public OutputStream openBinary (@Nonnull final String sDirName, @Nonnull final String sFilename) throws IOException
  {
    ValueEnforcer.notNull (sDirName, "DirName");
    ValueEnforcer.notNull (sFilename, "Filename");

    return m_aBinaries.computeIfAbsent (sDirName + '/' + sFilename, x -> new NonBlockingByteArrayOutputStream ());
  }

  @Override
  public void close ()
  {
    // empty
  }

  @Nonnull
  public String getAsString ()
  {
    final ICommonsList <Map.Entry <String, NonBlockingByteArrayOutputStream>> coll = new CommonsArrayList <> (m_aBinaries.entrySet ());
    coll.sort (Comparator.comparing (Entry::getKey));
    final String sNL = getNewLine ();
    final StringBuilder aSB = new StringBuilder ("model:");
    for (final var e : coll)
      aSB.append (sNL).append (e.getKey ()).append (sNL).append (e.getValue ().getAsString (encoding ()));
    return aSB.toString ();
  }

  @Override
  public String toString ()
  {
    return getAsString ();
  }

  /**
   * transform a {@link JCodeModel} into a {@link String} using this class.
   *
   * @param aTarget
   *        the codemodel to export
   * @return the representation fo the codemodel.
   */
  @Nonnull
  public static String represent (@Nonnull final JCodeModel aTarget)
  {
    final StringCodeWriter aSCW = new StringCodeWriter (StandardCharsets.UTF_8, "\n");
    try
    {
      new JCMWriter (aTarget).build (aSCW);
    }
    catch (final IOException e)
    {
      throw new UnsupportedOperationException ("catch this", e);
    }
    return aSCW.getAsString ();
  }
}
