package com.helger.jcodemodel.expressions.typed.java.lang;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.expressions.ITypedExpression;
import com.helger.jcodemodel.expressions.typed.TypedExpressionWrapper;
import com.helger.jcodemodel.expressions.typed.primitives.ASubIntExpression;
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
public class StringExpression extends TypedExpressionWrapper <String>
{

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

  /// creates `that.charAt(index)`
  public CharExpression charAt (ASubIntExpression <?, ?, ?> index)
  {
    return new CharExpression (raw.invoke ("charAt").arg (index));
  }

  /// creates `that.charAt(index)`
  public IntExpression codePointAt (ASubIntExpression <?, ?, ?> index)
  {
    return new IntExpression (raw.invoke ("codePointAt").arg (index));
  }

  /// creates `that.codePointBefore(index)`
  public IntExpression codePointBefore (ASubIntExpression <?, ?, ?> index)
  {
    return new IntExpression (raw.invoke ("codePointBefore").arg (index));
  }

  /// creates `that.codePointCount(beginIndex, endIndex)`
  public IntExpression codePointCount (ASubIntExpression <?, ?, ?> beginIndex, ASubIntExpression <?, ?, ?> endIndex)
  {
    return new IntExpression (raw.invoke ("codePointCount").arg (beginIndex).arg (endIndex));
  }

  /// creates `that.compareTo(anotherString)`
  public IntExpression compareTo (TypedExpressionWrapper <? extends String> anotherString)
  {
    return new IntExpression (raw.invoke ("compareTo").arg (anotherString));
  }

  /// creates `that.compareToFoldCase(str)`
  public IntExpression compareToFoldCase (TypedExpressionWrapper <? extends String> str)
  {
    return new IntExpression (raw.invoke ("compareToFoldCase").arg (str));
  }

  /// creates `that.compareToIgnoreCase(str)`
  public IntExpression compareToIgnoreCase (TypedExpressionWrapper <? extends String> str)
  {
    return new IntExpression (raw.invoke ("compareToIgnoreCase").arg (str));
  }

  /// creates `that.concat(str)`
  public StringExpression concat (TypedExpressionWrapper <? extends String> str)
  {
    return new StringExpression (raw.invoke ("concat").arg (str));
  }

  /// creates `that.contains(s)`
  public BoolExpression contains (TypedExpressionWrapper <? extends CharSequence> s)
  {
    return new BoolExpression (raw.invoke ("contains").arg (s));
  }

  /// creates `that.contentEquals(cs)`
  public BoolExpression contentEquals (TypedExpressionWrapper <? extends CharSequence> cs)
  {
    return new BoolExpression (raw.invoke ("contentEquals").arg (cs));
  }

  // can't keep base name since same erasure as the other contentEquals
  /// creates `that.contentEquals(sb)`
  public BoolExpression contentEqualsSB (TypedExpressionWrapper <? extends StringBuffer> sb)
  {
    return new BoolExpression (raw.invoke ("contentEquals").arg (sb));
  }

  /// creates `that.endsWith(suffix)`
  public BoolExpression endsWith (TypedExpressionWrapper <? extends String> suffix)
  {
    return new BoolExpression (raw.invoke ("endsWith").arg (suffix));
  }

  /// creates `that.equals(anObject)`
  public BoolExpression equals (TypedExpressionWrapper <?> anObject)
  {
    return new BoolExpression (raw.invoke ("equals").arg (anObject));
  }

  /// creates `that.equalsFoldCase(anotherString)`
  public BoolExpression equalsFoldCase (TypedExpressionWrapper <? extends String> anotherString)
  {
    return new BoolExpression (raw.invoke ("equalsFoldCase").arg (anotherString));
  }

  /// creates `that.equalsIgnoreCase(anotherString)`
  public BoolExpression equalsIgnoreCase (TypedExpressionWrapper <? extends String> anotherString)
  {
    return new BoolExpression (raw.invoke ("equalsIgnoreCase").arg (anotherString));
  }

  /// creates `that.hashCode()`
  public IntExpression hashCode_ ()
  {
    return new IntExpression (raw.invoke ("hashCode"));
  }

  /// creates `that.indent(n)`
  public StringExpression indent (ASubIntExpression <?, ?, ?> n)
  {
    return new StringExpression (raw.invoke ("indent").arg (n));
  }

  /// creates `that.indexOf(ch, beginIndex, endIndex)`
  public StringExpression indexOf (ASubIntExpression <?, ?, ?> ch,
                                   ASubIntExpression <?, ?, ?> beginIndex,
                                   ASubIntExpression <?, ?, ?> endIndex)
  {
    return new StringExpression (raw.invoke ("indexOf").arg (ch).arg (beginIndex).arg (endIndex));
  }

  /// creates `that.length()`
  public IntExpression length ()
  {
    return new IntExpression (raw.invoke ("length"));
  }

  // operator + with a String operands promotes anything else to String.
  /// creates `that + other`
  public StringExpression plus (ITypedExpression <?> other)
  {
    return new StringExpression (raw.plus (other.raw ()));
  }

  /// creates `that.startsWith(prefix)`
  public StringExpression startsWith (TypedExpressionWrapper <? extends String> prefix)
  {
    return new StringExpression (raw.invoke ("startsWith").arg (prefix));
  }

  /// creates `that.strip()`
  public StringExpression strip ()
  {
    return new StringExpression (raw.invoke ("strip"));
  }

  /// creates `that.stripIndent()`
  public StringExpression stripIndent ()
  {
    return new StringExpression (raw.invoke ("stripIndent"));
  }

  /// creates `that.stripLeading()`
  public StringExpression stripLeading ()
  {
    return new StringExpression (raw.invoke ("stripLeading"));
  }

  /// creates `that.stripTrailing()`
  public StringExpression stripTrailing ()
  {
    return new StringExpression (raw.invoke ("stripTrailing"));
  }

}
