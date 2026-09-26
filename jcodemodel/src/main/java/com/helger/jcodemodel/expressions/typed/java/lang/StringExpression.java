package com.helger.jcodemodel.expressions.typed.java.lang;

import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.expressions.ITypedExpression;
import com.helger.jcodemodel.expressions.typed.TypedExpressionWrapper;
import com.helger.jcodemodel.expressions.typed.primitives.ASubIntExpression;
import com.helger.jcodemodel.expressions.typed.primitives.ArrayExpression;
import com.helger.jcodemodel.expressions.typed.primitives.BoolExpression;
import com.helger.jcodemodel.expressions.typed.primitives.ByteExpression.ByteArrExp;
import com.helger.jcodemodel.expressions.typed.primitives.CharExpression;
import com.helger.jcodemodel.expressions.typed.primitives.CharExpression.CharArrExp;
import com.helger.jcodemodel.expressions.typed.primitives.IntExpression;
import com.helger.jcodemodel.expressions.typed.primitives.VoidStatExpression;

//
// Since operator+ is defined on String, this one must be done manually.
//
// Also a good way to find the algorithm ^^
//
// ref : https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/lang/String.html
//
public class StringExpression extends ObjectExpression <String>
{

  public static class StringArrExp extends ArrayExpression <String>
  {

    public StringArrExp (IJExpression raw)
    {
      super (raw);
    }

    @Override
    public StringExpression at (ASubIntExpression <?, ?, ?> index)
    {
      return StringExpression.of (super.at (index));
    }

  }

  public StringExpression (IJExpression raw)
  {
    super (raw);
  }

  public static StringExpression of (String value)
  {
    return new StringExpression (JExpr.lit (value));
  }

  public static StringExpression of (IJExpression raw)
  {
    return new StringExpression (raw);
  }

  public static StringExpression of (ITypedExpression <?> untyped)
  {
    return of (untyped.raw ());
  }

  public static StringExpression param (JMethod m, String name)
  {
    return m.paramTyped (name, String.class, StringExpression::of);
  }

  /// @return `that.charAt(index)`
  public CharExpression charAt (ASubIntExpression <?, ?, ?> index)
  {
    return new CharExpression (raw.invoke ("charAt").arg (index));
  }

  /// @return `that.chars()`
  public ObjectExpression <? extends IntStream> chars ()
  {
    return new ObjectExpression <> (raw.invoke ("chars"));
  }

  /// @return `that.charAt(index)`
  public IntExpression codePointAt (ASubIntExpression <?, ?, ?> index)
  {
    return new IntExpression (raw.invoke ("codePointAt").arg (index));
  }

  /// @return `that.codePointBefore(index)`
  public IntExpression codePointBefore (ASubIntExpression <?, ?, ?> index)
  {
    return new IntExpression (raw.invoke ("codePointBefore").arg (index));
  }

  /// @return `that.codePointCount(beginIndex, endIndex)`
  public IntExpression codePointCount (ASubIntExpression <?, ?, ?> beginIndex, ASubIntExpression <?, ?, ?> endIndex)
  {
    return new IntExpression (raw.invoke ("codePointCount").arg (beginIndex).arg (endIndex));
  }

  /// @return `that.codePoints()`
  public ObjectExpression <? extends IntStream> codePoints ()
  {
    return new ObjectExpression <> (raw.invoke ("codePoints"));
  }

  /// @return `that.compareTo(anotherString)`
  public IntExpression compareTo (TypedExpressionWrapper <? extends String> anotherString)
  {
    return new IntExpression (raw.invoke ("compareTo").arg (anotherString));
  }

  /// @return `that.compareToFoldCase(str)`
  public IntExpression compareToFoldCase (TypedExpressionWrapper <? extends String> str)
  {
    return new IntExpression (raw.invoke ("compareToFoldCase").arg (str));
  }

  /// @return `that.compareToIgnoreCase(str)`
  public IntExpression compareToIgnoreCase (TypedExpressionWrapper <? extends String> str)
  {
    return new IntExpression (raw.invoke ("compareToIgnoreCase").arg (str));
  }

  /// @return `that.concat(str)`
  public StringExpression concat (TypedExpressionWrapper <? extends String> str)
  {
    return new StringExpression (raw.invoke ("concat").arg (str));
  }

  /// @return `that.contains(s)`
  public BoolExpression contains (TypedExpressionWrapper <? extends CharSequence> s)
  {
    return new BoolExpression (raw.invoke ("contains").arg (s));
  }

  /// @return `that.contentEquals(cs)`
  public BoolExpression contentEquals (TypedExpressionWrapper <? extends CharSequence> cs)
  {
    return new BoolExpression (raw.invoke ("contentEquals").arg (cs));
  }

