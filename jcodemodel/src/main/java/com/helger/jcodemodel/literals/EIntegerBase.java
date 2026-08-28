package com.helger.jcodemodel.literals;

import java.util.Locale;
import java.util.function.IntFunction;
import java.util.function.LongFunction;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/// base to represent an int/long in.
/// 
/// Those also contain the way to apply parameters when representing, in the corresponding represent methods 
///
/// @see https://docs.oracle.com/javase/specs/jls/se17/html/jls-3.html#jls-3.10.1
public enum EIntegerBase
{
  BINARY ("0b", Integer::toBinaryString, Long::toBinaryString, false, true, false),
  DECIMAL ("", Integer::toString, Long::toString, false, false, false),
  HEXADECIMAL ("0x", Integer::toHexString, Long::toHexString, true, true, false),
  OCTAL ("0", Integer::toOctalString, Long::toOctalString, false, true, true);

  @NonNull
  final IntFunction <String> intFormat;

  @NonNull
  final LongFunction <String> longFormat;

  /// the lower-case base prefix. Only hex and bin have prefix case diff.
  @NonNull
  final String prefixLowerCased;

  /// the upper-case base prefix. Only hex and bin have prefix case diff.
  @NonNull
  final String prefixUpperCased;

  /// when true, the base allows padding. Only decimal does not allow padding, as a non-single
  /// leading 0 digit means base octal.
  final boolean enablePadding;

  /// when true (only hex), this base can produce different uppercase and lowercase body.
  final boolean enableBodyUpper;

  /// when true, the separator format can produce leading separators in the body ; when false, the
  /// body will always start with a base char.
  final boolean allowBodyLeadingSep;

  EIntegerBase (String prefix,
                IntFunction <String> intFormat,
                LongFunction <String> longFormat,
                boolean enableBodyUpper,
                boolean enablePadding,
                boolean allowBodyLeadingSep)
  {
    this.prefixLowerCased = prefix.toLowerCase (Locale.ROOT);
    this.prefixUpperCased = prefix.toUpperCase (Locale.ROOT);
    this.intFormat = intFormat;
    this.longFormat = longFormat;
    this.enableBodyUpper = enableBodyUpper;
    this.enablePadding = enablePadding;
    this.allowBodyLeadingSep = allowBodyLeadingSep;
  }

  /// @return sb
  public StringBuilder represent (int i,
                                  StringBuilder sb,
                                  IntegerRepresentation f)
  {
    boolean neg = i < 0;
    int posI = neg ? -i : i;
    if (neg)
      sb.append ('-');
    else
      if (f.positiveSign ())
        sb.append ('+');
    sb.append (f.prefixUpper () ? prefixUpperCased : prefixLowerCased);
    addSep (padBody (caseBody (intFormat.apply (posI), f.bodyUpper ()), f.padding ()),
            f.separateFormat (),
            allowBodyLeadingSep,
            f.separateEvery (),
            f.separatorSize (),
            sb);
    return sb;
  }

  /// @return sb
  public StringBuilder represent (long l,
                                  StringBuilder sb,
                                  IntegerRepresentation f)
  {
    boolean neg = l < 0;
    long posL = neg ? -l : l;
    if (neg)
      sb.append ('-');
    else
      if (f.positiveSign ())
        sb.append ('+');
    sb.append (f.prefixUpper () ? prefixUpperCased : prefixLowerCased);
    addSep (padBody (caseBody (longFormat.apply (posL), f.bodyUpper ()), f.padding ()),
            f.separateFormat (),
            allowBodyLeadingSep,
            f.separateEvery (),
            f.separatorSize (),
            sb);
    sb.append (f.suffixUpper () ? 'L' : 'l');
    return sb;
  }

  /// if the base differentiates upper and lower body, put it in the corresponding case.
  protected @NonNull String caseBody (@NonNull String body, boolean upper)
  {
    if (enableBodyUpper)
      return upper ? body.toUpperCase (Locale.ROOT) : body.toLowerCase (Locale.ROOT);
    return body;
  }

