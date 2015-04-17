package com.helger.jcodemodel;

import javax.annotation.Nonnull;

/**
 * This class represents a single direct statement. A direct statement is a
 * statement that is neither parsed not evaluated! Handle with care!
 *
 * @author Philip Helger
 * @since 2.7.10
 */
public final class JDirectStatement implements IJStatement
{
  private String m_sSource;

  public JDirectStatement (final String sSource)
  {
    source (sSource);
  }

  @Nonnull
  public String source ()
  {
    return m_sSource;
  }

  public void source (final String sSource)
  {
    if (sSource == null)
      throw new NullPointerException ("Source");
    m_sSource = sSource;
  }

  public void state (@Nonnull final JFormatter f)
  {
    f.print (m_sSource).newline ();
  }
}
