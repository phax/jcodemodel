package com.helger.jcodemodel.writer.settings.wrap;

import com.helger.jcodemodel.writer.settings.Wrap.ListWrapping;
import com.helger.jcodemodel.writer.settings.Wrap.ListWrapping.EListWrapStrategy;

public class ForLoop {

  /// declaration of the loop init
  public final ListWrapping init =
      new ListWrapping()
          .condition(EListWrapStrategy.NEVER)
          .indent(1);

}
