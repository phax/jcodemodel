package com.helger.jcodemodel.expression;

import java.util.List;
import java.util.stream.Stream;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.jcodemodel.IJFormatter;
import com.helger.jcodemodel.IVariableInitializer;
import com.helger.jcodemodel.JExpr;

public class JArrayInit implements IVariableInitializer
{
  @NonNull
  private final List <IVariableInitializer> m_aElements;

  public JArrayInit (@Nullable final IVariableInitializer... elements)
  {
    m_aElements = convertElements (elements);
  }

  @NonNull
  static List <IVariableInitializer> convertElements (@Nullable final IVariableInitializer [] elements)
  {
    if (elements == null || elements.length == 0)
      return List.of ();
    return Stream.of (elements).map (e -> e != null ? e : JExpr._null ()).toList ();
  }

  @Override
  public void generate (@NonNull final IJFormatter f)
  {
    f.print ('{');
    f.generable (m_aElements, ",", f.settings ().wrap.variables.array);
    f.print ('}');
  }
}
