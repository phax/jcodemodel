package com.helger.jcodemodel.expressions.typed.java.lang;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.expressions.ITypedExpression;
import com.helger.jcodemodel.expressions.typed.TypedExpressionWrapper;
import com.helger.jcodemodel.expressions.typed.primitives.ASubIntExpression;
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

  /// creates `that.compareTo(prefix)`
  public IntExpression compareTo (TypedExpressionWrapper <? extends String> prefix)
  {
    return new IntExpression (raw.invoke ("compareTo").arg (prefix));
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
