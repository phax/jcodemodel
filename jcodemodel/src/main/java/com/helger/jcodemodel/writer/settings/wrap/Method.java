package com.helger.jcodemodel.writer.settings.wrap;

import com.helger.jcodemodel.writer.settings.Wrap.ListWrapping;
import com.helger.jcodemodel.writer.settings.Wrap.WordWrapping;
import com.helger.jcodemodel.writer.settings.Wrap.ListWrapping.EListWrapStrategy;
import com.helger.jcodemodel.writer.settings.Wrap.WordWrapping.EWordWrapStrategy;

public class Method {

  /// wrapping of the method's return type
  public final WordWrapping type =
      new WordWrapping()
          .condition(EWordWrapStrategy.NEVER)
          .indent(1);

  /// wrapping of the method's name when declaring it
  public final WordWrapping name =
      new WordWrapping()
          .condition(EWordWrapStrategy.NEVER)
          .indent(1);

  /// declaration of the method's params
  public final ListWrapping params =
      new ListWrapping()
          .condition(EListWrapStrategy.PAST3)
          .indent(1);

  /// wrapping of opening bracket ( '{' ) when declaring
  public final WordWrapping bracket =
      new WordWrapping()
          .condition(EWordWrapStrategy.NEVER)
          .indent(0);

  /// wrapping of the arguments when calling the method
  public final ListWrapping args =
      new ListWrapping()
          .condition(EListWrapStrategy.PAST3)
          .indent(1);

}