  // can't keep base name since same erasure as the other contentEquals
  /// @return `that.contentEquals(sb)`
  public BoolExpression contentEqualsSB (TypedExpressionWrapper <? extends StringBuffer> sb)
  {
    return new BoolExpression (raw.invoke ("contentEquals").arg (sb));
  }

  /// @return `that.endsWith(suffix)`
  public BoolExpression endsWith (TypedExpressionWrapper <? extends String> suffix)
  {
    return new BoolExpression (raw.invoke ("endsWith").arg (suffix));
  }

  /// @return `that.equalsFoldCase(anotherString)`
  public BoolExpression equalsFoldCase (TypedExpressionWrapper <? extends String> anotherString)
  {
    return new BoolExpression (raw.invoke ("equalsFoldCase").arg (anotherString));
  }

  /// @return `that.equalsIgnoreCase(anotherString)`
  public BoolExpression equalsIgnoreCase (TypedExpressionWrapper <? extends String> anotherString)
  {
    return new BoolExpression (raw.invoke ("equalsIgnoreCase").arg (anotherString));
  }

  // ignore public static String format(Locale l, String format, Object... args)

  // ignore public static String format(String format, Object... args)

  /// @return `that.formatted(args)`
  public StringExpression formatted (ObjectExpression <?>... args)
  {
    JInvocation invk = raw.invoke ("formatted");
    if (args != null)
      for (ObjectExpression <?> a : args)
      {
        invk.arg (a);
      }
    return new StringExpression (invk);
  }

  /// @return `that.getBytes()`
  public ByteArrExp getBytes ()
  {
    return new ByteArrExp (raw.invoke ("getBytes"));
  }

  /// @return `that.getBytes(charset)`
  public ByteArrExp getBytes (ITypedExpression <? extends Charset> charset)
  {
    return new ByteArrExp (raw.invoke ("getBytes").arg (charset));
  }

  // ignore @Deprecated(since="1.1") public void getBytes(int srcBegin, int srcEnd, byte[] dst, int
  // dstBegin)

  /// Named to avoid classh with Charset one.
  /// @return `that.getBytes(charsetName)`
  public ByteArrExp getBytesStr (ITypedExpression <? extends String> charsetName)
  {
    return new ByteArrExp (raw.invoke ("getBytes").arg (charsetName));
  }

  /// @return `that.getChars(srcBegin, srcEnd, dst, dstBegin)`
  public VoidStatExpression getChars (ASubIntExpression <?, ?, ?> srcBegin,
                                      ASubIntExpression <?, ?, ?> srcEnd,
                                      CharArrExp dst,
                                      ASubIntExpression <?, ?, ?> dstBegin)
  {
    return new VoidStatExpression (raw.invoke ("getChars").arg (srcBegin).arg (srcEnd).arg (dst).arg (dstBegin));
  }

  /// @return `that.indent(n)`
  public StringExpression indent (ASubIntExpression <?, ?, ?> n)
  {
    return new StringExpression (raw.invoke ("indent").arg (n));
  }

  /// @return `that.indexOf(ch, beginIndex, endIndex)`
  public IntExpression indexOf (ASubIntExpression <?, ?, ?> ch,
                                ASubIntExpression <?, ?, ?> beginIndex,
                                ASubIntExpression <?, ?, ?> endIndex)
  {
    return new IntExpression (raw.invoke ("indexOf").arg (ch).arg (beginIndex).arg (endIndex));
  }

  /// @return `that.indexOf(ch, fromIndex)`
  public IntExpression indexOf (ASubIntExpression <?, ?, ?> ch, ASubIntExpression <?, ?, ?> fromIndex)
  {
    return new IntExpression (raw.invoke ("indexOf").arg (ch).arg (fromIndex));
  }

  /// @return `that.indexOf(ch)`
  public IntExpression indexOf (ASubIntExpression <?, ?, ?> ch)
  {
    return new IntExpression (raw.invoke ("indexOf").arg (ch));
  }

  /// @return `that.indexOf(str, beginIndex, endIndex)`
  public IntExpression indexOf (ITypedExpression <? extends String> str,
                                ASubIntExpression <?, ?, ?> beginIndex,
                                ASubIntExpression <?, ?, ?> endIndex)
  {
    return new IntExpression (raw.invoke ("indexOf").arg (str).arg (beginIndex).arg (endIndex));
  }

  /// @return `that.indexOf(str, fromIndex)`
  public IntExpression indexOf (ITypedExpression <? extends String> str, ASubIntExpression <?, ?, ?> fromIndex)
  {
    return new IntExpression (raw.invoke ("indexOf").arg (str).arg (fromIndex));
  }

  /// @return `that.indexOf(str)`
  public IntExpression indexOf (ITypedExpression <? extends String> str)
  {
    return new IntExpression (raw.invoke ("indexOf").arg (str));
  }

