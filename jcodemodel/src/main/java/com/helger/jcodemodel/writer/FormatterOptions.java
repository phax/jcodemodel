package com.helger.jcodemodel.writer;

import java.util.function.Consumer;

import com.helger.jcodemodel.writer.options.Indent;

public class FormatterOptions {

  public final Indent indent = new Indent();

  public FormatterOptions confIndent(Consumer<Indent> conf) {
    conf.accept(indent);
    return this;
  }

}
