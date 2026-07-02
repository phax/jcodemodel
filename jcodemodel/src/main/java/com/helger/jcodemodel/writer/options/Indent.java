package com.helger.jcodemodel.writer.options;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.writer.JCMWriter;

///
///  recommended way to set indentation is to call #useSpaces or  #useTabs ; but can directly set the string.
///
public class Indent {

  public String string = JCMWriter.DEFAULT_INDENT_STRING;

  /// how many spaces do we consider a tab to take
  public int tabSize = 4;

  public Indent useSpaces(int nb) {
    string = " ".repeat(nb);
    return this;
  }

  /// defaults to 4 spaces
  public Indent useSpaces() {
    return useSpaces(4);
  }

  public Indent useTabs(int nb) {
    string = "\t".repeat(nb);
    return this;
  }

  /// defaults to 1 tab
  public Indent useTabs() {
    return useTabs(1);
  }

  public String string() {
    return string;
  }

  public Indent withString(@NonNull String string) {
    if (string == null || string.isEmpty()) {
      this.string = "";
    } else {
      this.string = string;
    }
    return this;
  }

  public Indent tabSize(int size) {
    tabSize = size;
    return this;
  }

  public int tabSize() {
    return tabSize;
  }

}
