package com.helger.jcodemodel.writer.settings.wrap;

import com.helger.jcodemodel.writer.settings.Wrap.ListWrapping;
import com.helger.jcodemodel.writer.settings.Wrap.ListWrapping.EListWrapStrategy;

public class Variables {

  /// array init for array variable
  public final ListWrapping array =
      new ListWrapping()
          .condition(EListWrapStrategy.REQUIRED)
          .indent(1);

  /// declaration of variable inside a block
  public final ListWrapping block =
      new ListWrapping()
          .condition(EListWrapStrategy.REQUIRED)
          .indent(1);

  /// declaration a class' field
  public final ListWrapping field =
      new ListWrapping()
          .condition(EListWrapStrategy.REQUIRED)
          .indent(1);

}
