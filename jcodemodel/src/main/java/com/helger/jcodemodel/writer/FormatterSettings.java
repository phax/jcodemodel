package com.helger.jcodemodel.writer;

import java.util.function.Consumer;

import com.helger.jcodemodel.writer.settings.Indent;
import com.helger.jcodemodel.writer.settings.Wrap;

public class FormatterSettings {

  public final Indent indent = new Indent();

  public final Wrap wrap = new Wrap();

  public FormatterSettings configure(Consumer<FormatterSettings> conf) {
    conf.accept(this);
    return this;
  }

}
