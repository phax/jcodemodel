package com.helger.jcodemodel.literals;

import java.util.Locale;
import java.util.function.IntFunction;
import java.util.function.LongFunction;

import org.jspecify.annotations.NonNull;

/// base to represent an int/long in.
/// 
/// Those also contain the way to apply parameters when representing, in the corresponding represent methods 
///
/// @see https://docs.oracle.com/javase/specs/jls/se17/html/jls-3.html#jls-3.10.1
public enum IntegerBase
{
  BINARY ("0b", Integer::toBinaryString, Long::toBinaryString),
  DECIMAL ("", Integer::toString, Long::toString)
  {
    @Override
    protected String pad (@NonNull String body, int qtty)
    {
      return body;
    }
  },
  HEX ("0x", Integer::toHexString, Long::toHexString),
  OCTAL ("0", Integer::toOctalString, Long::toOctalString);

  @NonNull
  final IntFunction <String> intFormat;

  @NonNull
  final LongFunction <String> longFormat;

  @NonNull
  final String prefixLowerCased;
  @NonNull
  final String prefixUpperCased;

  IntegerBase (String prefix, IntFunction <String> intFormat, LongFunction <String> longFormat)
  {
    this.prefixLowerCased = prefix.toLowerCase (Locale.ROOT);
    this.prefixUpperCased = prefix.toUpperCase (Locale.ROOT);
    this.intFormat = intFormat;
    this.longFormat = longFormat;
  }

  public StringBuilder represent (int i, StringBuilder sb, boolean prefixUpper, int padding, int sepEvery, int sepSize)
  {
    boolean neg = i < 0;
    i = neg ? -i : i;
    if (neg)
      sb.append ('-');
    sb.append (prefixUpper ? prefixUpperCased : prefixLowerCased);
    addSep (pad (intFormat.apply (i), padding), sepEvery, sepSize, sb);
    return sb;
  }

  public StringBuilder represent (long l, StringBuilder sb, boolean prefixUpper, int padding, int sepEvery, int sepSize, boolean suffixUpper)
  {
    boolean neg = l < 0;
    l = neg ? -l : l;
    if (neg)
      sb.append ('-');
    sb.append (prefixUpper ? prefixUpperCased : prefixLowerCased);
    addSep (pad (longFormat.apply (l), padding), sepEvery, sepSize, sb);
    sb.append (suffixUpper ? 'L' : 'l');
    return sb;
  }

  /// @param source unsigned non-prefixed representation , eg a5 for -0xa5 .
  static void addSep (@NonNull String source, int sepEvery, int sepSize, StringBuilder sb)
  {
    if (sepEvery < 1 || sepEvery >= source.length () || sepSize < 1)
    {
      sb.append (source);
      return;
    }
    String sep = "_".repeat (sepSize);
    for (int start = 0, end = source.length () % sepEvery; end <= source.length (); start = end, end += sepEvery)
    {
      if (start != 0)
        sb.append (sep);
      sb.append (source.substring (start, end));
    }
  }

  protected String pad (@NonNull String body, int qtty)
  {
    if (qtty <= body.length ())
      return body;
    return "0".repeat (qtty - body.length ()) + body;
  }
}