package com.helger.jcodemodel.util;

import java.util.Locale;

import javax.annotation.Nonnull;

/**
 * Utility class to represent case sensitive or case insensitive keys for file
 * and directory names.
 *
 * @author Philip Helger
 * @since 3.4.0
 */
public final class FSName implements Comparable <FSName>
{
  private final String m_sName;
  private final String m_sKey;
  // status vars
  private int m_nHashCode = JCHashCodeGenerator.ILLEGAL_HASHCODE;

  private FSName (@Nonnull final String sName, @Nonnull final String sKey)
  {
    m_sName = sName;
    m_sKey = sKey;
  }

  @Nonnull
  public String getName ()
  {
    return m_sName;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final FSName rhs = (FSName) o;
    return m_sKey.equals (rhs.m_sKey);
  }

  @Override
  public int hashCode ()
  {
    int ret = m_nHashCode;
    if (ret == JCHashCodeGenerator.ILLEGAL_HASHCODE)
      ret = m_nHashCode = new JCHashCodeGenerator (this).append (m_sKey).getHashCode ();
    return ret;
  }

  public int compareTo (@Nonnull final FSName o)
  {
    return m_sKey.compareTo (o.m_sKey);
  }

  @Nonnull
  public static FSName createCaseSensitive (@Nonnull final String sName)
  {
    JCValueEnforcer.notNull (sName, "Name");
    return new FSName (sName, sName);
  }

  @Nonnull
  public static FSName createCaseInsensitive (@Nonnull final String sName)
  {
    JCValueEnforcer.notNull (sName, "Name");
    // Unify key to upper case
    return new FSName (sName, sName.toUpperCase (Locale.ROOT));
  }
}
