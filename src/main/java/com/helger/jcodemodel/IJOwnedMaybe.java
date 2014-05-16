package com.helger.jcodemodel;

import javax.annotation.Nullable;

/**
 * Base interface for objects optionally having a relation to a
 * {@link JCodeModel}.
 * 
 * @author Philip Helger
 */
public interface IJOwnedMaybe
{
  /**
   * Gets the owner code model object.
   * 
   * @return The owner and maybe <code>null</code>.
   */
  @Nullable
  JCodeModel owner ();
}
