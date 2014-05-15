package com.helger.jcodemodel;

import javax.annotation.Nonnull;

/**
 * Base interface for objects having a relation to a {@link JCodeModel}.
 * 
 * @author Philip Helger
 */
public interface IJOwned
{
  /**
   * Gets the owner code model object.
   * 
   * @return The owner and never <code>null</code>.
   */
  @Nonnull
  JCodeModel owner ();
}
