package com.helger.jcodemodel.expressions.typed.java.lang;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.expressions.ITypedExpression;
import com.helger.jcodemodel.expressions.typed.TypedExpressionWrapper;
import com.helger.jcodemodel.expressions.typed.primitives.ASubIntExpression;
import com.helger.jcodemodel.expressions.typed.primitives.ArrayExpression;
import com.helger.jcodemodel.expressions.typed.primitives.BoolExpression;
import com.helger.jcodemodel.expressions.typed.primitives.CharExpression;
import com.helger.jcodemodel.expressions.typed.primitives.IntExpression;

//
// Since operator+ is defined on String, this one must be done manually.
//
// Also a good way to find the algorithm ^^
//
// ref : https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/lang/String.html
//
public class StringExpression extends ObjectExpression <String>
{

  public static class Array extends ArrayExpression <String>
  {

    public Array (IJExpression raw)
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

  public static StringExpression param(JMethod m, String name) {
    return m.paramTyped (name, String.class, StringExpression::of);
  }

  /// @return `that.charAt(index)`
  public CharExpression charAt (ASubIntExpression <?, ?, ?> index)
  {
    return new CharExpression (raw.invoke ("charAt").arg (index));
  }

  // TODO public IntStream chars()

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

  // TODO public IntStream codePoints()

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

  // TODO public static String format(Locale l,
  // String format,
  // Object... args)

  // TODO public static String format(String format,
  // Object... args)

  // TODO public String formatted(Object... args)

  // TODO public byte[] getBytes()

  /// @return `that.indent(n)`
  public StringExpression indent (ASubIntExpression <?, ?, ?> n)
  {
    return new StringExpression (raw.invoke ("indent").arg (n));
  }

  /// @return `that.indexOf(ch, beginIndex, endIndex)`
  public StringExpression indexOf (ASubIntExpression <?, ?, ?> ch,
                                   ASubIntExpression <?, ?, ?> beginIndex,
                                   ASubIntExpression <?, ?, ?> endIndex)
  {
    return new StringExpression (raw.invoke ("indexOf").arg (ch).arg (beginIndex).arg (endIndex));
  }

  /// @return `that.length()`
  public IntExpression length ()
  {
    return new IntExpression (raw.invoke ("length"));
  }

  // operator + with a String operands promotes anything else to String.
  /// @return `that + other`
  public StringExpression plus (ITypedExpression <?> other)
  {
    return new StringExpression (raw.plus (other.raw ()));
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

}
