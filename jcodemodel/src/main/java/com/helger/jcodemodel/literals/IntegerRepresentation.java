package com.helger.jcodemodel.literals;

import java.util.Objects;
import java.util.function.Consumer;

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
                                     // if using hex, should we uppercase the body ?
                                     boolean bodyUpper,
                                     /// if possible (not dec base), append 0 to make the
                                     /// representation's body at least
                                     int padding,
                                     /// when non null, specifies where to put underscores in the
                                     /// body.
                                     /// For example, " _ __ " requires to add 2 underscores before
                                     /// the last char and 1 before the one before. Note that
                                     /// leading underscores may be discarded, depending on the
                                     /// base.
                                     String separateFormat,
                                     /// how many characters to skip in the body before adding a
                                     /// separator String.
                                     int separateEvery,
                                     /// number of "_" characters a separator string is made of.
                                     int separatorSize,
                                     /// if representing long, should we uppercase the terminal "l"
                                     /// ?
                                     boolean suffixUpper)
{

  // validation

  public IntegerRepresentation
  {
    Objects.requireNonNull (base);
  }

  // copier

  /// Intermediate mutable class for mutate a record. Waiting for java withers …
  public static class Copier
  {
    public boolean positiveSign;
    public boolean prefixUpper;
    @NonNull
    public EIntegerBase base;
    public boolean bodyUpper;
    public int padding;
    public String separateFormat;
    public int separateEvery;
    public int separatorSize;
    public boolean suffixUpper;

    Copier(IntegerRepresentation ir){
      positiveSign=ir.positiveSign;
      prefixUpper = ir.prefixUpper;
      base=ir.base;
      bodyUpper=ir.bodyUpper;
      padding=ir.padding;
      separateFormat=ir.separateFormat;
      separateEvery=ir.separateEvery;
      separatorSize=ir.separatorSize;
      suffixUpper = ir.suffixUpper;
    }

    IntegerRepresentation toRecord ()
    {
      return new IntegerRepresentation (positiveSign,
                                        prefixUpper,
                                        base,
                                        bodyUpper,
                                        padding,
                                        separateFormat,
                                        separateEvery,
                                        separatorSize,
                                        suffixUpper);
    }

    public Copier apply (Consumer <Copier> c)
    {
      c.accept (this);
      return this;
    }

  }

  public Copier copier ()
  {
    return new Copier (this);
  }

  public IntegerRepresentation with (Consumer <Copier> change)
  {
    return copier ().apply (change).toRecord ();
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
                                                                                 true,
                                                                                 0,
                                                                                 null,
                                                                                 0,
                                                                                 1,
                                                                                 true);

  /// print binary with 8-chars separations, eg "1_00000000" or "1_00000000_00000000L"
  public static final IntegerRepresentation BIN = DEFAULT.with (ir -> {
    ir.base = EIntegerBase.BINARY;
    ir.separateEvery = 8;
  });

  /// bits with padding to 8 chars
  public static final IntegerRepresentation BIN8 = BIN.padding (8);

  /// print decimals with 3-chars separations, eg "1_000" or "1_234_567L"
  public static final IntegerRepresentation DEC = DEFAULT.separateEvery (3);

  // print hexa with 2-chars separations, eg "0xAA_BB"
  public static final IntegerRepresentation HEX = DEFAULT.with (ir -> {
    ir.base = EIntegerBase.HEXADECIMAL;
    ir.separateEvery = 2;
  });

  // print octal with 4-chars separations, eg "01_0000"
  public static final IntegerRepresentation OCT = DEFAULT.with (ir -> {
    ir.base = EIntegerBase.OCTAL;
    ir.separateEvery = 4;
  });

  //
  // mutators
  //

  public IntegerRepresentation positiveSign (boolean positiveSign)
  {
    return positiveSign == this.positiveSign ? this : with (ir -> { ir.positiveSign = positiveSign; });
  }

  public IntegerRepresentation prefixUpper (boolean prefixUpper)
  {
    return prefixUpper == this.prefixUpper ? this : with (ir -> { ir.prefixUpper = prefixUpper; });
  }

  public IntegerRepresentation base (@NonNull EIntegerBase base)
  {
    return base == this.base ? this : with (ir -> { ir.base = base; });
  }

  public IntegerRepresentation padding (int padding)
  {
    return padding == this.padding ? this : with (ir -> { ir.padding = padding; });
  }

  public IntegerRepresentation separateEvery (int separateEvery)
  {
    return separateEvery == this.separateEvery ? this : with (ir -> { ir.separateEvery = separateEvery; });
  }

  public IntegerRepresentation separatorSize (int separatorSize)
  {
    return separatorSize == this.separatorSize ? this : with (ir -> { ir.separatorSize = separatorSize; });
  }

  public IntegerRepresentation sufixUpper (boolean suffixUpper)
  {
    return suffixUpper == this.suffixUpper ? this : with (ir -> { ir.suffixUpper = suffixUpper; });
  }

  //
  // actual formatting is delegated to the base
  //

  public String format (int i)
  {
    return base.represent (i, new StringBuilder (), this).toString ();
  }

  public String format (long l)
  {
    return base.represent (l, new StringBuilder (), this).toString ();
  }

}
