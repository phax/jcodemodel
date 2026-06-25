package com.helger.jcodemodel.writer.options;

import java.util.function.Consumer;

public class Wrap {

  public enum EWrapMode {
    /// always wrap all the elements
    ALWAYS,
    /// never wrap any element. All on the same line
    NEVER,
    /// only the minimum number of elements.
    REQUIRED,
    /// once an element should be wrapped, all are.
    BINARY;

  }

  /// wrapping is consired required if an element would increase the line above
  /// this number of characters
  public int lineCharacters = 80;

  public EWrapMode methodParams = EWrapMode.NEVER;

  public Wrap configure(Consumer<Wrap> conf) {
    conf.accept(this);
    return this;
  }

}
