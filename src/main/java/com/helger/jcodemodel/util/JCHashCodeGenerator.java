package com.helger.jcodemodel.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.hashcode.HashCodeGenerator;

public class JCHashCodeGenerator
{
  /**
   * Static helper method to create the hashcode of an object with a single
   * invocation. This method must be used by objects that directly derive from
   * Object.
   *
   * @param aThis
   *        <code>this</code>
   * @param aMembers
   *        A list of all members. Primitive types must be boxed.
   * @return The generated hashCode.
   */
  public static int getHashCode (@Nonnull final Object aThis, @Nullable final Object... aMembers)
  {
    final HashCodeGenerator aHCGen = new HashCodeGenerator (aThis);
    if (aMembers != null)
      for (final Object aMember : aMembers)
        aHCGen.append (aMember);
    return aHCGen.getHashCode ();
  }

  /**
   * Static helper method to create the hashcode of an object with a single
   * invocation. This method must be used by objects that derive from a class
   * other than Object.
   *
   * @param nSuperHashCode
   *        The result of <code>super.hashCode()</code>
   * @param aMembers
   *        A list of all members. Primitive types must be boxed.
   * @return The generated hashCode.
   */
  public static int getHashCode (final int nSuperHashCode, @Nullable final Object... aMembers)
  {
    final HashCodeGenerator aHCGen = HashCodeGenerator.getDerived (nSuperHashCode);
    if (aMembers != null)
      for (final Object aMember : aMembers)
        aHCGen.append (aMember);
    return aHCGen.getHashCode ();
  }
}
