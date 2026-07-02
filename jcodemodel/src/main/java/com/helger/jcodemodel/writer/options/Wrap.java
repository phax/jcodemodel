package com.helger.jcodemodel.writer.options;

import com.helger.jcodemodel.writer.options.wrap.Method;

public class Wrap {

  /// configuration of a generated code's word wrapping : when to wrap it, how
  /// many
  /// indent.
  ///
  /// This is a generic idea for any one-time element, like the "implements" part
  /// of a class, a method's return type, etc.
  public static class WrapWord {

  public enum EWrapWordStrategy {
    /// always wrap the element
    ALWAYS(),
    /// never wrap
    NEVER(),
    /// only wrap if otherwise would go over the line size.
    REQUIRED();
  }

    /// when do we wrap this specific code generation
    public EWrapWordStrategy condition = EWrapWordStrategy.NEVER;

    /// when we wrap, how much do we indent the code
    public int indent = 1;

    public WrapWord condition(EWrapWordStrategy value) {
      if (value != null) {
        condition = value;
      }
      return this;
    }

    public WrapWord indent(int value) {
      indent = value;
      return this;
    }

  }

  /// configuration of a generated code's elements wrapping : when to wrap the
  /// elements, how many indent.
  ///
  /// This is a generic idea for list of elements which can be wrapped
  /// individually, like the list of interfaces implemented by a class, its list
  /// of Generics, etc.
  public static class WrapList {

  public enum EWrapListStrategy {
    /// always wrap all the elements
    ALWAYS(false),
    /// never wrap any element. All on the same line
    NEVER(false),
    /// only the minimum number of elements.
    REQUIRED(false),
    /// once an element should be wrapped, all are.
    BINARY(true),
    /// wrap all if more than 3 elements ; first item never wrapped
    PAST3(false);

    public final boolean twoPasses;

    EWrapListStrategy(boolean twoPasses) {
      this.twoPasses = twoPasses;
    }

  }

    /// when do we wrap this specific code generation
    public EWrapListStrategy condition = EWrapListStrategy.PAST3;

    /// when we wrap, how much do we indent the code
    public int indent = 1;

    public WrapList condition(EWrapListStrategy value) {
      if (value != null) {
        condition = value;
      }
      return this;
    }

    public WrapList indent(int value) {
      indent = value;
      return this;
    }

  }

  /// wrapping is required if an element would increase the line above
  /// this number of characters
  public int lineWidth = 80;

  public Wrap lineWidth(int value) {
    lineWidth = value;
    return this;
  }

  /// feature flag. When set to true, all wrapping methods should be replaced with
  /// wrapping-oblivious ones.
  public boolean disabled = false;

  public Wrap disable(boolean value) {
    disabled = value;
    return this;
  }

  public Wrap disable() {
    return disable(true);
  }

  public final Method method = new Method();

}
