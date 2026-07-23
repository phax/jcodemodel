package com.helger.jcodemodel.writer.settings.wrap;

import com.helger.jcodemodel.writer.settings.Wrap.ListWrapping;
import com.helger.jcodemodel.writer.settings.Wrap.ListWrapping.EListWrapStrategy;

public class CatchClause {

  /// declaration of the catch types
  public final ListWrapping types =
      new ListWrapping()
          .condition(EListWrapStrategy.NEVER)
          .indent(1);

}
