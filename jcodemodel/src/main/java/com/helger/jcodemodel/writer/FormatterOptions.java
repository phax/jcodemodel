package com.helger.jcodemodel.writer;

import java.util.function.Consumer;

import com.helger.jcodemodel.writer.options.Indent;
import com.helger.jcodemodel.writer.options.Wrap;

public class FormatterOptions {

  public final Indent indent = new Indent();

  public final Wrap wrap = new Wrap();

  public FormatterOptions configure(Consumer<FormatterOptions> conf) {
    conf.accept(this);
    return this;
  }

}
