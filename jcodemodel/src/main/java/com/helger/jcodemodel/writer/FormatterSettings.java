package com.helger.jcodemodel.writer;

import java.util.function.Consumer;

import com.helger.jcodemodel.writer.settings.Indent;
import com.helger.jcodemodel.writer.settings.Wrap;
import com.helger.jcodemodel.writer.settings.Wrap.ListWrapping.EListWrapStrategy;
import com.helger.jcodemodel.writer.settings.Wrap.WordWrapping.EWordWrapStrategy;

public class FormatterSettings {

  //
  // help configs
  //

  public static final Consumer<FormatterSettings> CONF_PHELGER = settings -> {
    settings.indent
        .useSpaces (2)
        .tabSize (2);
    settings.wrap.method.args
        .condition(EListWrapStrategy.REQUIRED);
    settings.wrap.method.bracket
        .condition(EWordWrapStrategy.ALWAYS);
  };

  public static FormatterSettings phelger() {
    return new FormatterSettings().configure(CONF_PHELGER);
  }

  public static final Consumer<FormatterSettings> CONF_GLELOUET = settings -> {
    settings.indent
        .useTabs(1)
        .tabSize(2);
    settings.wrap.catchClause.types
        .condition(EListWrapStrategy.BINARY)
        .indent(2);
    settings.wrap.forLoop.init
        .condition(EListWrapStrategy.BINARY)
        .indent(2);
    settings.wrap.method.args
        .condition(EListWrapStrategy.BINARY)
        .indent(2);
    settings.wrap.method.bracket
        .condition(EWordWrapStrategy.NEVER);
    settings.wrap.method.params
        .condition(EListWrapStrategy.BINARY)
        .indent(2);
    settings.wrap.variables.block
        .condition(EListWrapStrategy.BINARY)
        .indent(2);
    settings.wrap.variables.field
        .condition(EListWrapStrategy.BINARY)
        .indent(2);
  };

  public static FormatterSettings glelouet() {
    return new FormatterSettings().configure(CONF_PHELGER);
  }

  //
  //
  //

  public final Indent indent = new Indent();

  public final Wrap wrap = new Wrap();

  public FormatterSettings configure(Consumer<FormatterSettings> conf) {
    conf.accept(this);
    return this;
  }

}
