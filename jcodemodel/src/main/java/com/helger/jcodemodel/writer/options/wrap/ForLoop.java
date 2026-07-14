package com.helger.jcodemodel.writer.options.wrap;

import com.helger.jcodemodel.writer.options.Wrap.ListWrapping;
import com.helger.jcodemodel.writer.options.Wrap.ListWrapping.EListWrapStrategy;

public class ForLoop {

  /// declaration of the loop init
  public final ListWrapping init =
      new ListWrapping()
          .condition(EListWrapStrategy.NEVER)
          .indent(1);

}
