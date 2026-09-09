package com.helger.jcodemodel.writer.settings;

public class Parentheses
{

  /// examples are given for [ ( a + b ) * c ] + d
  public static enum EParenthesesStrategy
  {
    // (((a)+(b))*(c))+(d)
    ALWAYS,

    // ((a+b)*c)+d
    NOTOKEN,

    // (a+b)*c+d
    REQUIRED
  }

  /// default strategy for parenthesis. Expressions can use a more precise one but should fall back
  /// to this when null.
  public EParenthesesStrategy global = EParenthesesStrategy.REQUIRED;

}
