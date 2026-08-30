package com.helger.jcodemodel.literals;

import org.jspecify.annotations.NonNull;

/// Something that has an IntegerRepresentation to update, in practice only JAtomInt and JAtomLong
/// 
/// Its abstract because it's just a tooling class to extend.
/// 
/// @param T must be declaring class, eg `class A extends AIntegerRepresented<A>`
public abstract class AIntegerRepresented <T extends AIntegerRepresented <T>>
{

  @NonNull
  protected IntegerRepresentation m_aRepresentation = IntegerRepresentation.DEFAULT;

  @SuppressWarnings ("unchecked")
  protected T self ()
  {
    return (T) this;
  }

  @NonNull
  public IntegerRepresentation representation ()
  {
    return m_aRepresentation;
  }

  /// change the internal representation to the provided one
  ///
  /// @return this
  /// @param representation if null, nothing changes.
  public @NonNull T representation (IntegerRepresentation representation)
  {
    if (representation != null)
      this.m_aRepresentation = representation;
    return self ();
  }

  /// change the internal representation to show positive sign
  ///
  /// @return this
  public @NonNull T positiveSign (boolean positiveSign)
  {
    return representation (representation ().positiveSign (positiveSign));
  }

  /// change the internal representation to use binary base
  ///
  /// @return this
  public @NonNull T binary ()
  {
    return representation (representation ().base (EIntegerBase.BINARY));
  }

  /// change the internal representation to use decimal base
  ///
  /// @return this
  public @NonNull T decimal ()
  {
    return representation (representation ().base (EIntegerBase.DECIMAL));
  }

  /// change the internal representation to use hexadecimal base
  ///
  /// @return this
  public @NonNull T hexadecimal ()
  {
    return representation (representation ().base (EIntegerBase.HEXADECIMAL));
  }

  /// change the internal representation to use octal base
  ///
  /// @return this
  public @NonNull T octal ()
  {
    return representation (representation ().base (EIntegerBase.OCTAL));
  }

  /// change the internal representation to use a fixed separator size (the number of character
  /// BETWEEN
  /// each separated group), used only when **NO** separator format is provided
  ///
  /// @return this
  public @NonNull T separatorSize (int size)
  {
    return representation (representation ().separatorSize (size));
  }

  /// change the internal representation to use a fixed separator distance (the maximum number of
  /// character IN
  /// a separated group), used only when **NO** separator format is provided
  ///
  /// @return this
  public @NonNull T separateEvery (int every)
  {
    return representation (representation ().separateEvery (every));
  }

  /// change the internal representation to use a padding value. The padding is not used for decimal
  /// base, since leading "0" makes an octal.
  ///
  /// @return this
  public @NonNull T padding (int padding)
  {
    return representation (representation ().padding (padding));
  }

}
