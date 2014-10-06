package com.helger.jcodemodel.util;

public final class StringUtils
{
  private StringUtils ()
  {}

  public static String lower (String cap)
  {
    return Character.toLowerCase (cap.charAt (0)) + cap.substring (1);
  }

  public static String upper (String low)
  {
    return Character.toUpperCase (low.charAt (0)) + low.substring (1);
  }
}