  /// @return `that.intern()`
  public StringExpression intern ()
  {
    return new StringExpression (raw.invoke ("intern"));
  }

  /// @return `that.isBlank()`
  public BoolExpression isBlank ()
  {
    return new BoolExpression (raw.invoke ("isBlank"));
  }

  /// @return `that.isEmpty()`
  public BoolExpression isEmpty ()
  {
    return new BoolExpression (raw.invoke ("isEmpty"));
  }

  /// @return `that.join(delimiter, elements)`
  @SuppressWarnings ("unchecked")
  public StringExpression join (TypedExpressionWrapper <? extends CharSequence> delimiter,
                                TypedExpressionWrapper <? extends CharSequence>... elements)
  {
    JInvocation invoke = raw.invoke ("join").arg (delimiter);
    if (elements != null)
    {
      for (TypedExpressionWrapper <? extends CharSequence> te : elements)
      {
        invoke.arg (te);
      }
    }
    return new StringExpression (invoke);
  }

  /// @return `that.join(delimiter, elements)`
  public StringExpression join (TypedExpressionWrapper <? extends CharSequence> delimiter,
                                TypedExpressionWrapper <? extends Iterable <? extends CharSequence>> elements)
  {
    return new StringExpression (raw.invoke ("join").arg (delimiter).arg (elements));
  }

  /// @return `that.lastIndexOf(ch, fromIndex)`
  public IntExpression lastIndexOf (ASubIntExpression <?, ?, ?> ch, ASubIntExpression <?, ?, ?> fromIndex)
  {
    return new IntExpression (raw.invoke ("lastIndexOf").arg (ch).arg (fromIndex));
  }

  /// @return `that.lastIndexOf(ch)`
  public IntExpression lastIndexOf (ASubIntExpression <?, ?, ?> ch)
  {
    return new IntExpression (raw.invoke ("lastIndexOf").arg (ch));
  }

  /// @return `that.lastIndexOf(str, fromIndex)`
  public IntExpression lastIndexOf (ITypedExpression <? extends String> str, ASubIntExpression <?, ?, ?> fromIndex)
  {
    return new IntExpression (raw.invoke ("lastIndexOf").arg (str).arg (fromIndex));
  }

  /// @return `that.lastIndexOf(str)`
  public IntExpression lastIndexOf (ITypedExpression <? extends String> str)
  {
    return new IntExpression (raw.invoke ("lastIndexOf").arg (str));
  }

  /// @return `that.length()`
  public IntExpression length ()
  {
    return new IntExpression (raw.invoke ("length"));
  }

  // TODO use StreamExpression when avail.
  /// @return `that.lines()`
  public ObjectExpression <? extends Stream <? extends String>> lines ()
  {
    return new ObjectExpression <> (raw.invoke ("lines"));
  }

  /// @return `that.matches(regex)`
  public BoolExpression matches (TypedExpressionWrapper <? extends String> regex)
  {
    return new BoolExpression (raw.invoke ("matches").arg (regex));
  }

  // operator + with a String operands promotes anything else to String.
  /// @return `that + other`
  public StringExpression plus (ITypedExpression <?> other)
  {
    return new StringExpression (raw.plus (other.raw ()));
  }

  /// @return `that.offsetByCodePoints(index, codePointOffset)`
  public IntExpression offsetByCodePoints (ASubIntExpression <?, ?, ?> index,
                                           ASubIntExpression <?, ?, ?> codePointOffset)
  {
    return new IntExpression (raw.invoke ("offsetByCodePoints").arg (index).arg (codePointOffset));
  }

  /// @return `that.regionMatches(ignoreCase, toffset, other, ooffset, len)`
  public BoolExpression regionMatches (BoolExpression ignoreCase,
                                       ASubIntExpression <?, ?, ?> toffset,
                                       TypedExpressionWrapper <? extends String> other,
                                       ASubIntExpression <?, ?, ?> ooffset,
                                       ASubIntExpression <?, ?, ?> len)
  {
    return new BoolExpression (raw.invoke ("regionMatches")
                                  .arg (ignoreCase)
                                  .arg (toffset)
                                  .arg (other)
                                  .arg (ooffset)
                                  .arg (len));
  }

  /// @return `that.regionMatches(toffset, other, ooffset, len)`
  public BoolExpression regionMatches (ASubIntExpression <?, ?, ?> toffset,
                                       TypedExpressionWrapper <? extends String> other,
                                       ASubIntExpression <?, ?, ?> ooffset,
                                       ASubIntExpression <?, ?, ?> len)
  {
    return new BoolExpression (raw.invoke ("regionMatches").arg (toffset).arg (other).arg (ooffset).arg (len));
  }

