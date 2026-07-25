package com.helger.jcodemodel.literals;

/// record containing the params to represent an int/long 
/// 
/// a representation is made of a prefix, a body, and a suffix 
///  - The prefix is absent for decimal, but discriminant for other bases.
///  - The prefix can be set upper or lower (as prefix "0x" is same as "0X" )
///  - The body is never empty, the actual representation depends on the base
///  - once build from the base, the body *may* be padded, if requested, depending on the base (dec base does not allow padding)
///  - separator are then applied to the (padded) body, using "_"
///  - suffix is only present for long type. They can be upper or lower cased.
public record IntegerRepresentation (
                                     /// if the base needs a prefix, should we uppercase it ?
                                     boolean prefixUpper,
                                     /// the base in which we want to represent the number, eg octal, binary
                                     IntegerBase base,
                                     /// if possible (not dec base), append 0 to make the representation's body at least   
                                     int padding,
                                     /// how many characters to skip in the body before adding a
                                     /// separator String.
                                     int separateEvery,
                                     /// number of "_" characters a separator string is made of.
                                     int separatorSize,
                                     /// if representing long, should we uppercase the terminal "l"
                                     boolean sufixUpper)
{

  /// Default options are
  /// - lowercase the prefix, so "0x" instead of "0X"
  /// - use decimal representation
  /// - no padding of the body
  /// - no separator
  /// - if adding separators, use size 1
  /// - for long type, uppercase the terminal "L"
  public static final IntegerRepresentation DEFAULT = new IntegerRepresentation (false,
                                                                                 IntegerBase.DECIMAL,
                                                                                 0,
                                                                                 0,
                                                                                 1,
                                                                                 true);

  //
  // mutators
  //

  public IntegerRepresentation prefixUppder (boolean prefixUpper)
  {
    return new IntegerRepresentation (prefixUpper, base, padding, separateEvery, separatorSize, sufixUpper);
  }

  public IntegerRepresentation base (IntegerBase base)
  {
    return new IntegerRepresentation (prefixUpper, base, padding, separateEvery, separatorSize, sufixUpper);
  }

  public IntegerRepresentation padding (int padding)
  {
    return new IntegerRepresentation (prefixUpper, base, padding, separateEvery, separatorSize, sufixUpper);
  }

  public IntegerRepresentation separateEvery (int separateEvery)
  {
    return new IntegerRepresentation (prefixUpper, base, padding, separateEvery, separatorSize, sufixUpper);
  }

  public IntegerRepresentation separatorSize (int separatorSize)
  {
    return new IntegerRepresentation (prefixUpper, base, padding, separateEvery, separatorSize, sufixUpper);
  }

  public IntegerRepresentation sufixUpper (boolean sufixUpper)
  {
    return new IntegerRepresentation (prefixUpper, base, padding, separateEvery, separatorSize, sufixUpper);
  }

  //
  // actual formatting is delegated to the base
  //

  public String format (int i)
  {
    return base.represent (i, new StringBuilder (), prefixUpper, padding, separateEvery, separatorSize).toString ();
  }

  public String format (long l)
  {
    return base.represent (l, new StringBuilder (), prefixUpper, padding, separateEvery, separatorSize, sufixUpper)
               .toString ();
  }

}
