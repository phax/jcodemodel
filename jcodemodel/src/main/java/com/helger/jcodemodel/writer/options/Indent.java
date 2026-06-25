package com.helger.jcodemodel.writer.options;

import org.jspecify.annotations.NonNull;

///
///
/// The space/tab part is made by using different values for space and tab number.
/// This means you can manually set spaces *plus* tabs.
/// The recommended usage is with #useTabs or #useSpaces to also return this, for faster chaining.
///
public class Indent {

  public int tabNb = 0;

  public int spaceNb = 4;

  public Indent useSpaces(int nb) {
    spaceNb = nb;
    tabNb = 0;
    return this;
  }

  /// defaults to 4 spaces
  public Indent useSpaces() {
    return useSpaces(4);
  }

  public Indent useTabs(int nb) {
    spaceNb = 0;
    tabNb = nb;
    return this;
  }

  /// defaults to 1 tab
  public Indent useTabs() {
    return useTabs(1);
  }

  public String string() {
    return " ".repeat(spaceNb) + "\t".repeat(tabNb);
  }

  /// set the number of spaces/tabs to the one in the provided string.
  public Indent withString(@NonNull String string) {
    if (string == null || string.isEmpty()) {
      spaceNb = tabNb = 0;
    } else {
      spaceNb = (int) string.chars().filter(c -> c == ' ').count();
      tabNb = (int) string.chars().filter(c -> c == '\t').count();
    }
    return this;
  }

}
