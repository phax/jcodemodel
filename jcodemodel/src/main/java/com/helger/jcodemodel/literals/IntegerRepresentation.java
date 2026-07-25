package com.helger.jcodemodel.literals;

import java.util.Objects;

import org.jspecify.annotations.NonNull;

/// record containing the params to represent an int/long. This is mostly a data carrier, the actual behavior is in the base.
/// 
/// A representation is made using a base, of a prefix, a body, and a suffix, with :
///  
///  - The prefix is absent for decimal, but discriminant for other bases.
///  - The prefix can be set upper or lower (as prefix "0x" is same as "0X" )
///  - The body is never empty, the actual representation depends on the base
///  - once build from the base, the body *may* be padded, if requested, depending on the base (dec base does not allow padding)
///  - separator are then applied to the (padded) body, using "_"
///  - suffix is only present for long type. They can be upper or lower cased.
public record IntegerRepresentation (
                                     /// should we print '+' if the number is positive ?
                                     boolean positiveSign,
                                     /// if the base needs a prefix, should we uppercase it ?
                                     boolean prefixUpper,
                                     /// the base in which we want to represent the number, eg
                                     /// octal, binary
                                     @NonNull EIntegerBase base,
                                     /// if possible (not dec base), append 0 to make the
                                     /// representation's body at least
                                     int padding,
                                     /// how many characters to skip in the body before adding a
                                     /// separator String.
                                     int separateEvery,
                                     /// number of "_" characters a separator string is made of.
                                     int separatorSize,
                                     /// if representing long, should we uppercase the terminal "l"
                                     /// ?
                                     boolean sufixUpper)
{

  // validation

  public IntegerRepresentation
  {
    Objects.requireNonNull (base);
  }

  // default values

  /// Default options are :
  ///
  /// - use decimal base.
  /// - lowercase the prefix, so "0x" instead of "0X"
  /// - no padding of the body
  /// - no separator
  /// - if adding separators, use size 1
  /// - for long type, uppercase the terminal "L"
  ///
  /// from spec :
  /// > The suffix L is preferred, because the letter l (ell) is often hard to distinguish from the
  /// > digit 1 (one).
  ///
  public static final IntegerRepresentation DEFAULT = new IntegerRepresentation (false,
                                                                                 false,
                                                                                 EIntegerBase.DECIMAL,
                                                                                 0,
                                                                                 0,
                                                                                 1,
                                                                                 true);

  /// print binary with 8-chars separations, eg "1_00000000" or "1_00000000_00000000L"
  public static final IntegerRepresentation BIN = DEFAULT.with (null, null, EIntegerBase.BINARY, null, 8, null, null);

  /// bits with padding to 8 chars
  public static final IntegerRepresentation BIN8 = BIN.padding (8);

  /// print decimals with 3-chars separations, eg "1_000" or "1_234_567L"
  public static final IntegerRepresentation DEC = DEFAULT.separateEvery (3);

  // print hexa with 2-chars separations, eg "0xAA_BB"
  public static final IntegerRepresentation HEX = DEFAULT.with (null,
                                                                null,
                                                                EIntegerBase.HEXADECIMAL,
                                                                null,
                                                                2,
                                                                null,
                                                                null);

  // print octal with 4-chars separations, eg "01_0000"
  public static final IntegerRepresentation OCT = DEFAULT.with (null, null, EIntegerBase.OCTAL, null, 4, null, null);

  //
  // mutators
  //

  public IntegerRepresentation positiveSign (boolean positiveSign)
  {
    return new IntegerRepresentation (positiveSign,
                                      prefixUpper,
                                      base,
                                      padding,
                                      separateEvery,
                                      separatorSize,
                                      sufixUpper);
  }

  public IntegerRepresentation prefixUpper (boolean prefixUpper)
  {
    return new IntegerRepresentation (positiveSign,
                                      prefixUpper,
                                      base,
                                      padding,
                                      separateEvery,
                                      separatorSize,
                                      sufixUpper);
  }

  public IntegerRepresentation base (EIntegerBase base)
  {
    return new IntegerRepresentation (positiveSign,
                                      prefixUpper,
                                      base,
                                      padding,
                                      separateEvery,
                                      separatorSize,
                                      sufixUpper);
  }

  public IntegerRepresentation padding (int padding)
  {
    return new IntegerRepresentation (positiveSign,
                                      prefixUpper,
                                      base,
                                      padding,
                                      separateEvery,
                                      separatorSize,
                                      sufixUpper);
  }

  public IntegerRepresentation separateEvery (int separateEvery)
  {
    return new IntegerRepresentation (positiveSign,
                                      prefixUpper,
                                      base,
                                      padding,
                                      separateEvery,
                                      separatorSize,
                                      sufixUpper);
  }

  public IntegerRepresentation separatorSize (int separatorSize)
  {
    return new IntegerRepresentation (positiveSign,
                                      prefixUpper,
                                      base,
                                      padding,
                                      separateEvery,
                                      separatorSize,
                                      sufixUpper);
  }

  public IntegerRepresentation sufixUpper (boolean sufixUpper)
  {
    return new IntegerRepresentation (positiveSign,
                                      prefixUpper,
                                      base,
                                      padding,
                                      separateEvery,
                                      separatorSize,
                                      sufixUpper);
  }

  /// This is present to avoid long chains of configuration when updating from a base model
  /// @return new record with fields overwritten by non-null params.
  public IntegerRepresentation with (Boolean newPositiveSign,
                                     Boolean newPrefixUpper,
                                     EIntegerBase newBase,
                                     Integer newPadding,
                                     Integer newSepEvery,
                                     Integer newSepSize,
                                     Boolean newSuffixUpper)
  {
    if ((newPositiveSign == null || newPositiveSign == positiveSign) &&
      (newPrefixUpper == null || newPrefixUpper == prefixUpper) &&
      (newBase == null || newBase == base) &&
      (newPadding == null || newPadding == padding) &&
      (newSepEvery == null || newSepEvery == separateEvery) &&
      (newSepSize == null || newSepSize == separatorSize) &&
      (newSuffixUpper == null || newSuffixUpper == sufixUpper))
    {
      return this;
    }
    return new IntegerRepresentation (newPositiveSign == null ? positiveSign : newPositiveSign,
                                      newPrefixUpper == null ? prefixUpper : newPrefixUpper,
                                      newBase == null ? base : newBase,
                                      newPadding == null ? padding : newPadding,
                                      newSepEvery == null ? separateEvery : newSepEvery,
                                      newSepSize == null ? separatorSize : newSepSize,
                                      newSuffixUpper == null ? sufixUpper : newSuffixUpper);
  }

  //
  // actual formatting is delegated to the base
  //

  public String format (int i)
  {
    return base.represent (i, new StringBuilder (), positiveSign, prefixUpper, padding, separateEvery, separatorSize)
               .toString ();
  }

  public String format (long l)
  {
    return base.represent (l,
                           new StringBuilder (),
                           positiveSign,
                           prefixUpper,
                           padding,
                           separateEvery,
                           separatorSize,
                           sufixUpper).toString ();
  }

}
