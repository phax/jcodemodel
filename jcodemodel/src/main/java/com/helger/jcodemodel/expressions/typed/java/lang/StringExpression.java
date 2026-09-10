package com.helger.jcodemodel.expressions.typed.java.lang;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.expressions.typed.ATypeExpressionWrapper;

public class StringExpression extends ATypeExpressionWrapper <String>
{

  public StringExpression (IJExpression raw)
  {
    super (raw);
  }
  
  public static StringExpression of (String value)
  {
    return new StringExpression (JExpr.lit (value));
  }

  /// creates `that + other`
  public StringExpression plus (ATypeExpressionWrapper <? extends String> other)
  {
    return new StringExpression (raw.plus (other.raw ()));
  }
  
  /// creates `that.length()`
  public IntExpression length ()
  {
    return new IntExpression (raw.invoke ("length"));
  }

  /// creates `that.startsWith(other)`
  public StringExpression startsWith (ATypeExpressionWrapper <? extends String> other)
  {
    return new StringExpression (raw.invoke ("startsWith"));
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
