package com.helger.jcodemodel.modifiers;

import java.util.Set;

/// Something that is modified using [EMod]
public interface IJModified {

  /// sequentially apply each mod. If the class can't be modified, then an [UnsupportedOperationException] should be thrown
  ///
  /// For each mod added :
  /// 1. its excluded mods are removed first
  /// 2. the mod itself is added
  ///
  /// @pram emod if null, nothing happens
  /// @param emods if null or empty, nothing happens
  /// @return this, for chaining. Implementations should change to actual type returned.
  IJModified emod(EMod emod, EMod... emods);

  /// @param emods if null or empty, nothing is changed
  /// @return this, for chaining. Implementations should change to actual type returned.
  IJModified removeEMod(EMod... emods);

  /// @return a set of the internal emods. For performances, the set may be modifiable, but the modification may not be reflected on the actual values.
  Set<EMod> emods();

  /// @param emods if null or empty, return true
  /// @return true if no mods is absent from the internal emods
  boolean isEMod(EMod... emods);

}
