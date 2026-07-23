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
  // Variable name is used externally
  public String string = JCMWriter.DEFAULT_INDENT_STRING;

  /// how many spaces do we consider a tab to take
  // Variable name is used externally
  public int tabSize = 4;

  public Indent ()
  {}

  @NonNull
  public Indent useSpaces (@Nonnegative final int nb)
  {
    string = " ".repeat (nb);
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
    string = "\t".repeat (nb);
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
    return string;
  }

  @NonNull
  public Indent withString (@Nullable final String str)
  {
    if (str == null || str.isEmpty ())
    {
      this.string = "";
    }
    else
    {
      this.string = str;
    }
    return this;
  }

  @NonNull
  public Indent tabSize (@Nonnegative final int size)
  {
    tabSize = size;
    return this;
  }

  public int tabSize ()
  {
    return tabSize;
  }
}
