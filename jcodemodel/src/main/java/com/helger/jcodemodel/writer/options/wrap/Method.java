package com.helger.jcodemodel.writer.options.wrap;

import com.helger.jcodemodel.writer.options.Wrap.EWrapListStrategy;
import com.helger.jcodemodel.writer.options.Wrap.EWrapWordStrategy;
import com.helger.jcodemodel.writer.options.Wrap.WrapListMode;
import com.helger.jcodemodel.writer.options.Wrap.WrapWordMode;

public class Method {

  /// declaration of the method's params
  public final WrapListMode params =
      new WrapListMode()
          .condition(EWrapListStrategy.PAST3)
          .indent(1);

  /// wrapping of opening bracket ( '{' )
  public final WrapWordMode bracket =
      new WrapWordMode()
          .condition(EWrapWordStrategy.NEVER)
          .indent(0);

}