  private static final char PAD_CHAR = '0';
  private static final String PAD_STRING = String.valueOf (PAD_CHAR);

  /// if the base is padding enabled (so not decimal), prefix the body to match given length
  protected @NonNull String padBody (@NonNull String body, int qtty)
  {
    if (!enablePadding || qtty <= body.length ())
      return body;
    return PAD_STRING.repeat (qtty - body.length ()) + body;
  }

  private static final char SEP_CHAR = '_';
  private static final String SEP_STRING = String.valueOf (SEP_CHAR);

  /// Append a source with inserted separators into a stringbuilder.
  ///
  ///
  /// ## Separator format
  ///
  /// The separator format is applied when non null ; otherwise, the sepEvery and sepSize are used.
  ///
  /// Each '_' in it specifies before which char (from the end) and in which quantity to insert
  /// separator ( '_' ) ; other chars mean to copy from source. Last char is always assumed to be
  /// non-separator, as terminating sep is not allowed in the body.
  ///
  /// For example, a format "c__l" means to insert 2 underscore before the last char, denoted with
  /// 'l'. In that example, the 'c' character is useless so this is functionally the same as "__l",
  /// or "cc__a".
  ///
  /// The format "__" means to insert a single sep before the last char. This is because the last
  /// format char is always assumed to be non-sep ; so this is functionally the same as "_X"
  ///
  /// ## Separate every, size
  ///
  /// When the format is null and both sepEvery and sepSize are >0 , series of *sepSize* separators
  /// are inserted every *sepEvery* character of source, starting from the end.
  ///
  /// @param source unsigned non-prefixed body representation , eg a5 for -0xa5L . If empty, nothing
  /// is done (should never be called)
  /// @param sepFormat separator format.
  /// @param allowLeadingSep when true, allow to insert separator before the first source char. When
  /// false, the first added char should be the one in source.
  static void addSep (@NonNull String source,
                      @Nullable String sepFormat,
                      boolean allowLeadingSep,
                      int sepEvery,
                      int sepSize,
                      @NonNull StringBuilder sb)
  {
    if (source.isEmpty ())
      return;
    if (sepFormat != null)
    {
      if (sepFormat.indexOf (SEP_CHAR) == -1)
      {
        sb.append (source);
        return;
      }
      else
      {
        // since we use separators, we start from the last chars, so we reverse the separator format
        // and build the reversed formatted representation.
        StringBuilder reversed = new StringBuilder ();
        // body must always end with non-sep, so here assume last format is non-sep
        reversed.append (source.charAt (source.length () - 1));
        for (int formatIndex = sepFormat.length () - 2, sourceIndex = source.length () - 2; formatIndex >= 0 ||
          sourceIndex >= 0; formatIndex--)
        {
          if (formatIndex < 0)
          {
            for (int i = sourceIndex; i >= 0; i--)
              reversed.append (source.charAt (i));
            break;
          }
          else
          {
            if (sepFormat.charAt (formatIndex) == SEP_CHAR)
              if (allowLeadingSep || sourceIndex >= 0)
                reversed.append (SEP_CHAR);
              else
                break;
            else
              if (sourceIndex < 0)
              {
                break;
              }
              else
              {
                reversed.append (source.charAt (sourceIndex));
                sourceIndex--;
              }
          }
        }
        sb.append (reversed.reverse ());
      }
    }
    else
    {
      if (sepEvery < 1 || sepEvery >= source.length () || sepSize < 1)
      {
        sb.append (source);
        return;
      }
      String sep = SEP_STRING.repeat (sepSize);
      for (int start = 0, end = source.length () % sepEvery; end <= source.length (); start = end, end += sepEvery)
      {
        if (start != 0)
          sb.append (sep);
        sb.append (source.substring (start, end));
      }
    }
  }
}