  /// @return `that.repeat(count)`
  public StringExpression repeat (ASubIntExpression <?, ?, ?> count)
  {
    return new StringExpression (raw.invoke ("repeat").arg (count));
  }

  /// @return `that.replace(oldChar, newChar)`
  public StringExpression replace (CharExpression oldChar, CharExpression newChar)
  {
    return new StringExpression (raw.invoke ("replace").arg (oldChar).arg (newChar));
  }

  /// @return `that.replace(target, replacement)`
  public StringExpression replace (ITypedExpression <? extends CharSequence> target,
                                   ITypedExpression <? extends CharSequence> replacement)
  {
    return new StringExpression (raw.invoke ("replace").arg (target).arg (replacement));
  }

  /// @return `that.replaceFirst(regex, replacement)`
  public StringExpression replaceFirst (ITypedExpression <? extends String> regex,
                                        ITypedExpression <? extends String> replacement)
  {
    return new StringExpression (raw.invoke ("replaceFirst").arg (regex).arg (replacement));
  }

  /// @return `that.replaceAll(regex, replacement)`
  public StringExpression replaceAll (ITypedExpression <? extends String> regex,
                                      ITypedExpression <? extends String> replacement)
  {
    return new StringExpression (raw.invoke ("replaceAll").arg (regex).arg (replacement));
  }

  /// @return `that.resolveConstantDesc(lookup)`
  public StringExpression resolveConstantDesc (ITypedExpression <? extends MethodHandles.Lookup> lookup)
  {
    return new StringExpression (raw.invoke ("resolveConstantDesc").arg (lookup));
  }

  /// @return `that.split(regex, limit)`
  public StringArrExp split (ITypedExpression <? extends String> regex, ASubIntExpression <?, ?, ?> limit)
  {
    return new StringArrExp (raw.invoke ("split").arg (regex).arg (limit));
  }

  /// @return `that.split(regex)`
  public StringArrExp split (ITypedExpression <? extends String> regex)
  {
    return new StringArrExp (raw.invoke ("split").arg (regex));
  }

  /// @return `that.splitWithDelimiters(regex, limit)`
  public StringArrExp splitWithDelimiters (ITypedExpression <? extends String> regex, ASubIntExpression <?, ?, ?> limit)
  {
    return new StringArrExp (raw.invoke ("splitWithDelimiters").arg (regex).arg (limit));
  }

  /// @return `that.startsWith(prefix)`
  public StringExpression startsWith (TypedExpressionWrapper <? extends String> prefix)
  {
    return new StringExpression (raw.invoke ("startsWith").arg (prefix));
  }

  /// @return `that.strip()`
  public StringExpression strip ()
  {
    return new StringExpression (raw.invoke ("strip"));
  }

  /// @return `that.stripIndent()`
  public StringExpression stripIndent ()
  {
    return new StringExpression (raw.invoke ("stripIndent"));
  }

  /// @return `that.stripLeading()`
  public StringExpression stripLeading ()
  {
    return new StringExpression (raw.invoke ("stripLeading"));
  }

  /// @return `that.stripTrailing()`
  public StringExpression stripTrailing ()
  {
    return new StringExpression (raw.invoke ("stripTrailing"));
  }

  // TODO use CharSequenceExpression when avail
  /// @return `that.subSequence(beginIndex, endIndex)`
  public ObjectExpression <CharSequence> subSequence (ASubIntExpression <?, ?, ?> beginIndex,
                                                      ASubIntExpression <?, ?, ?> endIndex)
  {
    return new ObjectExpression <> (raw.invoke ("subSequence").arg (beginIndex).arg (endIndex));
  }

  /// @return `that.substring(beginIndex, endIndex)`
  public StringExpression substring (ASubIntExpression <?, ?, ?> beginIndex, ASubIntExpression <?, ?, ?> endIndex)
  {
    return new StringExpression (raw.invoke ("substring").arg (beginIndex).arg (endIndex));
  }

  /// @return `that.substring(beginIndex)`
  public StringExpression substring (ASubIntExpression <?, ?, ?> beginIndex)
  {
    return new StringExpression (raw.invoke ("substring").arg (beginIndex));
  }

  /// @return `that.translateEscapes()`
  public StringExpression translateEscapes ()
  {
    return new StringExpression (raw.invoke ("translateEscapes"));
  }

  /// @return `that.transform(f)`
  public <R> ObjectExpression <? extends R> transform (ITypedExpression <? extends Function <? super String, ? extends R>> f)
  {
    return new ObjectExpression <> (raw.invoke ("transform").arg (f));
  }

  /// @return `that.trim()`
  public StringExpression trim ()
  {
    return new StringExpression (raw.invoke ("trim"));
  }

}
