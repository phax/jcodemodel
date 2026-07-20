package com.helger.jcodemodel.expression;

import java.util.stream.Stream;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJFormatter;
import com.helger.jcodemodel.IVariableInitializer;
import com.helger.jcodemodel.JExpr;

public class JArrayInit implements IVariableInitializer {

  @NonNull
  private final IVariableInitializer[] elements;

  public JArrayInit(IVariableInitializer... elements) {
    this.elements = convertElements(elements);
  }

  static IVariableInitializer[] convertElements(IVariableInitializer[] elements) {
    if (elements == null) {
      return new IVariableInitializer[] {};
    }
    return Stream.of(elements).map(e -> e != null ? e : JExpr._null()).toArray(IVariableInitializer[]::new);
  }

  @Override
  public void generate(@NonNull IJFormatter f) {
    f.print('{');
    for (IVariableInitializer e : elements) {
      f.generable(e);
    }
    f.print('}');
  }

}
