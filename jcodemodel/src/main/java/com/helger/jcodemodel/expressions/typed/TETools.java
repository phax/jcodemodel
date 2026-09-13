package com.helger.jcodemodel.expressions.typed;

import com.helger.jcodemodel.expressions.typed.java.lang.StringExpression;
import com.helger.jcodemodel.expressions.typed.primitives.BoolExpression;
import com.helger.jcodemodel.expressions.typed.primitives.CharExpression;
import com.helger.jcodemodel.expressions.typed.primitives.DblExpression;
import com.helger.jcodemodel.expressions.typed.primitives.FltExpression;
import com.helger.jcodemodel.expressions.typed.primitives.IntExpression;
import com.helger.jcodemodel.expressions.typed.primitives.LngExpression;

/// Typed Expressions Tools. Static methods to avoid calling constructors or finding the correct class
public class TETools
{

  public static BoolExpression of (boolean b)
  {
    return BoolExpression.of (b);
  }

  public static CharExpression of (char c)
  {
    return CharExpression.of (c);
  }

  public static DblExpression of (double d)
  {
    return DblExpression.of (d);
  }

  public static FltExpression of (float f)
  {
    return FltExpression.of (f);
  }

  public static IntExpression of (int i)
  {
    return IntExpression.of (i);
  }

  public static LngExpression of (long l)
  {
    return LngExpression.of (l);
  }

  public static StringExpression of (String s)
  {
    return StringExpression.of (s);
  }

}
