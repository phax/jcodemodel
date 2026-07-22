package com.helger.jcodemodel.expression;

import java.util.List;
import java.util.stream.Stream;

import org.jspecify.annotations.NonNull;

import com.helger.jcodemodel.IJFormatter;
import com.helger.jcodemodel.IVariableInitializer;
import com.helger.jcodemodel.JExpr;

public class JArrayInit implements IVariableInitializer {

  @NonNull
  private final List <IVariableInitializer> elements;

  public JArrayInit(IVariableInitializer... elements) {
    this.elements = convertElements(elements);
  }

  static List <IVariableInitializer> convertElements (IVariableInitializer[] elements)
  {
    if (elements == null)
      return List.of ();
    return Stream.of (elements).map (e -> e != null ? e : JExpr._null ()).toList ();
  }

  @Override
  public void generate(@NonNull IJFormatter f) {
    f.print('{');
    f.generable (elements, ",", f.settings ().wrap.variables.array);
    f.print('}');
  }

}
