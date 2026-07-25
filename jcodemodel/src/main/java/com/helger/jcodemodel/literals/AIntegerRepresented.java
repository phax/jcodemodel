package com.helger.jcodemodel.literals;

/// Something that has an IntegerRepresentation to update.
/// 
/// Its abstract because it's just a tooling class to extend.
/// 
/// @param T must be declaring class, eg `class A extends AIntegerRepresented<A>`
public abstract class AIntegerRepresented <T extends AIntegerRepresented <T>>
{

  protected IntegerRepresentation representation = IntegerRepresentation.DEFAULT;

  @SuppressWarnings ("unchecked")
  protected T self ()
  {
    return (T) this;
  }

  public IntegerRepresentation representation ()
  {
    return representation;
  }

  /// change the internal representation to the provided one
  ///
  /// @return this
  /// @see [IntegerRepresentation] for the meaning of individual fields
  public T representation (IntegerRepresentation representation)
  {
    if (representation != null)
      this.representation = representation;
    return self ();
  }

  /// change the internal representation to match the request
  ///
  /// @return this
  /// @see [IntegerRepresentation] for the meaning of individual fields
  public T binary ()
  {
    return representation (representation.base (IntegerBase.BINARY));
  }

  /// change the internal representation to match the request
  ///
  /// @return this
  /// @see [IntegerRepresentation] for the meaning of individual fields
  public T decimal ()
  {
    return representation (representation.base (IntegerBase.DECIMAL));
  }

  /// change the internal representation to match the request
  ///
  /// @return this
  /// @see [IntegerRepresentation] for the meaning of individual fields
  public T hex ()
  {
    return representation (representation.base (IntegerBase.HEX));
  }

  /// change the internal representation to match the request
  ///
  /// @return this
  /// @see [IntegerRepresentation] for the meaning of individual fields
  public T octal ()
  {
    return representation (representation.base (IntegerBase.OCTAL));
  }

  /// change the internal representation to match the request
  ///
  /// @return this
  /// @see [IntegerRepresentation] for the meaning of individual fields
  public T separatorSize (int size)
  {
    return representation (representation.separatorSize (size));
  }

  /// change the internal representation to match the request
  ///
  /// @return this
  /// @see [IntegerRepresentation] for the meaning of individual fields
  public T separateEvery (int every)
  {
    return representation (representation.separateEvery (every));
  }

  /// change the internal representation to match the request
  ///
  /// @return this
  /// @see [IntegerRepresentation] for the meaning of individual fields
  public T padding (int padding)
  {
    return representation (representation.padding (padding));
  }

}
