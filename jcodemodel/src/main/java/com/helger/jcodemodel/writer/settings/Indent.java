package com.helger.jcodemodel.writer.settings;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonnegative;
import com.helger.jcodemodel.writer.JCMWriter;

///
///  recommended way to set indentation is to call #useSpaces or  #useTabs ; but can directly set the string.
///
public class Indent
{
  public String m_sStr = JCMWriter.DEFAULT_INDENT_STRING;

  /// how many spaces do we consider a tab to take
  public int m_nTabSize = 4;

  public Indent ()
  {}

  @NonNull
  public Indent useSpaces (@Nonnegative final int nb)
  {
    m_sStr = " ".repeat (nb);
    return this;
  }

  /// defaults to 4 spaces
  @NonNull
  public Indent useSpaces ()
  {
    return useSpaces (4);
  }

  @NonNull
  public Indent useTabs (@Nonnegative final int nb)
  {
    m_sStr = "\t".repeat (nb);
    return this;
  }

  /// defaults to 1 tab
  @NonNull
  public Indent useTabs ()
  {
    return useTabs (1);
  }

  @NonNull
  public String string ()
  {
    return m_sStr;
  }

  @NonNull
  public Indent withString (@Nullable final String string)
  {
    if (string == null || string.isEmpty ())
    {
      this.m_sStr = "";
    }
    else
    {
      this.m_sStr = string;
    }
    return this;
  }

  @NonNull
  public Indent tabSize (@Nonnegative final int size)
  {
    m_nTabSize = size;
    return this;
  }

  public int tabSize ()
  {
    return m_nTabSize;
  }

}
