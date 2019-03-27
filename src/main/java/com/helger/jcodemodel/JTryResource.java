package com.helger.jcodemodel;

import javax.annotation.Nonnull;

/**
 * This class represents a single resources that is used in try-with-resources
 * statement. See {@link JTryBlock}.
 *
 * @author Philip Helger
 * @since 3.2.3
 */
public class JTryResource implements IJGenerable
{
  private final JVar m_aVar;

  public JTryResource (@Nonnull final AbstractJType aType,
                       @Nonnull final String sName,
                       @Nonnull final IJExpression aInitExpr)
  {
    this (JMod.FINAL, aType, sName, aInitExpr);
  }

  public JTryResource (final int nMods,
                       @Nonnull final AbstractJType aType,
                       @Nonnull final String sName,
                       @Nonnull final IJExpression aInitExpr)
  {
    m_aVar = new JVar (JMods.forVar (nMods), aType, sName, aInitExpr);
  }

  @Nonnull
  public JVar var ()
  {
    return m_aVar;
  }

  public void generate (@Nonnull final IJFormatter aFormatter)
  {
    aFormatter.var (m_aVar);
  }
}
